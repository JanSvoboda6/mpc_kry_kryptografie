package org.vut.kry.ca;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.vut.kry.ca.entities.CSRWithPrivateKey;
import org.vut.kry.ca.entities.DistinguishedName;
import org.vut.kry.ca.misc.KeyUtils;


/**
 * Class for creating a Certificate Signing Request.
 */
public class CSRBuilder
{
	// The type of algorithm used for signing process.
	private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

	/**
	 * 
	 * @param dn  Information about the subject.
	 * @return  Object with the complete Certificate Signing Request.
	 */
	public CSRWithPrivateKey generateRequest(final DistinguishedName dn) throws NoSuchAlgorithmException, OperatorCreationException, PEMException
	{
	    final KeyPair pair = KeyUtils.generateKeyPair();
        final PrivateKey privateKey = pair.getPrivate();
	    final PublicKey publicKey = pair.getPublic();
	    final X500Name x500Name = dn.getX500Name();
	    final ContentSigner signGen = new JcaContentSignerBuilder(SIGNATURE_ALGORITHM).build(privateKey);
	    final PKCS10CertificationRequestBuilder builder = new JcaPKCS10CertificationRequestBuilder(x500Name, publicKey);
	    final PKCS10CertificationRequest csr = builder.build(signGen);
	    return new CSRWithPrivateKey(csr, privateKey);
	}
}
