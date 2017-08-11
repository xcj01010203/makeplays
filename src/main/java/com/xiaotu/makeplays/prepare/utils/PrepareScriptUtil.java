package com.xiaotu.makeplays.prepare.utils;

import java.util.ArrayList;

public class PrepareScriptUtil {
	    private String id;
	    private String parentId;
	    private String scriptTypeId;
	    private String edition;
	    private String finishDate;
	    private String personLiable;
	    private String content;
	    private String status;
	    private String mark;
	    private String orderNumber;
	    private String name;
	    private String score;
	    private String reviewweightId;
	    private String totleScore;
	    private ArrayList<PrepareScriptUtil> children = new ArrayList<PrepareScriptUtil>();
	    public void add(PrepareScriptUtil node) {//递归添加节点
	        if ("0".equals(node.parentId)) {
	            this.children.add(node);
	        } else if (node.parentId.equals(this.id)) {
	            this.children.add(node);
	        } else {
	            for (PrepareScriptUtil tmp_node : children) {
	                tmp_node.add(node);
	            }
	        }
	    }
	    
	    
		public String getTotleScore() {
			return totleScore;
		}


		public void setTotleScore(String totleScore) {
			this.totleScore = totleScore;
		}


		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getParentId() {
			return parentId;
		}
		public void setParentId(String parentId) {
			this.parentId = parentId;
		}
		public String getScriptTypeId() {
			return scriptTypeId;
		}
		public void setScriptTypeId(String scriptTypeId) {
			this.scriptTypeId = scriptTypeId;
		}
		public String getEdition() {
			return edition;
		}
		public void setEdition(String edition) {
			this.edition = edition;
		}
		public String getFinishDate() {
			return finishDate;
		}
		public void setFinishDate(String finishDate) {
			this.finishDate = finishDate;
		}
		public String getContent() {
			return content;
		}
		
		public String getPersonLiable() {
			return personLiable;
		}
		public void setPersonLiable(String personLiable) {
			this.personLiable = personLiable;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getMark() {
			return mark;
		}
		public void setMark(String mark) {
			this.mark = mark;
		}
		public String getOrderNumber() {
			return orderNumber;
		}
		public void setOrderNumber(String orderNumber) {
			this.orderNumber = orderNumber;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getScore() {
			return score;
		}
		public void setScore(String score) {
			this.score = score;
		}
		public String getReviewweightId() {
			return reviewweightId;
		}
		public void setReviewweightId(String reviewweightId) {
			this.reviewweightId = reviewweightId;
		}
		public ArrayList<PrepareScriptUtil> getChildren() {
			return children;
		}
		public void setChildren(ArrayList<PrepareScriptUtil> children) {
			this.children = children;
		}
	    
}
