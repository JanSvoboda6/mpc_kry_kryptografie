import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;

import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.operator.OperatorCreationException;

public class CA {
	/**
	* Creates a self signed certificate for the CA.
	*
	* @param name  basic info about the CA ie. "CN=Root-CustomCA, O=CustomCA"
	*/
	public RootCert CreateSelfSignedCACertificate(String subjectAlternativeName, String caParams, int lifeTime, String certFilePath, String keyFilePath)
	{
		final DistinguishedName root = new DistinguishedName(caParams);
		
        RootCert caCert = null;
		try
		{
			caCert = new RootCertBuilder(root)
			    .ValidDuringYears(10)
			    .Build(subjectAlternativeName);
			
			caCert.Save(certFilePath);
			caCert.SaveKey(keyFilePath);
		}
		catch (InvalidKeyException | OperatorCreationException | NoSuchAlgorithmException | CertificateException | NoSuchProviderException | SignatureException | IOException e)
		{
			e.printStackTrace();
		}
		
		return caCert;
	}
	
	public CSRWithPrivKey RegisterClient(String commonName, String organization, String department, String province, String state)
	{
		CSRWithPrivKey csr = null;
		
		try {
			csr = new CSRBuilder().GenerateRequest(new DistinguisedNameBuilder()
			        .SetCn(commonName)
			        .SetO(organization)
			        .SetOu(department)
			        .SetSt(province)
			        .SetC(state)
			        .Build());
		} catch (NoSuchAlgorithmException | OperatorCreationException | PEMException e) {
			e.printStackTrace();
		}
		
		return csr;
	}
	
	public Cert CreateClientCertificate(String subjectAlternativeName, RootCert caCert, CSRWithPrivKey csr, String certFilePath, String keyFilePath)
	{
		Cert clientCert = null;
		try
		{
			clientCert = caCert.SignCsr(csr)
			        .SetRandomSerialNumber()
			        .ValidDuringYears(2)
			        .Sign(subjectAlternativeName);
		}
		catch (InvalidKeyException | OperatorCreationException | NoSuchAlgorithmException | CertIOException | CertificateException | NoSuchProviderException | SignatureException e)
		{
			e.printStackTrace();
		}
        
		// generate files
        try {
			clientCert.Save(certFilePath);
			CertWithPrivKey priv = clientCert.AttachPrivateKey(csr.GetPrivateKey());
	        priv.SaveKey(keyFilePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        return clientCert;
	}
	
	public boolean Validate (Cert clientCert, RootCert caCert)
	{
		try
		{
            // check certificate validity - datetime
            clientCert.GetX509Certificate().checkValidity();

            // check certificate validity - signature
            clientCert.GetX509Certificate().verify(caCert.GetX509Certificate().getPublicKey());

            return true;
        }
        catch(Exception e)
		{
            System.out.println("error: " + e);
        }
		
		return false;
	}
}