package xal.ext.servlet.cxmanager;

import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;


public class CXManagerServlet extends DispatcherServlet {

	private static final long serialVersionUID = 1L;

	public CXManagerServlet() {
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.register(CXManagerConfig.class);
		setApplicationContext(context);
	}
}
