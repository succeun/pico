/**
 * 
 */
package pico.engine.argumentable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pico.ControllerContext;

public class ServletContextArgument extends BaseArgument {
	public boolean isAvailable(Class<?> paramType) {
		return (ServletContext.class.isAssignableFrom(paramType));
	}
	
	public Object getArgument(int index, ArgumentInfo argumentInfo, ControllerContext controllerContext, 
			ServletConfig config, ServletContext context, HttpServletRequest req, HttpServletResponse res, Throwable ex) throws Exception {
    	if (config != null) {
        		return config.getServletContext();
        }
    	
        return context;
    }
}