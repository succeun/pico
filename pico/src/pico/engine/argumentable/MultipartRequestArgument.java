/**
 * 
 */
package pico.engine.argumentable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pico.ControllerContext;
import pico.MultipartRequest;

public class MultipartRequestArgument extends BaseArgument {
	public boolean isAvailable(Class<?> paramType) {
		return (MultipartRequest.class.isAssignableFrom(paramType));
	}
	
	public Object getArgument(int index, ArgumentInfo argumentInfo, ControllerContext controllerContext, 
			ServletConfig config, ServletContext context, HttpServletRequest req, HttpServletResponse res, Throwable ex) throws Exception {
		if (!(req instanceof MultipartRequest))
			return new MultipartRequest(context, req);
		return req;
    }
}