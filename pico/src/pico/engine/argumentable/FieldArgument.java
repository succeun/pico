/**
 * 
 */
package pico.engine.argumentable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.ConvertUtils;

import pico.ControllerContext;
import pico.commons.beans.BeanInfo;

public class FieldArgument extends BaseArgument {
	public boolean isAvailable(Class<?> paramType) {
		return (BeanInfo.isKnownType(paramType));
	}
	
	public Object getArgument(int index, ArgumentInfo argumentInfo, ControllerContext controllerContext, 
			ServletConfig config, ServletContext context, HttpServletRequest req, HttpServletResponse res, Throwable ex) throws Exception {
		Class<?> paramClass = argumentInfo.getParameterType();
		Object obj = getParameter(argumentInfo, req);
        //return Convertor.convert(paramClass, obj);
		return ConvertUtils.convert(obj, paramClass);
    }
}