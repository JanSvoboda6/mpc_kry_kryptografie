package org.vut.kry.ca.entities;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.ReasonFlags;
import org.vut.kry.ca.misc.NameType;


public class DistributionPoint extends CertificateExtension
{
	public DistributionPoint(final org.bouncycastle.asn1.x509.DistributionPoint... points)
	{
	    super(Extension.cRLDistributionPoints, false, new org.bouncycastle.asn1.x509.CRLDistPoint(points));
	}

	/**
	 * Creates a {@link CrlDistPointExtension} with only a {@code distributionPoint} URI (no {@code reasons}, no
	 * {@code cRLIssuer} specified).
	 */
	public static DistributionPoint create(final String uri)
	{
	    return create(NameType.URI, uri);
	}

	/**
	 * Creates a {@link CrlDistPointExtension} with only a {@code distributionPoint} {@link GeneralName} (no
	 * {@code reasons}, no {@code cRLIssuer} specified).
	 */
	public static DistributionPoint create(final NameType type, final String name)
	{
		return create(type, name, null, null, null);
	}

	public static DistributionPoint create(final NameType distribPointNameType, final String distribPointName, final NameType crlIssuerNameType, final String crlIssuer, final ReasonFlags reasons)
	{
	    final DistributionPointName dp = new DistributionPointName(distribPointNameType.generalNames(distribPointName));
	    final GeneralNames crl;
	    
	    if (crlIssuerNameType != null && crlIssuer != null)
	    {
	    	crl = crlIssuerNameType.generalNames(crlIssuer);
	    }
	    else
	    {
	    	crl = null;
	    }
	    
	    return create(dp, reasons, crl);
	  }

	  public static DistributionPoint create(final DistributionPointName distributionPoint, final ReasonFlags reasons, final GeneralNames cRLIssuer)
	  {
		  final org.bouncycastle.asn1.x509.DistributionPoint p = new org.bouncycastle.asn1.x509.DistributionPoint(distributionPoint, reasons, cRLIssuer);
		  return create(p);
	  }

	  public static DistributionPoint create(final org.bouncycastle.asn1.x509.DistributionPoint... points)
	  {
		  return new DistributionPoint(points);
	  }
}
