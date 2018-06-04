package com.erlangshen.sdk;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Base64;

public class SecretUtil {

	/**
	 * 传入字符串生成MD5加密码
	 * @param content 密码明文
	 * @return 密码密文
	 */
	public static String md5(String content) throws Exception {
		if (null == content || "".equals(content)) {
			return "";
		}

		// 加密过得直接返回
		if (content.length() == 32) {
			return content;
		}

		MessageDigest messageDigest;
		try {
			// 生成一个MD5加密计算摘要
			messageDigest = MessageDigest.getInstance("MD5");

			// 计算md5函数
			messageDigest.update(content.getBytes());
			// digest()最后确定返回md5 hash值，返回值为8位字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
			// BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
			String pwd = new BigInteger(1, messageDigest.digest()).toString(16);
			return pwd.toUpperCase();
		} catch (Exception e) {
			throw new Exception("md5生成错误：" + e.getMessage());
		}
	}

	/**
	 * HmacSHA1加密
	 * @param secret
	 * @param content
     * @return
     */
	public static String hmacsha1(String secret, String content) throws Exception {
		try {
			SecretKey secretKey = new SecretKeySpec(secret.getBytes("UTF8"), "HmacSHA1");
			Mac mac = Mac.getInstance(secretKey.getAlgorithm());
			mac.init(secretKey);
			byte[] digest = mac.doFinal(content.getBytes("UTF-8"));
			return byteArrayToHexString(digest);
		} catch (Exception e) {
			throw new Exception("加密异常");
		}
	}

	private static String byteArrayToHexString(byte[] b) {
		StringBuilder hs = new StringBuilder();

		for (int n = 0; b!=null && n < b.length; n++) {
			String stmp = Integer.toHexString(b[n] & 0XFF);

			if (stmp.length() == 1) {
				hs.append('0');
			}

			hs.append(stmp);
		}

		return hs.toString();

	}

	/**
	 * base64加密
	 * @param content
     * @return
     */
	public static String base64Encrypt(String content) throws Exception {
		try {
			return Base64.getUrlEncoder().encodeToString(content.getBytes("utf-8"));
		} catch (Exception e) {
			throw new Exception("base64加密错误：" + e.getMessage());
		}
	}

}
