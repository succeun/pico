package hi.pico;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;

/**
 * Properties의 화일에 들어 있는 키와 값을 Hierarchy 형태 즉, Tree 형태로 나타내기 위한 구조를 가지는 클래스이다.
 * 
 * @author Eun Jeong-Ho, silver@intos.biz
 * @version 2005. 10. 11.
 */
public class TreeProperties {
	public static TreeProperties getPropertiesInClassPath(String path) throws IOException {
		InputStream in = TreeProperties.class.getResourceAsStream(path);
		Properties props = new Properties();
		props.load(in);
		return new TreeProperties(props);
	}

	public static TreeProperties getPropertiesInRealPath(ServletContext ctx, String path)
			throws IOException {
		InputStream in = new BufferedInputStream(new FileInputStream(ctx.getRealPath(path)));
		Properties props = new Properties();
		props.load(in);
		return new TreeProperties(props);
	}

	private static TreeProperties getHierarchy(TreeProperties parent, String key) {
		if (key == null)
			throw new NullPointerException("key is null");

		String[] keys = split(".", key);

		int i = 0;
		String k = keys[i];
		for (; i < keys.length; i++) {
			k = keys[i];
			TreeProperties child = parent.getInMap(k);
			if (child == null) {
				child = new TreeProperties();
				parent.putInMap(k, child);
			}
			parent = child;
		}

		return parent;
	}

	private static String[] split(String token, String string) {

		if (string == null || string.length() == 0)
			return new String[] {};

		List<String> list = new ArrayList<String>();

		int prev = 0;
		int loc = string.indexOf(token, prev);

		if (loc != -1) {
			do {
				list.add(string.substring(prev, loc));
				prev = (loc + token.length());
				loc = string.indexOf(token, prev);
			} while ((loc != -1) && (prev < string.length()));

			list.add(string.substring(prev));

		} else
			list.add(string);

		return (String[]) (list.toArray(new String[] {}));
	}

	private Map<String, Object> children = new HashMap<String, Object>();

	private Object value = null;

	/**
	 * TreeProperties를 생성한다.
	 */
	public TreeProperties() {
		super();
	}

	/**
	 * TreeProperties를 생성한다.
	 */
	public TreeProperties(Properties props) {
		putAll(props);
	}

	private Object getValue() {
		return this.value;
	}

	private void setValue(Object obj) {
		this.value = obj;
	}

	private void putInMap(String key, Object value) {
		children.put(key, value);
	}

	private TreeProperties getInMap(String key) {
		return (TreeProperties) children.get(key);
	}

	private String[] getChildrenKeysInMap() {
		return (String[]) children.keySet().toArray(new String[0]);
	}

	/**
	 * 키의 값을 설정한다.
	 * 
	 * @param key
	 *            키 형태는 a.b.c 일 경우 a의 자식 b, b의 자식 c, c에 값을 설정한다.
	 * @param value
	 *            값
	 */
	public void put(String key, Object value) {
		TreeProperties parent = getHierarchy(this, key);
		parent.setValue(value);
	}

	/**
	 * 키의 값을 반환한다.
	 * 
	 * @param key
	 *            키 형태는 a.b.c 일 경우 a의 자식 b, b의 자식 c, c에 값을 반환한다.
	 * @return 값
	 */
	public Object get(String key) {
		TreeProperties parent = getHierarchy(this, key);
		return parent.getValue();
	}

	/**
	 * 키의 종단에 가지고 있는 자식 키 배열을 반환한다.
	 * 
	 * @param key
	 *            키 형태는 a.b.c 일 경우 a의 자식 b, b의 자식 c, c의 자식의 키 배열을 반환.
	 * @return 키의 자식의 배열
	 */
	public String[] getChildrenKeys(String key) {
		TreeProperties parent = getHierarchy(this, key);
		return parent.getChildrenKeysInMap();
	}

