package org.vut.kry.ca.entities;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import org.bouncycastle.openssl.jcajce.JcaPEMWriter;


/**
 * Class that holds the parameters of the certificate. Has methods to print and save the certificate to the filesystem.
 * Using a abbreviation because the BouncyCastle already has a class named Certificate - so they would be mistaken with each other.
 */
public class Cert {
	private final X509Certificate certificate;

	public Cert(final X509Certificate certificate)
	{
	    this.certificate = certificate;
	}

	public X509Certificate getX509Certificate()
	{
		return certificate;
	}

	public String print() throws IOException
	{
	    final StringWriter sw = new StringWriter();
	    JcaPEMWriter writer = new JcaPEMWriter(sw);
	    writer.writeObject(certificate);
	    writer.flush();
	    return sw.toString();
	}

	public void save(final File file) throws IOException
	{
		BufferedWriter fw = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8, StandardOpenOption.CREATE);
	    JcaPEMWriter writer = new JcaPEMWriter(fw);
	    writer.writeObject(certificate);
	    writer.flush();
	}

	public void save(final String fileName) throws IOException
	{
	    final File file = new File(fileName);
	    save(file);
	}

	public CertificateWithPrivKey attachPrivateKey(PrivateKey privateKey)
	{
	    return new CertificateWithPrivKey(certificate, privateKey);
	}
}