package org.vut.kry.ca.misc;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class KeyUtils
{
	private static final String ALGORITHM = "RSA";
	private static final int DEFAULT_KEY_SIZE = 2048;

	public static KeyPair generateKeyPair() throws NoSuchAlgorithmException
	{
	    return generateKeyPair(DEFAULT_KEY_SIZE);
	}

	public static KeyPair generateKeyPair(final int keySize) throws NoSuchAlgorithmException
	{
	    final KeyPairGenerator gen = KeyPairGenerator.getInstance(ALGORITHM);
	    gen.initialize(keySize);
	    return gen.generateKeyPair();
	}
}
