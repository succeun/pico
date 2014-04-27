package hi.pico.engine.util;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class RSAAuth {
	public static final String RSA_PRIVATE_KEY = "_RSA_PRIVATE_KEY_";

	public static final String DEFAULT_MODULUS_NAME = "modulus";
	public static final String DEFAULT_EXPONENT_NAME = "exponent";

	public static void encrypt(HttpServletRequest req) {
		encrypt(req, DEFAULT_MODULUS_NAME, DEFAULT_EXPONENT_NAME);
	}

	public static void encrypt(HttpServletRequest req, String modulusName, String exponentName) {
		try {
			KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
			int KEY_SIZE = 2048;
			generator.initialize(KEY_SIZE, new SecureRandom());

			KeyPair keyPair = generator.genKeyPair();
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");

			PublicKey publicKey = keyPair.getPublic();
			PrivateKey privateKey = keyPair.getPrivate();

			HttpSession session = req.getSession();

			// 세션에 공개키의 문자열을 키로하여 개인키를 저장한다.
			session.setAttribute(RSA_PRIVATE_KEY, privateKey);

			// 공개키를 문자열로 변환하여 JavaScript RSA 라이브러리 넘겨준다.
			RSAPublicKeySpec spec = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);

			String modulus = spec.getModulus().toString(16);
			String exponent = spec.getPublicExponent().toString(16);

			req.setAttribute(modulusName, modulus);
			req.setAttribute(exponentName, exponent);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static Map<String, String> decrypt(HttpServletRequest req) {
		Enumeration en = req.getParameterNames();
		List<String> nameList = new ArrayList<String>();
		while (en.hasMoreElements()) {
			String name = (String) en.nextElement();
			if (!name.startsWith("__") && !name.endsWith("__"))
				nameList.add(name);
		}

		String[] names = nameList.toArray(new String[0]);
		return decrypt(req, names);
	}

	public static Map<String, String> decrypt(HttpServletRequest req, String... parameterNames) {
		Map<String, String> params = new HashMap<String, String>();
		
		HttpSession session = req.getSession();
		PrivateKey privateKey = (PrivateKey) session.getAttribute(RSA_PRIVATE_KEY);
		// 키의 재사용을 막는다. 항상 새로운 키를 받도록 강제.
		session.removeAttribute(RSA_PRIVATE_KEY);

		if (privateKey == null) {
			throw new RuntimeException("Private Key 정보를 찾을 수 없습니다.");
		}

		try {
			if (parameterNames == null || parameterNames.length <= 0) {
				throw new RuntimeException("읽을 Parameter Names 정보를 정의하지 않았습니다.");
			}

			String[] values = new String[parameterNames.length];
			for (int i = 0; i < parameterNames.length; i++) {
				String securedValue = req.getParameter(parameterNames[i]);
				String value = null;
				try {
					value = decryptRSA(privateKey, securedValue);
				} catch (Exception e) {
					value = securedValue;
				}
				values[i] = value;
			}
			
			for (int i = 0; i < parameterNames.length; i++) {
				params.put(parameterNames[i], values[i]);
			}
			return params;
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static String decryptRSA(PrivateKey privateKey, String securedValue) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		byte[] encryptedBytes = hexToByteArray(securedValue);
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
		String decryptedValue = new String(decryptedBytes, "utf-8"); // 문자 인코딩
																		// 주의.
		return decryptedValue;
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
	
	private static String byteArrayToHex(byte[] ba) {
		if (ba == null || ba.length == 0) {
			return null;
		}
		
		StringBuilder sb = new StringBuilder(ba.length * 2);
		String hexNumber;
		for (int x = 0; x < ba.length; x++) {
			hexNumber = "0" + Integer.toHexString(0xff & ba[x]);
			sb.append(hexNumber.substring(hexNumber.length() - 2));
		}
		return sb.toString();
	}
	
	
	/*
	public static void distributePubKey() throws Exception {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		int KEY_SIZE = 2048;
		generator.initialize(KEY_SIZE, new SecureRandom());

		KeyPair pair = generator.generateKeyPair();
		Key pubKey = pair.getPublic();
		Key privKey = pair.getPrivate();

		// Send the public key bytes to the other party...
		byte[] publicKeyBytes = pubKey.getEncoded();

		// Convert Public key to String
		BASE64Encoder encoder = new BASE64Encoder();
		String pubKeyStr = encoder.encode(publicKeyBytes);
		
		System.out.println(pubKeyStr);

		// Convert PublicKeyString to Byte Stream
		BASE64Decoder decoder = new BASE64Decoder();
		byte[] sigBytes2 = decoder.decodeBuffer(pubKeyStr);

		// Convert the public key bytes into a PublicKey object
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(sigBytes2);
		KeyFactory keyFact = KeyFactory.getInstance("RSA");
		Key pubKey2 = keyFact.generatePublic(x509KeySpec);

		
		Cipher cipher = Cipher.getInstance("RSA");
		// encryption step
		cipher.init(Cipher.ENCRYPT_MODE, pubKey2, new SecureRandom());
		byte[] cipherText = cipher.doFinal("1234567890".getBytes());
		System.out.println("cipher: " + toHex(cipherText));

		// decryption step
		cipher.init(Cipher.DECRYPT_MODE, privKey);
		byte[] plainText = cipher.doFinal(cipherText);
		System.out.println("plain : " + new String(plainText));
	}
	
	private static String toHex(byte[] b) {
		return new BigInteger(b).toString(16);
	}
	
	*/
	
	/**
	 * Create a self-signed X.509 Certificate
	 * 1. 테스트
     * X509Certificate x509 = generateCertificate("CN=Test, L=London, C=GB", keyPair, 1, "SHA1withRSA");
     * byte[] publicKeyBytes = x509.getEncoded();
     * String publicKeyStr = Base64.encodeBase64String(publicKeyBytes);
     * req.setAttribute("publicKey", publicKeyStr);
     * 
	 * @param dn the X.509 Distinguished Name, eg "CN=Test, L=London, C=GB"
	 * @param pair the KeyPair
	 * @param days how many days from now the Certificate is valid for
	 * @param algorithm the signing algorithm, eg "SHA1withRSA"
	 */
	/*public X509Certificate generateCertificate(String dn, KeyPair pair, int days, String algorithm) throws GeneralSecurityException, IOException {
		PrivateKey privkey = pair.getPrivate();
		X509CertInfo info = new X509CertInfo();
		Date from = new Date();
		Date to = new Date(from.getTime() + days * 86400000l);
		CertificateValidity interval = new CertificateValidity(from, to);
		BigInteger sn = new BigInteger(64, new SecureRandom());
		X500Name owner = new X500Name(dn);

		info.set(X509CertInfo.VALIDITY, interval);
		info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(sn));
		info.set(X509CertInfo.SUBJECT, new CertificateSubjectName(owner));
		info.set(X509CertInfo.ISSUER, new CertificateIssuerName(owner));
		info.set(X509CertInfo.KEY, new CertificateX509Key(pair.getPublic()));
		info.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));
		AlgorithmId algo = new AlgorithmId(AlgorithmId.md5WithRSAEncryption_oid);
		info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(algo));

		// Sign the cert to identify the algorithm that's used.
		X509CertImpl cert = new X509CertImpl(info);
		cert.sign(privkey, algorithm);

		// Update the algorith, and resign.
		algo = (AlgorithmId) cert.get(X509CertImpl.SIG_ALG);
		info.set(CertificateAlgorithmId.NAME + "." + CertificateAlgorithmId.ALGORITHM, algo);
		cert = new X509CertImpl(info);
		cert.sign(privkey, algorithm);
		return cert;
	}*/
}
