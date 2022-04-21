package org.vut.kry.ca.entities;
import java.security.PrivateKey;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;


public class CSRWithPrivKey extends CSR
{
	private final PrivateKey privateKey;

	public CSRWithPrivKey(final PKCS10CertificationRequest request, final PrivateKey privateKey) throws PEMException
	{
	    super(request);
	    this.privateKey = privateKey;
	}

	public PrivateKey GetPrivateKey() {
	    return privateKey;
	}
}
