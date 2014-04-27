package hi.pico.view;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractData extends View {
	protected HttpServletRequest req;
	protected HttpServletResponse res;

	protected void write(String contentType, String text) throws IOException {
		PrintWriter out = null;
		try {
			setResponse(contentType);

			out = res.getWriter();
			out.print(text);
		} catch (IOException e) {
			throw e;
		} finally {
			if (out != null)
				out.flush();
		}
	}

	protected void write(String contentType, InputStream in) throws IOException {
		BufferedInputStream bin = null;
		BufferedOutputStream bout = null;
		try {
			setResponse(contentType);

			bin = new BufferedInputStream(in);
			bout = new BufferedOutputStream(res.getOutputStream());
			long totalSize = 0;
			int read;
			byte b[] = new byte[2048];
			while ((read = bin.read(b)) != -1) {
				bout.write(b, 0, read);
				totalSize += read;
			}

			if (totalSize > 0L)
				res.setHeader("Content-Length", totalSize + "");
		} finally {
			if (bout != null)
				bout.flush();
			if (bin != null)
				bin.close();
		}
	}

	private void setResponse(String contentType) {
		if (contentType == null || contentType.length() <= 0)
			contentType = "text/html";

		res.setDateHeader("Expires", -1);
		res.setHeader("Pragma", "no-cache");
		if (req.getProtocol().equals("HTTP/1.0"))
			res.setHeader("Cache-Control", "no-store"); // file://HTTP 1.0
		else
			res.setHeader("Cache-Control", "no-cache"); // file://HTTP 1.1
		res.setContentType(contentType);
	}
}
