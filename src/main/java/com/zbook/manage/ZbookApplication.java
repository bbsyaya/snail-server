package com.zbook.manage;

import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.system.ApplicationPidFileWriter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
public class ZbookApplication extends WebMvcConfigurerAdapter implements ApplicationContextAware {

	private static ApplicationContext context;
	
	public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ZbookApplication.class);
        application.addListeners(new ApplicationPidFileWriter());
        context=application.run(args);        
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
	}
	
	public static ApplicationContext getContext() {
		return context;
	}

}
