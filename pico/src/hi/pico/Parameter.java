package hi.pico;

import java.net.URLEncoder;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Eun Jeong-Ho, silver@intos.biz
 * @version 2007. 5. 23
 * @param <V>
 */
public class Parameter {
	private Map<String, Object> basket = new LinkedHashMap<String, Object>();

	public Parameter() {
		//
	}

	public Parameter(Map<String, ?> map) {
		if (map != null)
			basket.putAll(map);
	}

	public Parameter(String key, long value) {
		basket.put(key, new Long(value));
	}

	public Parameter(String key, double value) {
		basket.put(key, new Double(value));
	}

	public Parameter(String key, boolean value) {
		basket.put(key, Boolean.valueOf(value));
	}

	public Parameter(String key, Object value) {
		basket.put(key, value);
	}

	public Parameter set(String key, long value) {
		basket.put(key, new Long(value));
		return this;
	}

	public Parameter set(String key, double value) {
		basket.put(key, new Double(value));
		return this;
	}

	public Parameter set(String key, boolean value) {
		basket.put(key, Boolean.valueOf(value));
		return this;
	}

	public Parameter set(String key, Object value) {
		basket.put(key, value);
		return this;
	}

	public String toURLString() {
		return toURLString("utf-8");
	}

	public String toURLString(String encoding) {
		String parameters = "";
		try {
			Iterator<Map.Entry<String, Object>> itr = basket.entrySet()
					.iterator();
			StringBuffer buf = new StringBuffer();
			boolean isFirst = true;
			while (itr.hasNext()) {
				Map.Entry<String, Object> entry = itr.next();
				if (!isFirst)
					buf.append('&');
				isFirst = false;
				String value = (entry.getValue() != null) ? URLEncoder.encode(
						entry.getValue().toString(), encoding) : "";
				buf.append(entry.getKey()).append('=').append(value);
			}
			parameters = buf.toString();
		} catch (Exception ignored) {
		}
		return parameters;
	}

	public String toJSString() {
		String parameters = "";
		try {
			Iterator<Map.Entry<String, Object>> itr = basket.entrySet()
					.iterator();
			StringBuffer buf = new StringBuffer();
			buf.append("{");
			boolean isFirst = true;
			while (itr.hasNext()) {
				Map.Entry<String, Object> entry = itr.next();
				if (!isFirst)
					buf.append(',');
				isFirst = false;
				Object obj = entry.getValue();
				String key = "'" + jsString(entry.getKey().toString()) + "'";
				String value = "";
				if (obj != null) {
					if (obj instanceof String)
						value = "'" + jsString(obj.toString()) + "'";
					else if (obj instanceof Boolean)
						value = ((Boolean) obj).booleanValue() + "";
					else if (obj instanceof Parameter)
						value = ((Parameter) obj).toJSString();
					else
						value = obj.toString();
				}
				buf.append(key).append(':').append(value);
			}
			buf.append("}");
			parameters = buf.toString();
		} catch (Exception ignored) {
		}
		return parameters;
	}

	/**
	 * 자바스크립트의 문자열로 들어가면 구문 오류나는 문자열을 정상적인 문자열로 변환한다.
	 * 
	 * <pre>
	 * <% String user = "Big Joe's \"right hand\"" %>
	 * <script>
	 *   alert("Welcome <%=jsString(user)%>!");
	 * </script>
	 * ...
	 * output:
	 * <script>
	 *   alert("Welcome Big Joe\'s \"right hand\"!");
	 * </script>
	 * </pre>
	 * 
	 * @param s
	 *            문자열
	 * @return 변환된 문자열
	 */
	private static String jsString(String s) {
		int ln = s.length();
		for (int i = 0; i < ln; i++) {
			char c = s.charAt(i);
			if (c == '"' || c == '\'' || c == '\\' || c == '>' || c < 0x20) {
				StringBuffer b = new StringBuffer(ln + 4);
				b.append(s.substring(0, i));
				while (true) {
					if (c == '"') {
						b.append("\\\"");
					} else if (c == '\'') {
						b.append("\\'");
					} else if (c == '\\') {
						b.append("\\\\");
					} else if (c == '>') {
						b.append("\\>");
					} else if (c < 0x20) {
						if (c == '\n') {
							b.append("\\n");
						} else if (c == '\r') {
							b.append("\\r");
						} else if (c == '\f') {
							b.append("\\f");
						} else if (c == '\b') {
							b.append("\\b");
						} else if (c == '\t') {
							b.append("\\t");
						} else {
							b.append("\\x");
							int x = c / 0x10;
							b
									.append((char) (x < 0xA ? x + '0'
											: x - 0xA + 'A'));
							x = c & 0xF;
							b
									.append((char) (x < 0xA ? x + '0'
											: x - 0xA + 'A'));
						}
					} else {
						b.append(c);
					}
					i++;
					if (i >= ln) {
						return b.toString();
					}
					c = s.charAt(i);
				}
			} // if has to be escaped
		} // for each characters
		return s;
	}

	@Override
	public String toString() {
		String parameters = "";
		try {
			Iterator<Map.Entry<String, Object>> itr = basket.entrySet()
					.iterator();
			StringBuffer buf = new StringBuffer();
			buf.append("{");
			boolean isFirst = true;
			while (itr.hasNext()) {
				Map.Entry<String, Object> entry = itr.next();
				if (!isFirst)
					buf.append(',');
				isFirst = false;
				buf.append(entry.getKey()).append(':').append(entry.getValue());
			}
			buf.append("}");
			parameters = buf.toString();
		} catch (Exception ignored) {
		}
		return parameters;
	}
}
