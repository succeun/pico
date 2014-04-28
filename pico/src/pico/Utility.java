package pico;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Utility {

	public static String toStrackTraceString(Throwable e) {
		StringWriter write = new StringWriter();
		PrintWriter p = new PrintWriter(write);
		e.printStackTrace(p);
		return write.toString();
	}

	public static String toStrackTraceStringHTML(Throwable e) {
		String tmp = toStrackTraceString(e);
		tmp = tmp.replaceAll("\r\n", "<br>");
		return tmp.replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
	}

}
