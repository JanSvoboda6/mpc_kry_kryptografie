# CertificationAuthority

Nástroj slouží pro generování páru certifikát + soukromý klíč pro klientský server (ClientCertificate.crt, ClientPK.key) a Certifikační Autoritu (CertificationAuthorityCertificate.crt, CertificationAuthorityPK.key). Tyto soubory jsou uloženy v adresáři /mpc_kry_kryptografie/GeneratedCertificates/ po spuštění programu.

Certifikát certifikační autority se poté musí manuálně naimportovat do počítačů klientů snažící se přistoupit na internetové stránky. Toto je nutné především proto, že námi vytvořená Cert. autorita se nenachází mezi předinstalovanými důvěryhodnými společnostmi v OS. V reálném provozu se certifikáty instalovat nemusí - jsou už předinstalované v operačních systémech vývojaři, kteří OS vyvíjí. Tímto se pro koncové uživatele proces značně zjednodušuje a import díky tomuto není nutný.

Dále aplikace generuje také certifikát a privátní klíč pro server, na kterém naše stránky běží. Soubor certifikátu a klíče se jednoduše zkopíruje do speciálního adresáře serveru (Apache, Nginx atp.) vyhrazeného právě pro tyto účely. Tento klientský certifikát je podepsaný certifikační autoritou.

Webový prohlížeč nakonec načte certifikát, který byl postutnut webovým serverem a ověří jej s pomocí nainstalovaném certifikátu certifikační autority manuálně nainstalovaném v OS.

Tento program při každém spuštění vygeneruje úplně nové certifikáty pro certifikační autoritu i klientský server.

## Popis spuštění programu
Následuje podrobný popis toho, jakým způsobem lze program zkompilovat a spustit.

### Stažení a instalace Eclipse
Program je napsaný v Javě. Proto je nutné stáhnout a nainstalovat Eclipse ze stránek: [https://www.eclipse.org/downloads/](https://www.eclipse.org/downloads/).

### Stažení zdrojového kódu nástroje Certifikační autority
Pro stažení zdrovojého kódu je nutné přejít na Github server našeho projektu na adrese [https://github.com/JanSvoboda6/mpc_kry_kryptografie](https://github.com/JanSvoboda6/mpc_kry_kryptografie). Následně rozbalit zeleně označené tlačítko s popiskem "Code" a vybrat možnost "Download ZIP".
![Stažení zdrojového kódu projektu z Github serveru](https://github.com/JanSvoboda6/mpc_kry_kryptografie/blob/dev-hwired/CertificationAuthority/Documentation/img/github-download-source.png?raw=true)
Nakonec je už jen potřeba rozbalit tyto soubory z archivu na disk (umístění je potřeba si zapamatovat a následně jej vyhledat v dalším kroku).

### Spuštění eclipse a načtení projektu
Po spuštění eclipse vybereme náš pracovní prostor (workspace) tak, aby směřoval do námi rozbaleného adresáře do složky /CertificationAuthority/ viz. obrázek.

![Výběr workspace v Eclipse](https://github.com/JanSvoboda6/mpc_kry_kryptografie/blob/dev-hwired/CertificationAuthority/Documentation/img/eclipse-workspace.png?raw=true)

Následně v otevřeném Eclipse přejdeme do nabídky "File" -> "Open projects from File System...".
V nově otevřeném dialogovém okně klikneme na tlačítko "Directory...". Toto tlačítko nás přesune do výběru složky - defaultně by mělo směřovat do složky "CertificationAuthority" - potvrdíme kliknutím na OK.
![Výběr adresáře projektu](https://github.com/JanSvoboda6/mpc_kry_kryptografie/blob/dev-hwired/CertificationAuthority/Documentation/img/eclipse-folder-selection.png?raw=true)