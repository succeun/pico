package hi.pico.console;

import hi.pico.WebController;
import hi.pico.WebMethod;
import hi.pico.view.View;

import java.io.IOException;

/**
 * @author Eun Jeong-Ho, silver@intos.biz
 * @version 2006. 12. 8
 */
@WebController
public class MPlugin extends Console
{
	@WebMethod
    public View action() throws Exception
    {
        String id = req.getParameter("id");
        Plugin plugin = PluginManager.getPlugin(id);
        if (plugin != null) {
            String action = req.getParameter("action");
            View view = plugin.perform(action, engine, req, res, context, session);
            if (view != null && view instanceof PluginView) {
            	PluginView pluginView = (PluginView) view;
            	String ftlHtml = pluginView.getHtml();
            	req.setAttribute("_content_", (ftlHtml == null) ? "" : ftlHtml);
            	return renderer.forward(LAYOUT, "/MPlugin/action.ftl");
            } else {
            	return view;
            }
        } else {
        	throw new IOException("Not found Plugin[" + id + "]");
        }
    }
}
