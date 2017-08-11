package com.xiaotu.makeplays.crew.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.xiaotu.makeplays.authority.service.CrewAuthMapService;
import com.xiaotu.makeplays.crew.service.CrewClearService;

/**
 * @类名：crewTaskJob.java
 * @作者：李晓平
 * @时间：2017年2月24日 下午2:59:14
 * @描述：剧组处理任务
 */
@Component("crewTaskJob")
public class CrewTaskJob {

	Logger logger = LoggerFactory.getLogger(CrewTaskJob.class);
	
	@Autowired
	private CrewAuthMapService crewAuthMapService;
	
	@Autowired
	private CrewClearService crewClearService;
	
	/**
	 * 将过期剧组的可编辑权限去掉
	 * 每日0点执行一次
	 */
	@Scheduled(cron = "0 0 0 * * ?")  
	public void refreshExpiredCrewAuth(){  
		try {
			logger.info("*************过期剧组刷新任务开始*****************");
			//将过期剧组的权限设为只读
			crewAuthMapService.updateExpiredCrewAuth();
			logger.info("*************过期剧组刷新任务结束*****************");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	/**
	 * 将试用剧组的数据清空
	 * 每日8点执行一次
	 */
//	@Scheduled(cron = "0 0 08 * * ?")  
//	public void clearTrialCrewData(){  
//		try {
//			logger.info("*************清空试用剧组数据任务开始*****************");
//			//将过期剧组的权限设为只读
//			crewClearService.clearTrialCrewData();
//			logger.info("*************清空试用剧组数据任务结束*****************");
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//		}
//	}
}
