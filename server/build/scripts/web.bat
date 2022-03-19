@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  web startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and WEB_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto execute

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\web-0.0.1-SNAPSHOT-plain.jar;%APP_HOME%\lib\spring-boot-starter-data-jpa-2.5.5.jar;%APP_HOME%\lib\spring-boot-starter-mail-2.5.5.jar;%APP_HOME%\lib\spring-boot-starter-security-2.5.5.jar;%APP_HOME%\lib\spring-boot-starter-web-2.5.5.jar;%APP_HOME%\lib\spring-session-core-2.5.2.jar;%APP_HOME%\lib\jackson-module-json-org-0.9.1.jar;%APP_HOME%\lib\jjwt-jackson-0.11.2.jar;%APP_HOME%\lib\spring-boot-starter-json-2.5.5.jar;%APP_HOME%\lib\docker-java-3.2.6.jar;%APP_HOME%\lib\docker-java-transport-jersey-3.2.6.jar;%APP_HOME%\lib\docker-java-transport-netty-3.2.6.jar;%APP_HOME%\lib\docker-java-core-3.2.6.jar;%APP_HOME%\lib\jackson-datatype-jdk8-2.12.5.jar;%APP_HOME%\lib\jackson-datatype-jsr310-2.12.5.jar;%APP_HOME%\lib\jackson-module-parameter-names-2.12.5.jar;%APP_HOME%\lib\jackson-jaxrs-json-provider-2.12.5.jar;%APP_HOME%\lib\jackson-jaxrs-base-2.12.5.jar;%APP_HOME%\lib\jackson-module-jaxb-annotations-2.12.5.jar;%APP_HOME%\lib\jackson-databind-2.12.5.jar;%APP_HOME%\lib\docker-java-api-3.2.6.jar;%APP_HOME%\lib\jackson-annotations-2.12.5.jar;%APP_HOME%\lib\jackson-core-2.12.5.jar;%APP_HOME%\lib\junit-4.13.1.jar;%APP_HOME%\lib\jjwt-impl-0.11.2.jar;%APP_HOME%\lib\jjwt-api-0.11.2.jar;%APP_HOME%\lib\validation-api-2.0.1.Final.jar;%APP_HOME%\lib\httpmime-4.5.1.jar;%APP_HOME%\lib\jersey-apache-connector-2.33.jar;%APP_HOME%\lib\httpclient-4.5.13.jar;%APP_HOME%\lib\httpcore-4.4.3.jar;%APP_HOME%\lib\mysql-connector-java-8.0.26.jar;%APP_HOME%\lib\h2-1.4.200.jar;%APP_HOME%\lib\spring-boot-starter-aop-2.5.5.jar;%APP_HOME%\lib\spring-boot-starter-jdbc-2.5.5.jar;%APP_HOME%\lib\jakarta.transaction-api-1.3.3.jar;%APP_HOME%\lib\jakarta.persistence-api-2.2.3.jar;%APP_HOME%\lib\hibernate-core-5.4.32.Final.jar;%APP_HOME%\lib\spring-data-jpa-2.5.5.jar;%APP_HOME%\lib\spring-aspects-5.3.10.jar;%APP_HOME%\lib\spring-boot-starter-2.5.5.jar;%APP_HOME%\lib\spring-context-support-5.3.10.jar;%APP_HOME%\lib\jakarta.mail-1.6.7.jar;%APP_HOME%\lib\spring-security-config-5.5.2.jar;%APP_HOME%\lib\spring-security-web-5.5.2.jar;%APP_HOME%\lib\spring-webmvc-5.3.10.jar;%APP_HOME%\lib\spring-boot-autoconfigure-2.5.5.jar;%APP_HOME%\lib\spring-boot-2.5.5.jar;%APP_HOME%\lib\spring-security-core-5.5.2.jar;%APP_HOME%\lib\spring-context-5.3.10.jar;%APP_HOME%\lib\spring-aop-5.3.10.jar;%APP_HOME%\lib\spring-boot-starter-tomcat-2.5.5.jar;%APP_HOME%\lib\spring-web-5.3.10.jar;%APP_HOME%\lib\spring-orm-5.3.10.jar;%APP_HOME%\lib\spring-jdbc-5.3.10.jar;%APP_HOME%\lib\spring-data-commons-2.5.5.jar;%APP_HOME%\lib\spring-tx-5.3.10.jar;%APP_HOME%\lib\spring-beans-5.3.10.jar;%APP_HOME%\lib\spring-expression-5.3.10.jar;%APP_HOME%\lib\spring-core-5.3.10.jar;%APP_HOME%\lib\spring-jcl-5.3.10.jar;%APP_HOME%\lib\hamcrest-core-2.2.jar;%APP_HOME%\lib\jackson-mapper-asl-1.7.4.jar;%APP_HOME%\lib\json-20090211.jar;%APP_HOME%\lib\jcl-over-slf4j-1.7.32.jar;%APP_HOME%\lib\awaitility-4.0.3.jar;%APP_HOME%\lib\aspectjweaver-1.9.7.jar;%APP_HOME%\lib\HikariCP-4.0.3.jar;%APP_HOME%\lib\hibernate-commons-annotations-5.1.2.Final.jar;%APP_HOME%\lib\jboss-logging-3.4.2.Final.jar;%APP_HOME%\lib\jersey-hk2-2.33.jar;%APP_HOME%\lib\javassist-3.27.0-GA.jar;%APP_HOME%\lib\byte-buddy-1.10.22.jar;%APP_HOME%\lib\antlr-2.7.7.jar;%APP_HOME%\lib\jandex-2.2.3.Final.jar;%APP_HOME%\lib\classmate-1.5.1.jar;%APP_HOME%\lib\dom4j-2.1.3.jar;%APP_HOME%\lib\jaxb-runtime-2.3.5.jar;%APP_HOME%\lib\spring-boot-starter-logging-2.5.5.jar;%APP_HOME%\lib\logback-classic-1.2.6.jar;%APP_HOME%\lib\log4j-to-slf4j-2.14.1.jar;%APP_HOME%\lib\jul-to-slf4j-1.7.32.jar;%APP_HOME%\lib\slf4j-api-1.7.32.jar;%APP_HOME%\lib\jersey-client-2.33.jar;%APP_HOME%\lib\jersey-common-2.33.jar;%APP_HOME%\lib\jakarta.annotation-api-1.3.5.jar;%APP_HOME%\lib\snakeyaml-1.28.jar;%APP_HOME%\lib\jakarta.activation-1.2.2.jar;%APP_HOME%\lib\tomcat-embed-websocket-9.0.53.jar;%APP_HOME%\lib\tomcat-embed-core-9.0.53.jar;%APP_HOME%\lib\tomcat-embed-el-9.0.53.jar;%APP_HOME%\lib\hamcrest-2.2.jar;%APP_HOME%\lib\jackson-core-asl-1.7.4.jar;%APP_HOME%\lib\docker-java-transport-3.2.6.jar;%APP_HOME%\lib\commons-io-2.6.jar;%APP_HOME%\lib\commons-compress-1.20.jar;%APP_HOME%\lib\commons-lang-2.6.jar;%APP_HOME%\lib\guava-19.0.jar;%APP_HOME%\lib\bcpkix-jdk15on-1.64.jar;%APP_HOME%\lib\junixsocket-common-2.3.2.jar;%APP_HOME%\lib\junixsocket-native-common-2.3.2.jar;%APP_HOME%\lib\netty-handler-proxy-4.1.68.Final.jar;%APP_HOME%\lib\netty-codec-http-4.1.68.Final.jar;%APP_HOME%\lib\netty-handler-4.1.68.Final.jar;%APP_HOME%\lib\netty-transport-native-epoll-4.1.68.Final-linux-x86_64.jar;%APP_HOME%\lib\netty-transport-native-kqueue-4.1.68.Final-osx-x86_64.jar;%APP_HOME%\lib\commons-codec-1.15.jar;%APP_HOME%\lib\jakarta.xml.bind-api-2.3.3.jar;%APP_HOME%\lib\txw2-2.3.5.jar;%APP_HOME%\lib\istack-commons-runtime-3.0.12.jar;%APP_HOME%\lib\spring-security-crypto-5.5.2.jar;%APP_HOME%\lib\bcprov-jdk15on-1.64.jar;%APP_HOME%\lib\jakarta.ws.rs-api-2.1.6.jar;%APP_HOME%\lib\hk2-locator-2.6.1.jar;%APP_HOME%\lib\hk2-api-2.6.1.jar;%APP_HOME%\lib\hk2-utils-2.6.1.jar;%APP_HOME%\lib\jakarta.inject-2.6.1.jar;%APP_HOME%\lib\netty-codec-socks-4.1.68.Final.jar;%APP_HOME%\lib\netty-codec-4.1.68.Final.jar;%APP_HOME%\lib\netty-transport-native-unix-common-4.1.68.Final.jar;%APP_HOME%\lib\netty-transport-4.1.68.Final.jar;%APP_HOME%\lib\netty-buffer-4.1.68.Final.jar;%APP_HOME%\lib\netty-resolver-4.1.68.Final.jar;%APP_HOME%\lib\netty-common-4.1.68.Final.jar;%APP_HOME%\lib\logback-core-1.2.6.jar;%APP_HOME%\lib\log4j-api-2.14.1.jar;%APP_HOME%\lib\jakarta.activation-api-1.2.2.jar;%APP_HOME%\lib\osgi-resource-locator-1.0.3.jar;%APP_HOME%\lib\aopalliance-repackaged-2.6.1.jar


@rem Execute web
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %WEB_OPTS%  -classpath "%CLASSPATH%" com.jan.web.WebApplication %*

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable WEB_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%WEB_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
