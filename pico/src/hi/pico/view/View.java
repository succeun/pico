package hi.pico.view;

import hi.pico.ControllerContext;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class View {
	private Map<String, Object> attributes = new LinkedHashMap<String, Object>();
	
	public View setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
		return this;
	}

	public void render(ServletContext context, 
			HttpServletRequest req, HttpServletResponse res, ControllerContext controllerContext) throws Exception {
    	if (attributes != null && attributes.size() > 0) {
    		Iterator<String> itr = attributes.keySet().iterator();
    		while (itr.hasNext()) {
    			String key = itr.next();
    			req.setAttribute(key, attributes.get(key));
    		}
    	}
    	renderInternal(context, req, res, controllerContext);
    }
	
	public abstract void renderInternal(ServletContext context, 
			HttpServletRequest req, HttpServletResponse res, ControllerContext controllerContext) throws Exception;
}
