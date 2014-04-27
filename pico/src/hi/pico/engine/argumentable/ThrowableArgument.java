/**
 * 
 */
package hi.pico.engine.argumentable;


public class ThrowableArgument extends BaseArgument {
	public boolean isAvailable(Class<?> paramType) {
		return (Throwable.class.isAssignableFrom(paramType));
	}
	
    public Object getArgument(int index, ArgumentInfo argumentInfo, Object ... values) throws Exception {
        for (Object value : values) {
        	if (value instanceof Throwable) {
        		return value;
        	}
        }
        return null;
    }
}