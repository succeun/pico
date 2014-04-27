/**
 * 
 */
package hi.pico.engine.argumentable;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

public class HttpServletRequestArgument extends BaseArgument {
	public boolean isAvailable(Class<?> paramType) {
		return HttpServletRequest.class.isAssignableFrom(paramType)
				|| ServletRequest.class.isAssignableFrom(paramType);
	}
	
    public Object getArgument(int index, ArgumentInfo argumentInfo, Object ... values) throws Exception {
        for (Object value : values) {
        	if (value instanceof HttpServletRequest) {
        		return  value;
        	}
        }
        return null;
    }
}