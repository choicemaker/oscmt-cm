@echo off
rem
set "APP_DIR=%~dp0"
set "LIB_DIR=%APP_DIR%lib"
set "APP=com.choicemaker.cm.urm.client.app.UrmBmaApp"

rem Java command (Java 1.7 is required)
set "JAVA=java.exe"

rem Recommended memory allocations
set "JAVA_OPTS=-Xms384M -Xmx512M"

rem Logging configuration
set "JAVA_OPTS=%JAVA_OPTS% -Djava.util.logging.config.file=logging.properties"

rem Embedded E2 settings
set "JAVA_OPTS=%JAVA_OPTS% -DcmInstallablePlatform=com.choicemaker.e2.embed.EmbeddedPlatform"
set "JAVA_OPTS=%JAVA_OPTS% -DcmInstallableConfigurator=com.choicemaker.cm.core.xmlconf.XmlConfigurator"

rem Uncomment the following lines to enable remote debugging
set "JAVA_OPTS=%JAVA_OPTS% -Xdebug -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=n"

@echo CMD=%JAVA% %JAVA_OPTS% -cp "%APP_DIR%;%LIB_DIR%\*" %APP% %*
%JAVA% %JAVA_OPTS% -cp "%APP_DIR%;%LIB_DIR%\*" %APP% %*

