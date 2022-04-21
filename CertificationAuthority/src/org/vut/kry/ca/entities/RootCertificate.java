package org.vut.kry.ca.entities;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import org.bouncycastle.cert.X509CertificateHolder;
import org.vut.kry.ca.Signer;


public class RootCertificate extends CertificateWithPrivKey
{
	static final String KEYSTORE_TYPE = "PKCS12";

	private final X509CertificateHolder caCertificateHolder;

	public RootCertificate(final X509Certificate caCertificate, final PrivateKey caPrivateKey) throws CertificateEncodingException, IOException
	{
		super(caCertificate, caPrivateKey);
	    this.caCertificateHolder = new X509CertificateHolder(caCertificate.getEncoded());
	}

	public Signer signCSR(final CSR request)
	{
	    final KeyPair pair = new KeyPair(getX509Certificate().getPublicKey(), getPrivateKey());
	    final DistinguishedName signerSubject = new DistinguishedName(caCertificateHolder.getSubject());
	    return new Signer(pair, signerSubject, request.getPublicKey(), request.getSubject());
	}
}
