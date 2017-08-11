# -*- encoding:utf-8 -*-

import jieba.analyse
import pandas as pd
import time
import sys
import os
import re
import copy
import codecs
import numpy as np
from collections import Counter
import math
import locale
import threading
from compiler.ast import flatten
import json
import copy

'''
功能改进:
增加了地名的自动提取功能,并且自动解析功能
'''
#获取脚本文件的当前路径
def cur_file_dir():
     #获取脚本路径
     path = sys.path[0]
     #判断为脚本文件还是py2exe编译后的文件，如果是脚本文件，则返回的是脚本的目录，如果是py2exe编译后的文件，则返回的是编译后的文件路径
     if os.path.isdir(path):
         return path
     elif os.path.isfile(path):
         return os.path.dirname(path)
#给jieba字典加单词（剧中角色词）
def to_userdict(add_words):
    for item in list(set(add_words)):
        if item != u"":
            jieba.add_word(item, freq=50, tag=u'n')

'''
函数功能:
按拼音对中文字符串列表排序,并返回原序列的索引(水木社区最精简的方式)
参数说明:
输入:
chistr_list:中文字符串列表
输出:
index_list:索引列表
'''
def sortspell_index(chistr_list):
    old_list=chistr_list
    new_list=zip(old_list,range(len(old_list)))
    new_list.sort(key = lambda x : x[0],cmp = locale.strcoll,reverse=True)
    index_list=[item[1] for item in new_list]
    return index_list

'''
功能一：清洗并产生词向量
'''
'''
清洗地点词：【majorView】，【minorView】，【shootLocation】
'''
def txt2list(InputTxt):
    fin = open(InputTxt, "r")
    fin_list_temp = fin.readlines()
    if fin_list_temp[0][:3] == codecs.BOM_UTF8:  # 处理不可见字符，关键用到codecs包
        fin_list_temp[0] = fin_list_temp[0][3:]
    fin_list = []
    for i in fin_list_temp:
        fin_list.append(i.strip().decode('utf-8'))  # 将停用词存放如list中，并转码
    fin.close()
    return fin_list

def to_cleanDf(data,stopwords_change,stopwords_scene):
    list_stopwords_change = txt2list(stopwords_change)
    list_stopwords_scene = txt2list(stopwords_scene)
    # 根据主场景和此场景的相似度匹配初始实拍景地（location_init)
    view = list(set(list(data['majorView']) + list(data['minorView'])))  # 主场景词 和 次场景词合并去重 ，并转换为list格式(包含空项）
    view_clean = clean_scene(scene_init=view, list_stopwords=list_stopwords_change)
    view_seg = cut_scene(list_scene=view_clean, list_stopwords=list_stopwords_scene)
    view_vec = scene2vec(list_segScene=view_seg, list_scene=view_clean)
    view_df = pd.DataFrame(zip(view, view_clean, view_vec),columns=["init_view", "clean_view", "vec_view"])

    location = list(set(data['shootLocation']))
    location_clean = clean_location(list_location=location)
    location_seg = cut_scene(list_scene=location_clean, list_stopwords=list_stopwords_scene)
    location_vec = scene2vec(list_segScene=location_seg, list_scene=location_clean)
    location_df = pd.DataFrame(zip(location, location_clean, location_vec),columns=["init_location", "clean_location", "vec_location"])

    # 原始文本-清洗文本-词向量文本，根据data顺序
    major_view_table = sort_vec(table_df=view_df, list_init=list(data["majorView"]))  # 第四部分簇内排序需要用
    minor_view_table = sort_vec(table_df=view_df, list_init=list(data["minorView"]))
    location_table = sort_vec(table_df=location_df, list_init=list(data["shootLocation"]))
    return major_view_table,minor_view_table,location_table

# 清洗场景词，本函数针对剧本《神犬小七》
def clean_scene(scene_init, list_stopwords):
    scene_clean = copy.deepcopy(scene_init)
    idx_bracket = re.compile(ur"[(（]{1}(.*?)[)）]{1}")
    idx_del = re.compile(ur"[(（]{1}.*?[)）]{1}")
    idx_change = re.compile(ur".*改(?P<change>.+)")
    for count_scene, each_scene in enumerate(scene_init):  # 清理括号内容，包括括号内“改”
        item = each_scene.strip()  # 逐行读取
        idx_del_result = idx_bracket.findall(item)
        if idx_del_result:  # 匹配到括号中的内容
            for item_del in idx_del_result:  # 遍历每个括号中的内容
                item_del_result = idx_change.match(item_del)
                if item_del_result:  # 括号中的内容匹配到含“改”字
                    scene_change1 = item_del_result.group("change")
                    if scene_change1 not in list_stopwords:  # “改” 字后的内容不是 “台词、词”
                        scene_clean[count_scene] = scene_change1
                        break
                    else:
                        scene_clean[count_scene] = re.sub(idx_del, "", item)  # 括号中没有匹配到 改 字的，删除括号内容
                else:
                    scene_clean[count_scene] = re.sub(idx_del, "", item)  # 括号中没有匹配到 改 字的，删除括号内容
        for count_scene_clean, each_scene_clean in enumerate(scene_clean):  # 清理括号外“改”
            item_clean = each_scene_clean.strip()
            idx_del_result2 = idx_change.match(item_clean)
            if idx_del_result2:
                scene_change2 = idx_del_result2.group("change")
                scene_clean[count_scene_clean] = scene_change2
            elif u"改" in item_clean:
                scene_clean[count_scene_clean] = re.sub(u"改", "", item_clean)
    return scene_clean

def clean_location(list_location):
    location_clean = copy.deepcopy(list_location)
    idx_change = re.compile(ur"[(（].*(?:导演|备拍|关系).*[)）]{1}")
    for count_location, each_location in enumerate(list_location):
        item = each_location.strip()  # 逐行读取
        item_del_result = idx_change.findall(item)
        if item_del_result:
            location_clean[count_location] = re.sub(idx_change, "", item)
    return location_clean

