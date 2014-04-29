package pico.view;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import pico.ControllerContext;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * 
 * @author Eun Jeong-Ho, succeun@gmail.com
 * @since 2010. 8. 13.
 */
public class FTLView extends View {
	private static Map<String, Configuration> configs = Collections.synchronizedMap(new HashMap<String, Configuration>());
	
	private synchronized static Configuration getConfig(Class<?> classForTemplateLoading) {
		String basePath = classForTemplateLoading.getName();
		Configuration config = configs.get(basePath);
		if (config == null) {
			config = new Configuration();
			config.setClassForTemplateLoading(classForTemplateLoading, "");
			configs.put(basePath, config);
		}
		return config;
	}
	
	private synchronized static Configuration getConfig(String dirForTemplateLoading) {
		String basePath = dirForTemplateLoading;
		Configuration config = configs.get(basePath);
		if (config == null) {
			config = new Configuration();
			try {
				config.setDirectoryForTemplateLoading(new File(dirForTemplateLoading));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			configs.put(basePath, config);
		}
		return config;
	}
	
	private Configuration cfg;
	private String path;

	public FTLView(Class<?> classForTemplateLoading, String path) {
		this.cfg = getConfig(classForTemplateLoading);
		this.path = path;
	}

	public FTLView(String dirForTemplateLoading, String path) {
		this.cfg = getConfig(dirForTemplateLoading);
		this.path = path;
	}
	
	public FTLView(String path) {
		this.cfg = null;
		this.path = path;
	}
		
	@Override
	public void renderInternal(ServletContext context, HttpServletRequest req,
			HttpServletResponse res, ControllerContext controllerContext) throws Exception {
		if (cfg == null) {
			cfg = getConfig(context.getRealPath("/"));
		}
		Template temp = cfg.getTemplate(path);
		temp.setEncoding(res.getCharacterEncoding());
		SimpleHash root = new SimpleHash();
		root.put("request", new RequestContextModel(req));
		root.put("session", new SessionContextModel(req));
		temp.process(root, res.getWriter());
	}
		
	public static class RequestContextModel implements TemplateHashModel {
		private HttpServletRequest request;

		public RequestContextModel(HttpServletRequest request) {
			this.request = request;
		}

		public TemplateModel get(String key) throws TemplateModelException {

			Object bean = request.getParameter(key);
			if (bean == null)
				bean = request.getAttribute(key);

			if (bean instanceof Map<?, ?>)
				return DefaultObjectWrapper.DEFAULT_WRAPPER.wrap(bean);
			else
				return DefaultObjectWrapper.BEANS_WRAPPER.wrap(bean);
		}

		public boolean isEmpty() {
			return false;
		}
	}

	public static class SessionContextModel implements TemplateHashModel {
		private HttpSession session;

		public SessionContextModel(HttpServletRequest request) {
			this.session = request.getSession();
		}

		public TemplateModel get(String key) throws TemplateModelException {

			Object bean = session.getAttribute(key);

			if (bean instanceof Map)
				return DefaultObjectWrapper.DEFAULT_WRAPPER.wrap(bean);
			else
				return DefaultObjectWrapper.BEANS_WRAPPER.wrap(bean);
		}

		public boolean isEmpty() {
			return false;
		}
	}
}
