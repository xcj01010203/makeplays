package com.xiaotu.makeplays.prepare.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.prepare.dao.PrepareScriptDao;
import com.xiaotu.makeplays.prepare.utils.PrepareScriptUtil;
import com.xiaotu.makeplays.utils.UUIDUtils;
/**
 * @ClassName PrepareScriptService
 * @Description 筹备剧本信息
 * @author Administrator
 * @Date 2017年2月14日 上午9:21:51
 * @version 1.0.0
 */
@Service
public class PrepareScriptService {
	@Autowired
	private PrepareScriptDao prepareScriptDao;
	
	/**
	 * @Description 查询剧本类型
	 * @return
	 */
	public List<Map<String, Object>> queryScriptType(){
		return prepareScriptDao.queryScriptType();
		
	}
	
	public List<Map<String, Object>> queryScriptTypeChecked(String crewId){
		return prepareScriptDao.queryScriptTypeChecked(crewId);
	}
	public List<Map<String, Object>> queryWeightInfo(String crewId){
		return prepareScriptDao.queryWeightInfo(crewId);
	}
	/**
	 * @Description 批量保存或修改权重信息
	 */
	public void saveOrUpdateWeightInfo(List<Object[]>argsWeightListAdd,List<Object[]> argsWeightListUpdate){
		prepareScriptDao.saveOrUpdateWeightInfo(argsWeightListAdd,argsWeightListUpdate);
	}
	
	/**
	 * @Description 根据id删除权重信息
	 * @param id
	 */
	public void delWeightInfo(String id){
		//删除权重信息
		prepareScriptDao.delWeightInfo(id);
		//删除审核信息中权重不存在 的数据
		prepareScriptDao.delReviewInfo(id);
	}
	/**
	 * @Description 保存选中剧本类型
	 * @param id
	 */
	public void saveScriptTypeInfo(String scriptTypeId,String crewId){
		
		
		String [] scriptTypeIdList = scriptTypeId.split(",");
		
		List<Object[]> args = new ArrayList<Object[]>();
		if(scriptTypeIdList!=null&&scriptTypeIdList.length>0){
			List<String> typeList = Arrays.asList(scriptTypeIdList);
			
			//获取当前剧组数据库现有的scripttypeid和对应的screptid
			List<Map<String, Object>> scriptTypes = prepareScriptDao.queryHasScritpTyps(crewId);
			
			for(String id :scriptTypeIdList){
				args.add(new Object[]{UUIDUtils.getId(),id,crewId});
			}
			
			List<Object[]> delScript = new ArrayList<Object[]>();
			Set<String> hasScriptTypeId = new HashSet<String>();
			for(Map<String, Object> temp :scriptTypes){
				String sId = temp.get("scripttypeid")!=null?temp.get("scripttypeid").toString():"";
				hasScriptTypeId.add(sId);
				String scriptId = temp.get("id")!=null?temp.get("id").toString():"";
				if(StringUtils.isNotBlank(sId)&&!typeList.contains(sId)){
					delScript.add(new Object[]{scriptId});
				}
			}
			
			prepareScriptDao.delScriptById(delScript);
			
			prepareScriptDao.delScorByScriptId(delScript);
			
			
			
			List<Object[]> addScript = new ArrayList<Object[]>();
			for(String str:typeList){
				if(!hasScriptTypeId.contains(str)){
					addScript.add(new Object[]{UUIDUtils.getId(),str,crewId,"0"});
				}
			}
			
			prepareScriptDao.saveScriptInfo(addScript);
			
			
		}
		
		
		
		
		//删除原有数据
		prepareScriptDao.delScriptTypeInfoByCrewId(crewId);
		//保存现有数据
		prepareScriptDao.saveScriptTypeInfo(args);
	}
	
	
	public int queryMaxOrderNumber(String crewId){
		int maxOrderNumber = prepareScriptDao.queryMaxOrderNumber(crewId);
		return maxOrderNumber;
	}
	
	
	/**
	 * @Description 生成进度表 
	 */
	public void generateSchedule(String crewId,String scriptTypeId,String weightInfo){
		//保存、修改选中剧本类型
		
		saveScriptTypeInfo(scriptTypeId,crewId);
		
		//保存和修改权重信息
		int num = queryMaxOrderNumber(crewId);
		String [] weightInfoList = weightInfo.split(",");
		List<Object[]> argsWeightListAdd = new ArrayList<Object[]>();
		List<Object[]> argsWeightListUpdate = new ArrayList<Object[]>();
		if(weightInfoList!=null&&weightInfoList.length>0){
			for(String weightInfoTmp :weightInfoList){
				String[] tempList = weightInfoTmp.split("##");
				if(tempList!=null && tempList.length == 3){
					String id = tempList[0];
					String name = tempList[1];
					String weight = tempList[2];
					++num;
					if("blank".equals(id)){
						argsWeightListAdd.add(new Object[]{UUIDUtils.getId(),name,weight,crewId,num});
					}else{
						argsWeightListUpdate.add(new Object[]{name,weight,id});
					}
				}
			}
		}
		saveOrUpdateWeightInfo(argsWeightListAdd,argsWeightListUpdate);
		
	}
	
