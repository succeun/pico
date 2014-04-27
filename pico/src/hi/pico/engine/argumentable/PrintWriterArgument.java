/**
 * 
 */
package hi.pico.engine.argumentable;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

public class PrintWriterArgument extends BaseArgument {
	public boolean isAvailable(Class<?> paramType) {
		return (PrintWriter.class.isAssignableFrom(paramType));
	}
	
    public Object getArgument(int index, ArgumentInfo argumentInfo, Object ... values) throws Exception {
        for (Object value : values) {
        	if (value instanceof HttpServletResponse) {
        		return ((HttpServletResponse)value).getWriter();
        	}
        }
        return null;
    }
}