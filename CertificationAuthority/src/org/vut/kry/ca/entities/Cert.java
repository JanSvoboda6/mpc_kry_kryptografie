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


public class Cert {
	private final X509Certificate certificate;

	public Cert(final X509Certificate certificate)
	{
	    this.certificate = certificate;
	}

	public X509Certificate GetX509Certificate()
	{
		return certificate;
	}

	public String Print() throws IOException
	{
	    final StringWriter sw = new StringWriter();
	    JcaPEMWriter writer = new JcaPEMWriter(sw);
	    writer.writeObject(certificate);
	    writer.flush();
	    return sw.toString();
	}

	public void Save(final File file) throws IOException
	{
		BufferedWriter fw = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8, StandardOpenOption.CREATE);
	    JcaPEMWriter writer = new JcaPEMWriter(fw);
	    writer.writeObject(certificate);
	    writer.flush();
	}

	public void Save(final String fileName) throws IOException
	{
	    final File file = new File(fileName);
	    Save(file);
	}

	public CertificateWithPrivKey AttachPrivateKey(PrivateKey privateKey)
	{
	    return new CertificateWithPrivKey(certificate, privateKey);
	}
}