package com.choicemaker.cm.io.xmlenc.base;

import static com.choicemaker.cm.core.PropertyNames.INSTALLABLE_CHOICEMAKER_CONFIGURATOR;
//import static com.choicemaker.cm.io.db.sqlserver.dbom.SqlServerUtils.SQLSERVER_PLUGIN_ID;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.maven.it.util.ResourceExtractor;
import org.apache.xml.security.utils.XMLUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.choicemaker.cm.core.IProbabilityModel;
import com.choicemaker.cm.core.ImmutableProbabilityModel;
import com.choicemaker.cm.core.ImmutableRecordPair;
import com.choicemaker.cm.core.ModelConfigurationException;
import com.choicemaker.cm.core.Record;
import com.choicemaker.cm.core.base.PMManager;
import com.choicemaker.cm.core.xmlconf.XmlConfigurator;
import com.choicemaker.cm.io.xml.base.XmlMarkedRecordPairSource;
import com.choicemaker.demo.simple_person_matching.PersonMrpListComparator;
import com.choicemaker.e2.embed.EmbeddedPlatform;
import com.choicemaker.util.FileUtilities;
import com.choicemaker.xmlencryption.AwsKmsUtils;
import com.choicemaker.xmlencryption.DocumentDecryptor;
import com.choicemaker.xmlencryption.DocumentEncryptor;
import com.choicemaker.xmlencryption.EncryptionParameters;
import com.choicemaker.xmlencryption.SecretKeyInfoFactory;

public class XmlEncMarkedRecordPairIT {

	private static final Logger logger = Logger
			.getLogger(XmlEncMarkedRecordPairIT.class.getName());

	private static final String SIMPLE_CLASS = XmlEncMarkedRecordPairIT.class
			.getSimpleName();

	/**
	 * The properties file that holds security credentials used in this test.
	 * Note that this file should be excluded from version control and should be
	 * created before this test is run. This file may be overridden at run time
	 * by specifying a value for the System property {@link #X}.
	 */
	public static final String CREDENTIALS = "/Users/rphall/Documents/git/oscmt-cm/2.7.x/choicemaker-cmit/cmit-encryption/src/test/resources/santuario-kms_local.properties";

	/**
	 * The name of the System property that specifies at runtime the location of
	 * the properties file that holds security credentials used in this test. If
	 * this System property is defined, it overrides {@link #CREDENTIALS}.
	 */
	public static final String PN_CREDENTIALS = "cm.xmlenc.credentials";

	/** Clear-text MRPS data file */
	public static final String XML_FILE = "/simple_person_matching/etc/traindata/generic_person/mrps/test-01.xml";

	/** The name of the model used by this test */
	public static final String MODEL = "com.choicemaker.cm.simplePersonMatching.Model1";

	/**
	 * The working directory for this test. All paths are relative to this
	 * directory.
	 */
	public static final String WORKING_DIR = "/simple_person_matching";

	/** Default build directory relative to {@link #WORKING_DIR} */
	public static final String BUILD_DIRECTORY = "../target";

	/** The root of Java code that the Verifier results are checked against */
	public static final String EXPECTED_CODE_ROOT = "expected-source";

	public static final String RECORD_CLASSNAME = "com.choicemaker.demo.simple_person_matching.gendata.gend.Person.Person";

	private static Class<?> RECORD_CLASS;

	public static File computeTargetDirectory(File f) throws IOException {
		return new File(f, BUILD_DIRECTORY).getCanonicalFile();
	}

