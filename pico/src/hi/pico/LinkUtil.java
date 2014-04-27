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
     * �Ϲ����� Path�� �������� ��ũ�� ������.
     * <pre>
     * ../images/image.gif : �Էµ� URL �������� ����η� ���������.
     *
     * /images/image.gif : URL �������� �����η� ���������.
     *
     * //images/image.gif : Serlvet�� Mapping path�� �������� ���������.
     * ContextPath�� sample �̸�, Servlet mapping path��  /ctrl/* ���,
     * /sample/ctrl/images/image.gif �� ���������.
     * </pre>
     * @param path ���
     * @return �н�
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
