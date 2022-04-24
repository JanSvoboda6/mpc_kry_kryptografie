package org.vut.kry.ca;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.vut.kry.ca.entities.DistinguishedName;


/**
 * A builder of the DistinguisedName objects. Basically creates a DistinguisedName objects based on the parameters passed to this builder.
 * This builder needs t be set up with various method defined in here. These methods are in most cases self explanatory.
 * In the end, the build() method is called and as a result DistinguisedName object is obtained.
 */
public class DistinguisedNameBuilder {
	private final X500NameBuilder builder;

	public DistinguisedNameBuilder()
	{
	    builder = new X500NameBuilder();
	}

	private DistinguisedNameBuilder setCn(final String cn)
	{
	    builder.addRDN(BCStyle.CN, cn);
	    return this;
	}

	public DistinguisedNameBuilder setCommonName(final String cn)
	{
	    return setCn(cn);
	}

	private DistinguisedNameBuilder setL(final String l)
	{
	    builder.addRDN(BCStyle.L, l);
	    return this;
	}

	public DistinguisedNameBuilder setLocalityName(final String l)
	{
	    return setL(l);
	}

	private DistinguisedNameBuilder setSt(final String st)
	{
	    builder.addRDN(BCStyle.ST, st);
	    return this;
	}

	public DistinguisedNameBuilder setStateOrProvinceName(final String st)
	{
	    return setSt(st);
	}

	private DistinguisedNameBuilder setO(final String o)
	{
	    builder.addRDN(BCStyle.O, o);
	    return this;
	}

	public DistinguisedNameBuilder setOrganizationName(final String o)
	{
	    return setO(o);
	}

	private DistinguisedNameBuilder setOu(final String ou)
	{
	    builder.addRDN(BCStyle.OU, ou);
	    return this;
	}

	public DistinguisedNameBuilder setOrganizationalUnitName(final String ou)
	{
	    return setOu(ou);
	}

	private DistinguisedNameBuilder setC(final String c)
	{
	    builder.addRDN(BCStyle.C, c);
	    return this;
	}

	public DistinguisedNameBuilder setCountryName(final String c)
	{
	    return setC(c);
	}

	public DistinguisedNameBuilder setStreet(final String street)
	{
	    builder.addRDN(BCStyle.STREET, street);
	    return this;
	}

	public DistinguishedName build()
	{
	    final X500Name name = builder.build();
	    return new DistinguishedName(name);
	}
}
