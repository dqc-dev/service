package com.example.service.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;


public class SpringUtils {

	private static ApplicationContext applicationContext;

	/**
	 * 初始化springcontext
	 * @param
	 */
	public static void init(ServletContext servletContext) {
		applicationContext = (WebApplicationContext) servletContext
				.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
	}

	public static void init(ApplicationContext context){
		applicationContext = context;
	}

	/*@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringUtils.applicationContext = applicationContext;
	}*/
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	/**
	 * 获取spring bean
	 * @param beanName
	 */
	public static Object getBean(String beanName) {
		return applicationContext.getBean(beanName);
	}

	public static Object getBean(Class<?> beanClass) {
		return applicationContext.getBean(beanClass);
	}
}