# 切割场景词（结巴分词+循环切割）
def cut_scene(list_scene, list_stopwords):
    seg_scene = []  # 存储切割后的场景词
    scene_clean_cut = list(set(list_scene) - set(list_stopwords))
    for item_scene_clean in scene_clean_cut:
        seg_scene_temp = jieba.cut(item_scene_clean, cut_all=False)  # 精确模式切词
        seg_scene.extend(seg_scene_temp)
    seg_scene1 = list(set(seg_scene) - set(list_stopwords))  # 去停用词
    idx_chinese = re.compile(ur"[\u4e00-\u9fa5]")
    idx_number = re.compile(ur"[0-9]")
    seg_scene1_temp = copy.deepcopy(seg_scene1)
    for item1 in seg_scene1_temp:
        item1_1 = item1.strip()
        idx_chinese_re = idx_chinese.findall(item1_1)
        idx_number_re = idx_number.findall(item1_1)
        if len(item1_1) == 0:
            seg_scene1.remove(item1)
        elif (not idx_chinese_re) and idx_number_re:
            seg_scene1.remove(item1)
    seg_scene2 = copy.deepcopy(seg_scene1)  # 循环去切割
    add_scene = []
    j_idx = range(1, len(seg_scene1))
    for i in xrange(len(seg_scene1) - 1):
        for j in j_idx[i:]:
            if (seg_scene1[j] in seg_scene1[i]) and (len(seg_scene1[j]) >= 2):
                cut_scene1 = seg_scene1[i].split(seg_scene1[j])
                add_scene.extend(cut_scene1)
                add_scene.append(seg_scene1[j])
                seg_scene2[i] = ""
            elif (seg_scene1[i] in seg_scene1[j]) and (len(seg_scene1[i]) >= 2):
                cut_scene1 = seg_scene1[j].split(seg_scene1[i])
                add_scene.extend(cut_scene1)
                add_scene.append(seg_scene1[i])
                seg_scene2[j] = ""
    seg_scene3 = seg_scene2 + add_scene
    seg_scene_re = list(set(seg_scene3))
    seg_scene5 = copy.deepcopy(seg_scene_re)
    for item2 in seg_scene5:
        item2_scene = item2.strip()
        # if (item2_scene) == 1) and item2_scene != u"家" :                  #删除所有除 家 以外的单字词
        if (len(item2_scene) == 1) and (item2_scene in list_stopwords):  # 删除所有单字 的停用词
            seg_scene_re.remove(item2)
        elif len(item2_scene) == 0:
            seg_scene_re.remove(item2)
    return seg_scene_re

def scene2vec(list_segScene, list_scene):
    scene_vec = [[0 for col in xrange(len(list_segScene))] for row in xrange(len(list_scene))]
    for inum_scene in xrange(len(list_scene)):
        item_scene = list_scene[inum_scene]
        for inum_word in xrange(len(list_segScene)):
            item_word = list_segScene[inum_word]
            if item_word in item_scene:
                if (len(item_word) == 1) and (item_word != u"家") and (item_word != u"找"):
                    scene_vec[inum_scene][inum_word] = 0.5
                else:
                    scene_vec[inum_scene][inum_word] = 1
    return scene_vec

def sort_vec(table_df, list_init):
    list_vec = []
    list_clean = []
    for item in list_init:
            list_vec.append(table_df.ix[list(table_df[table_df.columns[0]]).index(item), table_df.columns[2]])
            list_clean.append(table_df.ix[list(table_df[table_df.columns[0]]).index(item), table_df.columns[1]])
    table_vec = pd.DataFrame(zip(list_init, list_clean, list_vec),columns=["init_words", "clean_words", "vec_words"])
    return table_vec
