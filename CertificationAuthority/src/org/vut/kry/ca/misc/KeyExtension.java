package org.vut.kry.ca.misc;
import org.bouncycastle.asn1.x509.Extension;
import org.vut.kry.ca.entities.CertificateExtension;


/**
 * Contains namely the common usages a certificate can be used for. This is solely used as a parameter to the BouncyCastle library.
 * @author Andy
 *
 */
public class KeyExtension extends CertificateExtension
{
	public KeyExtension(final int keyUsages)
	{
	    super(Extension.keyUsage, false, new org.bouncycastle.asn1.x509.KeyUsage(keyUsages));
	}

	public KeyExtension(final KeyUsage... usages)
	{
	    this(getUsages(usages));
	}

	public static KeyExtension create(final KeyUsage... usages)
	{
	    return new KeyExtension(usages);
	}

	private static int getUsages(final KeyUsage[] usages)
	{
	    int u = 0;
	    for (final KeyUsage ku : usages)
	    {
	    	u = u | ku.keyUsage;
	    }
	    return u;
	}

	public static enum KeyUsage
	{
	    DIGITAL_SIGNATURE(org.bouncycastle.asn1.x509.KeyUsage.digitalSignature),
	    NON_REPUDIATION(org.bouncycastle.asn1.x509.KeyUsage.nonRepudiation),
	    KEY_ENCIPHERMENT(org.bouncycastle.asn1.x509.KeyUsage.keyEncipherment),
	    DATA_ENCIPHERMENT(org.bouncycastle.asn1.x509.KeyUsage.dataEncipherment),
	    KEY_AGREEMENT(org.bouncycastle.asn1.x509.KeyUsage.keyAgreement),
	    KEY_CERT_SIGN(org.bouncycastle.asn1.x509.KeyUsage.keyCertSign),
	    CRL_SIGN(org.bouncycastle.asn1.x509.KeyUsage.cRLSign),
	    ENCIPHER_ONLY(org.bouncycastle.asn1.x509.KeyUsage.encipherOnly),
	    DECIPHER_ONLY(org.bouncycastle.asn1.x509.KeyUsage.encipherOnly);

	    private final int keyUsage;

	    private KeyUsage(final int keyUsage) {
	    	this.keyUsage = keyUsage;
	    }
	}
}
