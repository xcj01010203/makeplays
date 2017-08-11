package com.xiaotu.makeplays.sys.service;

import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaotu.makeplays.authority.dao.AuthorityDao;
import com.xiaotu.makeplays.crew.dao.CrewInfoDao;
import com.xiaotu.makeplays.crew.model.CrewInfoModel;
import com.xiaotu.makeplays.sys.dao.SysLogDao;
import com.xiaotu.makeplays.sys.dao.SysLoginLogDao;
import com.xiaotu.makeplays.sys.filter.SyslogFilter;
import com.xiaotu.makeplays.sys.model.SysLogDataModel;
import com.xiaotu.makeplays.sys.model.SysLogModel;
import com.xiaotu.makeplays.sys.model.SysLoginModel;
import com.xiaotu.makeplays.user.model.UserInfoModel;
import com.xiaotu.makeplays.utils.Constants;
import com.xiaotu.makeplays.utils.IpUtil;
import com.xiaotu.makeplays.utils.Page;
import com.xiaotu.makeplays.utils.StringUtil;
import com.xiaotu.makeplays.utils.UUIDUtils;

@Service
public class SysLogService {

	@Autowired
	private SysLogDao sysLogDAO;

	@Autowired
	private AuthorityDao authorityDao;

	@Autowired
	private SysLoginLogDao sysLoginLogDao;
	
	@Autowired
	private CrewInfoDao crewInfoDao;

