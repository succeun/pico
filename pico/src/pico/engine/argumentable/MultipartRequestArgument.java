/**
 * 
 */
package pico.engine.argumentable;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import pico.MultipartRequest;

public class MultipartRequestArgument extends BaseArgument {
	public boolean isAvailable(Class<?> paramType) {
		return (MultipartRequest.class.isAssignableFrom(paramType));
	}
	
    public Object getArgument(int index, ArgumentInfo argumentInfo, Object ... values) throws Exception {
    	ServletContext context = null;
    	HttpServletRequest req = null;
    	for (Object value : values) {
        	if (value instanceof HttpServletRequest) {
        		req = (HttpServletRequest) value;
        	} else if (value instanceof ServletContext) {
        		context = (ServletContext) value;
        	}
        }
    	if (!(req instanceof MultipartRequest))
    		return new MultipartRequest(context, req);
    	return req;
    }
}