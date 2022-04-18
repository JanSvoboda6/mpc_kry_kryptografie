public class CryptoCAMain
{
    public static void main(String[] args)
    {
    	CA ca = new CA();
    	
    	RootCert caCert = ca.CreateSelfSignedCACertificate("*.customca.org", "CN=Root-CustomCA, O=CustomCA", 10, "CACert.crt", "CACert.key");
        CSRWithPrivKey csr = ca.RegisterClient("*.securestorage.website", "SecureStorage", "undefined dept.", "Moravsky", "CZ");
        Cert clientCert = ca.CreateClientCertificate("*.securestorage.website", caCert, csr, "ServerCert.crt", "ServerCertPriv.key");
        boolean isCertValid = ca.Validate(clientCert, caCert);
        
        if (isCertValid)
        {
        	System.out.println("Generated certificate is valid.");
        }
        else
        {
        	System.out.println("Generated certificate is not valid.");
        }
    }
} 
