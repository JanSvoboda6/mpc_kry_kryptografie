package org.vut.kry.ca.entities;
import java.security.PublicKey;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;


/**
 * Class that holds information about the Client's Certificate Signing Request.
 */
public class CSR {
	// The information about the client.
	private final DistinguishedName dn;
	
	private final PublicKey publicKey;

	public CSR(final PKCS10CertificationRequest request) throws PEMException
	{
	    dn = new DistinguishedName(request.getSubject());
	    publicKey = new JcaPEMKeyConverter().getPublicKey(request.getSubjectPublicKeyInfo());
	}

	public DistinguishedName getSubject()
	{
	    return dn;
	}

	public PublicKey getPublicKey()
	{
	    return publicKey;
	}
}