	/**
	 * Properties의 키를 기준으로 Hierarchy를 형성하여 저장한다. Hierarchy를 형성하기 위해서는 키를 '.'를
	 * 기준으로 구분하여 형성한다.
	 * 
	 * @param props
	 */
	public void putAll(Properties props) {
		Iterator<Map.Entry<Object, Object>>  itr = props.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry<Object, Object> entry =  itr.next();
			this.put((String) entry.getKey(), entry.getValue());
		}
	}

	/**
	 * boolean값으로 키값을 가져온다.
	 * 
	 * @param key
	 *            키
	 * @return boolean 값
	 */
	public boolean getBoolean(String key) {
		try {
			return (new Boolean(getString(key))).booleanValue();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 정수형으로 키값을 가져온다.
	 * 
	 * @param key
	 *            키
	 * @return int 값
	 */
	public int getInt(String key) {
		try {
			return Integer.parseInt(getString(key));
		} catch (Exception e) {
			return 0;
		}

	}

	/**
	 * long형으로 키값을 가져온다.
	 * 
	 * @param key
	 *            키
	 * @return long 값
	 */
	public long getLong(String key) {
		try {
			return Long.parseLong(getString(key));
		} catch (Exception e) {
			return 0;
		}

	}

	/**
	 * 문자열로 키값을 가져온다. 키 값이 없을 경우 입력한 기본값을 가져온다.
	 * 
	 * @param key
	 *            config 키 이름, defaultValue 기본값
	 * @return String
	 */
	public String getString(String key, String defaultVal) {
		try {
			String result = getString(key);
			if (result != null)
				return result;
		} catch (Exception ex) {
		}
		return defaultVal;
	}

	/**
	 * 정수형으로 키값을 가져온다. Key가 없거나, 정수형이 아닌경우 기본값을 가져온다.
	 * 
	 * @return int
	 * @param key
	 *            config 키 이름, defaultValue 기본값
	 */
	public int getInt(String key, int defaultVal) {
		try {
			return Integer.parseInt(getString(key));
		} catch (Exception ex) {
			return defaultVal;
		}
	}

	/**
	 * long형으로 키값을 가져온다. Key가 없거나, long형이 아닌경우 기본값을 가져온다.
	 * 
	 * @return int
	 * @param key
	 *            config 키 이름, defaultValue 기본값
	 */
	public long getLong(String key, long defaultVal) {
		try {
			return Long.parseLong(getString(key));
		} catch (Exception ex) {
			return defaultVal;
		}
	}

	/**
	 * boolean 값으로 키값을 가져온다. Key가 없거나 잘못된 형태인 경우 기본값을 가져온다.
	 * 
	 * @return Boolean
	 * @param key
	 *            config 키 이름, defaultValue 기본값
	 */
	public boolean getBoolean(String key, boolean defaultValue) {
		try {
			String x = getString(key);
			if (x == null || x.length() == 0)
				return defaultValue;

			return (new Boolean(x)).booleanValue();
		} catch (Exception ex) {
			return defaultValue;
		}
	}

	/**
	 * 문자열로 키 값을 가져온다.
	 * 
	 * @return java.lang.String
	 * @param key
	 *            java.lang.String
	 */
	public String getString(String key) {
		String value = null;
		try {
			Object tmp = get(key);
			if (tmp == null)
				throw new Exception("The value of key is null.");
			value = tmp.toString();
		} catch (Exception e) {
			//
		}
		return value;
	}

	public static void main(String[] args) {
		TreeProperties map = new TreeProperties();
		map.put("first.second.third1", "1234");
		map.put("first.second.third", "hahaha");
		map.put("first.second.third.item1", "item1");
		map.put("first.second.third.item2", "item2");
		map.put("first.second.third.item3", "item3");
		System.out.println(map.get("first.second.third"));
		System.out.println(map.get("first.second"));
		System.out.println(map.get("first.second.third.item1"));
		System.out.println(map.get("first.second.third1"));

		String[] keys = map.getChildrenKeys("first.second.third");
		for (int i = 0; i < keys.length; i++) {
			System.out.println(keys[i]);
		}

		keys = map.getChildrenKeys("first.second");
		for (int i = 0; i < keys.length; i++) {
			System.out.println(keys[i]);
		}
	}
}
