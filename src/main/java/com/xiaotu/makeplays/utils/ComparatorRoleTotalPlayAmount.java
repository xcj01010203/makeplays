package com.xiaotu.makeplays.utils;

import java.util.Comparator;

import com.xiaotu.makeplays.crew.model.CrewAmountModel;
import com.xiaotu.makeplays.crew.model.RolecrewAmountBaseModel;

public class ComparatorRoleTotalPlayAmount implements Comparator{

	/**
	 * 比较角色的总戏量（先比较按场统计的戏量，再比较按页统计的戏量），按降序排列
	 * compareFlage： 1 o1总戏量少，0 o1与o2总戏量相等，-1 o1总戏量多
	 */
	@Override
	public int compare(Object o1,Object o2) {
		int compareFlag=0;
		
		if(o1 instanceof RolecrewAmountBaseModel && o2 instanceof RolecrewAmountBaseModel){
			//比较角色的总戏量
			RolecrewAmountBaseModel rpabm=(RolecrewAmountBaseModel)o1;
			double totalCrewAmountByView=rpabm.getTotalCrewAmountByView();
			double totalCrewAmountByPage=rpabm.getTotalCrewAmountByPage();
			rpabm=(RolecrewAmountBaseModel)o2;
			double nextTotalCrewAmountByView=rpabm.getTotalCrewAmountByView();
			double nextTotalCrewAmountByPage=rpabm.getTotalCrewAmountByPage();
			if(totalCrewAmountByView<nextTotalCrewAmountByView){
				compareFlag=1;
			}else if(totalCrewAmountByView==nextTotalCrewAmountByView){
				//按场统计的戏量相同时，再比较按页统计的戏量
				if(totalCrewAmountByPage<nextTotalCrewAmountByPage){
					compareFlag=1;
				}else if(totalCrewAmountByPage>nextTotalCrewAmountByPage){
					compareFlag=-1;
				}				
				
			}else if(totalCrewAmountByView>nextTotalCrewAmountByView){
				compareFlag=-1;
			}
		}else if(o1 instanceof CrewAmountModel && o2 instanceof CrewAmountModel){
			//比较角色的总戏量
			CrewAmountModel pam=(CrewAmountModel)o1;
			double crewAmountByView=pam.getcrewAmountByview();
			double crewAmountByPage=pam.getcrewAmountByPage();
			pam=(CrewAmountModel)o2;
			double nextCrewAmountByView=pam.getcrewAmountByview();
			double nextCrewAmountByPage=pam.getcrewAmountByPage();
			if(crewAmountByView<nextCrewAmountByView){
				compareFlag=1;
			}else if(crewAmountByView==nextCrewAmountByView){
				//按场统计的戏量相同时，再比较按页统计的戏量
				if(crewAmountByPage<nextCrewAmountByPage){
					compareFlag=1;
				}else if(crewAmountByPage>nextCrewAmountByPage){
					compareFlag=-1;
				}				
				
			}else if(crewAmountByView>nextCrewAmountByView){
				compareFlag=-1;
			}
		}
		return compareFlag;
	}

}
