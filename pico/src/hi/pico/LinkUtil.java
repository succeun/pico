package hi.pico;

import javax.servlet.http.HttpServletRequest;

public class LinkUtil {
	public static String toLink(HttpServletRequest req, Class<?> clazz, String methodName) {
    	return toLink(req, clazz, methodName, null);
    }
    
    public static String toLink(HttpServletRequest req, Class<?> clazz, String methodName, String urlParam) {
        StringBuffer buf = new StringBuffer();
        String contextPath = req.getContextPath();
        if (contextPath != null && contextPath.length() > 0)
        	buf.append(contextPath);
        String servletPath = req.getServletPath();
        if (servletPath != null && servletPath.length() > 0)
        	buf.append(servletPath);
        
        return toLink(buf.toString(), clazz, methodName, urlParam);
    }

    /**
     * 일반적인 Path를 기존으로 링크를 만들어낸다.
     * <pre>
     * ../images/image.gif : 입력된 URL 기준으로 상대경로로 만들어진다.
     *
     * /images/image.gif : URL 기준으로 절대경로로 만들어진다.
     *
     * //images/image.gif : Serlvet의 Mapping path를 기준으로 만들어진다.
     * ContextPath가 sample 이며, Servlet mapping path가  /ctrl/* 라면,
     * /sample/ctrl/images/image.gif 로 만들어진다.
     * </pre>
     * @param path 경로
     * @return 패스
     */
    public static String toLink(HttpServletRequest req, String path) {
        if (path.startsWith("//")) {
        	StringBuffer buf = new StringBuffer();
        	String contextPath = req.getContextPath();
            if (contextPath != null && contextPath.length() > 0)
            	buf.append(contextPath);
            String servletPath = req.getServletPath();
            if (servletPath != null && servletPath.length() > 0)
            	buf.append(servletPath);
            buf.append(path.substring(1));
            return buf.toString();
        } else {
            return path;
        }
    }
    
    public static String toLink(String prifixPath, Class<?> clazz, String methodName) {
    	return toLink(prifixPath, clazz, methodName, null);
    }
    
    public static String toLink(String prifixPath, Class<?> clazz, String methodName, String urlParam) {
        StringBuffer buf = new StringBuffer();
        buf.append(prifixPath);
        
        String str = clazz.getName();
        buf.append("/").append(str.replace('.','/')).append("/").append(methodName);
        
        String url = buf.toString().replaceAll("/+", "/");
        
        if (urlParam != null)
            url += "?" + urlParam;
        
        return url;
    }
}
