import static io.github.olivierlemasle.ca.CA.*;
import java.security.PublicKey;
import io.github.olivierlemasle.ca.*;

public class CryptoCAMain {
    public static void main(String[] args) {
        // vytvoreni ca root certifikatu
        final DistinguishedName root = dn("CN=Root-CustomCA, O=CustomCA");
        final RootCertificate ca = createSelfSignedCertificate(root)
            .validDuringYears(10)
            .build();
        
        ca.saveKey("CACert.key");
        ca.save("CACert.crt");

        // zadost o klient. certifikat
        final CsrWithPrivateKey csr = createCsr().generateRequest(
                dn()
                .setCn("*.navalproject.online")
                .setO("EncryptedStorage")
                .setOu("undefined dept.")
                .setSt("Moravsky")
                .setC("CZ")
                .setAlternativeName("DNS Name=*.navalproject.online")
                .build());
        
        // podepsani klient. certifikatu
        final Certificate cert = ca.signCsr(csr)
                .setRandomSerialNumber()
                .validDuringYears(2)
                .sign();
        
        System.out.println(csr.getPrivateKey());
        cert.save("ServerCert.crt");
        final CertificateWithPrivateKey priv = cert.attachPrivateKey(csr.getPrivateKey());
        priv.saveKey("ServerCertPriv.key");

        try {
            // overeni platnosti certifikatu - datetime
            cert.getX509Certificate().checkValidity();
            System.out.println("active - datetime check");

            // overeni platnosti - podpis
            cert.getX509Certificate().verify(ca.getX509Certificate().getPublicKey());
            System.out.println("valid - signature");

            // ziskani ver. klice
            PublicKey serverPubKey = cert.getX509Certificate().getPublicKey();
            System.out.println("pub key:" + serverPubKey);
        }
        catch(Exception e) {
            System.out.println("error: " + e);
        }
    }
} 
