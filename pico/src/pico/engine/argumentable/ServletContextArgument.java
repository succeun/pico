/**
 * 
 */
package pico.engine.argumentable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public class ServletContextArgument extends BaseArgument {
	public boolean isAvailable(Class<?> paramType) {
		return (ServletContext.class.isAssignableFrom(paramType));
	}
	
    public Object getArgument(int index, ArgumentInfo argumentInfo, Object ... values) throws Exception {
        ServletContext context = null;
    	for (Object value : values) {
        	if (value instanceof ServletConfig) {
        		context = ((ServletConfig)value).getServletContext();
        		break;
        	}
        }
        if (context == null) {
            for (Object value : values) {
            	if (value instanceof ServletContext) {
            		context = (ServletContext) value;
            		break;
            	}
            }
        }
        return context;
    }
}