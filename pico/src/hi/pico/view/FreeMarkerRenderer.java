package hi.pico.view;

import hi.pico.ControllerContext;

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
 * @author Eun Jeong-Ho, silver@intos.biz
 * @since 2010. 8. 13.
 */
public class FreeMarkerRenderer extends Renderer {
	private Configuration cfg;

	public FreeMarkerRenderer() {
		initilize(null, null, null);
	}

	public FreeMarkerRenderer(Class<?> classForTemplateLoading) {
		initilize(classForTemplateLoading, null, null);
	}

	public FreeMarkerRenderer(Class<?> classForTemplateLoading,
			String templateEncoding) {
		initilize(classForTemplateLoading, null, templateEncoding);
	}

	public FreeMarkerRenderer(File dirForTemplateLoading) {
		initilize(null, dirForTemplateLoading, null);
	}

	public FreeMarkerRenderer(File dirForTemplateLoading,
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
		return new FreemarkerForward(cfg, null, path);
	}

	/**
	 * Layout을 이용하여, View로 forward 나타낸다. 사용방법: <!-- Body Start --> <#include
	 * getContentPath()/> <!-- Body End -->
	 * 
	 * @param request
	 * @param response
	 * @param layoutPath
	 * @param path
	 * @throws ControllerException
	 */
	@Override
	public View forwardInternal(String layoutPath, String path) throws ServletException {
		if (layoutPath == null)
			throw new ServletException("Layout Path is null.");
		if (!layoutPath.startsWith("/"))
			throw new ServletException("LayoutPath(" + layoutPath
					+ ") is not abstract path. ex) '/layout/layout1.jsp'");
		if (!path.startsWith("/"))
			throw new ServletException("Path(" + path
					+ ") is not abstract path. ex) '/page/path1.jsp'");
		
		return new FreemarkerForward(cfg, layoutPath, path);
	}

	/**
	 * Layout을 이용하여, View로 forward 나타낸다. 사용방법: <!-- Body Start --> <#include
	 * getContentPath(0)/> <!-- Body End -->
	 * 
	 * @param request
	 * @param response
	 * @param layoutPath
	 * @param path
	 * @throws ControllerException
	 */
	@Override
	public View forwardInternal(String layoutPath, String[] paths) throws ServletException {
		if (layoutPath == null)
			throw new ServletException("Layout Path is null.");
		if (!layoutPath.startsWith("/"))
			throw new ServletException("LayoutPath(" + layoutPath + ") must be abstract path. ex) '/layout/layout1.jsp'");
		for (String path : paths) {
			if (!path.startsWith("/"))
				throw new ServletException("Path(" + path + ") must be abstract path. ex) '/page/path1.jsp'");
		}
		
		return new FreemarkerForward(cfg, layoutPath, paths);
	}
	
	public static class FreemarkerForward extends View {
		private Configuration config;
		private String layoutPath;
		private String path;
		private String[] paths;

		public FreemarkerForward(Configuration config, String layoutPath, String path) {
			this.config = config;
			this.layoutPath = layoutPath;
			this.path = path;
		}
		
		public FreemarkerForward(Configuration config, String layoutPath, String[] paths) {
			this.config = config;
			this.layoutPath = layoutPath;
			this.paths = paths;
		}

		@Override
		public void renderInternal(ServletContext context, HttpServletRequest req,
				HttpServletResponse res, ControllerContext controllerContext) throws Exception {
			if (layoutPath == null) {
				Template temp = config.getTemplate(path);
				SimpleHash root = new SimpleHash();
				root.put("request", new RequestContextModel(req));
				root.put("session", new SessionContextModel(req));
				temp.process(root, res.getWriter());
			} else {
				Template temp = config.getTemplate(layoutPath);
				SimpleHash root = new SimpleHash();
				root.put("request", new RequestContextModel(req));
				root.put("session", new SessionContextModel(req));
				if (layoutPath != null && layoutPath.length() > 0) {
					if (path != null) {
						root.put("getContentPath", new PathOfContentForLayoutMethod(path));
					} else {
						root.put("getContentPath", new PathOfContentForLayoutMethod(paths));
					}
				}
				temp.process(root, res.getWriter());
			}
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

		@SuppressWarnings("unchecked")
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

	public static class PathOfContentForLayoutMethod implements TemplateMethodModelEx {
		private String path = null;
		private List<String> paths = null;

		public PathOfContentForLayoutMethod(String path) {
			this.path = path;
		}

		public PathOfContentForLayoutMethod(String[] paths) {
			this.paths = Arrays.asList(paths);
		}

		@SuppressWarnings("unchecked")
		public Object exec(List list) throws TemplateModelException {
			if (paths == null)
				return new SimpleScalar(path);
			else {
				int i = Integer.parseInt(((String) list.get(0)));
				return new SimpleScalar(paths.get(i)); // new SimpleSequence(paths[i]);
			}
		}
	}
}
