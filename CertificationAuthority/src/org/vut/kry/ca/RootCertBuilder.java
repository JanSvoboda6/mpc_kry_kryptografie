package org.vut.kry.ca;
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
import org.bouncycastle.operator.OperatorCreationException;
import org.vut.kry.ca.entities.DistinguishedName;
import org.vut.kry.ca.entities.DistributionPoint;
import org.vut.kry.ca.entities.RootCertificate;
import org.vut.kry.ca.misc.KeyExtension;
import org.vut.kry.ca.misc.KeyUtils;


/**
 * A class that is used to build the Certificate Authority certificate.
 */
public class RootCertBuilder
{
	private String crlUri = null;

	private KeyPair pair;
	private Signer signer;

	public RootCertBuilder(final DistinguishedName subject)
	{
		try {
			// Generate private + public keys
			pair = KeyUtils.generateKeyPair();
			
			// Create a signer object that will be later used to build the certificate.
			signer = new Signer(pair, subject, pair.getPublic(), subject).setRandomSerialNumber();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public RootCertBuilder setNotBefore(final ZonedDateTime notBefore)
	{
		signer.setNotBefore(notBefore);
		return this;
	}

	public RootCertBuilder setNotAfter(final ZonedDateTime notAfter)
	{
		signer.setNotAfter(notAfter);
		return this;
	}

	public RootCertBuilder validDuringYears(final int years)
	{
		signer.validDuringYears(years);
		return this;
	}

	public RootCertBuilder setCrlUri(final String crlUri)
	{
		this.crlUri = crlUri;
		return this;
	}

	/**
	 * Method that will build the CA's certificate.
	 * @param subjectAlternativeName  A list of domains the certificate would be valid for.
	 * @return  CA's certificate.
	 */
	public RootCertificate build(final String subjectAlternativeName) throws InvalidKeyException, OperatorCreationException, NoSuchAlgorithmException, CertificateException, NoSuchProviderException, SignatureException, IOException
	{
		// Add the hits of usage to the certificate. Indicates for what purposes the certificate was built.
		signer.addExtension(KeyExtension.create(
				KeyExtension.KeyUsage.KEY_CERT_SIGN,
				KeyExtension.KeyUsage.CRL_SIGN));

	    if (crlUri != null) {
	    	signer.addExtension(DistributionPoint.create(crlUri));
	    }

	    // The signer object = Certification authority.
	    signer.addExtension(Extension.basicConstraints, false, new BasicConstraints(true));

	    // Sign and return the CA certificate.
	    final X509Certificate rootCertificate = signer.sign(subjectAlternativeName).getX509Certificate();
	    return new RootCertificate(rootCertificate, pair.getPrivate());
	  }
}
