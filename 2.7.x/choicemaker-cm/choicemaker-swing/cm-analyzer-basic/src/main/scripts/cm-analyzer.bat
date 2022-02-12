@rem ***************************************************************************
@rem Copyright (c) 2003, 2019 ChoiceMaker LLC and others.
@rem
@rem This program and the accompanying materials
@rem are made available under the terms of the Eclipse Public License 2.0
@rem which accompanies this distribution, and is available at
@rem https://www.eclipse.org/legal/epl-2.0/
@rem
@rem SPDX-License-Identifier: EPL-2.0
@rem
@rem Contributors:
@rem     ChoiceMaker Technologies, Inc. - initial API and implementation
@rem     ChoiceMaker LLC - version 2.7 and following
@rem ***************************************************************************
@echo off
rem
rem 2014-11-29 rphall
rem Bash script to start CM Analyzer as an embedded E2 application.
rem Tweak the JAVA variable to point at a 1.7 JRE or JDK.
rem Earlier JVMs won't work, and later JVMs haven't been tested.
rem See http://java.sun.com/products/archive
rem
set "APP_DIR=%~dp0"
set "LIB_DIR=%APP_DIR%lib"
set "APP=com.choicemaker.cm.modelmaker.app.ModelMakerApp"

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
rem set "JAVA_OPTS=%JAVA_OPTS% -Xdebug -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=n"

@echo CMD=%JAVA% %JAVA_OPTS% -cp "%LIB_DIR%\*" %APP%
%JAVA% %JAVA_OPTS% -cp "%LIB_DIR%\*" %APP%

