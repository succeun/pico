package pico.engine.url;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UrlResolver {
	public void init(ServletConfig config);

    public UrlInfo resolve(ServletContext context, HttpServletRequest req, HttpServletResponse res);

    public void destory();
}
