package hi.pico.view;

import java.awt.AWTException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

public abstract class AbstractImage extends View {
	protected HttpServletRequest req;
	protected HttpServletResponse res;

	protected void write(BufferedImage image, String type, String filename) throws IOException,
			AWTException {
		setResponse(type, filename);
		OutputStream out = res.getOutputStream();
		ImageIO.write(image, type, out);
	}

	private void setResponse(String type, String filename) {
		type = type.toLowerCase();
		res.setContentType("image/" + type);
		res.setDateHeader("Expires", -1);
		res.setHeader("Pragma", "no-cache");
		if (req.getProtocol().equals("HTTP/1.0"))
			res.setHeader("Cache-Control", "no-store"); // file://HTTP 1.0
		else
			res.setHeader("Cache-Control", "no-cache"); // file://HTTP 1.1
		
		if (filename != null && filename.length() > 0)
			res.setHeader("Content-disposition", "attachment; filename=" + filename);
	}
	
	protected void write(InputStream in, String type, String filename) throws IOException {
		setResponse(type, filename);
		long totalSize = in.available();
        if(totalSize > 0L)
            res.setHeader("Content-Length", totalSize + "");
        
		OutputStream out = res.getOutputStream();
		IOUtils.copy(in, out);
	}
}


