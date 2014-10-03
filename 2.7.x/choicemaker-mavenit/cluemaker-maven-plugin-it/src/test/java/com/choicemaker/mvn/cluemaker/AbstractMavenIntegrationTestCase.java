package com.choicemaker.mvn.cluemaker;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.apache.maven.shared.utils.io.FileUtils;

/**
 * @author Jason van Zyl
 * @author Kenney Westerhof
 */
public abstract class AbstractMavenIntegrationTestCase
    extends TestCase
{
    /**
     * Save System.out for progress reports etc.
     */
    protected static PrintStream out = System.out;

    /**
     * The format for elapsed time.
     */
    private static final DecimalFormat SECS_FORMAT =
        new DecimalFormat( "(0.0 s)", new DecimalFormatSymbols( Locale.ENGLISH ) );

    /**
     * The zero-based column index where to print the test result.
     */
    private static final int RESULT_COLUMN = 60;

    private boolean skip;

    private static ArtifactVersion javaVersion;

    private ArtifactVersion mavenVersion;

    private VersionRange versionRange;

    private String matchPattern;

    private static final String DEFAULT_MATCH_PATTERN = "(.*?)-(RC[0-9]+|SNAPSHOT|RC[0-9]+-SNAPSHOT)";

    protected static final String ALL_MAVEN_VERSIONS = "[2.0,)";

    protected AbstractMavenIntegrationTestCase( String versionRangeStr )
    {
        this( versionRangeStr, DEFAULT_MATCH_PATTERN );
    }

    protected AbstractMavenIntegrationTestCase( String versionRangeStr, String matchPattern )
    {
        this.matchPattern = matchPattern;

        try
        {
            versionRange = VersionRange.createFromVersionSpec( versionRangeStr );
        }
        catch ( InvalidVersionSpecificationException e)
        {
            throw (RuntimeException) new IllegalArgumentException( "Invalid version range: " + versionRangeStr ).initCause( e );
        }

        ArtifactVersion version = getMavenVersion();
        if ( version != null )
        {
            skip = !versionRange.containsVersion( removePattern( version ) );
        }
        else
        {
            out.println( "WARNING: " + getITName() + ": version range '" + versionRange
                + "' supplied but no Maven version - not skipping test." );
        }
    }

    /**
     * Gets the Java version used to run this test.
     *
     * @return The Java version, never <code>null</code>.
     */
    private ArtifactVersion getJavaVersion()
    {
        if ( javaVersion == null )
        {
            String version = System.getProperty( "java.version" );
            version = version.replaceAll( "[_-]", "." );
            Matcher matcher = Pattern.compile( "(?s).*?(([0-9]+\\.[0-9]+)(\\.[0-9]+)?).*" ).matcher( version );
            if ( matcher.matches() )
            {
                version = matcher.group( 1 );
            }
            javaVersion = new DefaultArtifactVersion( version );
        }
        return javaVersion;
    }

    /**
     * Gets the Maven version used to run this test.
     *
     * @return The Maven version or <code>null</code> if unknown.
     */
    private ArtifactVersion getMavenVersion()
    {
        if ( mavenVersion == null )
        {
            String version = System.getProperty( "maven.version", "" );

            if ( version.length() <= 0 || version.startsWith( "${" ) )
            {
                try
                {
                    Verifier verifier = new Verifier( "" );
                    try
                    {
                        version = verifier.getMavenVersion();
                        System.setProperty( "maven.version", version );
                    }
                    finally
                    {
                        verifier.resetStreams();
                    }
                }
                catch ( VerificationException e )
                {
                    e.printStackTrace();
                }
            }

            // NOTE: If the version looks like "${...}" it has been configured from an undefined expression
            if ( version != null && version.length() > 0 && !version.startsWith( "${" ) )
            {
                mavenVersion = new DefaultArtifactVersion( version );
            }
        }
        return mavenVersion;
    }

    /**
     * This allows fine-grained control over execution of individual test methods
     * by allowing tests to adjust to the current Maven version, or else simply avoid
     * executing altogether if the wrong version is present.
     */
    protected boolean matchesVersionRange( String versionRangeStr )
    {
        VersionRange versionRange;
        try
        {
            versionRange = VersionRange.createFromVersionSpec( versionRangeStr );
        }
        catch ( InvalidVersionSpecificationException e)
        {
            throw (RuntimeException) new IllegalArgumentException( "Invalid version range: " + versionRangeStr ).initCause( e );
        }

        ArtifactVersion version = getMavenVersion();
        if ( version != null )
        {
            return versionRange.containsVersion( removePattern( version ) );
        }
        else
        {
            out.println( "WARNING: " + getITName() + ": version range '" + versionRange
                + "' supplied but no Maven version found - returning true for match check." );

            return true;
        }
    }

    protected void runTest()
        throws Throwable
    {
        String line = getTestName();
        out.print( line );
        out.print( pad( RESULT_COLUMN - line.length() ) );

        if ( skip )
        {
            out.println( "SKIPPED - Maven version " + getMavenVersion() + " not in range " + versionRange );
            return;
        }

        if ( "true".equals( System.getProperty( "useEmptyLocalRepository", "false" ) ) )
        {
            setupLocalRepo();
        }

        long milliseconds = System.currentTimeMillis();
        try
        {
            super.runTest();
            milliseconds = System.currentTimeMillis() - milliseconds;
            out.println( "OK " + formatTime( milliseconds ) );
        }
        catch ( UnsupportedJavaVersionException e )
        {
            out.println( "SKIPPED - Java version " + e.unsupportedVersion + " not in range " + e.supportedRange );
            return;
        }
        catch ( UnsupportedMavenVersionException e )
        {
            out.println( "SKIPPED - Maven version " + e.unsupportedVersion + " not in range " + e.supportedRange );
            return;
        }
        catch ( Throwable t )
        {
            milliseconds = System.currentTimeMillis() - milliseconds;
            out.println( "FAILURE " + formatTime( milliseconds ) );
            throw t;
        }
    }

    /**
     * Guards the execution of a test case by checking that the current Java version matches the specified version
     * range. If the check fails, an exception will be thrown which aborts the current test and marks it as skipped. One
     * would usually call this method right at the start of a test method.
     *
     * @param versionRange The version range that specifies the acceptable Java versions for the test, must not be
     *            <code>null</code>.
     */
    protected void requiresJavaVersion( String versionRange )
    {
        VersionRange range;
        try
        {
            range = VersionRange.createFromVersionSpec( versionRange );
        }
        catch ( InvalidVersionSpecificationException e )
        {
            throw (RuntimeException) new IllegalArgumentException( "Invalid version range: " + versionRange ).initCause( e );
        }

        ArtifactVersion version = getJavaVersion();
        if ( !range.containsVersion( version ) )
        {
            throw new UnsupportedJavaVersionException( version, range );
        }
    }

    /**
     * Guards the execution of a test case by checking that the current Maven version matches the specified version
     * range. If the check fails, an exception will be thrown which aborts the current test and marks it as skipped. One
     * would usually call this method right at the start of a test method.
     *
     * @param versionRange The version range that specifies the acceptable Maven versions for the test, must not be
     *            <code>null</code>.
     */
    protected void requiresMavenVersion( String versionRange )
    {
        VersionRange range;
        try
        {
            range = VersionRange.createFromVersionSpec( versionRange );
        }
        catch ( InvalidVersionSpecificationException e )
        {
            throw (RuntimeException) new IllegalArgumentException( "Invalid version range: " + versionRange ).initCause( e );
        }

        ArtifactVersion version = getMavenVersion();
        if ( version != null )
        {
            if ( !range.containsVersion( removePattern( version ) ) )
            {
                throw new UnsupportedMavenVersionException( version, range );
            }
        }
        else
        {
            out.println( "WARNING: " + getITName() + ": version range '" + versionRange
                + "' supplied but no Maven version found - not skipping test." );
        }
    }

    private class UnsupportedJavaVersionException
        extends RuntimeException
    {

        private static final long serialVersionUID = 1L;

		public ArtifactVersion unsupportedVersion;

        public VersionRange supportedRange;

        public UnsupportedJavaVersionException( ArtifactVersion javaVersion, VersionRange supportedRange )
        {
            this.unsupportedVersion = javaVersion;
            this.supportedRange = supportedRange;
        }

    }

    private class UnsupportedMavenVersionException
        extends RuntimeException
    {

        private static final long serialVersionUID = 1L;

		public ArtifactVersion unsupportedVersion;

        public VersionRange supportedRange;

        public UnsupportedMavenVersionException( ArtifactVersion mavenVersion, VersionRange supportedRange )
        {
            this.unsupportedVersion = mavenVersion;
            this.supportedRange = supportedRange;
        }

    }

    private String getITName()
    {
        String simpleName = getClass().getName();
        int idx = simpleName.lastIndexOf( '.' );
        simpleName = idx >= 0 ? simpleName.substring( idx + 1 ) : simpleName;
        simpleName = simpleName.startsWith( "MavenIT" ) ? simpleName.substring( "MavenIT".length() ) : simpleName;
        simpleName = simpleName.endsWith( "Test" ) ? simpleName.substring( 0, simpleName.length() - 4 ) : simpleName;
        return simpleName;
    }

    protected String getTestName()
    {
        String className = getITName();
        String methodName = getName();
        if ( methodName.startsWith( "test" ) )
        {
            methodName = methodName.substring( 4 );
        }
        return className + '(' + methodName + ')';
    }

    private String pad( int chars )
    {
        StringBuffer buffer = new StringBuffer( 128 );
        for ( int i = 0; i < chars; i++ )
        {
            buffer.append( '.' );
        }
        return buffer.toString();
    }

    private String formatTime( long milliseconds )
    {
        return SECS_FORMAT.format( milliseconds / 1000.0 );
    }

    protected File setupLocalRepo()
        throws IOException
    {
        String tempDirPath = System.getProperty( "maven.it.tmpdir", System.getProperty( "java.io.tmpdir" ) );
        File localRepo = new File( tempDirPath, "local-repository/" + getITName() );
        if ( localRepo.isDirectory() )
        {
            FileUtils.deleteDirectory( localRepo );
        }

        System.setProperty( "maven.repo.local", localRepo.getAbsolutePath() );

        return localRepo;
    }

    ArtifactVersion removePattern( ArtifactVersion version )
    {
        String v = version.toString();

        Matcher m = Pattern.compile( matchPattern ).matcher( v );

        if ( m.matches() )
        {
            return new DefaultArtifactVersion( m.group( 1 ) );
        }
        return version;
    }

    protected Verifier newVerifier( String basedir )
        throws VerificationException
    {
        return newVerifier( basedir, false );
    }

    protected Verifier newVerifier( String basedir, String settings )
        throws VerificationException
    {
        return newVerifier( basedir, settings, false );
    }

    protected Verifier newVerifier( String basedir, boolean debug )
        throws VerificationException
    {
        return newVerifier( basedir, "", debug );
    }

    @SuppressWarnings("unchecked")
	protected Verifier newVerifier( String basedir, String settings, boolean debug )
        throws VerificationException
    {
        Verifier verifier = new Verifier( basedir, debug );

        verifier.setAutoclean( false );

        if ( settings != null )
        {
            File settingsFile;
            if ( settings.length() > 0 )
            {
                settingsFile = new File( "settings-" + settings + ".xml" );
            }
            else
            {
                settingsFile = new File( "settings.xml" );
            }

            if ( !settingsFile.isAbsolute() )
            {
                String settingsDir = System.getProperty( "maven.it.global-settings.dir", "" );
                if ( settingsDir.length() > 0 )
                {
                    settingsFile = new File( settingsDir, settingsFile.getPath() );
                }
                else
                {
                    //
                    // Make is easier to run ITs from m2e in Maven IT mode without having to set any additional properties.
                    //
                    settingsFile = new File( "target/test-classes", settingsFile.getPath() );
                }
            }

            String path = settingsFile.getAbsolutePath();

            // dedicated CLI option only available since MNG-3914
            if ( matchesVersionRange( "[2.1.0,)" ) )
            {
                verifier.getCliOptions().add( "--global-settings" );
                if ( path.indexOf( ' ' ) < 0 )
                {
                    verifier.getCliOptions().add( path );
                }
                else
                {
                    verifier.getCliOptions().add( '"' + path + '"' );
                }
            }
            else
            {
                verifier.getSystemProperties().put( "org.apache.maven.global-settings", path );
            }
        }

        return verifier;
    }

    static public void assertCanonicalFileEquals( String message, File expected, File actual )
        throws IOException
    {
        assertEquals( message, expected.getCanonicalFile(), actual.getCanonicalFile() );
    }

    static public void assertCanonicalFileEquals( File expected, File actual )
        throws IOException
    {
        assertCanonicalFileEquals( null, expected, actual );
    }

    static public void assertCanonicalFileEquals( String message, String expected, String actual )
        throws IOException
    {
        assertCanonicalFileEquals( message, new File( expected ), new File( actual ) );
    }

    static public void assertCanonicalFileEquals( String expected, String actual )
        throws IOException
    {
        assertCanonicalFileEquals( null, new File( expected ), new File( actual ) );
    }
}
