package xal.app.web.demo;

import java.util.Collections;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import xal.ext.servlet.cxmanager.CXManagerServlet;

@EnableAutoConfiguration
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@Bean
	public ServletRegistrationBean clock() {
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.register(WebSocketConfig.class);
		ServletRegistrationBean config = new ServletRegistrationBean();
		config.setName("Clock Servlet");
		config.setServlet(new DispatcherServlet(context));
		config.setUrlMappings(Collections.singletonList("/clock/*"));
		return config;
	}
	
	@Bean
	public ServletRegistrationBean cxm() {
		ServletRegistrationBean config = new ServletRegistrationBean();
		config.setName("CXM Servlet");
		config.setServlet(new CXManagerServlet());
		config.setUrlMappings(Collections.singletonList("/cxm/*"));
		return config;
	}
}
