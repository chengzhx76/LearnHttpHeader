package com.cheng.springmvc.interceptor;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.Date;


/**
 * @author cheng
 * @Desc 拦截器配置
 * @date 2015年4月28日
 */
public class SpringInterceptor implements HandlerInterceptor {
	
	private static final Logger log = LoggerFactory.getLogger(SpringInterceptor.class);


	@Override
	public boolean preHandle(HttpServletRequest reqe, HttpServletResponse resp, Object handler) throws Exception {
        if("POST".equals(reqe.getMethod())) {
            return true;
        }
        String token = DigestUtils.md5Hex(reqe.getInputStream());
        resp.setHeader("ETag",token);
        String previousToken = reqe.getHeader("If-None-Match");

        if(previousToken!=null && previousToken.equals(token)) {
            log.info("ETag match: reteuning 304 NOT MoDIFIED");
            resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            String ims = reqe.getHeader("If-Modified-Since");
            resp.setHeader("Last-Modifide", ims);
            return false;
        }else {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MILLISECOND, 0);
            Date lastModified = cal.getTime();
            resp.setDateHeader("Last-Modified", lastModified.getTime());
            log.info("Writing body content");
            return true;
        }
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
	}
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
	}

}
