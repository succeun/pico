package hi.pico.engine.util;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {
	private static final String KEY = "hdmf0082";
	private static Key skey = null;
	
	static {
		//init(KEY, new byte[]{1,2,3,4});
		init(KEY);
	}
	
	public static void init(String secureKey) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.reset();
			byte[] tmp = md.digest(secureKey.getBytes());
			byte[] b = new byte[16];
			System.arraycopy(tmp, 0, b, 0, 16);
			skey = new SecretKeySpec(b, "AES");
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public static void init(String secureKey, byte[] salt) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.reset();
			if (salt != null) {
				md.update(salt);
			}
			byte[] tmp = md.digest(secureKey.getBytes());
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
	
	public static String encryptString(String value) {
		if (value == null || value.length() <= 0) {
			return value;
		}
		byte[] b = encrypt(value); 
		return byteArrayToHex(b);//encodeBase64(b);
	}
	
	public static String decryptString(String encrypted) {
		if (encrypted == null || encrypted.length() <= 0) {
			return encrypted;
		}
		byte[] b = hexToByteArray(encrypted);//decodeBase64(encrypted);
		return new String(decrypt(b));
	}

	private static byte[] encrypt(String value) {
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
	
	private static byte[] decrypt(byte[] encrypted) {
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
	
	// Base64
	private final static char[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
	private static int[] toInt = new int[128];

	static {
		for (int i = 0; i < ALPHABET.length; i++) {
			toInt[ALPHABET[i]] = i;
		}
	}

	private static String encodeBase64(byte[] buf) {
		int size = buf.length;
		char[] ar = new char[((size + 2) / 3) * 4];
		int a = 0;
		int i = 0;
		while (i < size) {
			byte b0 = buf[i++];
			byte b1 = (i < size) ? buf[i++] : 0;
			byte b2 = (i < size) ? buf[i++] : 0;

			int mask = 0x3F;
			ar[a++] = ALPHABET[(b0 >> 2) & mask];
			ar[a++] = ALPHABET[((b0 << 4) | ((b1 & 0xFF) >> 4)) & mask];
			ar[a++] = ALPHABET[((b1 << 2) | ((b2 & 0xFF) >> 6)) & mask];
			ar[a++] = ALPHABET[b2 & mask];
		}
		switch (size % 3) {
		case 1:
			ar[--a] = '=';
		case 2:
			ar[--a] = '=';
		}
		return new String(ar);
	}

	private static byte[] decodeBase64(String s) {
		int delta = s.endsWith("==") ? 2 : s.endsWith("=") ? 1 : 0;
		byte[] buffer = new byte[s.length() * 3 / 4 - delta];
		int mask = 0xFF;
		int index = 0;
		for (int i = 0; i < s.length(); i += 4) {
			int c0 = toInt[s.charAt(i)];
			int c1 = toInt[s.charAt(i + 1)];
			buffer[index++] = (byte) (((c0 << 2) | (c1 >> 4)) & mask);
			if (index >= buffer.length) {
				return buffer;
			}
			int c2 = toInt[s.charAt(i + 2)];
			buffer[index++] = (byte) (((c1 << 4) | (c2 >> 2)) & mask);
			if (index >= buffer.length) {
				return buffer;
			}
			int c3 = toInt[s.charAt(i + 3)];
			buffer[index++] = (byte) (((c2 << 6) | c3) & mask);
		}
		return buffer;
	}
	
	
	private static byte[] hexToByteArray(String hex) {
		// 16진 문자열을 byte 배열로 변환한다.
		if (hex == null || hex.length() % 2 != 0) {
			return new byte[] {};
		}

		byte[] bytes = new byte[hex.length() / 2];
		for (int i = 0; i < hex.length(); i += 2) {
			byte value = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
			bytes[(int) Math.floor(i / 2)] = value;
		}
		return bytes;
	}
	
	private static String byteArrayToHex(byte[] b) {
		if (b == null || b.length == 0) {
			return null;
		}
		
		StringBuilder sb = new StringBuilder(b.length * 2);
		String hexNumber;
		for (int x = 0; x < b.length; x++) {
			hexNumber = "0" + Integer.toHexString(0xff & b[x]);
			sb.append(hexNumber.substring(hexNumber.length() - 2));
		}
		return sb.toString();
	}

	public static void main(String[] args) throws Exception {
		String[] arrs = new String[]{"IA0935", "IA0327", "1234567890qazwsxedcrfvtgbyhnujmiklop"};
		for (String s : arrs) {
			String enc = AESUtil.encryptString(s);
			System.out.println(enc);
			System.out.println(AESUtil.decryptString(enc));
		}
		
		for (String s : arrs) {
			String enc = AESUtil.encodeBase64(s.getBytes());
			System.out.println(enc);
		}
	}

}
