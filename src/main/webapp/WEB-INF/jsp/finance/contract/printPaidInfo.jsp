<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/"; %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>Insert title here</title>
		<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/finance/contract/printPaidInfo.css">
		<script type="text/javascript" src="<%=basePath%>/js/scripts/jquery-1.11.1.min.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/numberToCapital.js"></script>
		<script type="text/javascript" src="<%=basePath%>/js/finance/contract/printPaidInfo.js"></script>
</head>
<body>
        <input type="hidden" id="paidId" value="${id}">
        <!-- <div class="my-reimbursement-content pageNext">
                <div class="right-div-modular">
                    <dl class="make-content-dl">
                        <dt>
                            <h2>制作实支报销单</h2>
                            <div class="description-info">
                                <table class="basic-info-table">
                                    <tr>
                                        <td></td>
                                        <td>
                                            <p>No.(凭单号)&nbsp;:</p>
                                            <div class="voucher-no"  id=""></div>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>
                                            <p>项目/剧组:</p>
                                            <div class="pro-crew-name" id="currentCrewName"></div>
                                        </td>
                                        <td>
                                            <p>Date(日期)&nbsp;:</p>
                                            <input class="bill-date" type="text" id="" onclick="WdatePicker({isShowClear:false,readOnly:true})" readonly>
                                        </td>
                                    </tr>
                                </table>
                            </div>
                        </dt>
                        <dd>
                            <table class="detail-info-table-one" cellspacing=0 cellpadding=0 id="detailInfoTableOne">
                                <thead>
                                    <tr>
                                        <td>日期(date)</td>
                                        <td>编号(No.)</td>
                                        <td style="width: 227px;">Particulars<br>用途说明</td>
                                        <td>A/C code<br>科目</td>
                                        <td style="width: 256px;">Analysis code<br>明细科目</td>
                                        <td>Amount(RMB)<br>金额(人民币)</td>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr class="detail-info-tr">
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><input type="text" onblur="changeMoney(this);" onfocus="modifyMoney(this);" id="payTotalMoney"></td>  
                                    </tr>
                                    <tr class="detail-info-tr">
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>  
                                    </tr>
                                    <tr class="detail-info-tr">
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>  
                                    </tr>
                                    <tr class="detail-info-tr">
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>  
                                    </tr>
                                    <tr class="detail-info-tr">
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>  
                                    </tr>
                                    <tr class="detail-info-tr">
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>  
                                    </tr>
                                    <tr class="money-tr">
                                        <td colspan="4">
                                            <p>大写金额:</p>
                                            <div id="capitalPayMoney"></div>
                                        </td>
                                        <td colspan="2">
                                            <p>总额(Total):</p>
                                            <div id="readOnlyMoney"></div>
                                            <input type="hidden">
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </dd>
                        <dd>
                            <table class="readonly-explain" cellspacing=0 cellpadding=0>
                                <tr>
                                    <td style="width: 400px;">Applicant/Date<br>报销者/日期</td>
                                    <td>Position/Dept<br>职务/部门</td>
                                    <td rowspan="3" style="width: 180px;">Received by<br>签收</td>
                                </tr>
                                <tr>
                                    <td>line Producer/Date<br>监制/日期</td>
                                    <td>Prod Manager/Date<br>制片主任</td>
                                </tr>
                                <tr>
                                    <td>Producer/Date<br>制片人/日期</td>
                                    <td>
                                      <p>Producer Manager/Date<br>会计出纳/日期 &nbsp;&nbsp;<span id="userAgent">当前用户名</span></p>
                                    </td>
                                </tr>
                            </table>
                        </dd>
                        <dd class="last-dd-pack">
                            <div class="last-tips">Accounts Department
                                
                            </div>
                        </dd>
                        <p class="pack-count">附件:&nbsp; <input type="text">张</p>
                    </dl>
                </div>
            </div>
               <div class="my-reimbursement-content pageNext">
                <div class="right-div-modular">
                    <dl class="make-content-dl">
                        <dt>
                            <h2>制作实支报销单</h2>
                            <div class="description-info">
                                <table class="basic-info-table">
                                    <tr>
                                        <td></td>
                                        <td>
                                            <p>No.(凭单号)&nbsp;:</p>
                                            <div class="voucher-no"  id=""></div>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>
                                            <p>项目/剧组:</p>
                                            <div class="pro-crew-name" id="currentCrewName"></div>
                                        </td>
                                        <td>
                                            <p>Date(日期)&nbsp;:</p>
                                            <input class="bill-date" type="text" id="" onclick="WdatePicker({isShowClear:false,readOnly:true})" readonly>
                                        </td>
                                    </tr>
                                </table>
                            </div>
                        </dt>
                        <dd>
                            <table class="detail-info-table-one" cellspacing=0 cellpadding=0 id="detailInfoTableOne">
                                <thead>
                                    <tr>
                                        <td>日期(date)</td>
                                        <td>编号(No.)</td>
                                        <td style="width: 227px;">Particulars<br>用途说明</td>
                                        <td>A/C code<br>科目</td>
                                        <td style="width: 256px;">Analysis code<br>明细科目</td>
                                        <td>Amount(RMB)<br>金额(人民币)</td>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr class="detail-info-tr">
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><input type="text" onblur="changeMoney(this);" onfocus="modifyMoney(this);" id="payTotalMoney"></td>  
                                    </tr>
                                    <tr class="detail-info-tr">
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>  
                                    </tr>
                                    <tr class="detail-info-tr">
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>  
                                    </tr>
                                    <tr class="detail-info-tr">
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>  
                                    </tr>
                                    <tr class="detail-info-tr">
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>  
                                    </tr>
                                    <tr class="detail-info-tr">
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>  
                                    </tr>
                                    <tr class="money-tr">
                                        <td colspan="4">
                                            <p>大写金额:</p>
                                            <div id="capitalPayMoney"></div>
                                        </td>
                                        <td colspan="2">
                                            <p>总额(Total):</p>
                                            <div id="readOnlyMoney"></div>
                                            <input type="hidden">
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </dd>
                        <dd>
                            <table class="readonly-explain" cellspacing=0 cellpadding=0>
                                <tr>
                                    <td style="width: 400px;">Applicant/Date<br>报销者/日期</td>
                                    <td>Position/Dept<br>职务/部门</td>
                                    <td rowspan="3" style="width: 180px;">Received by<br>签收</td>
                                </tr>
                                <tr>
                                    <td>line Producer/Date<br>监制/日期</td>
                                    <td>Prod Manager/Date<br>制片主任</td>
                                </tr>
                                <tr>
                                    <td>Producer/Date<br>制片人/日期</td>
                                    <td>
                                      <p>Producer Manager/Date<br>会计出纳/日期 &nbsp;&nbsp;<span id="userAgent">当前用户名</span></p>
                                    </td>
                                </tr>
                            </table>
                        </dd>
                        <dd class="last-dd-pack">
                            <div class="last-tips">Accounts Department
                                
                            </div>
                        </dd>
                        <p class="pack-count">附件:&nbsp; <input type="text">张</p>
                    </dl>
                </div>
            </div>
               <div class="my-reimbursement-content">
                <div class="right-div-modular">
                    <dl class="make-content-dl">
                        <dt>
                            <h2>制作实支报销单</h2>
                            <div class="description-info">
                                <table class="basic-info-table">
                                    <tr>
                                        <td></td>
                                        <td>
                                            <p>No.(凭单号)&nbsp;:</p>
                                            <div class="voucher-no"  id=""></div>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>
                                            <p>项目/剧组:</p>
                                            <div class="pro-crew-name" id="currentCrewName"></div>
                                        </td>
                                        <td>
                                            <p>Date(日期)&nbsp;:</p>
                                            <input class="bill-date" type="text" id="" onclick="WdatePicker({isShowClear:false,readOnly:true})" readonly>
                                        </td>
                                    </tr>
                                </table>
                            </div>
                        </dt>
                        <dd>
                            <table class="detail-info-table-one" cellspacing=0 cellpadding=0 id="detailInfoTableOne">
                                <thead>
                                    <tr>
                                        <td>日期(date)</td>
                                        <td>编号(No.)</td>
                                        <td style="width: 227px;">Particulars<br>用途说明</td>
                                        <td>A/C code<br>科目</td>
                                        <td style="width: 256px;">Analysis code<br>明细科目</td>
                                        <td>Amount(RMB)<br>金额(人民币)</td>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr class="detail-info-tr">
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><input type="text" onblur="changeMoney(this);" onfocus="modifyMoney(this);" id="payTotalMoney"></td>  
                                    </tr>
                                    <tr class="detail-info-tr">
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>  
                                    </tr>
                                    <tr class="detail-info-tr">
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>  
                                    </tr>
                                    <tr class="detail-info-tr">
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>  
                                    </tr>
                                    <tr class="detail-info-tr">
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>  
                                    </tr>
                                    <tr class="detail-info-tr">
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>
                                        <td><div></div></td>  
                                    </tr>
                                    <tr class="money-tr">
                                        <td colspan="4">
                                            <p>大写金额:</p>
                                            <div id="capitalPayMoney"></div>
                                        </td>
                                        <td colspan="2">
                                            <p>总额(Total):</p>
                                            <div id="readOnlyMoney"></div>
                                            <input type="hidden">
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </dd>
                        <dd>
                            <table class="readonly-explain" cellspacing=0 cellpadding=0>
                                <tr>
                                    <td style="width: 400px;">Applicant/Date<br>报销者/日期</td>
                                    <td>Position/Dept<br>职务/部门</td>
                                    <td rowspan="3" style="width: 180px;">Received by<br>签收</td>
                                </tr>
                                <tr>
                                    <td>line Producer/Date<br>监制/日期</td>
                                    <td>Prod Manager/Date<br>制片主任</td>
                                </tr>
                                <tr>
                                    <td>Producer/Date<br>制片人/日期</td>
                                    <td>
                                      <p>Producer Manager/Date<br>会计出纳/日期 &nbsp;&nbsp;<span id="userAgent">当前用户名</span></p>
                                    </td>
                                </tr>
                            </table>
                        </dd>
                        <dd class="last-dd-pack">
                            <div class="last-tips">Accounts Department
                                
                            </div>
                        </dd>
                        <p class="pack-count">附件:&nbsp; <input type="text">张</p>
                    </dl>
                </div>
            </div> -->
</body>
</html>