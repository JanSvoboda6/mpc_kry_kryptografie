package org.vut.kry.ca.entities;
import java.io.IOException;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x500.X500Name;


/**
 * Entity that contains the information about the subject (name, domains, ...). Nothing more to it.
 */
public class DistinguishedName
{
	private final X500Name x500Name;
	
	public DistinguishedName(final X500Name name)
	{
	    this.x500Name = name;
	}
	
	public DistinguishedName(final String name)
	{
	    this.x500Name = new X500Name(name);
	}
	  
	public DistinguishedName(final X500Principal principal)
	{
	    this.x500Name = X500Name.getInstance(principal.getEncoded());
	}
	
	public X500Name getX500Name()
	{
	    return x500Name;
	}
	
	public X500Principal getX500Principal() throws IOException
	{
	    return new X500Principal(x500Name.getEncoded());
	}
	
	public byte[] getEncoded() throws Exception
	{
	      return x500Name.getEncoded();
	}
	
	public String getName()
	{
	    return x500Name.toString();
	}
	
	@Override
	public String toString()
	{
		return getName();
	}
}