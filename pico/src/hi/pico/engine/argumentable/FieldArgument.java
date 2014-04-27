/**
 * 
 */
package hi.pico.engine.argumentable;

import hi.pico.commons.beans.BeanInfo;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.ConvertUtils;

public class FieldArgument extends BaseArgument {
	public boolean isAvailable(Class<?> paramType) {
		return (BeanInfo.isKnownType(paramType));
	}
	
    public Object getArgument(int index, ArgumentInfo argumentInfo, Object ... values) throws Exception {
        for (Object value : values) {
        	if (value instanceof HttpServletRequest) {
        		HttpServletRequest req = (HttpServletRequest) value;
        		Class<?> paramClass = argumentInfo.getParameterType();
        		Object obj = getParameter(argumentInfo, req);
                //return Convertor.convert(paramClass, obj);
        		return ConvertUtils.convert(obj, paramClass);
        	}
        }
        return null;
    }
}