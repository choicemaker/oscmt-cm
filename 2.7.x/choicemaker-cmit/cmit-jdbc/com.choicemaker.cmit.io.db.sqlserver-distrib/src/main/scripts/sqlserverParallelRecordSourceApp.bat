echo off
rem  2018-10-24 rphall
rem  Batch script to start the command-line SqlServer RecordSourceSnapshot application.
rem  Requuires a 1.8 JRE or JDK in the PATH.  Earlier JVMs won't work, and
rem  later JVMs haven't been tested.

set APP=com.choicemaker.cmit.io.db.sqlserver.SqlServerParallelRecordSourceApp
set LIB=lib

rem  Java command
set JAVA=java

rem  Compute the class path
set "CP=%LIB%\com.choicemaker.cmit.io.db.sqlserver.jar"
set "CP=%CP%;%LIB%\com.choicemaker.cmit.io.db.oracle.jar"
set "CP=%CP%;%LIB%\ant.jar"
set "CP=%CP%;%LIB%\ant-launcher.jar"
set "CP=%CP%;%LIB%\bcel.jar"
set "CP=%CP%;%LIB%\c3p0.jar"
set "CP=%CP%;%LIB%\choicemaker-assembly.jar"
set "CP=%CP%;%LIB%\choicemaker-util.jar"
set "CP=%CP%;%LIB%\com.choicemaker.client.api.jar"
set "CP=%CP%;%LIB%\com.choicemaker.cm.aba.base.jar"
set "CP=%CP%;%LIB%\com.choicemaker.cm.args.jar"
set "CP=%CP%;%LIB%\com.choicemaker.cm.compiler.jar"
set "CP=%CP%;%LIB%\com.choicemaker.cm.core.jar"
set "CP=%CP%;%LIB%\com.choicemaker.cm.io.blocking.base.jar"
set "CP=%CP%;%LIB%\com.choicemaker.cm.io.composite.base.jar"
set "CP=%CP%;%LIB%\com.choicemaker.cm.io.db.base.jar"
set "CP=%CP%;%LIB%\com.choicemaker.cm.io.db.oracle.jar"
set "CP=%CP%;%LIB%\com.choicemaker.cm.io.db.sqlserver.jar"
set "CP=%CP%;%LIB%\com.choicemaker.cm.matching.gen.jar"
set "CP=%CP%;%LIB%\com.choicemaker.cm.ml.me.base.jar"
set "CP=%CP%;%LIB%\com.choicemaker.e2.embed.jar"
set "CP=%CP%;%LIB%\com.choicemaker.e2.jar"
set "CP=%CP%;%LIB%\jasypt-1.9.2-lite.jar"
set "CP=%CP%;%LIB%\jdom2.jar"
set "CP=%CP%;%LIB%\mchange-commons-java.jar"
set "CP=%CP%;%LIB%\sqljdbc4.jar"
set "CP=%CP%;%LIB%\simple-person-plugin.jar"

rem echo.
rem echo CP = %CP%

set "JAVA_OPTS="

rem  Recommended memory allocations
rem set "JAVA_OPTS=-Xms584M -Xmx512M"

rem  Uncomment the following line to specify the logging configuration
set "JAVA_OPTS=%JAVA_OPTS% -Djava.util.logging.config.file=logging.properties"
set "JAVA_OPTS=%JAVA_OPTS% -DpropertyFile=sqlserver_jdbc.properties"

rem  Uncomment the following line to enable assertions
set "JAVA_OPTS=%JAVA_OPTS% -ea"

rem  Uncomment the following line to enable remote debugging
rem set "JAVA_OPTS=%JAVA_OPTS% -debug"
rem set "JAVA_OPTS=%JAVA_OPTS% -Xdebug -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=y"

rem echo.
rem echo JAVA_OPTS = %JAVA_OPTS%

set "CMD=%JAVA% %JAVA_OPTS% -cp %CP% %APP%"
rem set "CMD=%JAVA% %JAVA_OPTS% %APP%"
rem set "CMD=%JAVA% %APP%"

rem echo.
rem echo CMD = %CMD%
rem echo.

%CMD% 2>NUL
rem echo.
