package hi.pico.console.plugins;

import hi.pico.console.Plugin;
import hi.pico.console.PluginMethod;
import hi.pico.console.PluginView;
import hi.pico.engine.ControllerEngine;
import hi.pico.engine.ControllerMapper;
import hi.pico.engine.MethodMapper;
import hi.pico.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Eun Jeong-Ho, silver@intos.biz
 * @version 2007. 1. 4
 */
public class DefaultPlugin extends Plugin {
	@Override
	public String getName() {
		return "Default";
	}
	
	@Override
	public void init() {

	}

	@Override
	public void destroy() {

	}

	@PluginMethod(name="controllers")
	public View getControllers(ControllerEngine engine, HttpServletRequest req)
			throws ServletException, IOException {
		Map<String, List<CountEntry>> sheet = new HashMap<String, List<CountEntry>>();
		ControllerMapper[] mappers = engine.getControllerMappers();
		List<CountEntry> totallist = new ArrayList<CountEntry>();
		long t = 0;
		for (int i = 0; i < mappers.length; i++) {
			List<CountEntry> list = new ArrayList<CountEntry>();
			long total = 0;
			MethodMapper[] mmappers = mappers[i].getMethodMappers();
			for (int j = 0; j < mmappers.length; j++) {
				total += mmappers[j].getCallCount();
				CountEntry entry = new CountEntry();
				entry.name = mmappers[j].getMethodName();
				entry.count = mmappers[j].getCallCount();
				list.add(entry);
			}
			sheet.put(mappers[i].getControllerName() + "(" + getSizeLabel(total) + ")", list);

			CountEntry entry = new CountEntry();
			entry.name = mappers[i].getControllerName();
			entry.count = total;
			totallist.add(entry);
			t += total;
		}
		sheet.put("total", totallist);
		req.setAttribute("totallabel", "Total(" + getSizeLabel(t) + ")");
		req.setAttribute("sheet", sheet);

		return new PluginView(this, "controllers.ftl");
	}

	private String getSizeLabel(long size) {
		// Byte, Kilo, Mega, Giga, Tera, Peta, Exa
		if (size > 1000000000000000000L)
			return (size / 1000000000000000000L) + "Exa";
		else if (size > 1000000000000000L)
			return (size / 1000000000000000L) + "Peta";
		else if (size > 1000000000000L)
			return (size / 1000000000000L) + "Tela";
		else if (size > 1000000000L)
			return (size / 1000000000L) + "Giga";
		else if (size > 1000000L)
			return (size / 1000000L) + "Mega";
		else if (size > 1000L)
			return (size / 1000L) + "Kilo";
		else
			return size + "";
	}

	public static class CountEntry {
		private String name;
		private long count;

		public String getName() {
			return name;
		}

		public long getCount() {
			return count;
		}
	}
}
