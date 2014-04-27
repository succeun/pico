/**
 * 
 */
package hi.pico.engine.argumentable;

import hi.pico.ControllerContext;
import hi.pico.engine.ControllerEngine;

public class ControllerEngineArgument extends BaseArgument {
	public boolean isAvailable(Class<?> paramType) {
		return (ControllerEngine.class.isAssignableFrom(paramType));
	}
	
    public Object getArgument(int index, ArgumentInfo argumentInfo, Object ... values) throws Exception {
    	ControllerEngine engine = null;
    	for (Object value : values) {
        	if (value instanceof ControllerContext) {
        		engine = ((ControllerContext)value).getControllerEngine();
        		break;
        	}
        }
        if (engine == null) {
            for (Object value : values) {
            	if (value instanceof ControllerEngine) {
            		engine = (ControllerEngine) value;
            		break;
            	}
            }
        }
        return engine;
    }
}