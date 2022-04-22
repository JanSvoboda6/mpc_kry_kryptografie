package org.vut.kry.ca;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.vut.kry.ca.entities.Cert;
import org.vut.kry.ca.entities.CertificateExtension;
import org.vut.kry.ca.entities.DistinguishedName;


/**
 * The class for signing certificates.
 */
public class Signer
{
	// Constants defining the certificate.
	private static final int DEFAULT_SERIAL_LENGTH = 128;
	private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
	
	// Basic properties of the certificate:
	private BigInteger serialNumber;

	private final KeyPair signerKeyPair;
	private final DistinguishedName signerDn;
	private final PublicKey publicKey;
	private final DistinguishedName dn;
	  
	private final List<CertificateExtension> extensions = new ArrayList<>();
	
	private ZonedDateTime notBefore = ZonedDateTime.now();
	private ZonedDateTime notAfter = notBefore.plusYears(1);
	
	public Signer(final KeyPair signerKeyPair, final DistinguishedName signerDn, final PublicKey publicKey, final DistinguishedName dn)
	{
		this.signerKeyPair = signerKeyPair;
		this.signerDn = signerDn;
		this.publicKey = publicKey;
		this.dn = dn;
	}
	
	public Signer setSerialNumber(final BigInteger serialNumber)
	{
	    this.serialNumber = serialNumber;
	    return this;
	}
	
	public Signer setRandomSerialNumber()
	{
	    this.serialNumber = new BigInteger(DEFAULT_SERIAL_LENGTH, new SecureRandom());
	    return this;
	}
	
	public Signer setNotBefore(final ZonedDateTime notBefore)
	{
	    this.notBefore = notBefore;
	    return this;
	}
	
	public Signer setNotAfter(final ZonedDateTime notAfter)
	{
	    this.notAfter = notAfter;
	    return this;
	}
	
	public Signer validDuringYears(final int years)
	{
	    notAfter = notBefore.plusYears(years);
	    return this;
	}
	
	public Signer addExtension(final CertificateExtension extension) {
	    extensions.add(extension);
	    return this;
	}
	
	public Signer addExtension(final ASN1ObjectIdentifier oid, final boolean isCritical, final ASN1Encodable value)
	{
		extensions.add(new CertificateExtension(oid, isCritical, value));
		return this;
	}
	
	/**
	 * Method used for signing the client's certificate.
	 * @param subjectAlternativeName - list of domains the certificate can be used for
	 * @return Signed certificate
	 * @throws OperatorCreationException
	 * @throws NoSuchAlgorithmException
	 * @throws CertIOException
	 * @throws CertificateException
	 * @throws InvalidKeyException
	 * @throws NoSuchProviderException
	 * @throws SignatureException
	 */
	public Cert sign(final String subjectAlternativeName) throws OperatorCreationException, NoSuchAlgorithmException, CertIOException, CertificateException, InvalidKeyException, NoSuchProviderException, SignatureException {
		final ContentSigner sigGen = new JcaContentSignerBuilder(SIGNATURE_ALGORITHM).build(signerKeyPair.getPrivate());

	    final SubjectPublicKeyInfo subPubKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());

	    // Helper
	    final JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();
	    
	    // The actual certificate generator (using the BouncyCastle library).
	    final X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(
	        // Add the info about the CA.
	        signerDn.getX500Name(),
	        // Assign a generated serial number to the certificate.
	        serialNumber,
	        // Set up the validity timeframe.
	        Date.from(notBefore.toInstant()),
	        Date.from(notAfter.toInstant()),
	        // Add the info about the client.
	        dn.getX500Name(),
	        subPubKeyInfo)
	    		// Add the V3 extensions "authorityKeyIdentifier" and "subjectKeyIdentifier" to the certificate
	            .addExtension(Extension.authorityKeyIdentifier, false, extUtils.createAuthorityKeyIdentifier(signerKeyPair.getPublic()))
	            .addExtension(Extension.subjectKeyIdentifier, false, extUtils.createSubjectKeyIdentifier(publicKey));

	    // Add all of the other extensions (CertificationAuthority.java), code:
	    //.setCommonName(commonName)
        //.setOrganizationName(organization)
        //.setOrganizationalUnitName(department)
        //.setStateOrProvinceName(province)
        //.setCountryName(state)
	    for (final CertificateExtension e : extensions) {
	    	certBuilder.addExtension(e.getOid(), e.isCritical(), e.getValue());
	    }
	    
	    // Add a new V3 extension - Subject Alternative Name - required by the Chromium based browsers - it is used for defining MULTIPLE domains for which the certificate is valid
	    GeneralName altName = new GeneralName(GeneralName.dNSName, subjectAlternativeName);
	    GeneralNames subjectAltName = new GeneralNames(altName);
	    certBuilder.addExtension(X509Extensions.SubjectAlternativeName, false, subjectAltName);

	    // Build the actual certificate, it needs to be converted as well.
	    final X509CertificateHolder holder = certBuilder.build(sigGen);
	    final X509Certificate cert = new JcaX509CertificateConverter().getCertificate(holder);
	    
	    // Finally, check the validity of the created certificate.
	    cert.checkValidity();
	    cert.verify(signerKeyPair.getPublic());

	    return new Cert(cert);
	}
}