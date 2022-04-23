package org.vut.kry.ca.entities;
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


/**
 * A class that handles the saving of the certificate in the PKCS12 format.
 * PKCS12 is essentially a container that can contain a certificate and private keys. These files can be encrypted using a secret password.
 * Only a extension of the Cert class, enabling to save the private keys as well.
 */
public class CertificateWithPrivKey extends Cert
{
	static final String KEYSTORE_TYPE = "PKCS12";
	private final PrivateKey privateKey;

	public CertificateWithPrivKey(final X509Certificate certificate, final PrivateKey privateKey)
	{
		super(certificate);
		this.privateKey = privateKey;
	}

	/**
	 * Adds the certificate + private key to the PKCS12 container. No password is set.
	 * @param keyStore  The keystore, that is used.
	 * @param alias  For which entuty is this container generated for.
	 * @return  Container with the certificate + priv. key.
	 */
	public KeyStore addToKeystore(KeyStore keyStore, String alias) throws KeyStoreException
	{
		final X509Certificate certificate = getX509Certificate();
		final Certificate[] chain = new Certificate[] { certificate };
		keyStore.setKeyEntry(alias, privateKey, null, chain);

		return keyStore;
	}

	public KeyStore saveInPkcs12Keystore(final String alias) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException
	{
		// Init the java keystore. A class that handles the Java key storage.
		final KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
		keyStore.load(null, null);

		addToKeystore(keyStore, alias);

		return keyStore;
	}

	/**
	 * Calls the exportPkcs12(final String keystorePath, final char[] keystorePassword, final String alias), the only difference is that this class creates a file if it doesn't exist in the first place.
	 * @param keystorePath  A path to the file that contains the PKCS12 container.
	 * @param keystorePassword  A possible password that is used to the keystore.
	 * @param alias  A string that identifies the entity for which a certificate is stored.
	 */
	public void exportPkcs12(final String keystorePath, final char[] keystorePassword, final String alias) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException
	{
		final File file = new File(keystorePath);
		exportPkcs12(file, keystorePassword, alias);
	}

	/**
	 * A method that exports the certificate into a PKCS12 container.
	 * @param keystoreFile  A file with the PKCS12 container.
	 * @param keystorePassword  A possible password that is used to the keystore.
	 * @param alias  A string that identifies the entity for which a certificate is stored.
	 */
	public void exportPkcs12(final File keystoreFile, final char[] keystorePassword, final String alias) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException
	{
		final KeyStore keyStore;
		if (keystoreFile.exists() && keystoreFile.isFile())
		{
			// Load existing keystore
			keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
			InputStream stream = new FileInputStream(keystoreFile);
			keyStore.load(stream, keystorePassword);
			addToKeystore(keyStore, alias);
		} else {
			keyStore = saveInPkcs12Keystore(alias);
		}
      
		OutputStream stream = new FileOutputStream(keystoreFile);
		keyStore.store(stream, keystorePassword);
	}

	public PrivateKey getPrivateKey()
	{
		return privateKey;
	}

	public String printKey() throws IOException
	{
		final StringWriter sw = new StringWriter();
		JcaPEMWriter writer = new JcaPEMWriter(sw);
		writer.writeObject(privateKey);
		writer.flush();
		return sw.toString();
	}

	public void saveKey(File file) throws IOException
	{
		BufferedWriter fw = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        JcaPEMWriter writer = new JcaPEMWriter(fw);
        writer.writeObject(privateKey);
        writer.flush();
	}

	public void saveKey(String fileName) throws IOException
	{
		final File file = new File(fileName);
		saveKey(file);
	}
}