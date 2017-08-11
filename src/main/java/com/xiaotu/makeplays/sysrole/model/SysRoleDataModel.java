package com.xiaotu.makeplays.sysrole.model;

import java.util.ArrayList;
import java.util.List;


/**
 * 系统角色信息的扩展类
 * @author xuchangjian 2016年8月8日上午9:57:12
 */
public class SysRoleDataModel extends SysroleInfoModel {
	
	/**
	 * 系统角色信息列表
	 */
	private List<SysroleInfoModel> child = new ArrayList<SysroleInfoModel>();

	public List<SysroleInfoModel> getChild() {
		return child;
	}

	public void setChild(List<SysroleInfoModel> child) {
		this.child = child;
	}

}
