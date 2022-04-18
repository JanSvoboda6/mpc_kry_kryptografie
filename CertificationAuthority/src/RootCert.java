import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import org.bouncycastle.cert.X509CertificateHolder;

public class RootCert extends CertWithPrivKey
{
	static final String KEYSTORE_TYPE = "PKCS12";

	private final X509CertificateHolder caCertificateHolder;

	RootCert(final X509Certificate caCertificate, final PrivateKey caPrivateKey) throws CertificateEncodingException, IOException
	{
		super(caCertificate, caPrivateKey);
	    this.caCertificateHolder = new X509CertificateHolder(caCertificate.getEncoded());
	}

	public Signer SignCsr(final CSR request)
	{
	    final KeyPair pair = new KeyPair(GetX509Certificate().getPublicKey(), GetPrivateKey());
	    final DistinguishedName signerSubject = new DistinguishedName(caCertificateHolder.getSubject());
	    return new Signer(pair, signerSubject, request.GetPublicKey(), request.GetSubject());
	}
}
