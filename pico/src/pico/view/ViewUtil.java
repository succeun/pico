package pico.view;

import javax.servlet.http.HttpServletRequest;

public class ViewUtil {
	public static String getContentsPath(HttpServletRequest request) {
		return (String)request.getAttribute(Renderer.BODY_PATH);
	}
	
	public static String getContentsPath(HttpServletRequest request, int index) {
		return ((String[])request.getAttribute(Renderer.BODY_PATH))[index];
	}
}