'''
功能二:簇间排序
输入:play_df-数据框,sort_list-排序列表
输出:outer_class 输出类别、向量、索引
    classTagList 输出对应的类别标签
'''
class Cluster_outer_sort(object):
    def __init__(self,play_df,sort_list):
        self.scene_df = play_df
        self.Asort_list = sort_list
        self.outer_class,self.classTagList = self.outer_sort(df=self.scene_df,sort_list=self.Asort_list)

    #修改部分函数
    '''
        函数功能:将数据框簇间
    '''
    def outer_sort(self,df,sort_list):
        all_dict = {} #存储逻辑向量
        scene_arr = np.mat(np.array(np.array(df, dtype=np.bool), dtype=np.int)) #将场景表变成布尔值类型,1-0向量
        i = 0
        for item in df.columns:
            all_dict[item] = scene_arr[:, i] #利用字典存储每一项的向量
            i += 1
        #删除非角色的向量,majorView-minorView,shootLocation,shootRegion
        del_item = [u'shootRegion',u'shootLocation',u'majorView',u'minorView',u'viewId']
        for item in del_item:
            if item in all_dict.keys(): all_dict.pop(item)
        shootRegion_dict ={}
        if u'shootLocation' in sort_list:
            #修改部分------处理shootRegion
            shootRegion_list = list(df[u'shootRegion']) #shootRegion去重后的集合  --作为输入shootRegion
            #此处需要一个函数将shootRegion_list转换成对应的地名 - 省市区
            region_info = pd.read_csv(cur_file_dir()+u"/province_info_detail.csv", sep=',', encoding="utf-8", na_filter=False)
            region_class, region_table = self.dataFrame_to_dict(region_df=region_info)  # 建议pro_info直接以字典形式输入
            region_match = [self.regionMatch_L1(region_str=item.strip(), regionTable_dict=region_table, head_num=4)  for item in shootRegion_list]
            shootRegion_dict = {u'province': flatten(map(lambda x:x[u'province'],region_match)),u'city': flatten(map(lambda x:x[u'city'],region_match)) }
            # shootRegion_dict = json.loads(open(u"feature_json.txt","r").readlines()[0])
            #此处有个函数将地名转换成矩阵
            shootLocation_arr = self.to_shootRegionMatrix(shootRegion_dict = shootRegion_dict)
            all_dict[u'shootLocation'] = shootLocation_arr #将这个信息存储进all_dict
            feature_arr = self.to_featureArr(feature_dict=all_dict,sort_list = sort_list)
        else:
            feature_arr = self.to_featureArr(feature_dict=all_dict, sort_list=sort_list)
        veclist, indlist = self.to_classIndex(feature_arr=feature_arr)  # 通过顺序和数据框获得向量列表和索引
        matlist = self.Cluster_sorting(np.array(veclist)).tolist()  # 根据唯一的向量列表进行排序
        # for item in matlist:
        #     print item
        poslist = [matlist.index(item) for item in veclist]  # 根据排序后的向量矩阵调整排序前的向量 -可保留
        com_list = zip(poslist, veclist, indlist) #组合位置信息+向量信息+索引信息
        com_list.sort(key=lambda x: x[0], reverse=False) #按照matlist的顺序进行排序
        #生成classTagList
        classTagList = []
        for class_info in com_list:
            temp1_list = []
            idx = class_info[2][0]
            for item in sort_list:
                if item == u"shootLocation":
                    temp2_list = []
                    temp2_list.append(shootRegion_dict[u'province'][idx])
                    temp2_list.append(shootRegion_dict[u'city'][idx])
                    temp1_list.append(u"-".join(temp2_list))
                else:
                    if all_dict[item].T.tolist()[0][idx] == 1:
                        temp1_list.append(item)
            if len(temp1_list) != 0:
                classTagList.append(u",".join(temp1_list))
            else:
                classTagList.append(u"")
        #处理拍摄地的向量-----------这块儿待完善
        # shoot_place = set(list(df[u'shootRegion'])) #shootRegion去重后的集合
        # if u"" in shoot_place:
        #     shoot_place.remove(u"") #移除shoot_place中为空的拍摄地
        # shoot_place_list = list(shoot_place) #列表化字符串
        # shoot_place_numlist = [[]] * len(shoot_place) #拍摄地的数字列表
        # for item in df[u'shootRegion']: #对每个拍摄地生成向量
        #     for i in xrange(len(shoot_place)):
        #         shoot_place_numlist[i] = shoot_place_numlist[i] + [self.logist_prematch(match_str=shoot_place_list[i], target_str=item)]
        # shoot_place_sumlist = []
        # for item in shoot_place_numlist:
        #     shoot_place_sumlist.append(sum(item)) #拍摄地的数量列表
        # shoot_place_sort = zip(shoot_place, shoot_place_sumlist) #将拍摄地和拍摄地数量结合在一起
        # shoot_place_sort.sort(key=lambda x: x[1], reverse=True) #降序排列各自的数量
        # shoot_place_sort = [item[0] for item in shoot_place_sort] #获得拍摄地排序的第一个数
        # # 添加到字典
        # for i in xrange(len(shoot_place_list)):
        #     all_dict[shoot_place_list[i]] = shoot_place_numlist[i] #将向量添加进字典中
        # #处理道具
        # all_numdf = pd.DataFrame(all_dict) #将all_dict变成数据框
        # if u'shootLocation' in sort_list: #如果shootLocation在这个sort_list中则将shoot_place_sort放进去
        #     sort_list[sort_list.index(u'shootLocation')] = shoot_place_sort
        # sort_list = flatten(sort_list) #顺序进行unlist化
        # sort_listLen = len(sort_list) #sort_list计算长度
        # veclist, indlist = self.fli_comidx(str_priolist=sort_list, logi_df=all_numdf) #通过顺序和数据框获得向量列表和索引
        # matlist = self.Cluster_sorting(np.array(veclist)).tolist() #根据唯一的向量列表进行排序
        # for item in matlist: #
        #     temp_list = []
        #     for i in xrange(sort_listLen):
        #         if(item[i] == 1):
        #             temp_list.append(sort_list[i])
        #     if len(temp_list) != 0 :
        #         classTagList.append(u",".join(temp_list))
        #     else:
        #         classTagList.append(u"") #获取对应的类别标签
        # poslist = [matlist.index(item) for item in veclist] #根据排序后的向量矩阵调整排序前的向量 -可保留
        # com_list = zip(poslist, veclist, indlist)
        # com_list.sort(key=lambda x: x[0], reverse=False)
        return com_list,classTagList

    #################旧的函数#########################
    '''
    函数功能:
    通过满足优先级的字段,得出所有的向量组合及其对应的索引
    输入：
    str_priolist:字符优先级列表,logi_df:逻辑数据框
    输出:
    com_veclist:向量列表，com_indlist:索引列表
    '''
    def fli_comidx(self,str_priolist, logi_df):
        all_list = []
        for str in str_priolist:
            all_list.append(list(logi_df[str]))
        com_veclist = []
        all_list_T = np.array(all_list).T.tolist()
        for item in all_list_T:
            try:
                if com_veclist.index(item) > -1:
                    pass
            except ValueError:
                com_veclist.append(item)
        com_indlist = []
        for item in com_veclist:
            com_indlist.append(self.myfind(x=item, alist=all_list_T))
        return com_veclist, com_indlist

    def myfind(self,x, alist):
        return [a for a in range(len(alist)) if alist[a] == x]
    '''
    函数功能:逻辑模糊匹配，如果匹配字符项在目标项中则返回1，否则返回0
    输入:
    match_str:匹配字符项,target_str:目标字符项
    输出:
    返回1-0逻辑值
    '''
    def logist_fuzmatch(self, match_str, target_str):
        if match_str in target_str:
            return 1
        else:
            return 0
    '''
    函数功能:逻辑精确匹配，如果匹配字符项在目标项中则返回1，否则返回0
    输入:
    match_str:匹配字符项,target_str:目标字符项
    输出:
    返回1-0逻辑值
    '''
    def logist_prematch(self, match_str, target_str):
        if match_str == target_str:
            return 1
        else:
            return 0

    #######################################################
    '''
    函数功能:根据标记列表，将初始列表中的字符串转化为逻辑值，并生成逻辑向量
    输入:init_list:初始化列表;traget_list:标记列表
    输出:judge_m:列项矩阵
    '''
    def to_matrix(self, init_list, traget_list):
        init_m = np.array(init_list)  # 将列表转换成numpy形式
        temp_list = []
        for i in traget_list:
            judge_i = np.where(init_m == i, 1, 0)  # 对于标记列表中的每个元素,如果init_m中的含有第i项标记信息,则赋值为1，否则为0
            temp_list.append(list(judge_i))  # 加入列表中构造矩阵
        judge_m = np.array(temp_list).T  # 将矩阵做转置
        return judge_m
    '''
    函数功能:根据给定的位置字典，生成相应的0-1矩阵
    输入:shootRegion_dict:划分的省市区
    输出:m_all:所有列的矩阵
    '''
    def to_shootRegionMatrix(self,shootRegion_dict):
        loc_all = []  # 定义空列表loc_all
        m_all = np.array([])  # 定义空的矩阵m_all
        for i_dict in [u'province',u'city']:
            lines_temp = shootRegion_dict[i_dict]  # 对于最大长度length,取lines_dict中每一项
            loc_name = map(lambda x: x[0], Counter(lines_temp).most_common())  # 对列表中的每一项元素统计频次降序排列,并依次获得降序排列对应的名称
            if i_dict == u'province':
                loc_name.remove(u'其他')
                loc_name = loc_name + [u'其他']
            # print "--------------------"
            # for item in Counter(lines_temp).most_common():
            #     print item[0],item[1]
            # print "--------------------"
            # if i_dict == 0:
            #     print loc_name, " ".join(loc_name)
            loc_all.extend(loc_name)  # loc_all用于存储每个降序后的列表
            m = self.to_matrix(init_list=lines_temp, traget_list=loc_name)  # 进行依次地编码
            if len(m_all) == 0:
                m_all = m
            else:
                m_all = np.hstack((m_all, m))  #合并所有
        return m_all
    '''
    函数功能:根据特征矩阵字典和排序优先级生成完整的特征矩阵
    输入:feature_dict:特征字典,sort_list:排序规则
    输出:featureArr:特征矩阵
    '''
    def to_featureArr(self,feature_dict, sort_list):
        featureArr = np.array([])
        for item in sort_list:
            if len(featureArr) == 0:
                featureArr = feature_dict[item]
            else:
                featureArr = np.hstack((featureArr, feature_dict[item]))
        return featureArr

    def to_classIndex(self,feature_arr):
        uniq_list = self.unique_rows(feature_arr).tolist()
        raw_list = feature_arr.tolist()
        return uniq_list, [self.myfind(item, raw_list) for item in uniq_list]
    #--------------------------------------------------------------------------------
    """
    regionMatch_L1功能：将客户输入的实拍地规范化【按地域信息表进行分级整理】,一级整理
    输入：region_str，str类型,
         各种格式：【海南省-昌江市-七叉】,【海南-昌江-七叉】,【海南-昌江】,【昌江-七叉】,【海南-七叉】
                  【海南昌江七叉】,【海南昌江】,【昌江七叉】,【海南七叉】,【】,,,
                   【昌江】，【海南】,【七叉】
         regionTable_dict,字典格式{province:[],city:[],district:[]},省、市、区名称的去重整理
         head_num ,数字，取地域字符串的前几个字来进行省级匹配
    输出：region_re，dict类型，规范的三级地域格式,{province:[],city:[],district:[]}，例如{province:[海南],city:[昌江],district:[七叉]}
    """

    def regionMatch_L1(self, region_str, regionTable_dict, head_num):
        region_re = {}
        if u"-" in region_str:
            region_list = region_str.split("-")  # region_list 地域切割成list
            if len(region_list) == 0:
                region_list = [u"其他"] * 3
            elif len(region_list) == 1 and len(region_list[0].strip()) == 0:
                region_list = [u"其他"] * 3
            # 一级整理：地域省级匹配
            region_list = filter(lambda x: len(x.strip()) > 0, region_list)  # 删除列表中的空字符串
            if region_list == []:
                region_list = [u"其他"]
            prov_re = filter(lambda prov: prov in region_list[0], regionTable_dict["province"])
            if len(prov_re) == 0:
                region_re["province"] = [u"其他"]
            else:
                region_re["province"] = prov_re  # 可能出现prov_re长度不是1
                for i_del in prov_re:
                    region_list[0] = re.sub(i_del, "", region_list[0])
            # 一级整理：地域城市级匹配
            region_list = filter(lambda x: len(x.strip()) > 0, region_list)  # 删除列表中的空字符串
            if region_list == []:
                region_list = [u"其他"]
            for idx_city, item_city in enumerate(region_list):
                city_re = filter(lambda city: city in item_city, regionTable_dict["city"])
                if len(city_re) == 0:
                    region_re["city"] = [u"其他"]
                else:
                    region_re["city"] = city_re
                    for i_del in city_re:
                        region_list[idx_city] = re.sub(i_del, "", region_list[idx_city])
                    break
            # 一级整理：地域城市级匹配
            region_list = filter(lambda x: len(x.strip()) > 0, region_list)  # 删除列表中的空字符串
            if region_list == []:
                region_list = [u"其他"]
            for idx_dist, item_dist in enumerate(region_list):
                dist_re = filter(lambda dist: dist in item_dist, regionTable_dict["district"])
                if len(dist_re) == 0:
                    region_re["district"] = [u"其他"]
                else:
                    region_re["district"] = dist_re
                    for i_del in dist_re:
                        region_list[idx_dist] = re.sub(i_del, "", region_list[idx_dist])
                    break
        else:
            # 一级整理：地域省级匹配
            if len(region_str.strip()) == 0:
                region_str = u"其他"
            if len(region_str) <= head_num:
                head_str = region_str
                tail_str = u""
            else:
                head_str = region_str[:head_num]
                tail_str = region_str[head_num:]
            prov_re = filter(lambda prov: prov in head_str, regionTable_dict["province"])
            if len(prov_re) == 0:
                region_re["province"] = [u"其他"]
            else:
                region_re["province"] = prov_re  # 可能出现prov_re长度不是1
                for i_del in prov_re:
                    head_str = re.sub(i_del, "", head_str)
            region_str = '%s%s' % (head_str, tail_str)

            # 一级整理：地域城市级匹配
            if len(region_str.strip()) == 0:
                region_str = u"其他"
            city_re = filter(lambda city: city in region_str, regionTable_dict["city"])
            if len(city_re) == 0:
                region_re["city"] = [u"其他"]
            else:
                region_re["city"] = city_re
                for i_del in city_re:
                    region_str = re.sub(i_del, "", region_str)
            # 一级整理：地域区级匹配
            if len(region_str.strip()) == 0:
                region_str = u"其他"
            dist_re = filter(lambda dist: dist in region_str, regionTable_dict["district"])
            if len(dist_re) == 0:
                region_re["district"] = [u"其他"]
            else:
                region_re["district"] = dist_re
                for i_del in dist_re:
                    region_str = re.sub(i_del, "", region_str)
        return region_re

    """
    功能：将地域信息表从df格式转为dict格式
    输入：region_df，列名分别为【province,city,district】
    输出：regionClass_dict，字典格式{province{city:[district]}},信息内容的分级整理,省嵌套市,市嵌套区;
          regionTable_dict,字典格式{province:[],city:[],district:[]},省、市、区名称的去重整理
    """

    def dataFrame_to_dict(self,region_df):
        regionClass_dict = {}
        prov_uni = list(set(list(region_df["province"])))
        for i_prov in prov_uni:
            prov_df = region_df[region_df.province == i_prov]
            prov_dict = {}
            for idx in list(prov_df.index):
                prov_dict[prov_df.ix[idx, "city"]] = prov_df.ix[idx, "district"].split(",")
            regionClass_dict[i_prov] = prov_dict
        regionTable_dict = {}
        for i_column in list(region_df.columns):
            regionTable_list_temp = map(lambda x: x.split(","), list(region_df[i_column]))
            regionTable_list_uni = list(set(flatten(regionTable_list_temp)))
            regionTable_list = filter(lambda x: len(x.strip()) > 0, regionTable_list_uni)
            regionTable_dict[i_column] = regionTable_list
        return regionClass_dict, regionTable_dict

    # 输入矩阵a,删除重复行，顺序不变
    def unique_rows(self, a):
        a = np.ascontiguousarray(a)
        b = a.view([('', a.dtype)] * a.shape[1])
        b = [item[0] for item in b]
        unique_a = np.unique(a.view([('', a.dtype)] * a.shape[1]))
        unique_a_ind = [b.index(item) for item in unique_a]
        unique_zip = zip(unique_a_ind, unique_a)
        unique_zip.sort(key=lambda x: x[0], reverse=False)
        c = np.array([item[1] for item in unique_zip])
        return c.view(a.dtype).reshape((c.shape[0], a.shape[1]))
    # 输入一维矩阵a
    def judge_one(self,a):
        j1 = (a == np.ones(a.shape)).all()  # 是否所有的元素都等于1
        j2 = (a == np.zeros(a.shape)).all()  # 是否所有的元素都等于0
        if j1:  # 全1
            re = 1
        elif j2:  # 全0
            re = 0
        else:  # 有1有0
            re = 2
        return re
    # 输入一个矩阵a，截取a的row行，根据第col列,a_up是将该列为1的行放在上部分，为0的行放在下部分，a_down反之
    def order_col(self, a, row, col):
        a_up = a.copy()
        a_down = a.copy()
        a_temp = a[row, :].copy()
        location_1 = np.where(a_temp[:, col] == 1)[0]
        location_0 = np.where(a_temp[:, col] == 0)[0]
        up_idx = np.hstack((location_1, location_0))
        down_idx = np.hstack((location_0, location_1))
        a_up_temp = a_temp[up_idx, :]
        a_up[row, :] = a_up_temp
        a_down_temp = a_temp[down_idx, :]
        a_down[row, :] = a_down_temp
        return a_up, a_down
    # 输入M是各簇类别矩阵，生成标准顺序矩阵M2
    def Cluster_sorting(self, M):
        [r, c] = M.shape
        add = np.ones([r, 1])
        M1 = np.concatenate((add, M), axis=1)
        for j in xrange(1, (c + 1)):
            group = self.unique_rows(M1[:, 0:j])
            group_num = group.shape[0]  # 组数
            group_idx = ()
            for i_uni in xrange(group_num):
                temp1 = np.tile(group[i_uni], (r, 1))
                temp2 = np.where(M1[:, 0:j] == temp1, 1, 0)
                temp3 = np.where(np.sum(temp2, axis=1) == j)
                group_idx = group_idx + temp3
            if group_num == 1:
                M1 = self.order_col(M1, group_idx[0], j)[0]
            else:
                i_row = 0
                while i_row <= (group_num - 1):
                    if group_num - i_row >= 2:
                        col_up = M1[group_idx[i_row], j]
                        col_down = M1[group_idx[i_row + 1], j]
                        status_up = self.judge_one(col_up)
                        status_down = self.judge_one(col_down)
                        if (status_up >= 1) and (status_down == 2):  # up存在1，down有1有0
                            location = np.where(np.hstack((col_up, col_down)) == 1)[0]  # 两组中等于1 的位置
                            is_near_temp = np.diff(location)  # 两组中等于1 的位置是否连续
                            is_near = self.judge_one(is_near_temp)
                            if is_near != 1:
                                M1 = self.order_col(M1, group_idx[i_row], j)[1]
                                M1 = self.order_col(M1, group_idx[i_row + 1], j)[0]
                            i_row = i_row + 2
                        elif (status_up >= 1) and (status_down == 1):  # up存在1，down全1
                            location = np.where(np.hstack((col_up, col_down)) == 1)[0]  # 两组中等于1 的位置
                            is_near_temp = np.diff(location)  # 两组中等于1 的位置是否连续
                            is_near = self.judge_one(is_near_temp)
                            if is_near != 1:
                                M1 = self.order_col(M1, group_idx[i_row], j)[1]
                                M1 = self.order_col(M1, group_idx[i_row + 1], j)[0]
                            i_row = i_row + 1
                        elif (status_up >= 1) and (status_down == 0):  # up存在1，down全0
                            M1 = self.order_col(M1, group_idx[i_row], j)[0]
                            i_row = i_row + 2
                        elif (status_up == 0) and (status_down >= 1):  # up全是0，down存在1
                            M1 = self.order_col(M1, group_idx[i_row + 1], j)[0]
                            i_row = i_row + 1
                        else:
                            i_row = i_row + 2
                    else:
                        M1 = self.order_col(M1, group_idx[i_row], j)[0]
                        i_row = i_row + 1
        M2 = M1[:, 1:]
        return M2
