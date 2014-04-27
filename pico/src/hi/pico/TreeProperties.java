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
 * Properties�� ȭ�Ͽ� ��� �ִ� Ű�� ���� Hierarchy ���� ��, Tree ���·� ��Ÿ���� ���� ������ ������ Ŭ�����̴�.
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
	 * TreeProperties�� �����Ѵ�.
	 */
	public TreeProperties() {
		super();
	}

	/**
	 * TreeProperties�� �����Ѵ�.
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
	 * Ű�� ���� �����Ѵ�.
	 * 
	 * @param key
	 *            Ű ���´� a.b.c �� ��� a�� �ڽ� b, b�� �ڽ� c, c�� ���� �����Ѵ�.
	 * @param value
	 *            ��
	 */
	public void put(String key, Object value) {
		TreeProperties parent = getHierarchy(this, key);
		parent.setValue(value);
	}

	/**
	 * Ű�� ���� ��ȯ�Ѵ�.
	 * 
	 * @param key
	 *            Ű ���´� a.b.c �� ��� a�� �ڽ� b, b�� �ڽ� c, c�� ���� ��ȯ�Ѵ�.
	 * @return ��
	 */
	public Object get(String key) {
		TreeProperties parent = getHierarchy(this, key);
		return parent.getValue();
	}

	/**
	 * Ű�� ���ܿ� ������ �ִ� �ڽ� Ű �迭�� ��ȯ�Ѵ�.
	 * 
	 * @param key
	 *            Ű ���´� a.b.c �� ��� a�� �ڽ� b, b�� �ڽ� c, c�� �ڽ��� Ű �迭�� ��ȯ.
	 * @return Ű�� �ڽ��� �迭
	 */
	public String[] getChildrenKeys(String key) {
		TreeProperties parent = getHierarchy(this, key);
		return parent.getChildrenKeysInMap();
	}

	/**
	 * Properties�� Ű�� �������� Hierarchy�� �����Ͽ� �����Ѵ�. Hierarchy�� �����ϱ� ���ؼ��� Ű�� '.'��
	 * �������� �����Ͽ� �����Ѵ�.
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
	 * boolean������ Ű���� �����´�.
	 * 
	 * @param key
	 *            Ű
	 * @return boolean ��
	 */
	public boolean getBoolean(String key) {
		try {
			return (new Boolean(getString(key))).booleanValue();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * ���������� Ű���� �����´�.
	 * 
	 * @param key
	 *            Ű
	 * @return int ��
	 */
	public int getInt(String key) {
		try {
			return Integer.parseInt(getString(key));
		} catch (Exception e) {
			return 0;
		}

	}

	/**
	 * long������ Ű���� �����´�.
	 * 
	 * @param key
	 *            Ű
	 * @return long ��
	 */
	public long getLong(String key) {
		try {
			return Long.parseLong(getString(key));
		} catch (Exception e) {
			return 0;
		}

	}

	/**
	 * ���ڿ��� Ű���� �����´�. Ű ���� ���� ��� �Է��� �⺻���� �����´�.
	 * 
	 * @param key
	 *            config Ű �̸�, defaultValue �⺻��
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
	 * ���������� Ű���� �����´�. Key�� ���ų�, �������� �ƴѰ�� �⺻���� �����´�.
	 * 
	 * @return int
	 * @param key
	 *            config Ű �̸�, defaultValue �⺻��
	 */
	public int getInt(String key, int defaultVal) {
		try {
			return Integer.parseInt(getString(key));
		} catch (Exception ex) {
			return defaultVal;
		}
	}

	/**
	 * long������ Ű���� �����´�. Key�� ���ų�, long���� �ƴѰ�� �⺻���� �����´�.
	 * 
	 * @return int
	 * @param key
	 *            config Ű �̸�, defaultValue �⺻��
	 */
	public long getLong(String key, long defaultVal) {
		try {
			return Long.parseLong(getString(key));
		} catch (Exception ex) {
			return defaultVal;
		}
	}

	/**
	 * boolean ������ Ű���� �����´�. Key�� ���ų� �߸��� ������ ��� �⺻���� �����´�.
	 * 
	 * @return Boolean
	 * @param key
	 *            config Ű �̸�, defaultValue �⺻��
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
	 * ���ڿ��� Ű ���� �����´�.
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
