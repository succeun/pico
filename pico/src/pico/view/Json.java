package pico.view;

import java.io.StringWriter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import pico.ControllerContext;

public class Json extends AbstractData {
	private String json;
	private Object bean;

	public Json(String json) {
		this.json = json;
	}
	
	public Json(Object bean) {
		if (bean instanceof String) {
			this.json = (String) bean;
		} else {
			this.bean = bean;
		}
	}
	
	private String getJson() throws Exception {
		if (json == null && bean != null) {
			StringWriter writer = new StringWriter();
	        ObjectMapper mapper = new ObjectMapper();
	        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
	        
	        mapper.writeValue(writer, bean);
	        return writer.toString();
		} else {
			return (json == null) ? "" : json;
		}
	}
	
	@Override
	public void renderInternal(ServletContext context, HttpServletRequest req,
			HttpServletResponse res, ControllerContext controllerContext) throws Exception {
		this.req = req;
		this.res = res;
		String str = getJson();
		write("application/json", str);
	}
}