'''
功能三:簇内排序
'''
'''
类功能:
簇单元架构
-增加节点(add_node)、移除节点(remove_node)、移动节点(move_node)
参数说明:
node_list:节点列表,node_num:节点数量,centroid:簇质心
'''
class ClusterUnit:
    def __init__(self):
        self.node_list = []  # 该簇存在的节点列表
        self.node_num = 0  # 该簇节点数
        self.centroid = None  # 该簇质心

    def add_node(self, node, node_vec):
        """
        为本簇添加指定节点，并更新簇心
         node_vec:该节点的特征向量
         node:节点
         return:null
        """
        self.node_list.append(node) #增加节点
        try:
            self.centroid = (self.node_num * self.centroid + node_vec) / (self.node_num + 1)  # 更新簇心
        except TypeError:
            self.centroid = np.array(node_vec) * 1  # 初始化质心
        self.node_num += 1  # 节点数加1

    def remove_node(self, node):
        # 移除本簇指定节点
        try:
            self.node_list.remove(node) #移除节点
            self.node_num -= 1
        except ValueError:
            raise ValueError("%s not in this cluster" % node)  #该簇本身就不存在该节点，移除失败

    def move_node(self, node, another_cluster):
        # 将本簇中的其中一个节点移至另一个簇
        self.remove_node(node=node)
        another_cluster.add_node(node=node)
