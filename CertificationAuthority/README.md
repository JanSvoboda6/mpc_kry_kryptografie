# CertificationAuthority

Nástroj slouží pro generování páru certifikát + soukromý klíč pro klientský server (ClientCertificate.crt, ClientPK.key) a Certifikační Autoritu (CertificationAuthorityCertificate.crt, CertificationAuthorityPK.key). Tyto soubory jsou uloženy v adresáři /mpc_kry_kryptografie/GeneratedCertificates/ po spuštění programu.

Certifikát certifikační autority se poté musí manuálně naimportovat do počítačů klientů snažící se přistoupit na internetové stránky. Toto je nutné především proto, že námi vytvořená Cert. autorita se nenachází mezi předinstalovanými důvěryhodnými společnostmi v OS. V reálném provozu se certifikáty instalovat nemusí - jsou už předinstalované v operačních systémech vývojaři, kteří OS vyvíjí. Tímto se pro koncové uživatele proces značně zjednodušuje a import díky tomuto není nutný.

Dále aplikace generuje také certifikát a privátní klíč pro server, na kterém naše stránky běží. Soubor certifikátu a klíče se jednoduše zkopíruje do speciálního adresáře serveru (Apache, Nginx atp.) vyhrazeného právě pro tyto účely. Tento klientský certifikát je podepsaný certifikační autoritou.

Webový prohlížeč nakonec načte certifikát, který byl postutnut webovým serverem a ověří jej s pomocí nainstalovaném certifikátu certifikační autority manuálně nainstalovaném v OS.

