/**
 * 
 */
package pico.engine;

import pico.engine.argumentable.ArgumentInfo;

public interface Argumentable {
	public boolean isAvailable(Class<?> paramType);
    public Object getArgument(int index, ArgumentInfo argumentInfo, Object ... values) throws Exception;
}