'''
类功能:
通过阈值和词向量矩阵进行一簇聚类,返回各自的类及其对应的索引位置
参数说明:
输入:
wordvec:词向量矩阵,t:阈值
输出:
cluster_list:涵盖簇类对象列表
'''
class OnePassCluster:
    def __init__(self,wordvec, t):
        # t:一趟聚类的阈值
        self.threshold = 1.0-t  # 一趟聚类的阈值,设定阈值
        self.word_vectors = wordvec
        self.cluster_list =[] #用于存储聚类的类别对象
        self.source_data_index=[] #设置原数据索引序列

    #计算余弦距离公式
    def cosine_distance(self,vec_a, vec_b):
        d_a = math.sqrt(np.dot(vec_a, vec_a))
        d_b = math.sqrt(np.dot(vec_b, vec_b))
        if d_a == 0 and d_b ==0:
            return 0.0
        if d_a == 0 or d_b == 0:
            return 1.0
        else:
            return 1.0 - np.dot(vec_a, vec_b) / (d_a * d_b)
    '''
     思路:
      遍历每个点，计算第一个点质心与第一个点的距离作为初始最小距离,如果最小距离<最大限制距离,则对拥有最小距离的簇增加该节点,修改质心位置,否则增加新簇
    '''
    def clustering(self):
        self.cluster_list.append(ClusterUnit())  # 初始新建一个簇
        self.cluster_list[0].add_node(0, self.word_vectors[0])  # 将读入的第一个节点归于该簇
        for index in range(len(self.word_vectors))[1:]: #代表从第二项开始的列表
            distance_list=[] #定义距离列表
            for i in xrange(len(self.cluster_list)):
                # enumerate会将数组或列表组成一个索引序列
                # 寻找距离最小的簇，记录下距离和对应的簇的索引
                distance = self.cosine_distance(vec_a=self.word_vectors[index],vec_b=self.cluster_list[i].centroid) #计算余弦距离
                distance_list.append(distance) #增加距离
            min_distance=min(distance_list) #获取最小距离
            min_cluster_index=distance_list.index(min_distance) #最小距离的索引
            if min_distance < self.threshold:  # 最小距离小于阈值，则归于该簇
                self.cluster_list[min_cluster_index].add_node(index, self.word_vectors[index])
            else:  # 否则新建一个簇
                new_cluster = ClusterUnit()
                new_cluster.add_node(index, self.word_vectors[index])
                self.cluster_list.append(new_cluster)
                del new_cluster #回收变量的作用
        return self.cluster_list
