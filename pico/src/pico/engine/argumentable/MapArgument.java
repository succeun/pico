/**
 * 
 */
package pico.engine.argumentable;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pico.ControllerContext;

public class MapArgument extends BaseArgument {
    private Class<?> paramClass = null;
	
    public boolean isAvailable(Class<?> paramType) {
		return (Map.class.isAssignableFrom(paramType));
	}

    @SuppressWarnings("unchecked")
    public Object getArgument(int index, ArgumentInfo argumentInfo, ControllerContext controllerContext, 
    		ServletConfig config, ServletContext context, HttpServletRequest req, HttpServletResponse res, Throwable ex) throws Exception {
    		if (paramClass == null) {
        		// ���� interface�� ���� �Ǿ� �ִٸ�, HashMap Ŭ������ �ν��Ͻ�ȭ �Ͽ� �����Ѵ�.
        		if (argumentInfo.getParameterType().isInterface())
        			paramClass = HashMap.class;
        		else
        			paramClass = argumentInfo.getParameterType();
        	}
        	
        	Object param;
            try {
            	param = paramClass.newInstance();
            	Map<String, String[]> map = getParameterMap(req);
            	Iterable<Entry<String, String[]>> itr = map.entrySet(); 
            	for (Entry<String, String[]> entry : itr) {
            		String name = entry.getKey();
                    String[] obj = entry.getValue();
                    if (obj != null) {
                        if (obj.length == 1)
                            ((Map) param).put(name,  obj[0]);
                        else
                            ((Map) param).put(name, obj);
                    } 
            	}
            } catch (InstantiationException e) {
                throw e;
            } catch (IllegalAccessException e) {
                throw e;
            }
            return param;
    }
}