Tento program při každém spuštění vygeneruje úplně nové certifikáty pro certifikační autoritu i klientský server.
![Vygenerované certifikáty](https://github.com/JanSvoboda6/mpc_kry_kryptografie/blob/dev-hwired/CertificationAuthority/Documentation/img/generated-certificates.png?raw=true)

## Import certifikátu cerifikační autority
Celý proces komunikace mezi klientem a serverem a role certifikační autority je popsán obrázkem viz. níže:
![Model komunikace Server-Klient a role CA](https://github.com/JanSvoboda6/mpc_kry_kryptografie/blob/dev-hwired/CertificationAuthority/Documentation/uml/CommunicationDiagram.drawio.png?raw=true)

Pokud se uživatel bude chtít vydat na stránky [https://www.securestorage.website/](https://www.securestorage.website/) narazí na chybu ustanovení SSL bezpečného spojení. K úspěšnému ustanovení spojení je zapotřebí na klientský stroj nainstalovat certifikát certifikační autority. Ten slouží k ověření totožnosti cílového WWW serveru prohlížeči který dotaz inicioval. Certifikát lze najít na této adrese: [https://github.com/JanSvoboda6/mpc_kry_kryptografie/blob/main/CertificationAuthority/ProjectCertificates/Root-CustomCA.crt?raw=1](https://github.com/JanSvoboda6/mpc_kry_kryptografie/blob/main/CertificationAuthority/ProjectCertificates/Root-CustomCA.crt?raw=1). 

### Postup instalace certifikátu certifikační autority
Nejprve je potřeba stáhnout certifikát viz. odkaz výše.

Poté certifikát v průzkumníku otevřít a zvolit "Install Certificate...".

![Instalace certifikátu 1](https://github.com/JanSvoboda6/mpc_kry_kryptografie/blob/dev-hwired/CertificationAuthority/Documentation/img/certificate-installation1.png?raw=true)

V dalším dialogovém okně zvolit z nabídky "Store Location" možnost "Local Machine".
![Instalace certifikátu 2](https://github.com/JanSvoboda6/mpc_kry_kryptografie/blob/dev-hwired/CertificationAuthority/Documentation/img/certificate-installation2.png?raw=true)

Následuje nastavení v dalším okně. Zde zvolíme úložiště certifikátů na "Trusted Root Certificate Authorities". Pokračujeme kliknutím na tlačítko Next.

![Instalace certifikátu 3](https://github.com/JanSvoboda6/mpc_kry_kryptografie/blob/dev-hwired/CertificationAuthority/Documentation/img/certificate-installation3.png?raw=true)

Poté už jen potvrdíme import kliknutím na tlačítko "Finish". Po tomto kroku by měl být certifikát certifikační autority úspěšně nainstalován na počítači a stránky projektu [https://www.securestorage.website/](https://www.securestorage.website/) by měly být dostupné bez chyb.


## Základní popis struktury programu
Detailní dokumentace programu je dostupná na této adrese: [https://rawcdn.githack.com/JanSvoboda6/mpc_kry_kryptografie/34196e71ad39178c1a95993fb5ff8a49eb9853a5/CertificationAuthority/Documentation/html/index.html](https://rawcdn.githack.com/JanSvoboda6/mpc_kry_kryptografie/34196e71ad39178c1a95993fb5ff8a49eb9853a5/CertificationAuthority/Documentation/html/index.html).

### Základní přehled tříd
Program se skládá celkem ze tří složek: hlavní, entities, misc.

Hlavní složka obsahuje třídy "CertificationAuthority.java", "CSRBuilder.java", "DistinguisedNameBuilder.java", "**Main.java**", "RootCertBuilder.java" a "Signer.java". Tyto třídy obsahují hlavní logiku programu - probíhají v něm procesy od vytvoření certifikátu CA, registrace klienta až po vytvoření klientského certifikátu a validace.

Složka "**entities**" obsahuje třídy které slouží k uložení stavu programu - jednotlivé třídy vytváří objekty z těchto tříd a uchovávají v nich informace v průběhu vykonávání. Dále tyto třídy obsahují případné pomocné metody např. uložení certifáku na disk atd.

Složka "**misc**" obsahuje pomocné třídy zajišťující např. generování veřejných a privátních klíčů.

#### Diagram tříd
![Diagram tříd](https://github.com/JanSvoboda6/mpc_kry_kryptografie/blob/dev-hwired/CertificationAuthority/Documentation/uml/ClassDiagram.drawio.png?raw=true)

#### Diagram chování
![Diagram chování](https://github.com/JanSvoboda6/mpc_kry_kryptografie/blob/dev-hwired/CertificationAuthority/Documentation/uml/BehavioralDiagram.drawio.png?raw=true)



## Popis spuštění programu
Následuje podrobný popis toho, jakým způsobem lze program zkompilovat a spustit.

### Stažení a instalace Eclipse
Program je napsaný v Javě. Proto je nutné stáhnout a nainstalovat Eclipse ze stránek: [https://www.eclipse.org/downloads/](https://www.eclipse.org/downloads/).

### Stažení zdrojového kódu nástroje Certifikační autority
Pro stažení zdrovojého kódu je nutné přejít na Github server našeho projektu na adrese [https://github.com/JanSvoboda6/mpc_kry_kryptografie](https://github.com/JanSvoboda6/mpc_kry_kryptografie). Následně rozbalit zeleně označené tlačítko s popiskem "Code" a vybrat možnost "Download ZIP".
![Stažení zdrojového kódu projektu z Github serveru](https://github.com/JanSvoboda6/mpc_kry_kryptografie/blob/dev-hwired/CertificationAuthority/Documentation/img/github-download-source.png?raw=true)
Nakonec je už jen potřeba rozbalit tyto soubory z archivu na disk (umístění je potřeba si zapamatovat a následně jej vyhledat v dalším kroku).

### Spuštění Eclipse a načtení projektu
Po spuštění eclipse vybereme náš pracovní prostor (workspace) tak, aby směřoval do nově vytvořené složky libovolně umístěné na disku.

Uvítací obrazovku, která se automaticky zobrazila, je potřeba zavřít. Následně je na kartě "Package Explorer" potřeba kliknout na "Import projects..." (v případě, že by nebyla karta automaticky otevřena, je potřeba ji otevřít přes nabídku "Window" -> "Show view" -> "Package explorer").
![Výběr adresáře projektu](https://github.com/JanSvoboda6/mpc_kry_kryptografie/blob/dev-hwired/CertificationAuthority/Documentation/img/eclipse-import-project.png?raw=true)

Následně z dialogového okna vybrat "Maven" -> "Existing Maven Projects".

V otevřeném dialogovém okně přejdeme do složky "CertificationAuthority" (nacházející se ve stažené složce zdrojových kódu z Githubu). Po nalezení složky potvrdíme výběr pomocí tlačítka "Select Folder" a následně "Finish".
![Výběr složky pro import](https://github.com/JanSvoboda6/mpc_kry_kryptografie/blob/dev-hwired/CertificationAuthority/Documentation/img/eclipse-import-browse.png?raw=true)

### Spuštění projektu
Nakonec je možné projekt spustit. Je potřeba otevřít soubor "Main.java" a spustit jej viz. obr.
![Spuštění projektu](https://github.com/JanSvoboda6/mpc_kry_kryptografie/blob/dev-hwired/CertificationAuthority/Documentation/img/eclipse-project-run.png?raw=true)

Pokud projekt nepůjde spustit, je potřeba aktualizovat a naimportovat závislosti (knihovna BouncyCastle). Toho lze dosáhnout kliknutím pravého tlačítka na projekt a z kontextového menu vybrat "Maven" -> "Update project...". V otevřeném okně potvrdit kliknutím na tlačítko "OK". Poté by již měl projekt nastartovat.
![Případná aktualizace závislostí](https://github.com/JanSvoboda6/mpc_kry_kryptografie/blob/dev-hwired/CertificationAuthority/Documentation/img/maven-update-project.png?raw=true)

Pozor! certifikáty se vytvoří ve složce se staženým a extrahovaným zdrojovým kódem, ne ve složce workspace Eclipse, která byla vytvořena v rámci sekce "Spuštění Eclipse a vytvoření projektu".