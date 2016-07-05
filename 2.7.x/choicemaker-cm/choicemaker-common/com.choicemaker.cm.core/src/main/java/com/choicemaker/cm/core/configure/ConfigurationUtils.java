package com.choicemaker.cm.core.configure;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.jasypt.digest.StandardStringDigester;
import org.jasypt.digest.StringDigester;
import org.jasypt.digest.config.EnvironmentStringDigesterConfig;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PBEStringCleanablePasswordEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.choicemaker.cm.core.XmlConfException;
import com.choicemaker.cm.core.xmlconf.XmlParserFactory;
import com.choicemaker.util.FileUtilities;
import com.choicemaker.util.Precondition;
import com.choicemaker.util.StringUtils;

public class ConfigurationUtils {

	private ConfigurationUtils() {
	}

	private static final Logger logger = Logger
			.getLogger(ConfigurationUtils.class.getName());

	// private static final FileUtils FILE_UTILS = FileUtils.newFileUtils();

	public static final String DEFAULT_CODE_ROOT = "etc/models/gen";
	public static final String CONFIGURATION_GENERATOR_ELEMENT = "generator";
	public static final String CONFIGURATION_CODE_ROOT = "codeRoot";
	public static final String CONFIGURATION_CORE_ELEMENT = "core";
	public static final String CONFIGURATION_CLASSPATH_ELEMENT = "classpath";
	public static final String CONFIGURATION_WORKINGDIR_ELEMENT = "workingDir";
	public static final String CONFIGURATION_RELOAD_ELEMENT = "reload";
	public static final String CONFIGURATION_MODULE_ELEMENT = "module";
	public static final String SYSTEM_USER_DIR = "user.dir";
	public static final String SYSTEM_JAVA_CLASS_PATH = "java.class.path";

	public static final String ENC_START = "ENC(";
	protected static final int ENC_START_LEN = ENC_START.length();
	public static final String ENC_END = ")";
	protected static final int ENC_END_LEN = ENC_END.length();

	public static StringDigester createStringDigester(Document ignored) {
		// The return value from this method is consistent with the instance
		// used by a default command-line JasyptStringDigestCLI instance. The
		// JaSypt bash script 'digest.sh' uses a default JasyptStringDigestCLI
		// instance if no arguments other than 'input' are specified.
		//
		// If the password digest is computed by a non-default
		// JasyptStringDigestCLI instance (for example, if configuration
		// argument are passed to the 'digest.sh' script), then Document
		// argument to this method must be used to retrieve appropriate
		// configuration parameters from the 'encryption' element.
		EnvironmentStringDigesterConfig config = new EnvironmentStringDigesterConfig();
		StandardStringDigester retVal = new StandardStringDigester();
		retVal.setConfig(config);
		return retVal;
	}

	public static StringEncryptor createTextEncryptor(char[] password) {
		Precondition.assertBoolean("null or empty password", password != null
				&& password.length > 0);
		PBEStringCleanablePasswordEncryptor retVal = new StandardPBEStringEncryptor();
		retVal.setPasswordCharArray(password);
		return retVal;
	}

	public static String decryptText(final String s,
			StringEncryptor encryptor) {
		String retVal = s;
		if (encryptor != null && s != null && s.startsWith(ENC_START)
				&& s.endsWith(ENC_END)) {
			final int stripEnd = s.length() - ENC_END_LEN;
			assert stripEnd >= ENC_START_LEN;
			String s2 = s.substring(ENC_START_LEN, stripEnd);
			retVal = encryptor.decrypt(s2);
		}
		return retVal;
	}

	public static String getTextValue(Element e, StringEncryptor encryptor) {
		String retVal = null;
		if (e != null) {
			retVal = decryptText(e.getText(), encryptor);
		}
		return retVal;
	}

	public static String getChildText(Element e, String name,
			StringEncryptor encryptor) {
		String retVal = null;
		if (e != null && StringUtils.nonEmptyString(name)) {
			retVal = decryptText(e.getChildText(name), encryptor);
		}
		return retVal;
	}

	public static Document readConfigurationFile(String fileName)
			throws XmlConfException {
		Document document = null;
		SAXBuilder builder = XmlParserFactory.createSAXBuilder(false);
		try {
			document = builder.build(fileName);
		} catch (Exception ex) {
			throw new XmlConfException("Internal error.", ex);
		}
		assert document != null;
		return document;
	}

	/**
	 * Equivalent to:
	 *
	 * <pre>
	 * isEncryptionEnabled(readConfigurationFile(fileName));
	 *
	 * <pre>
	 */
	public static boolean isEncryptionEnabled(String fileName)
			throws XmlConfException {
		Document d = readConfigurationFile(fileName);
		return isEncryptionEnabled(d);
	}