	/**
	 * * 记录日志
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param logDesc
	 *            操作日志描述
	 * @param terminal
	 *            操作终端类型0：PC；1：IOS；2：安卓
	 * @param tableName
	 *            操作表名
	 * @param idArrayStr
	 *            操作对象的id，多个使用英文逗号分隔
	 * @param operType
	 *            0：读；1：插入；2：修改；3：删除；4：批量导入；5:导出；6:异常；99：其他,
	 * @throws Exception
	 */
	public void saveSysLog(HttpServletRequest request, String logDesc,
			Integer terminal, String tableName, String idArrayStr,
			Integer operType) {

		try {

			SysLogModel syslog = new SysLogModel();
			syslog.setLogId(UUIDUtils.getId());

			HttpSession session = request.getSession();

			UserInfoModel userInfo = (UserInfoModel) session
					.getAttribute(Constants.SESSION_USER_INFO);

			if(null != userInfo) {
				syslog.setUserId(userInfo.getUserId());
			}

			CrewInfoModel crewInfo = (CrewInfoModel) session
					.getAttribute(Constants.SESSION_CREW_INFO);

			if (null != crewInfo) {
				syslog.setCrewId(crewInfo.getCrewId());
				syslog.setProjectType(crewInfo.getProjectType());
			}

			String url = request.getRequestURI();
			syslog.setAuthUrl(url);

			String uri = "";
			if (url.indexOf("?") > 0) {
				uri = url.substring(0, url.indexOf("?"));
			} else {
				uri = url;
			}

			syslog.setOperType(operType);

			Enumeration<String> attributeNames = request.getAttributeNames();

			while (attributeNames.hasMoreElements()) {
				String element = attributeNames.nextElement();
				if (element.contains("springframework")
						|| element.contains("characterEncodingFilter")
						|| element.contains("__sitemesh__filterapplied")) {
					continue;
				}

				if (StringUtils.isBlank(syslog.getParams())) {
					syslog.setParams(element + "=" + request.getAttribute(element)
							+ ",");
				} else {
					syslog.setParams(syslog.getParams() + element + "="
							+ request.getAttribute(element) + ",");
				}

			}

			Enumeration<String> parameterNames = request.getParameterNames();

			while (parameterNames.hasMoreElements()) {
				String element = parameterNames.nextElement();
				if (element.contains("springframework")
						|| element.contains("characterEncodingFilter")
						|| element.contains("__sitemesh__filterapplied")) {
					continue;
				}

				if (StringUtils.isBlank(syslog.getParams())) {
					syslog.setParams(element + "=" + request.getParameter(element)
							+ ",");
				} else {
					syslog.setParams(syslog.getParams() + element + "="
							+ request.getParameter(element) + ",");
				}

			}
			// 截取参数，防止数据溢出
			if (StringUtils.isNotBlank(syslog.getParams())) {
				if (syslog.getParams().length() > 1601)
					syslog.setParams(syslog.getParams().substring(0, 1600));
				else
					syslog.setParams(syslog.getParams());
			}

			if (StringUtils.isNotBlank(tableName)) {
				if(tableName.length() > 100) {
					syslog.setTableName(tableName.substring(0, 100));
				} else {
					syslog.setTableName(tableName);
				}
			}

			if (StringUtils.isNotBlank(idArrayStr)) {
				if (idArrayStr.length() > 161)
					syslog.setObjectId(idArrayStr.substring(0, 160));
				else
					syslog.setObjectId(idArrayStr);
			}

//			syslog.setUserIp(HttpUtils.getRemoteHost(request));
			syslog.setUserIp(IpUtil.getUserIp(request));

			syslog.setLogDesc(logDesc);
			syslog.setTerminal(terminal);
			sysLogDAO.add(syslog);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 记录日志
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param logDesc
	 *            操作日志描述
	 * @param terminal
	 *            操作终端类型0：PC；1：IOS；2：安卓
	 * @param tableName
	 *            操作表名
	 * @param idArrayStr
	 *            操作对象的id，多个使用英文逗号分隔
	 * @throws Exception
	 */
	public void saveSysLogForApp(HttpServletRequest request, String logDesc,
			Integer terminal, String tableName, String idArrayStr,
			Integer operType)  {

		try {

			SysLogModel syslog = new SysLogModel();
			syslog.setLogId(UUIDUtils.getId());
			if(StringUtil.isBlank(request.getParameter("userId"))) {
				if(StringUtil.isBlank(request.getParameter("createUser"))) {
					if(StringUtil.isBlank(request.getAttribute("userId") + "")) {
						syslog.setUserId(null);
					} else {
						syslog.setUserId(request.getAttribute("userId") + "");
					}
				} else {
					syslog.setUserId(request.getParameter("createUser"));
				}
			} else {
				syslog.setUserId(request.getParameter("userId"));
			}
			if(StringUtil.isBlank(request.getParameter("crewId"))) {
				if(StringUtil.isBlank(request.getAttribute("crewId") + "")) {
					syslog.setCrewId(null);
				} else {
					syslog.setCrewId(request.getAttribute("crewId") + "");
				}
			} else {
				syslog.setCrewId(request.getParameter("crewId"));
			}
			
			if(StringUtils.isNotBlank(syslog.getCrewId())) {
				CrewInfoModel crewInfo = crewInfoDao.queryById(syslog.getCrewId());
				if(crewInfo != null) {
					syslog.setProjectType(crewInfo.getProjectType());
				}
			}
			
			String url = request.getRequestURI();
			syslog.setAuthUrl(url);

			String uri = "";
			if (url.indexOf("?") > 0) {
				uri = url.substring(0, url.indexOf("?"));
			} else {
				uri = url;
			}

			syslog.setOperType(operType);

			Enumeration<String> attributeNames = request.getAttributeNames();

			while (attributeNames.hasMoreElements()) {
				String element = attributeNames.nextElement();
				if (element.contains("springframework")
						|| element.contains("characterEncodingFilter")
						|| element.contains("__sitemesh__filterapplied")) {
					continue;
				}

				if (StringUtils.isBlank(syslog.getParams())) {
					syslog.setParams(element + "=" + request.getAttribute(element)
							+ ",");
				} else {
					syslog.setParams(syslog.getParams() + element + "="
							+ request.getAttribute(element) + ",");
				}

			}

			Enumeration<String> parameterNames = request.getParameterNames();

			while (parameterNames.hasMoreElements()) {
				String element = parameterNames.nextElement();
				if (element.contains("springframework")
						|| element.contains("characterEncodingFilter")
						|| element.contains("__sitemesh__filterapplied")) {
					continue;
				}

				if (StringUtils.isBlank(syslog.getParams())) {
					syslog.setParams(element + "=" + request.getParameter(element)
							+ ",");
				} else {
					syslog.setParams(syslog.getParams() + element + "="
							+ request.getParameter(element) + ",");
				}

			}
			// 截取参数，防止数据溢出
			if (StringUtils.isNotBlank(syslog.getParams())) {
				if (syslog.getParams().length() > 1601)
					syslog.setParams(syslog.getParams().substring(0, 1600));
				else
					syslog.setParams(syslog.getParams());
			}

			if (StringUtils.isNotBlank(tableName)) {
				if(tableName.length() > 100) {
					syslog.setTableName(tableName.substring(0, 100));
				} else {
					syslog.setTableName(tableName);
				}
			}

			if (StringUtils.isNotBlank(idArrayStr)) {
				if (idArrayStr.length() > 161)
					syslog.setObjectId(idArrayStr.substring(0, 160));
				else
					syslog.setObjectId(idArrayStr);
			}

//			syslog.setUserIp(HttpUtils.getRemoteHost(request));
			syslog.setUserIp(IpUtil.getUserIp(request));

			syslog.setLogDesc(logDesc);
			syslog.setTerminal(terminal);
			sysLogDAO.add(syslog);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询日志信息
	 * 
	 * @param page
	 * @param filter
	 * @return
	 */
	public List<SysLogDataModel> querySyslogList(Page page, SyslogFilter filter) {
		return this.sysLogDAO.querySyslogList(page, filter);
	}

	/**
	 * 添加日志
	 * 
	 * @param sysLogModel
	 * @throws Exception
	 */
	public void addOneLog(SysLogModel sysLogModel) throws Exception {
		this.sysLogDAO.add(sysLogModel);
	}

	public boolean getIsExistLog(String userId, String ip) throws Exception {
		return this.sysLoginLogDao.getIsExistLog(userId, ip);
	}

	public void add(SysLoginModel slm) throws Exception {
		this.sysLoginLogDao.add(slm);
	}

}
