/**
 * 
 */
package pico.engine.argumentable;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

public class MapArgument extends BaseArgument {
    private Class<?> paramClass = null;
	
    public boolean isAvailable(Class<?> paramType) {
		return (Map.class.isAssignableFrom(paramType));
	}

    @SuppressWarnings("unchecked")
	public Object getArgument(int index, ArgumentInfo argumentInfo, Object ... values) throws Exception {
        for (Object value : values) {
        	if (value instanceof HttpServletRequest) {
        		HttpServletRequest req = (HttpServletRequest) value;
        		if (paramClass == null) {
            		// 만약 interface로 정의 되어 있다면, HashMap 클래스롤 인스턴스화 하여 전달한다.
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
        return null;
    }
}