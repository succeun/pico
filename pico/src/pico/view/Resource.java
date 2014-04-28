package pico.view;

import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pico.ControllerContext;

public class Resource extends AbstractDownload {
	private String mimetype;
	private URL url;
	private String filename;

	public Resource(String path) {
		this(null, Resource.class.getResource(path));
	}

	public Resource(URL url) {
		this(null, url);
	}

	public Resource(String mimetype, String path) {
		this(mimetype, Resource.class.getResource(path));
	}
	
	public Resource(String mimetype, URL url) {
		this.mimetype = mimetype;
		this.url = url;
		this.filename = url.getFile();
	}

	public Resource(String mimetype, URL url, String filename) {
		this.mimetype = mimetype;
		this.url = url;
		this.filename = filename;
	}

	@Override
	public void renderInternal(ServletContext context, HttpServletRequest req,
			HttpServletResponse res, ControllerContext controllerContext) throws Exception {
		this.req = req;
		this.res = res;
		if (mimetype == null) {
			mimetype = context.getMimeType(url.getFile());
		}
		write(mimetype, filename, url.openStream(), false);
	}
}
