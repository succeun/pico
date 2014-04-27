/**
 * 
 */
package hi.pico.engine.argumentable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class HttpSessionArgument extends BaseArgument {
	public boolean isAvailable(Class<?> paramType) {
		return HttpSession.class.isAssignableFrom(paramType);
	}
	
    public Object getArgument(int index, ArgumentInfo argumentInfo, Object ... values) throws Exception {
        for (Object value : values) {
        	if (value instanceof HttpServletRequest) {
        		return ((HttpServletRequest) value).getSession();
        	}
        }
        return null;
    }
}