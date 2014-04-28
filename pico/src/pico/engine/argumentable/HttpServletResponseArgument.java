/**
 * 
 */
package pico.engine.argumentable;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class HttpServletResponseArgument extends BaseArgument {
	public boolean isAvailable(Class<?> paramType) {
		return (HttpServletResponse.class.isAssignableFrom(paramType)
				|| ServletResponse.class.isAssignableFrom(paramType));
	}
	
    public Object getArgument(int index, ArgumentInfo argumentInfo, Object ... values) throws Exception {
        for (Object value : values) {
        	if (value instanceof HttpServletResponse) {
        		return value;
        	}
        }
        return null;
    }
}