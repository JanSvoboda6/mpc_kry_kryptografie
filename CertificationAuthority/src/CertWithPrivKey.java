import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.bouncycastle.openssl.jcajce.JcaPEMWriter;


class CertWithPrivKey extends Cert
{
	static final String KEYSTORE_TYPE = "PKCS12";
	private final PrivateKey privateKey;

	CertWithPrivKey(final X509Certificate certificate, final PrivateKey privateKey)
	{
		super(certificate);
		this.privateKey = privateKey;
	}

	public KeyStore AddToKeystore(KeyStore keyStore, String alias) throws KeyStoreException
	{
		final X509Certificate certificate = GetX509Certificate();
		final Certificate[] chain = new Certificate[] { certificate };
		keyStore.setKeyEntry(alias, privateKey, null, chain);

		return keyStore;
	}

	public KeyStore SaveInPkcs12Keystore(final String alias) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException
	{
		// init keystore
		final KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
		keyStore.load(null, null);

		AddToKeystore(keyStore, alias);

		return keyStore;
	}

	public void ExportPkcs12(final String keystorePath, final char[] keystorePassword, final String alias) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException
	{
		final File file = new File(keystorePath);
		ExportPkcs12(file, keystorePassword, alias);
	}

	public void ExportPkcs12(final File keystoreFile, final char[] keystorePassword, final String alias) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException
	{
		final KeyStore keyStore;
		if (keystoreFile.exists() && keystoreFile.isFile())
		{
			// Load existing keystore
			keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
			InputStream stream = new FileInputStream(keystoreFile);
			keyStore.load(stream, keystorePassword);
			AddToKeystore(keyStore, alias);
		} else {
			keyStore = SaveInPkcs12Keystore(alias);
		}
      
		OutputStream stream = new FileOutputStream(keystoreFile);
		keyStore.store(stream, keystorePassword);
	}

	public PrivateKey GetPrivateKey()
	{
		return privateKey;
	}

	public String PrintKey() throws IOException
	{
		final StringWriter sw = new StringWriter();
		JcaPEMWriter writer = new JcaPEMWriter(sw);
		writer.writeObject(privateKey);
		writer.flush();
		return sw.toString();
	}

	public void SaveKey(File file) throws IOException
	{
		BufferedWriter fw = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        JcaPEMWriter writer = new JcaPEMWriter(fw);
        writer.writeObject(privateKey);
        writer.flush();
	}

	public void SaveKey(String fileName) throws IOException
	{
		final File file = new File(fileName);
		SaveKey(file);
	}
}