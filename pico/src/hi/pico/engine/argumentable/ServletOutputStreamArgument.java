/**
 * 
 */
package hi.pico.engine.argumentable;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

public class ServletOutputStreamArgument extends BaseArgument {
	public boolean isAvailable(Class<?> paramType) {
		return (ServletOutputStream.class.isAssignableFrom(paramType));
	}
	
    public Object getArgument(int index, ArgumentInfo argumentInfo, Object ... values) throws Exception {
        for (Object value : values) {
        	if (value instanceof HttpServletResponse) {
        		return ((HttpServletResponse)value).getOutputStream();
        	}
        }
        return null;
    }
}