import java.security.PublicKey;

import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

public class CSR {
	private final DistinguishedName dn;
	private final PublicKey publicKey;

	public CSR(final PKCS10CertificationRequest request) throws PEMException
	{
	    dn = new DistinguishedName(request.getSubject());
	    publicKey = new JcaPEMKeyConverter().getPublicKey(request.getSubjectPublicKeyInfo());
	}

	public DistinguishedName GetSubject()
	{
	    return dn;
	}

	public PublicKey GetPublicKey()
	{
	    return publicKey;
	}
}
