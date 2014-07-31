/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.choicemaker.fake;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Singleton that implements PluginDiscovery
 * 
 * @author rphall
 */
public class EmbeddedPluginDiscovery implements PluginDiscovery {

	public static final String COMMENT_SYMBOL = "#";
	public static final String PREFIX = "META-INF/plugins/";
	public static final String DEFAULT_PLUGIN_CONFIGURATION = "default";
	public static final String PLUGIN_DESCRIPTOR_FILE = "plugin.xml";
	public static final String FRAGMENT_DESCRIPTOR_FILE = "fragment.xml";

	private static void fail(String msg, Throwable cause)
			throws PluginDiscoveryException {
		throw new PluginDiscoveryException(msg, cause);
	}

	// The plugin configuration to load
	private String configuration;

	// The class loader used to locate, load, and instantiate providers
	private ClassLoader loader;

	// Cached plugin URLs, in discovery order
	private LinkedHashMap<PluginIdVersionType, URL> plugins =
		new LinkedHashMap<>();

	// The current lazy-lookup iterator
	private PluginConfigurationIterator lookupIterator;

	/**
	 * Looks up the default configuration of Eclipse 2 plugins using the context
	 * ClassLoader of the current thread.
	 */
	public EmbeddedPluginDiscovery() {
		this(DEFAULT_PLUGIN_CONFIGURATION);
	}

	/**
	 * Looks up the specified configuration of Eclipse 2 plugins using the
	 * context ClassLoader of the current thread.
	 * 
	 * @param configuration
	 *            the name of the plugin configuration. If the name is null or
	 *            blank, the default configuration is used.
	 */
	public EmbeddedPluginDiscovery(String configuration) {
		this(configuration, Thread.currentThread().getContextClassLoader());
	}

	/**
	 * Looks up the specified configuration of Eclipse 2 plugins using the
	 * specified class loader. If the class loader is null, looks for plugin
	 * configurations in the System resources.
	 * 
	 * @param configuration
	 *            the name of the plugin configuration. If the name is null or
	 *            blank, the default configuration is used.
	 * @param cl
	 *            the class loader to use when looking for the plugins in the
	 *            specified configuration. If the class loader is null or blank,
	 *            looks for plugin configurations in the System resources.
	 */
	public EmbeddedPluginDiscovery(String config, ClassLoader cl) {
		if (config == null) {
			this.configuration = DEFAULT_PLUGIN_CONFIGURATION;
		} else {
			config = config.trim();
			if (config.isEmpty()) {
				this.configuration = DEFAULT_PLUGIN_CONFIGURATION;
			} else {
				this.configuration = config;
			}
		}
		loader = cl;
		reload();
	}

	public void reload() {
		Set<URL> newURL = new HashSet<>();
		plugins.clear();
		lookupIterator = new PluginConfigurationIterator(configuration, loader);
		while (lookupIterator.hasNext()) {

			// Get the next descriptor URL
			URL u = lookupIterator.next();

			// Fail if the descriptor is a duplicate
			boolean isNew = newURL.add(u);
			if (!isNew) {
				fail("Duplicate URL: " + u, null);
			}

			// Parse the descriptor URL
			PluginIdVersionType pivt = null;
			try {
				pivt = parseDescriptor(u);
			} catch (SAXException | ParserConfigurationException | IOException e) {
				fail("Descriptor parsing failed for " + u, e);
			}
			assert pivt != null;

			// Add a mapping for the plugin or fragment to the other mappings
			URL u0 = plugins.put(pivt, u);

			// Fail if there are mapping conflicts
			if (u0 != null) {
				StringBuilder sb =
					new StringBuilder()
							.append("Conflicting URLs for " + pivt.toString())
							.append(":  urls[ ").append(u0 + ", ")
							.append(u + "]");
				fail(sb.toString(), null);
			}
		}
	}

	@Override
	public Set<URL> getPluginUrls() {
		Set<URL> retVal = new HashSet<>();
		retVal.addAll(plugins.values());
		return Collections.unmodifiableSet(retVal);
	}

