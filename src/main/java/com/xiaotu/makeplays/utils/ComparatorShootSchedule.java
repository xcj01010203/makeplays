package com.xiaotu.makeplays.utils;

import java.util.Comparator;

import com.xiaotu.makeplays.crew.model.ShootScheduleBaseModel;


public class ComparatorShootSchedule implements Comparator {

	/**
	 * 拍摄进度总戏量比较，降序
	 * compareFlage： 1 o1总戏量多，0 o1与o2总戏量相等，-1 o1总戏量少
	 */
	@Override
	public int compare(Object o1, Object o2) {

		int compareFlag=0;
		
		if(o1 instanceof ShootScheduleBaseModel && o2 instanceof ShootScheduleBaseModel){
			//比较总戏量
			ShootScheduleBaseModel ssm=(ShootScheduleBaseModel)o1;
			double totalPlayAmount=ssm.getTotalCrewAmount();
			ssm=(ShootScheduleBaseModel)o2;
			double nextTotalPlayAmount=ssm.getTotalCrewAmount();
			if(totalPlayAmount<nextTotalPlayAmount){
				compareFlag=1;
			}else if(totalPlayAmount>nextTotalPlayAmount){
				compareFlag=-1;
			}
		}
		
		return compareFlag;
	}

}
