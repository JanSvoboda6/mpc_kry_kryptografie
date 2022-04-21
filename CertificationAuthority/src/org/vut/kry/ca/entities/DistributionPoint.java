package org.vut.kry.ca.entities;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.ReasonFlags;
import org.vut.kry.ca.misc.NameType;


public class DistributionPoint extends CrtExtension
{
	DistributionPoint(final org.bouncycastle.asn1.x509.DistributionPoint... points)
	{
	    super(Extension.cRLDistributionPoints, false, new org.bouncycastle.asn1.x509.CRLDistPoint(points));
	}

	/**
	 * Creates a {@link CrlDistPointExtension} with only a {@code distributionPoint} URI (no {@code reasons}, no
	 * {@code cRLIssuer} specified).
	 */
	public static DistributionPoint Create(final String uri)
	{
	    return Create(NameType.URI, uri);
	}

	/**
	 * Creates a {@link CrlDistPointExtension} with only a {@code distributionPoint} {@link GeneralName} (no
	 * {@code reasons}, no {@code cRLIssuer} specified).
	 */
	public static DistributionPoint Create(final NameType type, final String name)
	{
		return Create(type, name, null, null, null);
	}

	public static DistributionPoint Create(final NameType distribPointNameType, final String distribPointName, final NameType crlIssuerNameType, final String crlIssuer, final ReasonFlags reasons)
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
	    
	    return Create(dp, reasons, crl);
	  }

	  public static DistributionPoint Create(final DistributionPointName distributionPoint, final ReasonFlags reasons, final GeneralNames cRLIssuer)
	  {
		  final org.bouncycastle.asn1.x509.DistributionPoint p = new org.bouncycastle.asn1.x509.DistributionPoint(distributionPoint, reasons, cRLIssuer);
		  return Create(p);
	  }

	  public static DistributionPoint Create(final org.bouncycastle.asn1.x509.DistributionPoint... points)
	  {
		  return new DistributionPoint(points);
	  }
}