	/**
	 * Parse the content of the given URL as a configuration of plugins. A
	 * configuration is a file that lists paths to plugin or fragment
	 * descriptors (plugin.xml or fragment.xml, respectively). The paths are
	 * typically relative to the listing file, although they may be absolute.
	 * Paths are listed separate lines within the file. Blank lines are ignored.
	 * Lines beginning with a hash symbol ('#') are ignored.
	 * 
	 * @param u
	 *            The URL naming the configuration file to be parsed
	 * @return A (possibly empty) iterator that will yield the plugin or
	 *         fragment URLs in the given configuration file that are not yet
	 *         members of the returned set
	 * @throws PluginDiscoveryException
	 *             If an I/O error occurs while reading from the given URL, or
	 *             if a configuration-file format error is detected
	 */
	private Iterator<URL> parseConfiguration(URL config)
			throws PluginDiscoveryException {
		if (config == null) {
			throw new IllegalArgumentException("null base URL");
		}
		Set<URL> urls = new LinkedHashSet<>();
		InputStream in = null;
		try {
			in = config.openStream();
			BufferedReader reader =
				new BufferedReader(new InputStreamReader(in));
			String line = reader.readLine();
			while (line != null) {
				line = line.trim();
				boolean isComment = line.startsWith(COMMENT_SYMBOL);
				if (!line.isEmpty() && !isComment) {
					URL url = new URL(config, line);
					urls.add(url);
				}
				line = reader.readLine();
			}
		} catch (IOException x) {
			fail("Error reading configuration '" + config.toString() + "': ", x);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception x) {
					fail("Error closing configuration '" + config.toString()
							+ "': ", x);
				}
			}
		}
		return urls.iterator();
	}

	private PluginIdVersionType parseDescriptor(URL descriptor)
			throws SAXException, ParserConfigurationException, IOException {
		XMLReader xmlReader =
			SAXParserFactory.newInstance().newSAXParser().getXMLReader();
		DescriptorParser descriptorParser = new DescriptorParser();
		xmlReader.setContentHandler(descriptorParser);
		InputStream in = descriptor.openStream();
		InputSource is = new InputSource(in);
		xmlReader.parse(is);
		PluginIdVersionType retVal = descriptorParser.toPluginIdVersionType();
		return retVal;
	}

	private static class DescriptorParser extends DefaultHandler {
		private String id;
		private String version;
		private PluginIdVersionType.TYPE type;

		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			qName = qName.intern();
			if (qName == IModel.PLUGIN || qName == IModel.FRAGMENT) {
				// IModel.PLUGIN or IModel.FRAGMENT may occur several times
				// within a descriptor, first within the root element, and then
				// perhaps later in declarations of extensions (as an example).
				// We're interest in the first occurrence, which we'll use to
				// set the id, version and type of this instance.
				if (id == null) {
					id = attributes.getValue(IModel.PLUGIN_ID);
					assert id != null;
					assert id.trim().length() > 0;

					assert type == null;
					if (qName == IModel.PLUGIN) {
						type = PluginIdVersionType.TYPE.fragment;
					} else {
						assert qName == IModel.FRAGMENT;
						type = PluginIdVersionType.TYPE.fragment;
					}

					assert version == null;
					version = attributes.getValue(IModel.PLUGIN_VERSION);
					assert version != null;
					assert version.trim().length() > 0;
				}
			}
		}

		PluginIdVersionType toPluginIdVersionType() throws PluginDiscoveryException {
			if (id == null || version == null || type == null) {
				throw new PluginDiscoveryException("parsing  failed");
			}
			return new PluginIdVersionType(id, version, type);
		}
	}

	private class PluginConfigurationIterator implements Iterator<URL> {

		private final String configuration;
		private final ClassLoader loader;
		private Enumeration<URL> configs = null;
		private Iterator<URL> pending = null;
		private URL nextURL = null;

		private PluginConfigurationIterator(String config, ClassLoader loader) {
			assert config != null;
			assert !config.trim().isEmpty();
			this.configuration = config;
			this.loader = loader;
		}

		public boolean hasNext() {
			if (nextURL != null) {
				return true;
			}
			if (configs == null) {
				try {
					String fullName = PREFIX + configuration;
					if (loader == null)
						configs = ClassLoader.getSystemResources(fullName);
					else
						configs = loader.getResources(fullName);
				} catch (IOException x) {
					fail("Error locating configuration files", x);
				}
			}
			while ((pending == null) || !pending.hasNext()) {
				if (!configs.hasMoreElements()) {
					return false;
				}
				pending = parseConfiguration(configs.nextElement());
			}
			nextURL = pending.next();
			return true;
		}

		public URL next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			URL cn = nextURL;
			nextURL = null;
			return cn;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

}
