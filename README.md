# PROJEKT - ZABEZPEČENÉ ÚLOŽIŠTĚ

**Demo ukázka**: [demo_05](https://drive.google.com/drive/u/1/folders/1OWAeGiqHXlsqJpuPy8OdtXMclCjfLnNg) <br/>
**Produkční prostředí**: https://www.securestorage.website.<br />
**Upozornění**: Je použit self-signed certifikát. Je proto nutné instalovat certifikát do lokálního počítače.[Certifikát](https://drive.google.com/drive/u/1/folders/1OWAeGiqHXlsqJpuPy8OdtXMclCjfLnNg)

DOKUMENTACE
===
Celý text dokumentace je dostupný [zde](https://docs.google.com/document/d/1-vZJSSBGAZJ8tjcOkN6NLF4O7KlfCc14oJ300SroZno/edit). <br />
Aplikace je tvořena klientskou částí za použití knihovny React. <br />
V rámci webového serveru je aplikační kód postaven na aplikačním rámci Spring.

Klienstká část - React
===
Stežejní částí je komponenta pro generování klíče na straně uživatele. [`CryptoPage.tsx`](https://github.com/JanSvoboda6/mpc_kry_kryptografie/blob/main/client/src/components/crypto/CryptoPage.tsx). <br />

Po stanovení klíče je uživateli umožněno přistoupit na komponentu [`FileHandler.tsx`](https://github.com/JanSvoboda6/mpc_kry_kryptografie/blob/main/client/src/components/file/FileHandler.tsx).

Na úrovni klienstké aplikace je použito šifrování pomocí symetrické šifry AES (soubor [`CryptoService.js`](https://github.com/JanSvoboda6/mpc_kry_kryptografie/blob/main/client/src/components/crypto/CryptoService.js)). Na server jsou tak zasílany jen soubory (pomocí [`FileService.tsx`](https://github.com/JanSvoboda6/mpc_kry_kryptografie/blob/main/client/src/components/file/FileService.tsx)), které byly již 
klientem zašifrovány. Server je tak pouhým úložištěm těchto šifrovaných souborů. Pokud uživatel potřebuje své již uložené soubory na serveru stáhnout,
je mu zašifrovaný obsah ze serveru zaslán. V klientské části aplikace je pak obsah souborů dešifrován a uložen na lokální počítač.

Webový server - Spring
===
Spring definuje anotaci `@Controller`. Třídy takto anotované jsou vstupním bodem do aplikace. Probíhá zde mapování HTTP dotazu na Java objekty.
API odpovídá pomocí formátu JSON. <br />

Důležité třídy pro interakci s vnějším volajícím jsou: <br />
[`FileController.java`](https://github.com/JanSvoboda6/mpc_kry_kryptografie/blob/main/server/src/main/java/com/web/file/FileController.java) - zprostředkovává přístup k souborům. <br />
[`AuthenticationController.java`](https://github.com/JanSvoboda6/mpc_kry_kryptografie/blob/main/server/src/main/java/com/web/security/authentication/AuthenticationController.java) - umožňuje registraci uživatele, následnou verifikaci uživatelského účtu a přihlášení. <br />

Logy jsou ukládány do souboru `server/log_secure_storage.log` či `server/log_secure_storage_dev.log` v závislosti na konfiguraci (PROD či DEV).

Lokální konfigurace
===
Lokální konfiguraci je doporučeno použít pro vývoj, popřípadě pro kontrolu kódu projektu. Pro verifikaci funkcionality, prosím, použijte produkční prostředí dostupné na adrese https://www.securestorage.website.
1. Stáhnutí VM na adrese: https://drive.google.com/drive/u/1/folders/1LVJd7Ku5segg078bTi1CUhJ__aTS2V5s
2. Import do VirtualBox
3. Otevřít terminal
4. `cd app/mpc_kry_kryptografie/client`
5. `npm install`
6. `npm run build`
8. `npm start`
9.  Otevřít další okno terminálu
10. `cd app/mpc_kry_kryptografie/server`
11. `./gradlew run`
12. Klientská aplikace je dostupná na portu 3000, API na portu 8088

Schémata
===
**Architektura** <br />
<img src="https://github.com/JanSvoboda6/mpc_kry_kryptografie/blob/main/documentation/architecture.png" width="790"/>

**Autorizace pomocí JWT** <br />
<img src="https://github.com/JanSvoboda6/mpc_kry_kryptografie/blob/main/documentation/jwt.png" width="790"/>

**Proces manipulace se soubory** <br />
<img src="https://github.com/JanSvoboda6/mpc_kry_kryptografie/blob/main/documentation/flow.png" width="800"/>

