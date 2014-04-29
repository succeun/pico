package pico.view;

import pico.ControllerContext;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleHash;
import freemarker.template.SimpleScalar;
import freemarker.template.Template;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * 
 * @author Eun Jeong-Ho, succeun@gmail.com
 * @since 2010. 8. 13.
 */
public class FTLViews extends Views {
	private Configuration cfg;

	public FTLViews() {
		initilize(null, null, null);
	}

	public FTLViews(Class<?> classForTemplateLoading) {
		initilize(classForTemplateLoading, null, null);
	}

	public FTLViews(Class<?> classForTemplateLoading,
			String templateEncoding) {
		initilize(classForTemplateLoading, null, templateEncoding);
	}

	public FTLViews(File dirForTemplateLoading) {
		initilize(null, dirForTemplateLoading, null);
	}

	public FTLViews(File dirForTemplateLoading,
			String templateEncoding) {
		initilize(null, dirForTemplateLoading, templateEncoding);
	}

	private void initilize(Class<?> classForTemplateLoading,
			File dirForTemplateLoading, String templateEncoding) {
		cfg = new Configuration();

		if (classForTemplateLoading != null) {
			cfg.setClassForTemplateLoading(classForTemplateLoading, "");
		}

		if (dirForTemplateLoading != null) {
			try {
				cfg.setDirectoryForTemplateLoading(dirForTemplateLoading);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// - Use beans wrapper (recommmended for most applications)
		cfg.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);

		if (templateEncoding != null && templateEncoding.length() > 0) {
			cfg.setDefaultEncoding(templateEncoding);
		}
	}

	/**
	 * View로 forward 나타낸다.
	 * 
	 * @param request
	 * @param response
	 * @param path
	 * @throws ServletException
	 */
	@Override
	public View forwardInternal(String path) throws ServletException {
		return new FreemarkerForward(cfg, path);
	}

	public static class FreemarkerForward extends View {
		private Configuration config;
		private String path;

		public FreemarkerForward(Configuration config, String path) {
			this.config = config;
			this.path = path;
		}
		
		@Override
		public void renderInternal(ServletContext context, HttpServletRequest req,
				HttpServletResponse res, ControllerContext controllerContext) throws Exception {
			Template temp = config.getTemplate(path);
			SimpleHash root = new SimpleHash();
			root.put("request", new RequestContextModel(req));
			root.put("session", new SessionContextModel(req));
			temp.process(root, res.getWriter());
		}
		
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
