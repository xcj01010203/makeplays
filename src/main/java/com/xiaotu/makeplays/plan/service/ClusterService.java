package com.xiaotu.makeplays.plan.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instances;

import com.xiaotu.makeplays.plan.model.Cluster;
import com.xiaotu.makeplays.plan.model.Factor;

/**
 * 聚类场景
 * 
 * @author subin
 */
@Service
public class ClusterService {
	
	private final Log log = LogFactory.getLog(getClass());
	
	/**
	 * 第一步： 组装数据
	 * <p>数值类型
	 * 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Instances createNumericInstances(List<Map<String, Object>> scenario, List<Factor> factors) throws Exception{
		
		FastVector atts = new FastVector();
		Instances data;
		double[] vals;
		
		for(Factor one : factors){
			
			for(int i = 0; i < one.getPriority(); i++){
				
				atts.addElement(new Attribute(one.getFactorid() + i));
			}
		}
		
		//创建关系
		data = new Instances("scene", atts, 0);
		
		//创建数据
		for(Map one : scenario){
			
			vals = new double[data.numAttributes()];
			
			int x = 0;
			
			for(Factor factor : factors){
				
				for(int i = 0; i < factor.getPriority(); i++){
					
					vals[x] = Integer.valueOf(one.get(factor.getFactorid()).toString());
					x++;
				}
			}
			
			data.add(new DenseInstance(1.0, vals));
		}
		
		return data;
	}
	
	/**
	 * 第二步： 使用KMeans进行聚类(一次)
	 * 
	 * @param 待聚类的数据
	 * @param 聚多少类
	 * @param 参考的随机数量
	 * @return 聚类后的分类
	 */
	private Cluster buildKMeansClusters(Instances data, int k, int seed) throws Exception{
		
		Cluster result = new Cluster();
		List<Integer> cluster =  new ArrayList<Integer>();
		
		SimpleKMeans cl = new SimpleKMeans();
		cl.setNumClusters(k);
		
		//修改seed的值获得不同的结果
		cl.setSeed(seed);
		cl.buildClusterer(data);
		
		for (int i = 0; i < data.numInstances(); i++) {
			
			int cate = cl.clusterInstance(data.instance(i));
			cluster.add(cate);
			
			log.info("[ @.@ ] : < [RESULT] 使用KMeans聚类后的结果 : ["+ data.get(i) +"] CLUSTER ["+ cate +"] >");
		}
		
		log.info("[ @.@ ] : < [RESULT] SquaredError : ["+ cl.getSquaredError() +"] seed : ["+seed+"] >");
		
		result.setCluster(cluster);
		result.setSquaredError(cl.getSquaredError());
		
		return result;
	}
	
	/**
	 * 第二步： 使用KMeans进行聚类(多次)
	 * 
	 * @param 待聚类的数据
	 * @param 聚多少类
	 * @param 循环多少次
	 * @return 聚类后的分类
	 */
	private Cluster buildManyKMeansClusters(Instances data, int k, int num) throws Exception{
		
		Cluster result = new Cluster();
		
		int seed = data.numInstances() / 10;
		
		for(int i=0;i<num;i++){
			
			Cluster one = this.buildKMeansClusters(data, k, seed + (seed/10 * i));
			
			if(result.getCluster() == null || one.getSquaredError() < result.getSquaredError()){
				
				result.setCluster(one.getCluster());
				result.setSquaredError(one.getSquaredError());
			}
		}
		
		for (int i = 0; i < data.numInstances(); i++) {
			
			log.info("[ @.@ ] : < [RESULT] 使用KMeans聚类后的结果 : ["+ data.get(i) +"] CLUSTER ["+ result.getCluster().get(i) +"] >");
		}
		
		log.info("[ @.@ ] : < [RESULT] SquaredError : ["+ result.getSquaredError() +"]>");
		
		return result;
	}
	
	/**
	 * 执行聚类
	 */
	public List<Integer> doCluster(List<Map<String, Object>> scenario, List<Factor> factors) throws Exception{
		
		Date s = new Date();
		
		//Instances data = scc.createNominalInstances();
		Instances data = this.createNumericInstances(scenario, factors);
		
		//保存的文件
		//DataSink.write("E://scene.arff", data);
		
		//EM
		//scc.buildEMClusters(data);
		
		//单次KMeans
		//scc.buildKMeansClusters(data, 3);
		
		//多次KMeans//4次/5次概率高  6次也会出现但是概率就低, 等到实际测试调整 (< 1600)
		Cluster cluster = this.buildManyKMeansClusters(data, 3, 6);
		
		Date e = new Date();
		
		log.info("[ @.@ ] : < [RESULT] 运行所需时间: ["+ ( ((double)e.getTime() - (double)s.getTime())/1000 ) +"]s >");
		
		return cluster.getCluster();
	}
}