package hi.pico.engine.url;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DefaultUrlResolver implements UrlResolver {

	public void destory() {
		//
	}

	public void init(ServletConfig config) {
		//
	}

	public UrlInfo resolve(ServletContext context, HttpServletRequest req, HttpServletResponse res) {
	    /*
	    web.xml의 url-pattern 입니다.
	    1)  *.jsp
	    req.getRequestURI() = "/pico/ctrl/blog/Blog/index.jsp"
	    req.getContextPath() = "/pico"
	    req.getServletPath() = "/ctrl/blog/Blog/index.jsp"
	    req.getPathInfo() = null

	    2) /ctrl/*
	    req.getRequestURI() = "/pico/ctrl/blog/Blog/index.jsp"
	    req.getContextPath() = "/pico"
	    req.getServletPath() = "/ctrl"
	    req.getPathInfo() = "/blog/Blog/index.jsp"  <- 정답
	    */
		String pathInfo = req.getPathInfo();
		if (pathInfo != null && pathInfo.length() > 0) {
			String args[] = pathInfo.split("/");
        	if (args.length >= 2) {
        		String methodName = args[args.length - 1];
                String classFullName = getFullClassName(args);
                return new UrlInfo(classFullName, methodName);
        	}
		}
		return null;
	}
	
	private String getFullClassName(String[] args) {
    	StringBuffer buf = new StringBuffer();
    	for (int i = 1; i < args.length - 2; i++) {
    		buf.append(args[i]).append('.');
    	}
    	buf.append(args[args.length - 2]);
    	return buf.toString();
    }
}
