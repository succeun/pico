/**
 * 
 */
package pico.engine.argumentable;

import pico.engine.Argumentable;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

public abstract class BaseArgument implements Argumentable {
	public Object getParameter(ArgumentInfo argumentInfo, HttpServletRequest req) throws ServletException {
		Object obj = req.getParameter(argumentInfo.getName());
		if (obj == null) {
			obj = req.getAttribute(argumentInfo.getName());
			if (obj == null) {
				obj = argumentInfo.getDefaultValue();
				if (obj == null) {
					String contentType = req.getContentType();
					if (contentType != null
							&& contentType.startsWith("multipart/form-data")
							&& req.getContentLength() > -1) {
						throw new ServletException("The request is multipart. In multipart, you can't use a param['" + argumentInfo.getName() + "'].");
					}
				}
			}
		}
		return obj;
	}

    @SuppressWarnings("unchecked")
	public Map<String, String[]> getParameterMap(HttpServletRequest req) {
    	Map<String, String[]> basket = new HashMap<String, String[]>();
    	Enumeration em = req.getParameterNames();
        while (em.hasMoreElements()) {
            String name = (String) em.nextElement();
            String[] value = req.getParameterValues(name);
            basket.put(name, value);
        }
        
        Enumeration am = req.getAttributeNames();
        while (am.hasMoreElements()) {
            String name = (String) am.nextElement();
            Object value = req.getAttribute(name);
            if (value != null) {
            	if (value instanceof String[]) {
            		basket.put(name, (String[])value);
            	} else if (value instanceof String) {
            			basket.put(name, new String[]{(String)value});
            	}
            }
        }
        return basket;
    }
}