	public String queryScriptScheduleInfo(String crewId){
		List<Map<String, Object>> list = prepareScriptDao.queryScriptScheduleInfo(crewId);
		String data = arrangeData(list);
		
		return data;
	}	
	
	public String arrangeData(List<Map<String, Object>> data){
		PrepareScriptUtil root = new PrepareScriptUtil();
    	root.setId("0");
    	root.setParentId("0");
		root.setName("root");
		
		for(Map<String, Object> temp:data){
			String parentId = temp.get("parentId")!=null?temp.get("parentId").toString():"0";
			String id = temp.get("id")!=null?temp.get("id").toString():"0";
			String name = temp.get("name")!=null?temp.get("name").toString():"";
			String scriptTypeId = temp.get("scriptTypeId")!=null?temp.get("scriptTypeId").toString():"";
			String edition = temp.get("edition")!=null?temp.get("edition").toString():"";
			String finishDate = temp.get("finishDate")!=null?temp.get("finishDate").toString():"";
			String personliable = temp.get("personLiable")!=null?temp.get("personLiable").toString():"";
			String content = temp.get("content")!=null?temp.get("content").toString():"";
			String status = temp.get("status")!=null?temp.get("status").toString():"";
			String mark = temp.get("mark")!=null?temp.get("mark").toString():"";
			String orderNumber = temp.get("orderNumber")!=null?temp.get("orderNumber").toString():"";
			String reviewweightId = temp.get("reviewweightId")!=null?temp.get("reviewweightId").toString():"";
			
			String score = temp.get("score")!=null?temp.get("score").toString():"";
			String weight = temp.get("weight")!=null?temp.get("weight").toString():"";
			
			//计算总分
			DecimalFormat    df   = new DecimalFormat("######0.00");  
			Double totleScore = 0.00;
			boolean hasScore = false;
			if(StringUtils.isNotBlank(score) && StringUtils.isNotBlank(weight)){
				String [] scoreArray = score.split(",");
				String [] weightArray = weight.split(",");
				if(scoreArray !=null && scoreArray.length >0 && weightArray!=null && weightArray.length > 0){
					
					for(int i =0,le = scoreArray.length;i<le;i++){
						String sc = scoreArray[i];
						String wa = weightArray[i];
						if(!"blank".equals(sc)){
							totleScore += Double.valueOf(sc) * Double.valueOf(wa)/100.00;
							hasScore = true;
						}
					}
				}
				
			}
			
			PrepareScriptUtil node  = new PrepareScriptUtil();
	        node.setId(id);
	        node.setParentId(parentId);
	        node.setName(name);
	        node.setScriptTypeId(scriptTypeId);
	        node.setEdition(edition);
	        node.setFinishDate(finishDate);
	        node.setPersonLiable(personliable);
	        node.setContent(content);
	        node.setStatus(status);
	        node.setMark(mark);
	        node.setOrderNumber(orderNumber);
	        node.setScore(score);
	        node.setReviewweightId(reviewweightId);
	        if (!hasScore) {
	        	node.setTotleScore(null);
			} else {
				String totleScoreStr = df.format(totleScore);
				node.setTotleScore(totleScoreStr);
			}
	        root.add(node);
		}
		JSONArray obj = JSONArray.fromObject(root.getChildren());// 不要根  
		return obj.toString();
	}
	
	public String saveOrUpdateScriptInfo(String id,String parentId,String scriptTypeId,String edition,
			String finishDate,String personLiable,String content,String status,String mark,String crewId,String weightInfoId,String score){
		boolean addFlag = true;//添加数据标志位
		if(StringUtils.isNotBlank(id)){
			addFlag = false;
		}
		
		
		
		if(addFlag){
			id = UUIDUtils.getId();
			prepareScriptDao.saveScriptInfo(id,parentId,scriptTypeId,edition,finishDate,personLiable,content,status,mark,crewId);
		}else{
			prepareScriptDao.updateScriptInfo(id,parentId,scriptTypeId,edition,finishDate,personLiable,content,status,mark);
		}
		
		if(StringUtils.isNotBlank(weightInfoId) && StringUtils.isNotBlank(score)){
			String[] reviewweightIds = weightInfoId.split(",");
			String[] scores = score.split(",");
			if(reviewweightIds!=null && reviewweightIds.length>0 && scores != null && scores.length > 0){
				List<Object[]> batchAddArgs = new ArrayList<Object[]>();
				List<Object[]> batchDelArgs = new ArrayList<Object[]>();
				for(int i = 0,le = reviewweightIds.length;i<le;i++){
					String scoreStr = scores[i];
					if("blank".equals(scoreStr)){
						scoreStr = null;
					}
					batchDelArgs.add(new Object[]{id,reviewweightIds[i]});
					batchAddArgs.add(new Object[]{UUIDUtils.getId(),id,reviewweightIds[i],scoreStr,crewId});
				}
				
				prepareScriptDao.delScore(batchDelArgs);
				prepareScriptDao.saveScore(batchAddArgs);
				
			}
			
		}
		
		
		return id;
		
	}
	
	
	public void delScriptInfo(String[] id){
		
		prepareScriptDao.delScriptInfo(id);
	}
	
}
