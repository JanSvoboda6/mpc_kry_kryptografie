package org.vut.kry.ca;
import org.vut.kry.ca.entities.CSRWithPrivKey;
import org.vut.kry.ca.entities.Cert;
import org.vut.kry.ca.entities.RootCert;


public class Main
{
	/**
	 * The entry method of the Certificate Generator.
	 * This app will generate certificates and private keys for both - the Certification Authority and the Client.
	 * The generated certificate for the Certification Authority will need to be imported on the machines that wish to visit the client's website.
	 * Normally, the well known CA certificates are already installed on the computers with whatever operating systems are they running.
	 * But because we are not well known, we need to import the generated CA's certificate on machines manually.
	 * 
	 * @param args  Command line arguments - we are not using any.
	 */
    public static void main(String[] args)
    {
    	// Create a new Certification Authority
    	CA ca = new CA();
    	
    	// Create a certificate for the Certification Authority
    	RootCert caCert = ca.createSelfSignedCACertificate("*.customca.org", "CN=Root-CustomCA, O=CustomCA", 10, "CACert.crt", "CACert.key");
        // Register a new client with the Certification Authority
    	CSRWithPrivKey csr = ca.registerClient("*.securestorage.website", "SecureStorage", "undefined dept.", "Moravsky", "CZ");
        // Generate a certificate for the client signed by the Certification Authority
    	Cert clientCert = ca.createClientCertificate("*.securestorage.website", caCert, csr, "ServerCert.crt", "ServerCertPriv.key");
        
    	// Validate the client certificate just in case something in the process went wrong
    	boolean isCertValid = ca.validate(clientCert, caCert);
        if (isCertValid)
        	System.out.println("Generated certificate is valid.");
        else
        	System.out.println("Generated certificate is not valid.");
    }
} 
