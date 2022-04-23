package org.vut.kry.ca.entities;
import java.security.PrivateKey;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;


/**
 * Only extends the CSR class with private keys.
 */
public class CSRWithPrivateKey extends CSR
{
	private final PrivateKey privateKey;

	public CSRWithPrivateKey(final PKCS10CertificationRequest request, final PrivateKey privateKey) throws PEMException
	{
	    super(request);
	    this.privateKey = privateKey;
	}

	public PrivateKey getPrivateKey() {
	    return privateKey;
	}
}
