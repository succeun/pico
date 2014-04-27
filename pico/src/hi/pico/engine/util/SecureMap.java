package hi.pico.engine.util;

import java.math.BigInteger;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 일반적인 Map과 달리, Key 및 Value를 모두 암호화 보관함으로서,
 * 메모리 덤프를 방지한다.
 * 내부적으로, 단방향 암호화(SHA-256)를 통해 랜덤키를 암호화 하며 
 * 대칭키 암호화(AES)를 이용하여 Key와 Value를 암호화 보관한다.  
 * @author Eun Jeong-Ho
 */
public class SecureMap extends LinkedHashMap<String, String> {
	private static final long serialVersionUID = -3668305692332662939L;
	
	private Key skey = null;
	
	public SecureMap() {
		this(Long.toString(System.currentTimeMillis()), null);
	}
	
	public SecureMap(byte[] salt) {
		this(Long.toString(System.currentTimeMillis()), salt);
	}
	     
	public SecureMap(String secureKey) {
		this(secureKey, null);
	}
	
	public SecureMap(String secureKey, byte[] salt) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.reset();
			// 원본vs암호화 매핑사전(디셔너리)을 이용하여 알아낼수 있으므로, 
			// 알려지지 않는 난수(salt)를 추가하여 더욱더 어렵게 만듬 
			if (salt != null) {
				md.update(salt);
			}
			byte[] tmp = md.digest(new BigInteger(secureKey, 16).toByteArray());
			// digest를 1000번 이상 반복하여, 복호화를 더욱더 어렵게 만듬
			for (int i = 0; i < 1000; i++) {
				md.reset();
				tmp = md.digest(tmp);
			}
			
			byte[] b = new byte[16];
			System.arraycopy(tmp, 0, b, 0, 16);
			skey = new SecretKeySpec(b, "AES");
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	protected String encryptString(Object value) {
		if (value == null)
			return null;
		else
			return encryptString(value.toString());
	}
	
	protected String decryptString(Object value) {
		if (value == null)
			return null;
		else
			return decryptString(value.toString());
	}
	
	protected String encryptString(String value) {
		if (value == null || value.length() <= 0) {
			return value;
		}
		byte[] b = encrypt(value); 
		return new BigInteger(b).toString(16);
	}
	
	protected String decryptString(String encrypted) {
		if (encrypted == null || encrypted.length() <= 0) {
			return encrypted;
		}
		byte[] b = new BigInteger(encrypted, 16).toByteArray();
		return new String(decrypt(b));
	}

	protected byte[] encrypt(String value) {
		byte[] encrypted = null;
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			byte[] iv = new byte[cipher.getBlockSize()];

			IvParameterSpec ivParams = new IvParameterSpec(iv);
			cipher.init(Cipher.ENCRYPT_MODE, skey, ivParams);
			encrypted = cipher.doFinal(value.getBytes());

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return encrypted;
	}
	
	protected byte[] decrypt(byte[] encrypted) {
		byte[] original = null;
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			byte[] ivByte = new byte[cipher.getBlockSize()];
			IvParameterSpec ivParamsSpec = new IvParameterSpec(ivByte);
			cipher.init(Cipher.DECRYPT_MODE, skey, ivParamsSpec);
			original = cipher.doFinal(encrypted);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return original;
	}
	
	@Override
	public String get(Object key) {
		return decryptString(super.get(encryptString(key)));
	}

	@Override
	public String put(String key, String value) {
		String k = encryptString(key);
		String v = encryptString(value);
		return super.put(k, v);
	}

	@Override
	public void putAll(Map<? extends String, ? extends String> m) {
		Iterator<? extends String> itr = m.keySet().iterator();
		while (itr.hasNext()) {
			String key = itr.next();
			String value = m.get(key);
			put(key, value);
		}
	}
	
	@Override
	public Set<String> keySet() {
		Set<String> keys = super.keySet();
		Set<String> keySet = new HashSet<String>();
		Iterator<String> itr = keys.iterator();
		while(itr.hasNext()) {
			keySet.add(decryptString(itr.next()));
		}
		return keySet;
	};

	@Override
	public Collection<String> values() {
		Collection<String> col =  super.values();
		Collection<String> values = new ArrayList<String>();
		Iterator<String> itr = col.iterator();
		while (itr.hasNext()) {
			values.add(decryptString(itr.next()));
		}
		return values;
	}

	@Override
	public boolean containsKey(Object key) {
		return super.containsKey(encryptString(key));
	}

	@Override
	public boolean containsValue(Object value) {
		return super.containsValue(encryptString(value));
	}

	@Override
	public Set<Map.Entry<String, String>> entrySet() {
		final Set<Map.Entry<String, String>> entries = super.entrySet();
		
		return new AbstractSet<Map.Entry<String, String>>() {

			@Override
			public Iterator<Map.Entry<String, String>> iterator() {
				final Iterator<Map.Entry<String, String>> itr = entries.iterator();
				return new Iterator<Map.Entry<String, String>>() {

					public boolean hasNext() {
						return itr.hasNext();
					}
					
					public Map.Entry<String, String> next() {
						Map.Entry<String, String> e = itr.next();
						SimpleEntry<String, String> se = new SimpleEntry<String, String>(
									decryptString(e.getKey()), decryptString(e.getValue()));
						return se;
					}

					public void remove() {
						itr.remove();
					}
				};
			}

			@Override
			public int size() {
				return entries.size();
			}

		};
	}
	
	public static class SimpleEntry<K, V> implements Entry<K, V>, java.io.Serializable {
		private static final long serialVersionUID = -8849827112218397272L;
		
		private final K key;
		private V value;

		public SimpleEntry(K key, V value) {
			this.key = key;
			this.value = value;
		}

		public SimpleEntry(Entry<? extends K, ? extends V> entry) {
			this.key = entry.getKey();
			this.value = entry.getValue();
		}

		public K getKey() {
			return key;
		}

		public V getValue() {
			return value;
		}

		public V setValue(V value) {
			V oldValue = this.value;
			this.value = value;
			return oldValue;
		}
		
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Map.Entry<?,?>))
				return false;
			Map.Entry<?,?> e = (Map.Entry<?,?>) o;
			return eq(key, e.getKey()) && eq(value, e.getValue());
		}
		
	    private static boolean eq(Object o1, Object o2) {
	        return o1 == null ? o2 == null : o1.equals(o2);
	    }
		
		@Override
		public int hashCode() {
			return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
		}
		
		@Override
		public String toString() {
			return key + "=" + value;
		}

	}
	 

	
	public static void main(String[] args) {
		//SecureMap map = new SecureMap(new byte[]{1,23,35,4,5,6});
		SecureMap map = new SecureMap();
		map.put("t1", "s1");
		map.put("t2", "s2");
		System.out.println(map.containsKey("t1"));
		System.out.println(map.containsValue("s2"));
		System.out.println(map.containsKey("aaaaa"));
		System.out.println(map.containsValue("aaaaa"));
		System.out.println(map.get("t1"));
		System.out.println(map.get("t2"));
		System.out.println(map.values());
		System.out.println(map.keySet());
		
		System.out.println(map);
		
		Map<String, String> m = new HashMap<String, String>();
		m.put("t3", "s3");
		m.put("t4", "s4");
		map.putAll(m);
		System.out.println(map);
		System.out.println(map.values());
		System.out.println(map.keySet());
	}
}