'''
类功能:
字符串列表(同名)进行one-hot聚类,分成多个类,提取每个类的核心字符串,利用核心字符串簇间排序,再进行簇内拼音排序
输入:
str_indlist:字符串索引列表;all_strlist:所有的字符串列表;all_wordveclist:所有的词向量列表
输出:
onehot_cluster_indlist:onehot聚类索引列表;
cluster_keystr_list:各类核心词
sortspell_keystr_indlist:核心字符串簇间排序索引结果
'''
class Semantic_sort(object):
    def __init__(self,str_indlist,all_strlist,all_wordveclist,threshold):
        self.Astr_indlist=str_indlist
        self.Aall_strlist=all_strlist
        self.Aall_wordveclist=all_wordveclist
        self.Athreshold=threshold
        self.str_wordveclist=[all_wordveclist[index] for index in str_indlist]
        self.cluster_indlist,self.cluster_strlist= self.op_cluster() #环节一:聚类后索引
        self.class_outer_indlist=self.class_outer_sort() #环节二:类间排序后索引
        self.class_inner_indlist=self.class_inner_sort() #环节三:类内排序后索引
    #返回聚类结果对应的原始索引
    def op_cluster(self):
        cluster_object=OnePassCluster(wordvec=self.str_wordveclist,t=self.Athreshold)
        cluster_indlist= [item.node_list for item in cluster_object.clustering()]
        cluster_origindlist=[]
        cluster_orgistrlist=[]
        for item in cluster_indlist:
            cluster_origindlist.append([self.Astr_indlist[index] for index in item])
            cluster_orgistrlist.append([self.Aall_strlist[self.Astr_indlist[index]] for index in item])
        return cluster_origindlist,cluster_orgistrlist
    #类间排序--返回排完序的索引结果
    def class_outer_sort(self):
        cla_num_list=[]
        for ind_list in self.cluster_indlist:
            cla_num_list.append(len(ind_list))
        num_ind_list=zip(cla_num_list,self.cluster_indlist)
        num_ind_list.sort(key=lambda x:x[0],reverse=True)
        alsort_indlist=[item[1] for item in num_ind_list]
        return alsort_indlist
    #类内排序--返回排完序的索引结果
    def class_inner_sort(self):
        class_is_indlist=[]
        for item in self.class_outer_indlist:
            temp_str_list=[ self.Aall_strlist[index] for index in item ]
            temp_str_sortlist=sortspell_index(temp_str_list)
            temp_sort_indlist=[item[index] for index in temp_str_sortlist]
            class_is_indlist.append(temp_sort_indlist)
        return class_is_indlist

