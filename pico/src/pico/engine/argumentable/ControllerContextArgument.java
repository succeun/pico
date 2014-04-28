/**
 * 
 */
package pico.engine.argumentable;

import pico.ControllerContext;

public class ControllerContextArgument extends BaseArgument {
	public boolean isAvailable(Class<?> paramType) {
		return (ControllerContext.class.isAssignableFrom(paramType));
	}
	
    public Object getArgument(int index, ArgumentInfo argumentInfo, Object ... values) throws Exception {
        for (Object value : values) {
        	if (value instanceof ControllerContext) {
        		return value;
        	}
        }
        return null;
    }
}