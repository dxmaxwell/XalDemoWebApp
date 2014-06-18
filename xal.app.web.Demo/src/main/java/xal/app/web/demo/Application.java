package xal.app.web.demo;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.server.ServerApplicationConfig;
import javax.websocket.server.ServerEndpointConfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import xal.ext.servlet.cxmanager.CXManagerServlet;

import javax.websocket.server.ServerContainer;


@EnableAutoConfiguration
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public ServletRegistrationBean clock() {
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.register(ClockSpringConfig.class);
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

	@Bean
	public ServletContextAware tt() {
		return new ServletContextAware() {
			@Override
			public void setServletContext(ServletContext context) {
				Object attr = context.getAttribute("javax.websocket.server.ServerContainer");
				if( !(attr instanceof ServerContainer) ) {
					throw new RuntimeException("WebSocket ServerContainer not found in servlet context");
				}

				ServerContainer serverContainer = (ServerContainer)attr;
				ServerApplicationConfig serverConfig = new GatewayAppConfig();

				Set<Class<? extends Endpoint>> scannedEndpointClazzes = new HashSet<Class<? extends Endpoint>>();
				Set<Class<?>> scannedPojoEndpoints = new HashSet<Class<?>>();

				Set<ServerEndpointConfig> filteredEndpointConfigs = serverConfig.getEndpointConfigs(scannedEndpointClazzes);
				Set<Class<?>> filteredPojoEndpoints = serverConfig.getAnnotatedEndpointClasses(scannedPojoEndpoints);

				try {
		            // Deploy ServerEndpointConfigs
					if( filteredEndpointConfigs != null ) {
						for (ServerEndpointConfig config : filteredEndpointConfigs) {
							serverContainer.addEndpoint(config);
						}
					}
		            // Deploy POJOs with @ServerEndpoint
					if( filteredPojoEndpoints != null ) {
						for (Class<?> clazz : filteredPojoEndpoints) {
							serverContainer.addEndpoint(clazz);
						}
					}
		        } catch (DeploymentException e) {
		            throw new RuntimeException(e);
		        }
			}
		};
	}
}
