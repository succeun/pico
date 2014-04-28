package pico.engine.argumentable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.xml.sax.InputSource;

import pico.ArgumentType;
import pico.commons.beans.BeanWrapper;


public class BeanArgument extends BaseArgument {
	public boolean isAvailable(Class<?> paramType) {
		return true;
	}
	
    public Object getArgument(int index, ArgumentInfo argumentInfo, Object ... values) throws Exception {
        for (Object value : values) {
        	if (value instanceof HttpServletRequest) {
        		HttpServletRequest req = (HttpServletRequest) value;
        		Class<?> beanClass = argumentInfo.getParameterType();
                Object bean = null;
                try {
                	if (argumentInfo.getArgumentType() == ArgumentType.XML) {
                		String reqValue = null;
                		if (argumentInfo.isRequestBody()) {
                			reqValue = getRequestBody(req);
                		} else {
                			reqValue = (String) getParameter(argumentInfo, req);
                		}
                		bean = getArgumentFromXml(reqValue, argumentInfo, beanClass);
            		} else if (argumentInfo.getArgumentType() == ArgumentType.JSON) {
            			String reqValue = null;
                		if (argumentInfo.isRequestBody()) {
                			reqValue = getRequestBody(req);
                		} else {
                			reqValue = (String) getParameter(argumentInfo, req);
                		}
            			bean = getArgumentFromJson(reqValue, argumentInfo, beanClass);
            		} else {
	                    bean = getArgumentFromAny(req, beanClass);
            		}
                } catch (InstantiationException e) {
                    throw e;
                } catch (IllegalAccessException e) {
                    throw e;
                }
	
                return bean;
        	}
        }
        return null;
    }
    
    private String getRequestBody(HttpServletRequest req) throws IOException {
    	BufferedReader reader = req.getReader();
    	StringBuffer buf = new StringBuffer();
    	char[] c = new char[512];
    	int len = 0;
    	while ((len = reader.read(c)) != -1) {
    		buf.append(c, 0, len);
    	}
    	return buf.toString();
    }

	private Object getArgumentFromAny(HttpServletRequest req, Class<?> beanClass) throws IllegalAccessException, InstantiationException {
		Object bean;
		if (beanClass.isArray()) {
		    Class<?> elementClass = beanClass.getComponentType();
		    BeanWrapper tmp = new BeanWrapper(elementClass);
		    int max = 0;
		    Map<String, String[]> requests = new LinkedHashMap<String, String[]>();
		    Map<String, String[]> map = getParameterMap(req);
			Iterable<Entry<String, String[]>> itr = map.entrySet(); 
			for (Entry<String, String[]> entry : itr) {
				String name = entry.getKey();
		        String[] obj = entry.getValue();
		        if (tmp.contains(name) && obj != null && obj.length > max)
		            max = obj.length;
		        requests.put(name, obj);
		    }

		    bean = Array.newInstance(elementClass, max);

		    for (int i = 0; i < max; i++) {
		        BeanWrapper elementWrapper = new BeanWrapper(elementClass);
		        Iterator<String> itrs = requests.keySet().iterator();
		        while (itrs.hasNext()) {
		            String name = (String) itrs.next();
		            String[] obj = (String[]) requests.get(name);
		            if (elementWrapper.contains(name) && obj.length > i && obj[i] != null)
		                elementWrapper.set(name, obj[i]);
		        }

		        Array.set(bean, i, elementWrapper.getBean());
		    }
		} else {
		    BeanWrapper beanWrapper = new BeanWrapper(beanClass);
		    Map<String, String[]> map = getParameterMap(req);
			Iterable<Entry<String, String[]>> itr = map.entrySet(); 
			for (Entry<String, String[]> entry : itr) {
				String name = entry.getKey();
		        String[] obj = entry.getValue();
		        if (beanWrapper.contains(name))
		        	beanWrapper.set(name, obj);
			}
		    bean = beanWrapper.getBean();
		}
		return bean;
	}

	private Object getArgumentFromXml(String reqValue, ArgumentInfo argumentInfo, Class<?> beanClass) throws ServletException, JAXBException {
		if (reqValue == null || reqValue.length() <= 0)
			return null;
		
		JAXBContext jaxbContext = JAXBContext.newInstance(beanClass);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		return unmarshaller.unmarshal(new InputSource(new StringReader(reqValue)));
	}

	private Object getArgumentFromJson(String reqValue, ArgumentInfo argumentInfo, Class<?> beanClass) throws ServletException, IOException, JsonParseException, JsonMappingException {
		if (reqValue == null || reqValue.length() <= 0)
			return null;
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);	// 필드를 매핑
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);	// 필드명에 "를 포함하지 않아도 파싱 가능
		//mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		//mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
		//mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);	// 필드가 맞지 않아도 무시
		return mapper.readValue(reqValue, beanClass);
	}
}