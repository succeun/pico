package pico.console;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import pico.WebController;
import pico.WebMethod;
import pico.console.templates.ConsoleRoot;
import pico.view.View;
import pico.view.Views;

@WebController
public class MServer extends Console {
	@WebMethod
	public View thread() throws ServletException {
        req.setAttribute("threadgroup", dumpThreadGroup());
        return forward("/MServer/thread.ftl");
    }
	
	@WebMethod
    public View config() throws ServletException {
        req.setAttribute("props", System.getProperties());
        return forward("/MServer/config.ftl");
    }

    private TGroup dumpThreadGroup()
    {
        TGroup grup = new TGroup();
        //Find the top thread group
        ThreadGroup topThreadGroup = Thread.currentThread().getThreadGroup();
        while (topThreadGroup.getParent() != null) topThreadGroup = topThreadGroup.getParent();

        //Get all the thread groups under the top.
        ThreadGroup[] allGroups = new ThreadGroup[1000];
        int nr = topThreadGroup.enumerate(allGroups, true);

        //Dump the info.
        for (int i = 0; i < nr; i++)
            dumpThreadGroupInfo(grup, allGroups[i]);

        return grup;
    }

    private void dumpThreadGroupInfo(TGroup grup, ThreadGroup tg)
    {
        grup.parentName = (tg.getParent() == null ? "NO PARENT" : tg.getParent().getName());
        grup.name = tg.getName();
        grup.daemon = (tg.isDaemon() ? "DAEMON" : "");
        grup.destroyed = (tg.isDestroyed() ? "DESTROYED" : "");

        //Dump info for each thread.
        Thread[] allThreads = new Thread[1000];
        int threadCount = tg.enumerate(allThreads, false);

        for (int i = 0; i < threadCount; i++)
        {
            TEntry entry = new TEntry();
            entry.name = allThreads[i].getName();
            entry.daemon = (allThreads[i].isDaemon() ? " DAEMON" : "");
            entry.alive = (allThreads[i].isAlive() ? "ALIVE" : " DEAD");
            entry.priority = allThreads[i].getPriority();
            entry.code = allThreads[i].hashCode();
            grup.entries.add(entry);
        }

    }

    public static class TGroup
    {
        private String parentName;
        private String name;
        private String daemon;
        private String destroyed;
        private List<TEntry> entries = new ArrayList<TEntry>();

        public String getParentName()
        {
            return parentName;
        }

        public String getName()
        {
            return name;
        }

        public String getDaemon()
        {
            return daemon;
        }

        public String getDestroyed()
        {
            return destroyed;
        }

        public List<TEntry> getEntries()
        {
            return entries;
        }
    }

    public static class TEntry
    {
        private String name;
        private String daemon;
        private String alive;
        public int priority;
        public int code;

        public String getName()
        {
            return name;
        }

        public String getDaemon()
        {
            return daemon;
        }

        public String getAlive()
        {
            return alive;
        }

        public int getPriority()
        {
            return priority;
        }

        public int getCode()
        {
            return code;
        }
    }
    
    @WebMethod
    public View memory() throws ServletException
    {
        getMemory();
        return forward("/MServer/memory.ftl");
    }

    private void getMemory()
    {
        MemoryEntry entry = new MemoryEntry();
        entry.total = Runtime.getRuntime().totalMemory();
        entry.free = Runtime.getRuntime().freeMemory();
        entry.max = Runtime.getRuntime().maxMemory();
        entry.used = entry.total - entry.free;

        req.setAttribute("memory", entry);
    }

    public static class MemoryEntry
    {
        private long total;
        private long free;
        private long max;
        private long used;

        public long getTotal()
        {
            return total;
        }

        public long getFree()
        {
            return free;
        }

        public long getMax()
        {
            return max;
        }
        
        public long getUsed()
        {
            return used;
        }
    }

    @WebMethod
    public View gc() throws ServletException
    {
        System.gc();
        getMemory();
        return forward("/MServer/memory.ftl");
    }
}
