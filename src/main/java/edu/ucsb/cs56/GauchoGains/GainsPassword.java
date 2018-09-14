/*
 * Maga Kim and Howard Lin
 * GauchoGains Web App
 * MongoDB Implementation
 * UCSB CS56 Summer 2018
 *
 * https://howtodoinjava.com/security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/#PBKDF2WithHmacSHA1
 */
package edu.ucsb.cs56.GauchoGains;

import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class GainsPassword {
	private String saltString;
	private String hashString;

	public GainsPassword(String password) {
		try {
			int iterations = 1000;
			char[] chars = password.toCharArray();
			byte[] salt = getSalt();
			PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
			SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			byte[] hash = skf.generateSecret(spec).getEncoded();

			this.saltString = toHex(salt);
			this.hashString = toHex(hash);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {

		}
	}

	public static boolean checkPassword(String password, String saltString, String hashString) {
		try {
			int iterations = 1000;
			byte[] salt = fromHex(saltString);
			byte[] hash = fromHex(hashString);

			PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, hash.length * 8);
			SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			byte[] testHash = skf.generateSecret(spec).getEncoded();

			int diff = hash.length ^ testHash.length;
			for(int i = 0; i < hash.length && i < testHash.length; i++)
			{
				diff |= hash[i] ^ testHash[i];
			}
			return diff == 0;
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			return false;
		}
	}	

	private static byte[] getSalt() throws NoSuchAlgorithmException {
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		byte[] salt = new byte[16];
		sr.nextBytes(salt);
		return salt;
	}

	private static String toHex(byte[] arr) throws NoSuchAlgorithmException {
		java.math.BigInteger bigInt = new java.math.BigInteger(1, arr);
		String hex = bigInt.toString(16);
		int padLength = (arr.length*2) - hex.length();
		if (padLength > 0)
		{
			return String.format("%0" + padLength + "d", 0) + hex;
		} else {
			return hex;
		}
	}
	private static byte[] fromHex(String hex) throws NoSuchAlgorithmException
	{
		byte[] bytes = new byte[hex.length() / 2];
		for(int i = 0; i<bytes.length ;i++)
		{
			bytes[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
		}
		return bytes;
	}
	public String getHashString() {
		return this.hashString;
	}
	public String getSaltString() {
		return this.saltString;
	}
}
