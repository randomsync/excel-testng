package net.randomsync.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class CryptoUtils {
	private static final String key = "5E834D5C3FD3DB60"; // hardcoded key and
															// initialization
															// vector
	private static final String ivx = "FD6E2173066B41A4";

	private static String encrypt(String message)
			throws UnsupportedEncodingException {

		SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
		IvParameterSpec ivSpec = new IvParameterSpec(ivx.getBytes());

		Cipher cipher;
		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Invalid Algorithm", e);
		} catch (NoSuchPaddingException e) {
			throw new RuntimeException("Invalid Padding", e);
		}
		try {
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
		} catch (InvalidKeyException e) {
			throw new RuntimeException("Invalid Key", e);
		} catch (InvalidAlgorithmParameterException e) {
			throw new RuntimeException("Invalid Algorithm Parameter.", e);
		}

		// Gets the raw bytes to encrypt, UTF8
		byte[] stringBytes = message.getBytes("UTF8");

		// encrypt using the cipher
		byte[] raw;
		try {
			raw = cipher.doFinal(stringBytes);
		} catch (IllegalBlockSizeException e) {
			throw new RuntimeException(e);
		} catch (BadPaddingException e) {
			throw new RuntimeException(e);
		}

		// return base64
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(raw);
	}

	public static String decrypt(String encrypted) {
		SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
		IvParameterSpec ivSpec = new IvParameterSpec(ivx.getBytes());
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Invalid Algorithm", e);
		} catch (NoSuchPaddingException e) {
			throw new RuntimeException("Invalid Padding", e);
		}
		try {
			cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
		} catch (InvalidKeyException e) {
			throw new RuntimeException("Invalid Key", e);
		} catch (InvalidAlgorithmParameterException e) {
			throw new RuntimeException("Invalid Algorithm Parameter.", e);
		}
		// decode the BASE64 coded message
		BASE64Decoder decoder = new BASE64Decoder();
		byte[] raw;
		try {
			raw = decoder.decodeBuffer(encrypted);
		} catch (IOException e) {
			throw new RuntimeException("Reading error", e);
		}

		// decode the message
		byte[] stringBytes;
		try {
			stringBytes = cipher.doFinal(raw);
		} catch (IllegalBlockSizeException e) {
			throw new RuntimeException("Encrypted message was corrupted", e);
		} catch (BadPaddingException e) {
			throw new RuntimeException("Encrypted message was corrupted", e);
		}

		// converts the decoded message to a String
		String clear;
		try {
			clear = new String(stringBytes, "UTF8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return clear;
	}

	public static void main(String[] args) {
		String encrypted;
		if (args.length < 1) {
			System.out
					.println("Usage: java CryptoUtils [message1] <[message2] ... [messageN]>");
			System.out
					.print("Please provide at least 1 message to encrypt\n>");
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			try {
				args = in.readLine().split(" ");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Clear Message\t\tEncrypted Message");
		System.out.println("-------------\t\t-----------------");
		for (int i = 0; i < args.length; i++) {
			System.out.print(args[i] + "\t\t");
			try {
				encrypted = encrypt(args[i]);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return;
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			System.out.println(encrypted);

			/*
			 * String decrypted; try { decrypted = decrypt(encrypted); } catch
			 * (Exception e) { e.printStackTrace(); return; }
			 * System.out.println("\t\t" + decrypted);
			 */

		}

	}

}
