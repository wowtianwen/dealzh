package com.junhong.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptionUtil {

	public static String md5(String clearString) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(clearString.getBytes(), 0, clearString.length());
			// byte[] encrypedByte = md.digest();
			return new BigInteger(1, md.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;

	}

}
