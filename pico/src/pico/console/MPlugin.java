package pico.console;

import java.io.IOException;

import pico.WebController;
import pico.WebMethod;
import pico.console.templates.ConsoleRoot;
import pico.view.View;
import pico.view.Views;

/**
 * @author Eun Jeong-Ho, succeun@gmail.com
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
            View view = plugin.perform(action, controllerContext, req, res, context);
            if (view != null && view instanceof PluginView) {
            	PluginView pluginView = (PluginView) view;
            	String ftlHtml = pluginView.getHtml();
            	req.setAttribute("_content_", (ftlHtml == null) ? "" : ftlHtml);
            	return forward("/MPlugin/action.ftl");
            } else {
            	return view;
            }
        } else {
        	throw new IOException("Not found Plugin[" + id + "]");
        }
    }
}
