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
import org.vut.kry.ca.entities.CSRWithPrivKey;
import org.vut.kry.ca.entities.DistinguishedName;
import org.vut.kry.ca.misc.KeyUtils;


public class CSRBuilder
{
	private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

	public CSRWithPrivKey GenerateRequest(final DistinguishedName dn) throws NoSuchAlgorithmException, OperatorCreationException, PEMException
	{
	    final KeyPair pair = KeyUtils.GenerateKeyPair();
        final PrivateKey privateKey = pair.getPrivate();
	    final PublicKey publicKey = pair.getPublic();
	    final X500Name x500Name = dn.GetX500Name();
	    final ContentSigner signGen = new JcaContentSignerBuilder(SIGNATURE_ALGORITHM).build(privateKey);
	    final PKCS10CertificationRequestBuilder builder = new JcaPKCS10CertificationRequestBuilder(x500Name, publicKey);
	    final PKCS10CertificationRequest csr = builder.build(signGen);
	    return new CSRWithPrivKey(csr, privateKey);
	}
}