	/**
	 * Returns false only if an encryption element is present in the document
	 * and the value of the <code>enabled</code> attribute is false or nonsense
	 * (that is, the enabled attribute is not true).
	 */
	public static boolean isEncryptionEnabled(Document d)
			throws XmlConfException {
		boolean retVal = true;
		if (d != null) {
			Element c = getCore(d);
			Element e = c == null ? null : c.getChild("encryption");
			Attribute a = e == null ? null : e.getAttribute("enabled");
			if (a != null) {
				String s = a.getValue();
				boolean tf = Boolean.parseBoolean(s);
				retVal = tf;
			}
		}
		return retVal;
	}

	/**
	 * Equivalent to:
	 *
	 * <pre>
	 * isPasswordValid(password, readConfigurationFile(fileName));
	 *
	 * <pre>
	 */
	public static boolean isPasswordValid(char[] password, String fileName)
			throws XmlConfException {
		Document d = readConfigurationFile(fileName);
		return isPasswordValid(password, d);
	}

	/**
	 * Returns false only if encryption is enabled, there's a digestValue
	 * specified, and the digest of the password does not equal the specified
	 * digestValue.
	 *
	 * @param password
	 * @param d
	 *            a non-null document
	 */
	public static boolean isPasswordValid(char[] password, Document d) throws XmlConfException {
		boolean retVal = true;
		if (d != null) {
			Element c = getCore(d);
			Element e = c == null ? null : c.getChild("encryption");
			String expected = c == null ? null : e.getChildTextTrim("digestValue");
			if (expected != null && !expected.isEmpty()) {
				StringDigester digester = createStringDigester(d);
				String pw = new String(password);
				retVal = digester.matches(pw, expected);
			}
		}
		return retVal;
	}

	public static File getWorkingDirectory(String configFile,
			Document document, StringEncryptor encryptor)
			throws XmlConfException {
		File wdir = new File(configFile).getAbsoluteFile().getParentFile();
		Element e = getCore(document);
		if (e != null) {
			e = e.getChild(CONFIGURATION_WORKINGDIR_ELEMENT);
			if (e != null) {
				String clearText = getTextValue(e, encryptor);
				wdir = FileUtilities.getAbsoluteFile(wdir, clearText);
			}
		}
		try {
			wdir = wdir.getCanonicalFile();
		} catch (IOException e1) {
			throw new XmlConfException(e1.toString(), e1);
		}
		return wdir;
	}

	public static Element getRoot(Document document) {
		return document.getRootElement();
	}

	public static Element getCore(Document document) {
		return getRoot(document).getChild(CONFIGURATION_CORE_ELEMENT);
	}

	public static List<Element> getModules(Document document) {
		@SuppressWarnings("unchecked")
		List<Element> retVal = getCore(document).getChildren(
				CONFIGURATION_MODULE_ELEMENT);
		return retVal;
	}

	@SuppressWarnings("unchecked")
	public static List<Element> getReloadModules(Document document) {
		List<Element> retVal = Collections.EMPTY_LIST;
		Element e = getCore(document).getChild(CONFIGURATION_RELOAD_ELEMENT);
		if (e != null) {
			retVal = e.getChildren(CONFIGURATION_MODULE_ELEMENT);
		}
		return retVal;
	}

	public static String getClassPath(File wdir, Document document,
			StringEncryptor encryptor) throws XmlConfException {
		String res = System.getProperty(SYSTEM_JAVA_CLASS_PATH);
		try {
			Element e = getCore(document).getChild(
					CONFIGURATION_CLASSPATH_ELEMENT);
			if (e != null) {
				String clearText = getTextValue(e, encryptor);
				res += FileUtilities.toAbsoluteClasspath(wdir, clearText);
			}
			e = getCore(document).getChild(CONFIGURATION_RELOAD_ELEMENT);
			if (e != null) {
				e = e.getChild(CONFIGURATION_CLASSPATH_ELEMENT);
				if (e != null) {
					res += FileUtilities.toAbsoluteClasspath(wdir, e.getText());
				}
			}
		} catch (IOException ex) {
			logger.severe("Problem with classpath: " + ex.toString());
			throw new XmlConfException(ex.toString(), ex);
		}
		return res;
	}

	public static String getCodeRoot(File wdir, Document document,
			StringEncryptor encryptor) throws XmlConfException {
		String retVal = null;
		File f = FileUtilities.resolveFile(wdir, DEFAULT_CODE_ROOT);
		Element e = getCore(document);
		if (e != null) {
			e = e.getChild(CONFIGURATION_GENERATOR_ELEMENT);
			if (e != null) {
				String t = getTextValue(e, encryptor);
				t = t == null ? null : t.trim();
				if (t != null && !t.isEmpty()) {
					f = FileUtilities.resolveFile(wdir, t);
				}
			}
		}
		retVal = f.getAbsolutePath();

		return retVal;
	}

}
