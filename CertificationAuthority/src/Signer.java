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

public class Signer
{
	private static final int DEFAULT_SERIAL_LENGTH = 128;
	private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
	
	private BigInteger serialNumber;

	private final KeyPair signerKeyPair;
	private final DistinguishedName signerDn;
	private final PublicKey publicKey;
	private final DistinguishedName dn;
	  
	private final List<CrtExtension> extensions = new ArrayList<>();
	
	private ZonedDateTime notBefore = ZonedDateTime.now();
	private ZonedDateTime notAfter = notBefore.plusYears(1);
	
	Signer(final KeyPair signerKeyPair, final DistinguishedName signerDn, final PublicKey publicKey, final DistinguishedName dn)
	{
		this.signerKeyPair = signerKeyPair;
		this.signerDn = signerDn;
		this.publicKey = publicKey;
		this.dn = dn;
	}
	
	public Signer SetSerialNumber(final BigInteger serialNumber)
	{
	    this.serialNumber = serialNumber;
	    return this;
	}
	
	public Signer SetRandomSerialNumber()
	{
	    this.serialNumber = new BigInteger(DEFAULT_SERIAL_LENGTH, new SecureRandom());
	    return this;
	}
	
	public Signer SetNotBefore(final ZonedDateTime notBefore)
	{
	    this.notBefore = notBefore;
	    return this;
	}
	
	public Signer SetNotAfter(final ZonedDateTime notAfter)
	{
	    this.notAfter = notAfter;
	    return this;
	}
	
	public Signer ValidDuringYears(final int years)
	{
	    notAfter = notBefore.plusYears(years);
	    return this;
	}
	
	public Signer AddExtension(final CrtExtension extension) {
	    extensions.add(extension);
	    return this;
	}
	
	public Signer AddExtension(final ASN1ObjectIdentifier oid, final boolean isCritical, final ASN1Encodable value)
	{
		extensions.add(new CrtExtension(oid, isCritical, value));
		return this;
	}
	
	public Cert Sign(final String subjectAlternativeName) throws OperatorCreationException, NoSuchAlgorithmException, CertIOException, CertificateException, InvalidKeyException, NoSuchProviderException, SignatureException {
		final ContentSigner sigGen = new JcaContentSignerBuilder(SIGNATURE_ALGORITHM).build(signerKeyPair.getPrivate());

	    final SubjectPublicKeyInfo subPubKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());

	    final JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();
	    final X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(
	        signerDn.GetX500Name(),
	        serialNumber,
	        Date.from(notBefore.toInstant()),
	        Date.from(notAfter.toInstant()),
	        dn.GetX500Name(),
	        subPubKeyInfo)
	            .addExtension(Extension.authorityKeyIdentifier, false, extUtils.createAuthorityKeyIdentifier(signerKeyPair.getPublic()))
	            .addExtension(Extension.subjectKeyIdentifier, false, extUtils.createSubjectKeyIdentifier(publicKey));

	    for (final CrtExtension e : extensions) {
	    	certBuilder.addExtension(e.getOid(), e.isCritical(), e.getValue());
	    }
	    
	    GeneralName altName = new GeneralName(GeneralName.dNSName, subjectAlternativeName);
	    GeneralNames subjectAltName = new GeneralNames(altName);
	    certBuilder.addExtension(X509Extensions.SubjectAlternativeName, false, subjectAltName);

	    final X509CertificateHolder holder = certBuilder.build(sigGen);
	    final X509Certificate cert = new JcaX509CertificateConverter().getCertificate(holder);

	    cert.checkValidity();
	    cert.verify(signerKeyPair.getPublic());

	    return new Cert(cert);
	}
}