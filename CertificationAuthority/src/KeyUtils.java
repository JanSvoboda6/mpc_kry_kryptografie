import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class KeyUtils
{
	private static final String ALGORITHM = "RSA";
	private static final int DEFAULT_KEY_SIZE = 2048;

	static KeyPair GenerateKeyPair() throws NoSuchAlgorithmException
	{
	    return GenerateKeyPair(DEFAULT_KEY_SIZE);
	}

	static KeyPair GenerateKeyPair(final int keySize) throws NoSuchAlgorithmException
	{
	    final KeyPairGenerator gen = KeyPairGenerator.getInstance(ALGORITHM);
	    gen.initialize(keySize);
	    return gen.generateKeyPair();
	}
}
