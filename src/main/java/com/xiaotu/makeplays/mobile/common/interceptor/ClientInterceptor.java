package com.xiaotu.makeplays.mobile.common.interceptor;

import net.sf.json.JSONObject;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.xiaotu.makeplays.mobile.common.ServerResponse;


/**
 * 客户端请求拦截器
 * 
 * @author xuchangjian
 */
@Aspect
@Component
public class ClientInterceptor {

	final Logger logger = LoggerFactory.getLogger(ClientInterceptor.class);

	/**
	 * 对controller的方法进行横切，调用方法前后都做相应处理
	 * 
	 * @param joinPoint
	 *            连接点
	 * @return 执行结果
	 */
	@Around("execution(* com.xiaotu.makeplays.mobile.server..*.*(..))")
	public ServerResponse<Object> aroundMethod(ProceedingJoinPoint joinPoint) {
		logger.info("开始调用接口");
		
		ServerResponse<Object> response = new ServerResponse<Object>();
		try {
			Object result = joinPoint.proceed();
			if (result != null && result instanceof Exception) {
				throw new RuntimeException((Exception) result);
			}
			response.setData(result);
			

//			response.setSuccess(false);
//			response.setMessage("服务不可用");
//			response.setData(null);
		} catch (Throwable e) {
			response.setSuccess(false);
			response.setMessage(e.getMessage());
			logger.error(e.getMessage(), e);
		}

		logger.debug("response json：" + JSONObject.fromObject(response).toString());

		logger.info("after method");
		return response;
	}
}