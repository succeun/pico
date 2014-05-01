/**
 * 
 */
package pico.engine.argumentable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pico.ControllerContext;

public class HttpServletRequestArgument extends BaseArgument {
	public boolean isAvailable(Class<?> paramType) {
		return HttpServletRequest.class.isAssignableFrom(paramType)
				|| ServletRequest.class.isAssignableFrom(paramType);
	}
	
	public Object getArgument(int index, ArgumentInfo argumentInfo, ControllerContext controllerContext, 
			ServletConfig config, ServletContext context, HttpServletRequest req, HttpServletResponse res, Throwable ex) throws Exception {
        return req;
    }
}