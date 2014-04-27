package hi.pico.view;

import hi.pico.ControllerContext;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Image extends AbstractImage {
	private BufferedImage image;
	private InputStream in;
	private String type;
	private String filename;

	public Image(BufferedImage image) {
		this(image, "png", null);
	}
	
	public Image(InputStream in) {
		this(in, "png", null);
	}
	
	public Image(BufferedImage image, String filename) {
		this.image = image;
		this.type = filename.substring(filename.lastIndexOf('.') + 1);
		this.filename = filename;
	}

	public Image(BufferedImage image, String type, String filename) {
		this.image = image;
		this.type = type;
		this.filename = filename;
	}
	
	public Image(InputStream in, String filename) {
		this.in = in;
		this.type = filename.substring(filename.lastIndexOf('.') + 1);
		this.filename = filename;
	}
	
	public Image(InputStream in, String type, String filename) {
		this.in = in;
		this.type = type;
	}

	@Override
	public void renderInternal(ServletContext context, HttpServletRequest req,
			HttpServletResponse res, ControllerContext controllerContext) throws Exception {
		this.req = req;
		this.res = res;
		if (image != null)
			write(image, type, filename);
		else if (in != null)
			write(in, type, filename);
	}
}
