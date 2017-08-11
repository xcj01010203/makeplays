package com.xiaotu.makeplays.utils;

import java.util.List;

public class Page {

	//由于ui框架翻页功能默认首页为第0页,则调用set方法是多加1
	private int pageNo=1;
	
	private int pagenum=0;
	
	private int PAGE_SIZE=20;
	//为适应ui框架使用，
	private int pagesize=20;
	
	private int pageCount;
	
	private boolean nextFlag=false;
	
	private boolean previousFlag = false;
	
	private int total;
	
	private String html;
	
	private List resultList;
	
	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		
		if(total%PAGE_SIZE==0){
			this.pageCount=total/PAGE_SIZE;
		}else{
			this.pageCount=total/PAGE_SIZE+1;
		}
		this.total = total;
	}

	

	

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}
	
	
	
	public boolean isNextFlag() {
		return nextFlag;
	}

	public void setNextFlag(boolean nextFlag) {
		this.nextFlag = nextFlag;
	}
	
	

	public boolean isPreviousFlag() {
		return previousFlag;
	}

	public void setPreviousFlag(boolean previousFlag) {
		this.previousFlag = previousFlag;
	}

	public int getNextIndex(){
		
		if(true ==nextFlag){
			pageNo+=1;
		}
		if(true==previousFlag){
			pageNo-=1;
		}
		return (pageNo-1)*PAGE_SIZE;
		
	}

	public String getHtml() {
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("<div class='page_box'>");
		buffer.append("<div class='f_l ml_20'>");
		buffer.append("<span class='ico_begin ico_common' onclick='firstPage();'></span>&nbsp;");
		buffer.append("<span class='ico_prev ico_common' onclick='previousPage();'></span>&nbsp;");
		buffer.append("<span class='ico_next ico_common' onclick='nextPage();'></span>&nbsp;");
		buffer.append("<span class='ico_end ico_common' onclick='lastPage();'></span>&nbsp;");
		buffer.append("</div>");
		buffer.append("<div class='f_r page_jump_box'>");
		buffer.append("<em>每页"+PAGE_SIZE+"条</em>");
		buffer.append("<em>共"+total+"条记录，共"+pageCount+"页</em>");
		buffer.append("<span class='ico_jump ico_common' onclick='toPage();'></span>");
		buffer.append("<input type='text' class='w_30' id='pageno' />");
		buffer.append("<em>页</em>");
		buffer.append("</div></div>");
		html=buffer.toString();
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public int getPAGE_SIZE() {
		return PAGE_SIZE;
	}

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public int getPagenum() {
		return pagenum;
	}

	public void setPagenum(int pagenum) {
		this.pageNo=pagenum+1;
		this.pagenum = pagenum;
	}

	public int getPagesize() {
		return pagesize;
	}

	public void setPagesize(int pagesize) {
		this.PAGE_SIZE=pagesize;
		this.pagesize = pagesize;
	}
}
