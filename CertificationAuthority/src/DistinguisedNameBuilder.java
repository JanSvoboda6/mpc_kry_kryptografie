import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.X509Extensions;

public class DistinguisedNameBuilder {
	private final X500NameBuilder builder;

	DistinguisedNameBuilder()
	{
	    builder = new X500NameBuilder();
	}

	public DistinguisedNameBuilder SetCn(final String cn)
	{
	    builder.addRDN(BCStyle.CN, cn);
	    return this;
	}

	public DistinguisedNameBuilder SetCommonName(final String cn)
	{
	    return SetCn(cn);
	}

	public DistinguisedNameBuilder SetL(final String l)
	{
	    builder.addRDN(BCStyle.L, l);
	    return this;
	}

	public DistinguisedNameBuilder SetLocalityName(final String l)
	{
	    return SetL(l);
	}

	public DistinguisedNameBuilder SetSt(final String st)
	{
	    builder.addRDN(BCStyle.ST, st);
	    return this;
	}

	public DistinguisedNameBuilder SetStateOrProvinceName(final String st)
	{
	    return SetSt(st);
	}

	public DistinguisedNameBuilder SetO(final String o)
	{
	    builder.addRDN(BCStyle.O, o);
	    return this;
	}

	public DistinguisedNameBuilder SetOrganizationName(final String o)
	{
	    return SetO(o);
	}

	public DistinguisedNameBuilder SetOu(final String ou)
	{
	    builder.addRDN(BCStyle.OU, ou);
	    return this;
	}

	public DistinguisedNameBuilder SetOrganizationalUnitName(final String ou)
	{
	    return SetOu(ou);
	}

	public DistinguisedNameBuilder SetC(final String c)
	{
	    builder.addRDN(BCStyle.C, c);
	    return this;
	}

	public DistinguisedNameBuilder SetCountryName(final String c)
	{
	    return SetC(c);
	}

	public DistinguisedNameBuilder SetStreet(final String street)
	{
	    builder.addRDN(BCStyle.STREET, street);
	    return this;
	}

	public DistinguishedName Build()
	{
	    final X500Name name = builder.build();
	    return new DistinguishedName(name);
	}
}
