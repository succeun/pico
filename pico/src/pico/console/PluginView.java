package pico.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pico.ControllerContext;
import pico.Utility;
import pico.view.AbstractData;

public class PluginView extends AbstractData {
	private Plugin plugin;
	private String html;

	public PluginView(Plugin plugin, String ftlResource) throws IOException {
		this.plugin = plugin;
		this.html = getResource(ftlResource);
	}
	
	public PluginView(Plugin plugin, Exception ex) {
		this.plugin = plugin;
		this.html = Utility.toStrackTraceStringHTML(ex);
	}
	
	public String getHtml() {
		return html;
	}
	
	public String getResource(String res) throws IOException {
    	InputStream in = plugin.getClass().getResourceAsStream(res);
    	if (in != null) {
    		return read(in, System.getProperty("file.encoding"));
    	}
    	return "";
    }
    
	public static String read(InputStream in, String charsetName)
			throws IOException {

		BufferedReader bin = null;
		try {
			if (charsetName == null || charsetName.length() <= 0) {
				charsetName = System.getProperty("file.encoding");
			}
			bin = new BufferedReader(new InputStreamReader(in, charsetName));

			StringBuilder sb = new StringBuilder();
			char[] c = new char[1024];
			int len;
			while ((len = bin.read(c)) != -1) {
				sb.append(c, 0, len);
			}

			return sb.toString();
		} catch (IOException ioe) {
			throw ioe;
		} finally {
			try {
				bin.close();
			} catch (IOException ioe) {
			}
		}
	}

	@Override
	public void renderInternal(ServletContext context, HttpServletRequest req,
			HttpServletResponse res, ControllerContext controllerContext) throws Exception {
		this.req = req;
		this.res = res;
		write("text/html", html);
	}
}
