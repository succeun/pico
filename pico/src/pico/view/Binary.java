package pico.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pico.ControllerContext;

public class Binary extends AbstractDownload {
	private String mimetype;
	private File file;
	private String fileName;
	private InputStream in;
	private boolean isDownload = true;

	public Binary(String mimetype, File file) {
		this(mimetype, file, file.getName());
	}

	public Binary(File file) {
		this("application/x-msdownload", file, file.getName());
	}

	public Binary(File file, String fileName) {
		this("application/x-msdownload", file, fileName);
	}

	public Binary(String mimetype, File file, String fileName) {
		this.mimetype = mimetype;
		this.file = file;
		this.fileName = fileName;
	}

	public Binary(String fileName, InputStream in) {
		this("application/x-msdownload", fileName, in);
	}

	public Binary(String mimetype, String fileName, InputStream in) {
		this.mimetype = mimetype;
		this.fileName = fileName;
		this.in = in;
	}

	@Override
	public void renderInternal(ServletContext context, HttpServletRequest req,
			HttpServletResponse res, ControllerContext controllerContext) throws Exception {
		this.req = req;
		this.res = res;

		if (file != null) {
			if (!file.isFile())
				throw new IOException("'" + file.getName() + "' is not File.");

			InputStream in = null;
			try {
				in = new FileInputStream(file);
				write(mimetype, fileName, in, isDownload);
			} finally {
				if (in != null)
					in.close();
			}
		} else {
			write(mimetype, fileName, in, isDownload);
		}
	}

}
