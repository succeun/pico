package pico.view;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author Eun Jeong-Ho, succeun@gmail.com
 * @version 2005. 6. 15.
 */
public abstract class Renderer {
	public static final String BODY_PATH = "BODY_PATH";
	
	public static View binary(String mimetype, File file) {
		return setView(new Binary(mimetype, file, file.getName()));
	}

	public static View binary(File file) {
		return setView(new Binary("application/x-msdownload", file, file.getName()));
	}

	public static View binary(File file, String fileName) {
		return setView(new Binary("application/x-msdownload", file, fileName));
	}

	public static View binary(String mimetype, File file, String fileName) {
		return setView(new Binary(mimetype, file, fileName));
	}

	public static View binary(String fileName, InputStream in) {
		return setView(new Binary("application/x-msdownload", fileName, in));
	}
	
	public static View image(BufferedImage image) {
		return setView(new Image(image, "png" , null));
	}

	public static View image(BufferedImage image, String filename) {
		return setView(new Image(image, filename));
	}
	
	public static View image(InputStream in) {
		return setView(new Image(in, "png" , null));
	}

	public static View image(InputStream in, String filename) {
		return setView(new Image(in, filename));
	}
	
	public static View resource(String path) {
		return setView(new Resource(null, Resource.class.getResource(path)));
	}

	public static View resource(URL url) {
		return setView(new Resource(null, url));
	}

	public static View resource(String mimetype, String path) {
		return setView(new Resource(mimetype, Resource.class.getResource(path)));
	}

	public static View resource(String mimetype, URL url) {
		return setView(new Resource(mimetype, url));
	}
	
	public static View text(String text) {
		return setView(new Text(null, text));
	}

	public static View text(String contentType, String text) {
		return setView(new Text(contentType, text));
	}
	
	public static View xml(String text) {
		return setView(new Xml(text));
	}
	
	public static View xml(Object bean) {
		return setView(new Xml(bean));
	}
	
	public static View json(String text) {
		return setView(new Json(text));
	}
	
	public static View json(Object bean) {
		return setView(new Json(bean));
	}
	
	public static View html(String text) {
		return setView(new Html(text));
	}
	
	public static View redirect(String location) {
		return setView(new Redirect(location));
	}

	public static View redirect(String location, boolean permanent) {
		return setView(new Redirect(location, permanent));
	}

	public static View redirect(String location, int code) {
		return setView(new Redirect(location, code));
	}
	
	public static View ok() {
		return setView(new StatusCode(HttpServletResponse.SC_OK));
	}
	
	public static View notFound() {
		return setView(new StatusCode(HttpServletResponse.SC_NOT_FOUND));
	}
	
	public static View noContent() {
		return setView(new StatusCode(HttpServletResponse.SC_NO_CONTENT));
	}
	
	public static ThreadLocal<Map<String, Object>> local = new ThreadLocal<Map<String, Object>>();
	
	private Map<String, Object> getLocal() {
		Map<String, Object> attributes = local.get();
		if (local.get() == null) {
			attributes = new LinkedHashMap<String, Object>();
			local.set(attributes);
		}
		return attributes; 
	}
	
	private static View setView(View view) {
		view.setAttributes(local.get());
		local.remove();
		return view;
	}
	
	public Renderer set(String name, long value) {
		getLocal().put(name, value);
		return this;
	}

	public Renderer set(String name, double value) {
		getLocal().put(name, value);
		return this;
	}

	public Renderer set(String name, boolean value) {
		getLocal().put(name, value);
		return this;
	}

	public Renderer set(String name, Object value) {
		getLocal().put(name, value);
		return this;
	}
	
	public Renderer setAll(Map<String, ?> map) {
		getLocal().putAll(map);
		return this;
	}
	
	/**
	 * View로 forward 나타낸다.
	 * 
	 * @param request
	 * @param response
	 * @param path
	 * @throws ControllerException
	 */
	public View forward(String path) throws ServletException {
		return setView(forwardInternal(path));
	}

	public abstract View forwardInternal(String path) throws ServletException;
}
