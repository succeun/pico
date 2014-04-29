package pico.console;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pico.ServiceServlet;

/**
 * @author Eun Jeong-Ho, succeun@gmail.com
 * @version 2006. 4. 21
 */
public final class PluginManager {
	private static final Logger logger = LoggerFactory.getLogger(ServiceServlet.class);
	
    private static Map<String, PluginEntry> plugins;

    static {
        init();
    }

    /**
     * 정의된 Id의 Plugin을 반환한다.
     * @param id 아이디
     * @return Plugin
     */
    public static Plugin getPlugin(String id) {
        PluginEntry entry = (PluginEntry) plugins.get(id);
        if (entry == null)
            throw new NullPointerException("Not exist \""+id+"\" of Plugin.");
        return entry.plugin;
    }

    private static void init() {
        plugins = Collections.synchronizedMap(new HashMap<String, PluginEntry>());
    }

    public static void reload() {
        Iterator<PluginEntry> itr = plugins.values().iterator();
        while (itr.hasNext())
            ((PluginEntry) itr.next()).plugin.destroy();

        init();
    }

    public static void destory() {
        Collection<PluginEntry> col = plugins.values();
        Iterator<PluginEntry> itr = col.iterator();
        while (itr.hasNext())
            ((PluginEntry) itr.next()).plugin.destroy();
        plugins.clear();
        plugins = null;
    }

    public static void register(Class<?> pluginClass) {
        try {
        	if (!Plugin.class.isAssignableFrom(pluginClass)) {
        		throw new IllegalArgumentException(pluginClass + " is not Plugin class.");
        	}
            logger.info("Plugin[" + pluginClass + "] is loading.");

            PluginEntry entry = new PluginEntry();
            
            Plugin instance = (Plugin) pluginClass.newInstance();
            entry.id = instance.getName();
            entry.plugin = instance;

            entry.plugin.init();

            plugins.put(entry.id, entry);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static PluginEntry[] getPluginEntries() {
        return (PluginEntry[]) plugins.values().toArray(new PluginEntry[0]);
    }

    public static Plugin[] getPlugins() {
        PluginEntry[] entries = getPluginEntries();
        Plugin[] widgets = new Plugin[entries.length];
        for (int i = 0; i < entries.length;i++) {
            widgets[i] = entries[i].plugin;
        }
        return widgets;
    }

    public static class PluginEntry {
        private String id;
        private Plugin plugin;

        public String getId() {
            return id;
        }

        public Plugin getPlugin() {
            return plugin;
        }
    }
}
