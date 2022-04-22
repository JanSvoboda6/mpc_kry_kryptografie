package org.vut.kry.ca;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;

import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.operator.OperatorCreationException;
import org.vut.kry.ca.entities.CSRWithPrivateKey;
import org.vut.kry.ca.entities.Cert;
import org.vut.kry.ca.entities.CertificateWithPrivKey;
import org.vut.kry.ca.entities.DistinguishedName;
import org.vut.kry.ca.entities.RootCertificate;

public class CertificationAuthority {
	/**
	* Creates a simple self signed certificate for the CA.
	* The certificate and private key files are later stored in the destination path specified in parameters "certFilePath" and "keyFilePath".
	*
	* @param subjectAlternativeName  the list of domains that can use this certificate.
	* 								 necessary for the modern browsers based on Chromium (V3 extension of the certificate "Subject Alternative Name").
	* 								 without this the certificate is not valid in the chromium based browsers.
	* @param caParameters  Basic informations about the CA ie. "CN=Root-CustomCA, O=CustomCA" - the CN stands for Common Name and O for Organization.
	* @param lifeTime  Lifetime of the CA certificate in years.
	* @param certificateFilePath  The path to the CA certificate that will be generated by this method.
	* @param keyFilePath  The path to the CA's private key file - also generated by this method.
	*/
	public RootCertificate createSelfSignedCACertificate(String subjectAlternativeName, String caParameters, int lifeTime, String certificateFilePath, String keyFilePath)
	{
		// create a container that holds the information about the Certification Authority
		final DistinguishedName root = new DistinguishedName(caParameters);
		
        RootCertificate caCert = null;
		try
		{
			// Create the CA certificate
			caCert = new RootCertBuilder(root)
			    .validDuringYears(10)
			    .build(subjectAlternativeName);
			
			// the certificate and private keys are saved to the selected destination
			caCert.save(certificateFilePath);
			caCert.saveKey(keyFilePath);
		}
		catch (InvalidKeyException | OperatorCreationException | NoSuchAlgorithmException | CertificateException | NoSuchProviderException | SignatureException | IOException e)
		{
			e.printStackTrace();
		}
		
		// also returns the result, so we can work with it later
		return caCert;
	}
	
	/**
	* Registers a client with the CA. The client will provide all of the details to the CA.
	*
	* @param commonName  Basically a website address a client runs in a format: "*.example.com".
	* @param organization  The name of client's organization.
	* @param department  The name of client's department within specified organization.
	* @param province  The province in which the organization is located.
	* @param state  The state in which the organization is located.
	*/
	public CSRWithPrivateKey registerClient(String commonName, String organization, String department, String province, String state)
	{
		CSRWithPrivateKey csr = null;
		
		try {
			// create a Certificate Signing Request (CSR)
			csr = new CSRBuilder().generateRequest(new DistinguisedNameBuilder()
			        .setCommonName(commonName)
			        .setOrganizationName(organization)
			        .setOrganizationalUnitName(department)
			        .setStateOrProvinceName(province)
			        .setCountryName(state)
			        .build());
		} catch (NoSuchAlgorithmException | OperatorCreationException | PEMException e) {
			e.printStackTrace();
		}
		
		// returns the CSR, so we can later create a valid certificate for the client
		return csr;
	}
	
	/**
	* Creates a certificate for the client which needs to be signed by the CA.
	*
	* @param subjectAlternativeName  The list of domains that can use this certificate.
	* 								 Necessary for the modern browsers based on Chromium (V3 extension of the certificate "Subject Alternative Name").
	* 								 Without this the certificate is not valid in the chromium based browsers.
	* @param caCertificate  The certificate of the CA.
	* @param csr  Certificate Signing Request generated when the client was registered in the CA.
	* @param certificateFilePath  The path to the client's certificate that will be generated by this method, this file will be imported into the client's server.
	* @param keyFilePath  The path to the client's private key file - also generated by this method, this file will be imported into the client's server.
	*/
	public Cert createClientCertificate(String subjectAlternativeName, RootCertificate caCertificate, CSRWithPrivateKey csr, String certificateFilePath, String keyFilePath)
	{
		Cert clientCert = null;
		try
		{
			clientCert = caCertificate.signCSR(csr)
			        .setRandomSerialNumber()
			        .validDuringYears(2)
			        .sign(subjectAlternativeName);
		}
		catch (InvalidKeyException | OperatorCreationException | NoSuchAlgorithmException | CertIOException | CertificateException | NoSuchProviderException | SignatureException e)
		{
			e.printStackTrace();
		}
        
        try {
        	// generate the certificate + key files
			clientCert.save(certificateFilePath);
			CertificateWithPrivKey priv = clientCert.attachPrivateKey(csr.getPrivateKey());
	        priv.saveKey(keyFilePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        return clientCert;
	}
	
	/**
	* Validates the client certificate. This method will print out any errors if there are any.
	*
	* @param clientCertificate  A client's certificate that will be checked.
	* @param caCertificate  The certificate of the CA.
	*/
	public boolean validate (Cert clientCertificate, RootCertificate caCertificate)
	{
		try
		{
            // check the validity of the client's certificate - time
            clientCertificate.getX509Certificate().checkValidity();

            // check the validity of the client's certificate - signature
            clientCertificate.getX509Certificate().verify(caCertificate.getX509Certificate().getPublicKey());

            return true;
        }
        catch(Exception e)
		{
            System.out.println("error: " + e);
        }
		
		return false;
	}
}