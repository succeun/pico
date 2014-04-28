package pico;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pico.console.Console;
import pico.engine.ControllerEngine;
import pico.engine.url.DefaultUrlResolver;
import pico.engine.url.UrlResolver;
import pico.engine.util.ClassUtil;

public class ServiceServlet extends HttpServlet {
	private static final long serialVersionUID = 8896384313820453904L;

	private final Logger logger = LoggerFactory.getLogger(ServiceServlet.class);
	
	private ControllerEngine engine = null;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		ServletContext context = this.getServletContext();
		logger.info(this.getClass().getName() + " is started.");
		logger.info("Application root path is " + context.getRealPath("/"));
		
		String packageName = config.getInitParameter("base-package");
		String[] packageNames = (packageName == null || packageName.length() <= 0) 
							? new String[]{} : packageName.replaceAll("\\n|\\t| ", "").split(",");
		UrlResolver urlrvr = getUrlResolver(config);
		
		String consoleEnable = config.getInitParameter("console-enable");
		boolean isConsoleEnable = true;
		if (consoleEnable != null && consoleEnable.length() > 0)
			isConsoleEnable = Boolean.getBoolean(consoleEnable);
		
		if (isConsoleEnable) {
			boolean isConsole = false;
			for (String name : packageNames) {
				if ("pico.console".equals(name)) {
					isConsole = true;
				}
			}
			if (!isConsole) {
				List<String> list = new ArrayList<String>(Arrays.asList(packageNames));
				list.add("pico.console");
				packageNames = (String[]) list.toArray(new String[]{});
			}
		}
		
		engine = new ControllerEngine(config, packageNames, urlrvr);
		
		String password = config.getInitParameter("console-password");
		if (password != null) {
			Console.setConsolePassword(password);
		}
	}
	
	private UrlResolver getUrlResolver(ServletConfig config) throws ServletException {
		UrlResolver urlResolver = null;
		try {
			String urlr = config.getInitParameter("url-resolver");
			if (urlr != null && urlr.length() > 0) {
				urlResolver = (UrlResolver) ClassUtil.loadClass(urlr).newInstance();
	    	}
	    	else
	    		urlResolver = new DefaultUrlResolver();
	    	urlResolver.init(config);
	    	return urlResolver;
		} catch (InstantiationException e) {
			throw new ServletException(e);
		} catch (IllegalAccessException e) {
			throw new ServletException(e);
		} catch (ClassNotFoundException e) {
			throw new ServletException(e);
		}
	}
	
	@Override
	public void destroy() {
		engine.destory();
		super.destroy();
	}

	protected void doService(MethodType type, HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {		
		engine.invoke(type, this.getServletContext(), req, res);
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doService(MethodType.Post, req, res);
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doService(MethodType.Get, req, res);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doService(MethodType.Delete, req, res);
	}

	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doService(MethodType.Head, req, res);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doService(MethodType.Put, req, res);
	}

	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doService(MethodType.Options, req, res);
	}
	
	
}
