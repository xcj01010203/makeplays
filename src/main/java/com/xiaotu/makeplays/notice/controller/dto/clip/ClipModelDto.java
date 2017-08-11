package com.xiaotu.makeplays.notice.controller.dto.clip;

import java.util.Map;

/**
 * 日志信息
 * @author xuchangjian 2016年8月26日上午10:28:27
 */
public class ClipModelDto {
	
	/**
	 * 分组类型
	 */
	private String type;
	
	/**
	 * 数量
	 */
	private Integer num = Integer.valueOf(0);
	
	/**
	 * 存放日志列表数据的map
	 */
	private Map<String,ClipModelDto> map;
	
	/**
	 * 存放数据列表
	 */
	private Map<String,Object> list;
	
	/**
	 * 镜次信息的map
	 */
	private Map<String,Map<String,Object>> cameraMap;
	
	public Map<String, Map<String, Object>> getCameraMap() {
		return cameraMap;
	}
	public void setCameraMap(Map<String, Map<String, Object>> cameraMap) {
		this.cameraMap = cameraMap;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public Map<String, ClipModelDto> getMap() {
		return map;
	}
	public void setMap(Map<String, ClipModelDto> map) {
		this.map = map;
	}
	public Map<String, Object> getList() {
		return list;
	}
	public void setList(Map<String, Object> list) {
		this.list = list;
	}
	

}
