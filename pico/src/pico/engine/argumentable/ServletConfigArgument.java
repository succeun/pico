/**
 * 
 */
package pico.engine.argumentable;

import javax.servlet.ServletConfig;

public class ServletConfigArgument extends BaseArgument {
	public boolean isAvailable(Class<?> paramType) {
		return (ServletConfig.class.isAssignableFrom(paramType));
	}
	
    public Object getArgument(int index, ArgumentInfo argumentInfo, Object ... values) throws Exception {
        for (Object value : values) {
        	if (value instanceof ServletConfig) {
        		return value;
        	}
        }
        return null;
    }
}