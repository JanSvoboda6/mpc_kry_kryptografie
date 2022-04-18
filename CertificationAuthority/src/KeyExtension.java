import org.bouncycastle.asn1.x509.Extension;

public class KeyExtension extends CrtExtension
{
	KeyExtension(final int keyUsages)
	{
	    super(Extension.keyUsage, false, new org.bouncycastle.asn1.x509.KeyUsage(keyUsages));
	}

	KeyExtension(final KeyUsage... usages)
	{
	    this(GetUsages(usages));
	}

	public static KeyExtension Create(final KeyUsage... usages)
	{
	    return new KeyExtension(usages);
	}

	private static int GetUsages(final KeyUsage[] usages)
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