	protected static File createEncryptedXmlFile(DocumentEncryptor encryptor,
			String content) {
		File retVal = null;
		FileOutputStream fos = null;
		try {
			InputStream is = XmlEncMarkedRecordPairIT.class
					.getResourceAsStream(content);
			DocumentBuilder builder = XMLUtils.createDocumentBuilder(false);
			Document doc = builder.parse(is);
			encryptor.encrypt(doc);
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			retVal = File.createTempFile("xmlenc-test", ".xmlenc");
			fos = new FileOutputStream(retVal);
			StreamResult result = new StreamResult(fos);
			transformer.transform(source, result);
		} catch (Exception x) {
			fail(x.toString());
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					logger.warning(e.toString());
					;
				}
			}
			fos = null;
		}
		return retVal;
	}

	protected static List<ImmutableRecordPair> readClearTextMRPS(
			String xmlFile, ImmutableProbabilityModel model) {
		List<ImmutableRecordPair> retVal = new ArrayList<>();
		XmlMarkedRecordPairSource clearSource = null;
		try {
			InputStream is = XmlEncMarkedRecordPairIT.class
					.getResourceAsStream(xmlFile);
			clearSource = new XmlMarkedRecordPairSource(is, "fake.mrps",
					xmlFile, model);
			clearSource.open();
			while (clearSource.hasNext()) {
				ImmutableRecordPair mrp = clearSource.getNext();
				Record q = mrp.getQueryRecord();
				assertTrue(RECORD_CLASS.isInstance(q));
				Record m = mrp.getMatchRecord();
				assertTrue(RECORD_CLASS.isInstance(m));
				retVal.add(mrp);
			}
		} catch (Exception x) {
			fail(x.toString());
		} finally {
			if (clearSource != null) {
				clearSource.close();
			}
			clearSource = null;
		}
		return Collections.unmodifiableList(retVal);
	}

	protected static List<ImmutableRecordPair> readEncryptedMRPS(
			DocumentDecryptor decryptor, String xmlencFile,
			ImmutableProbabilityModel model) {
		List<ImmutableRecordPair> retVal = new ArrayList<>();
		XmlMarkedRecordPairSource clearSource = null;
		try {
			InputStream is = XmlEncMarkedRecordPairIT.class
					.getResourceAsStream(xmlencFile);
			clearSource = new XmlEncMarkedRecordPairSource(is, "fake",
					xmlencFile, model, decryptor);
			clearSource.open();
			while (clearSource.hasNext()) {
				ImmutableRecordPair mrp = clearSource.getNext();
				Record q = mrp.getQueryRecord();
				assertTrue(RECORD_CLASS.isInstance(q));
				Record m = mrp.getMatchRecord();
				assertTrue(RECORD_CLASS.isInstance(m));
				retVal.add(mrp);
			}
		} catch (Exception x) {
			fail(x.toString());
		} finally {
			if (clearSource != null) {
				clearSource.close();
			}
			clearSource = null;
		}
		return Collections.unmodifiableList(retVal);
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		final String METHOD = "XmlEncMarkedRecordPairSourceIT.setUpBeforeClass: ";
		EmbeddedPlatform.install();
		String pn = INSTALLABLE_CHOICEMAKER_CONFIGURATOR;
		String pv = XmlConfigurator.class.getName();
		System.setProperty(pn, pv);
		try {
			int count = PMManager.loadModelPlugins();
			if (count == 0) {
				String msg = METHOD + "No probability models loaded";
				logger.warning(msg);
			}
			IProbabilityModel[] models = PMManager.getModels();
			boolean isModelLoaded = false;
			for (IProbabilityModel model : models) {
				logger.fine(model.getModelName());
				if (MODEL.equals(model.getModelName())) {
					isModelLoaded = true;
					break;
				}
			}
			if (!isModelLoaded) {
				String msg = METHOD + MODEL + " is not available";
				logger.severe(msg);
				throw new IllegalStateException(msg);
			}
		} catch (ModelConfigurationException | IOException e) {
			String msg = METHOD + "Unable to load model plugins: "
					+ e.toString();
			logger.severe(msg);
			throw new IllegalStateException(msg);
		}

		RECORD_CLASS = Class.forName(RECORD_CLASSNAME);
	}

	protected static File writeEncryptedMRPS(DocumentEncryptor encryptor,
			List<ImmutableRecordPair> mrps, ImmutableProbabilityModel model)
			throws IOException {

		// Create the name of a temporary file (and delete the file)
		final File tmp = File.createTempFile("xmlenc-test", ".xmlenc");
		final String xmlencFileName = tmp.getAbsolutePath();
		boolean deleted = tmp.delete();
		assertTrue(deleted);

		XmlEncMarkedRecordPairSink encSink = null;
		try {
			encSink = new XmlEncMarkedRecordPairSink("fake.mrps",
					xmlencFileName, model, encryptor);
			encSink.open();
			for (ImmutableRecordPair mrp : mrps) {
				encSink.put(mrp);
			}

		} catch (Exception x) {
			fail(x.toString());

		} finally {
			if (encSink != null) {
				encSink.close();
			}
			encSink = null;
		}

		File retVal = new File(xmlencFileName);
		assertTrue(retVal.exists());
		assertTrue(retVal.canRead());

		return retVal;
	}

	private File workingDir;

	private DocumentDecryptor decryptor;

	private DocumentEncryptor encryptor;

	@Before
	public void setUp() {
		// Set up the working directory for a test
		Class<? extends XmlEncMarkedRecordPairIT> c = getClass();
		workingDir = null;
		try {
			workingDir = ResourceExtractor.simpleExtractResources(c,
					WORKING_DIR);
			logger.fine("workingDir: " + workingDir);
			assertTrue(workingDir != null);

			File targetDir = computeTargetDirectory(workingDir);
			logger.fine("targetDir: " + targetDir);
			if (targetDir.exists()) {
				FileUtilities.removeDir(targetDir);
			}
		} catch (IOException e) {
			fail(e.toString());
		}

		String credsfn = System.getProperty(PN_CREDENTIALS, CREDENTIALS);
		File f = new File(credsfn);
		Properties credProps = null;
		try {
			FileInputStream fis = new FileInputStream(f);
			credProps = new Properties();
			credProps.load(fis);
		} catch (IOException e) {
			fail(e.toString());
		}

		try {
			final boolean isHelp = false;
			final List<String> errors = Collections.emptyList();
			final File inputFile = null;
			final EncryptionParameters params = new EncryptionParameters(
					isHelp, errors, credProps, inputFile);
			final AWSCredentials creds = new BasicAWSCredentials(
					params.getAwsAccessKey(), params.getAwsSecretkey());
			final SecretKeyInfoFactory skif = new SecretKeyInfoFactory(
					params.getAwsMasterKeyId(),
					AwsKmsUtils.DEFAULT_AWS_KEY_ENCRYPTION_ALGORITHM,
					params.getAwsEndpoint(), creds);

			decryptor = new DocumentDecryptor(params.getAwsEndpoint(), creds);
			encryptor = new DocumentEncryptor(skif);
		} catch (Exception x) {
			fail(x.toString());
		}

	}

	@Test
	public void testDecryption() throws NoSuchMethodException {
		final String METHOD = "testDecryption";
		logger.entering(SIMPLE_CLASS, METHOD);

		// Retrieve the model for the marked record pairs
		ImmutableProbabilityModel model = PMManager
				.getImmutableModelInstance(MODEL);
		assertTrue(model != null);

		// Read in a list of MRP's from a clear text file
		final List<ImmutableRecordPair> mrps = readClearTextMRPS(XML_FILE,
				model);

		// Encrypt the file
		final File encryptedFile = createEncryptedXmlFile(encryptor, XML_FILE);
		final String xmlencFileName = encryptedFile.getAbsolutePath();

		// Read in a list of MRP's from the encrypted file
		final List<ImmutableRecordPair> emrps = readEncryptedMRPS(decryptor,
				xmlencFileName, model);

		// Compare the MRP lists
		PersonMrpListComparator mrpsComparator = new PersonMrpListComparator();
		boolean equalLists = mrpsComparator.areEqual(mrps, emrps);
		assertTrue(equalLists);

		logger.exiting(SIMPLE_CLASS, METHOD);
	}

	/**
	 * Implicitly assumes {@link #testDecryption() decryption} is already
	 * tested.
	 */
	@Test
	public void testEncryption() throws IOException, Exception {
		final String METHOD = "testSqlServerExtensions";
		logger.entering(SIMPLE_CLASS, METHOD);

		// Retrieve the model for the marked record pairs
		ImmutableProbabilityModel model = PMManager
				.getImmutableModelInstance(MODEL);
		assertTrue(model != null);

		// Read in a list of MRP's from a clear text file
		final List<ImmutableRecordPair> mrps = readClearTextMRPS(XML_FILE,
				model);

		// Write the MRP's to an encrypted file
		final File encryptedFile = writeEncryptedMRPS(encryptor, mrps, model);
		final String xmlencFileName = encryptedFile.getAbsolutePath();

		// Read in a list of MRP's from the encrypted file
		final List<ImmutableRecordPair> emrps = readEncryptedMRPS(decryptor,
				xmlencFileName, model);

		// Compare the MRP lists
		PersonMrpListComparator mrpsComparator = new PersonMrpListComparator();
		boolean equalLists = mrpsComparator.areEqual(mrps, emrps);
		assertTrue(equalLists);

		logger.exiting(SIMPLE_CLASS, METHOD);
	}
}
