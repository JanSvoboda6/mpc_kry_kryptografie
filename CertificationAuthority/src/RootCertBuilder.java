import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;

import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.operator.OperatorCreationException;

public class RootCertBuilder
{
	private String crlUri = null;

	private KeyPair pair;
	private Signer signer;

	RootCertBuilder(final DistinguishedName subject)
	{
		try {
			pair = KeyUtils.GenerateKeyPair();
			signer = new Signer(pair, subject, pair.getPublic(), subject).SetRandomSerialNumber();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public RootCertBuilder SetNotBefore(final ZonedDateTime notBefore)
	{
		signer.SetNotBefore(notBefore);
		return this;
	}

	public RootCertBuilder SetNotAfter(final ZonedDateTime notAfter)
	{
		signer.SetNotAfter(notAfter);
		return this;
	}

	public RootCertBuilder ValidDuringYears(final int years)
	{
		signer.ValidDuringYears(years);
		return this;
	}

	public RootCertBuilder SetCrlUri(final String crlUri)
	{
		this.crlUri = crlUri;
		return this;
	}

	public RootCert Build(final String subjectAlternativeName) throws InvalidKeyException, OperatorCreationException, NoSuchAlgorithmException, CertificateException, NoSuchProviderException, SignatureException, IOException
	{
		signer.AddExtension(KeyExtension.Create(
				KeyExtension.KeyUsage.KEY_CERT_SIGN,
				KeyExtension.KeyUsage.CRL_SIGN));

	    if (crlUri != null) {
	    	signer.AddExtension(DistributionPoint.Create(crlUri));
	    }

	    // This is a CA
	    signer.AddExtension(Extension.basicConstraints, false, new BasicConstraints(true));

	    final X509Certificate rootCertificate = signer.Sign(subjectAlternativeName).GetX509Certificate();

	    return new RootCert(rootCertificate, pair.getPrivate());
	  }
}