'综合以上类进行簇内排序'
class Cluster_inner_sort(object):
    def __init__(self,play_df,major_scene_table,minor_scene_table,location_table):
        self.play_df=play_df
        self.major_scene_table=major_scene_table
        self.minor_scene_table=minor_scene_table
        self.location_table=location_table
        self.thread=[]
        self.temp_df=[]
    def process(self,indlist):
        # self.cluster_inner_sort(indlist)
        th1 = threading.Thread(target=Cluster_inner_sort.cluster_inner_sort, args=(self,indlist))
        self.thread.append(th1)
        # th1.setDaemon(True)
        th1.start()
        th1.join()
    '''
    函数功能:
    对一列字符串进行同名聚类,要求返回索引
    参数说明:
    输入:
    str_list:字符串列表
    输出:
    index_list:字符串+索引列表
    '''
    def same_cluster(self, str_list):
        str_set = set(str_list)
        cluster_index_list = []
        for item in str_set:
            cluster_index_list.append([index for index in xrange(len(str_list)) if str_list[index] == item])
        cluster_list = zip(list(str_set), cluster_index_list)
        return cluster_list
    '''
    函数功能:
    利用给定的行列表,数据框格式，清洗后的词及词向量(拍摄场地、主场景、次场景)
    输入:
    row_list:行号,all_data:所有的数据,sp_word_clean:清洗后的拍摄地字符串,sp_wordvec:拍摄地词向量,ms_word_clean：清洗后的主场景字符串,
    ms_word_wordvec:主场景词向量,ss_word_clean:清洗后的次场景字符串,ss_word_wordvec:次场景词向量
    输出:
    result:排完序的数据框
    '''
    def cluster_inner_sort(self, row_list):
        all_data = self.play_df
        sp_word_clean= self.location_table[u'clean_words']
        sp_wordvec = np.array(self.location_table[u'vec_words'])
        ms_word_clean = self.major_scene_table[u'clean_words']
        ms_word_wordvec = np.array(self.major_scene_table[u'vec_words'])
        ss_word_clean = self.minor_scene_table[u'clean_words']
        ss_word_wordvec =np.array(self.minor_scene_table[u'vec_words'])
        loc_data = all_data.loc[row_list,]
        loc_data = loc_data.reset_index(drop=True)
        # 一、提取省份城市信息-完成第一层的聚类排序(顺序排好)

        prov_city_list = list(loc_data[u'shootRegion'])
        pc_cluster_list = self.same_cluster(str_list=prov_city_list)

        #修改部分
        pc_outer_sortspell_indlist = sortspell_index(chistr_list=[item[0] for item in pc_cluster_list])
        pc_inner_indlist = [item[1] for item in pc_cluster_list]
        pc_adjust_inner_indlist = [pc_inner_indlist[index] for index in pc_outer_sortspell_indlist]

        #原先部分
        # temp_set = ()
        # index = 0
        # pc_cluster_list_copy = copy.deepcopy(pc_cluster_list)
        # for i in xrange(len(pc_cluster_list_copy)):
        #     if pc_cluster_list_copy[i][0] == u"":
        #         temp_set = pc_cluster_list_copy[i]
        #         index = i
        #         pc_cluster_list.remove(pc_cluster_list[i])
        # del pc_cluster_list_copy
        # pc_outer_sortspell_indlist = sortspell_index(chistr_list=[item[0] for item in pc_cluster_list])
        # pc_inner_indlist = [item[1] for item in pc_cluster_list] + [index]
        # if len(temp_set) != 0:
        #     pc_adjust_inner_indlist = [pc_inner_indlist[index] for index in pc_outer_sortspell_indlist] + [temp_set[1]]
        # else:
        #     pc_adjust_inner_indlist = [pc_inner_indlist[index] for index in pc_outer_sortspell_indlist]

        # 二、提取拍摄场地的信息-完成第二层的聚类排序(承接上一层)
        shoot_place_df = sp_word_clean.loc[row_list,]
        shoot_place_list = list(shoot_place_df)
        sp_wordvec_list = sp_wordvec[row_list,]
        # 返回对应省市的拍摄场地的字符串和索引(规定:所有字符串放前面,索引放后面)
        sp_sort_indlist = []  # 表示第二层排完序的结果
        for item in pc_adjust_inner_indlist:
            Sematic_sort_unit = Semantic_sort(str_indlist=item, all_strlist=shoot_place_list,
                                              all_wordveclist=sp_wordvec_list, threshold=0.70)
            sp_sort_indlist.append(Sematic_sort_unit.class_inner_indlist)  # 问题：找为全零向量了
        # 三、提取主场景的信息,完成第三层的聚类排序,提取次场景的信息完成第四层的聚类排序
        main_scene_df = ms_word_clean.loc[row_list,]
        main_scene_list = list(main_scene_df)
        ms_wordvec_list = ms_word_wordvec[row_list,]
        sec_scene_df = ss_word_clean.loc[row_list,]
        sec_scene_list = list(sec_scene_df)
        ss_wordvec_list = ss_word_wordvec[row_list,]
        ms_ss_sortlist = []
        for item1 in sp_sort_indlist:
            temp1_list = []  # 第一层
            for item2 in item1:
                temp2_list = []  # 第二层
                # 上层同名聚类
                if len(item2) == 1:
                    temp2_list.append(item2)
                else:
                    temp3_list = []  # 第三层
                    temp_sp_strlist = [shoot_place_list[index] for index in item2]
                    temp_sp_samestrlist = self.same_cluster(temp_sp_strlist)
                    for samename_item3 in temp_sp_samestrlist:
                        temp4_list = []
                        samename1_indlist = [item2[index] for index in samename_item3[1]]  # 调换索引位置
                        temp1_sematic_sort_unit = Semantic_sort(str_indlist=samename1_indlist,
                                                                all_strlist=main_scene_list,
                                                                all_wordveclist=ms_wordvec_list, threshold=0.45)
                        for item3 in temp1_sematic_sort_unit.class_inner_indlist:
                            temp5_list = []  # 第四层
                            if len(item3) == 1:
                                temp5_list.append(item3)
                            else:
                                temp6_list = []  # 第五层
                                temp_ms_strlist = [main_scene_list[index] for index in item3]
                                temp_ms_samestrlist = self.same_cluster(temp_ms_strlist)
                                for samename_item4 in temp_ms_samestrlist:
                                    samename2_indlist = [item3[index] for index in samename_item4[1]]  # 调换索引位置
                                    temp2_sematic_sort_unit = Semantic_sort(str_indlist=samename2_indlist,
                                                                            all_strlist=sec_scene_list,
                                                                            all_wordveclist=ss_wordvec_list,
                                                                            threshold=0.45)
                                    temp6_list.append(temp2_sematic_sort_unit.class_inner_indlist)
                                temp5_list.append(temp6_list)
                            temp4_list.append(temp5_list)
                        temp3_list.append(temp4_list)
                    temp2_list.append(temp3_list)
                temp1_list.append(temp2_list)
            ms_ss_sortlist.append(temp1_list)
        ms_ss_sortunlist = flatten(ms_ss_sortlist)
        result = loc_data.loc[ms_ss_sortunlist,]
        self.temp_df.append(result)

