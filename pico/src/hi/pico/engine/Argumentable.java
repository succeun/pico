/**
 * 
 */
package hi.pico.engine;

import hi.pico.engine.argumentable.ArgumentInfo;

public interface Argumentable {
	public boolean isAvailable(Class<?> paramType);
    public Object getArgument(int index, ArgumentInfo argumentInfo, Object ... values) throws Exception;
}