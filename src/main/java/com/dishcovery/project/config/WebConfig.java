package com.dishcovery.project.config;

import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.Filter;

// web.xml과 동일
public class WebConfig extends AbstractAnnotationConfigDispatcherServletInitializer{

	@Override
	protected Class<?>[] getRootConfigClasses() {
		
		return new Class[] {RootConfig.class, SecurityConfig.class}; // RootConfig, SecurityConfig 클래스 리턴
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		
		return new Class[] {ServletConfig.class}; // ServletConfig 클래스 리턴
	}

	@Override
	protected String[] getServletMappings() {
		
		return new String[] {"/"}; // 기본 경로 리턴
	}

	@Override
	protected Filter[] getServletFilters() {
		CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
		encodingFilter.setEncoding("UTF-8");
		encodingFilter.setForceEncoding(true);
		
		return new Filter[] { encodingFilter };
	}
	

} // end WebConfig