if __name__=="__main__":
    # 计时开始
    t1=time.clock()
    #-------------用txt文本读入Json串-----------------
    #fin_json = open(cur_file_dir()+u"/data_rank.txt", "r")
    #input_json = fin_json.readlines()[0]
    #------------------------------------------------
    #-------------命令行字符串读入Json串--------------
    input_json = u""
    for line in sys.stdin:
        input_json = line
    #------------------------------------------------
    respCmtJson = re.sub(r"(,?)(\w+?)\s+?:", r"\1'\2' :", input_json)
    respCmtJson = respCmtJson.replace("'", "\"")
    input_dict = json.loads(respCmtJson, encoding="utf-8")
    sort_keywords = input_dict[u'condition']  # sort_keywords为排序关键词
    add_words = input_dict[u'viewRoleNames'].split(u',') #将剧中人物切割并加入结巴分词的词典中
    to_userdict(add_words)  # 增加结巴词典的单词-剧中人物
    play_info = pd.DataFrame(input_dict[u'datas']) #将数据部分变成数据框形式
    #第一个功能块：对主场景、次场景和拍摄地分词生成词向量---需要文件指定相应路径
    [majorView_df, minorView_df, location_df] = to_cleanDf(data=play_info,
                                                           stopwords_change=cur_file_dir()+u"/stopwords_change.txt",
                                                           stopwords_scene=cur_file_dir()+u"/stopwords_scene.txt") #主场景,次场景,拍摄地的清洗
    #第二个功能块:簇间排序
    outer_sort = Cluster_outer_sort(play_df=play_info, sort_list=sort_keywords)
    #outer_sort.outer_class 输出类别、向量、索引
    #outer_sort.classTagList 输出类别标签
    #第三块功能块:簇内排序
    inner_sort = Cluster_inner_sort(play_df=play_info,major_scene_table=majorView_df,\
                             minor_scene_table=minorView_df,location_table=location_df)
    indlist=[item[2] for item in outer_sort.outer_class]
    for item in indlist:
        inner_sort.process(indlist=item)
    i = 1
    sort_df = pd.DataFrame()
    for item in inner_sort.temp_df:
        if i == 1:
            item[u'class'] = i
            item[u'classTag'] = outer_sort.classTagList[i-1]
            sort_df=item
            i += 1
        else:
            item[u'class'] = i
            item[u'classTag'] = outer_sort.classTagList[i-1]
            sort_df = pd.concat((sort_df, item))
            i += 1
    #保存到本地用于实际效果,可以删除
    #sort_df.to_csv(u"result0629.csv", sep=',', encoding="utf-8",index=False)
    #以下用于打印出结果
    result_dict = {}
    result_dict['idList'] = map(lambda x : x.encode("utf-8"),list(sort_df[u'viewId']))
    result_dict['classList'] = map(lambda x : int(x),list(sort_df[u'class']))
    result_dict['classTagList'] = map(lambda x : x.encode("utf-8"),list(sort_df[u'classTag']))
    # print json.dumps(str(result_dict))
    #print str(result_dict)
    print json.dumps(result_dict)
    t2 = time.clock()  # 设置时间结束节点
    # print "spend time %d min %0.9fs" % (divmod((t2-t1),60)[0],divmod((t2-t1),60)[1])