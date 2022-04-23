package org.vut.kry.ca.entities;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;


/**
 * A class that holds the parameters of the certificate. These are contained in the certificate in a key-value format.
 */
public class CertificateExtension {
	private final ASN1ObjectIdentifier oid;
	private final boolean isCritical;
	private final ASN1Encodable value;

	public CertificateExtension(final ASN1ObjectIdentifier oid, final boolean isCritical, final ASN1Encodable value) {
		this.oid = oid;
	    this.isCritical = isCritical;
	    this.value = value;
	}

	public ASN1ObjectIdentifier getOid() {
	    return oid;
	}

	public boolean isCritical() {
	    return isCritical;
	}

	public ASN1Encodable getValue() {
	    return value;
	}

	@Override
	public String toString() {
	    return "Extension [" + oid + "=" + value + "]";
	}
}