/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002 The ObjectStyle Group 
 * and individual authors of the software.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:  
 *       "This product includes software developed by the 
 *        ObjectStyle Group (http://objectstyle.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "ObjectStyle Group" and "Cayenne" 
 *    must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact andrus@objectstyle.org.
 *
 * 5. Products derived from this software may not be called "ObjectStyle"
 *    nor may "ObjectStyle" appear in their names without prior written
 *    permission of the ObjectStyle Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE OBJECTSTYLE GROUP OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the ObjectStyle Group.  For more
 * information on the ObjectStyle Group, please see
 * <http://objectstyle.org/>.
 *
 */
package org.objectstyle.cayenne.conf;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.objectstyle.cayenne.CayenneRuntimeException;
import org.objectstyle.cayenne.ConfigurationException;
import org.objectstyle.cayenne.access.DataDomain;
import org.objectstyle.cayenne.util.CayenneMap;
import org.objectstyle.cayenne.util.ResourceLocator;

/**
 * This class is an entry point to Cayenne. It loads all 
 * configuration files and instantiates main Cayenne objects. Used as a 
 * singleton via 'getSharedConfig' method.
 *
 * <p>To force custom subclass of Configuration, Java application must
 * call "initSharedConfig" with the name of such subclass. This will initialize
 * Configuration singleton instance with new object of a specified class.
 * By default org.objectstyle.cayenne.conf.DefaultConfiguration is instantiated.
 * </p>
 *
 * @author Andrei Adamchik
 */
public abstract class Configuration {
    private static Logger logObj = Logger.getLogger(Configuration.class);

    public static final String DEFAULT_LOGGING_PROPS_FILE = ".cayenne/cayenne-log.properties";
    public static final String DEFAULT_DOMAIN_FILE = "cayenne.xml";
    public static final Class DEFAULT_CONFIGURATION_CLASS = DefaultConfiguration.class;

    protected static Configuration sharedConfiguration;
    private static boolean loggingConfigured;

    /** 
     * Defines ClassLoader to use for resource lookup.
     * Configuration objects that are using ClassLoaders
     * to locate reosurces may need to be bootstrapped
     * explicitly.
     */
    private static ClassLoader resourceLoader = Configuration.class.getClassLoader();

    /** Lookup map that stores DataDomains with names as keys. */
    protected CayenneMap dataDomains = new CayenneMap(this);
	protected Collection dataDomainsRef = Collections.unmodifiableCollection(dataDomains.values());
    protected DataSourceFactory overrideFactory;
    protected ConfigStatus loadStatus = new ConfigStatus();
	protected boolean ignoringLoadFailures = false;

    /** 
     * Sets <code>cl</code> class's ClassLoader to serve
     * as shared configuration resource ClassLoader.
     * If shared Configuration object does not use ClassLoader,
     * this method call will have no effect on how resources are loaded.
     */
    public static void bootstrapSharedConfig(Class cl) {
        resourceLoader = cl.getClassLoader();
    }

    /** 
     * Configures Cayenne logging properties. 
     * Search for the properties file called <code>cayenne-log.properties</code> 
     * is first done in $HOME/.cayenne, then in CLASSPATH.
     */
    public synchronized static void configureCommonLogging() {
        if (!Configuration.isLoggingConfigured()) {
			// create a simple CLASSPATH/$HOME locator
            ResourceLocator locator = new ResourceLocator();
            locator.setSkipAbsolutePath(true);
            locator.setSkipClasspath(false);
            locator.setSkipCurrentDirectory(true);
            locator.setSkipHomeDirectory(false);

            // and load the default logging config file
            URL configURL = locator.findResource(DEFAULT_LOGGING_PROPS_FILE);
			Configuration.configCommonLogging(configURL);
        }
    }

    /** 
     * Configures Cayenne logging properties using properties found at specified URL. 
     */
    public synchronized static void configCommonLogging(URL propsFile) {
        if (!Configuration.isLoggingConfigured()) {
            if (propsFile != null) {
                PropertyConfigurator.configure(propsFile);
				logObj.debug("configured log4j from: " + propsFile);
            } else {
                BasicConfigurator.configure();
                logObj.debug("configured log4j with BasicConfigurator.");
            }

			// remember configuraton success
            Configuration.setLoggingConfigured(true);
        }
    }

	/**
	 * @HH todo
	 */
	public static boolean isLoggingConfigured() {
		return loggingConfigured;
	}

	/**
	 * @HH todo
	 */
	protected synchronized static void setLoggingConfigured(boolean state) {
		loggingConfigured = state;
	}

	/**
	 * Use this method as an entry point to all Cayenne access objects.
	 * <p>Note that if you want to provide custom Configuration,
	 * make sure you call one of <code>initSharedConfig</code> methods
	 * before your application code has a chance to call this method.
	 * @deprecated Since 1.0 Beta1; use #getSharedConfiguration() instead.
	 */
	public synchronized static Configuration getSharedConfig() {
		return Configuration.getSharedConfiguration();
	}

	/**
	 * Use this method as an entry point to all Cayenne access objects.
	 * <p>Note that if you want to provide custom Configuration,
	 * make sure you call one of <code>initSharedConfig</code> methods
	 * before your application code has a chance to call this method.
	 */
	public synchronized static Configuration getSharedConfiguration() {
		if (sharedConfiguration == null) {
			Configuration.initializeSharedConfiguration();
		}

		return sharedConfiguration;
	}

	/**
	 * @HH todo
	 */
    public static ClassLoader getResourceLoader() {
        return resourceLoader;
    }

    /** 
     * Returns default log level for loading configuration. 
     * Log level is made static so that applications can set it 
     * before shared Configuration object is instantiated.
     */
    public static Level getLoggingLevel() {
    	Level l = logObj.getLevel();
    	return (l != null ? l : Level.DEBUG);
    }

    /**
     * Sets the default log level for loading a configuration.
     */
    public static void setLoggingLevel(Level logLevel) {
    	logObj.setLevel(logLevel);
    }

	/**
	 * Creates and initializes shared Configuration object.
	 * org.objectstyle.cayenne.conf.DefaultConfiguration will be 
	 * instantiated and assigned to a singleton instance of
	 * Configuration.
	 */
	public static void initializeSharedConfiguration() {
		Configuration.initializeSharedConfiguration(DEFAULT_CONFIGURATION_CLASS);
	}

	/**
	 * Creates and initializes shared Configuration object with
	 * custom Configuration subclass.
	 * @deprecated Since 1.0 Beta1; use #initSharedConfiguration(Class) instead.
	 */
	public static void initSharedConfig(String configClass) {
		try {
			Configuration.initializeSharedConfiguration(Class.forName(configClass));
		} catch (Exception ex) {
			logObj.error("Error initializing shared Configuration", ex);
			throw new ConfigurationException("Error initializing shared Configuration");
		}
	}

	/**
	 * Creates and initializes shared Configuration object with
	 * custom Configuration subclass.
	 */
	public static void initializeSharedConfiguration(Class configClass) {
		try {
			Configuration conf = (Configuration)configClass.newInstance();
			Configuration.setSharedConfiguration(conf);
		} catch (Exception ex) {
			logObj.error("Error initializing shared Configuration", ex);
			throw new ConfigurationException("Error initializing shared Configuration");
		}
	}

	/**
	 * Sets shared Configuration object to a new Configuration object.
	 * calls <code>init</code> method of <code>conf</code> object.
	 * @deprecated Since 1.0 Beta1; use #setSharedConfiguration(Configuration) instead.
	 */
	public static void initSharedConfig(Configuration conf) {
		Configuration.setSharedConfiguration(conf);
	}

	/**
	 * Sets the shared Configuration object to a new Configuration object.
	 */
	public static void setSharedConfiguration(Configuration conf) {
		sharedConfiguration = conf;
	}

	/**
	 * @HH todo
	 */
	protected Configuration() {
		super();

		// first of all set up logging
		this.configureLogging();

		// Load domains if we can; Subclasses can call #initialize() later
		// when they feel like it.
		if (this.shouldInitialize()) {
			try {
				this.initialize();
			} catch (Exception ex) {
				throw new ConfigurationException(ex);
			}
		}
	}

	/**
	 * @HH todo
	 * @throws Exception
	 */
	protected abstract boolean shouldInitialize();

	/**
	 * @HH todo
	 * @throws Exception
	 */
	protected abstract void initialize() throws Exception;

	/**
	 * @HH todo
	 * @throws Exception
	 */
	protected abstract void didInitialize();

	/**
	 * Returns the resource locator used for finding and loading resources 
	 */
	public abstract ResourceLocator getResourceLocator();

	/**
	 * Returns domain configuration as a stream or null if it
	 * can not be found.
	 */
	protected abstract InputStream getDomainConfiguration();

	/**
	 * Returns DataMap configuration from a specified location or
	 * null if it can not be found.
	 */
	protected abstract InputStream getMapConfiguration(String location);

    /**
     * Configures Log4J. This implementation calls
     * <code>Configuration.configureCommonLogging</code>.
     */
    protected void configureLogging() {
        Configuration.configureCommonLogging();
    }

    /**
     * Returns an internal property for the DataSource factory that 
     * will override any settings configured in XML. 
     * Subclasses may override this method to provide a special factory for
     * DataSource creation that will take precedence over any factories
     * configured in cayenne project. 
     */
    public DataSourceFactory getOverrideFactory() {
        return overrideFactory;
    }

    public void setOverrideFactory(DataSourceFactory overrideFactory) {
        this.overrideFactory = overrideFactory;
    }

    /**
     * Adds new DataDomain to the list of registered domains.
     */
    public void addDomain(DataDomain domain) {
        dataDomains.put(domain.getName(), domain);
		logObj.debug("added domain: " + domain.getName());
    }

    /**
     * Returns registered domain matching <code>name</code>
     * or null if no such domain is found.
     */
    public DataDomain getDomain(String name) {
        return (DataDomain) dataDomains.get(name);
    }

    /** 
     * Returns default domain of this configuration. If no domains are 
     * configured, null is returned. If more then 1 domain exists in this
     * configuration, a CayenneRuntimeException is thrown, indicating that
     * domain name must be explicitly specified. In such cases
     * <code>getDomain(String name)</code> method must be used instead.
     */
    public DataDomain getDomain() {
        int size = dataDomains.size();
        if (size == 0) {
            return null;
        } else if (size == 1) {
            return (DataDomain)dataDomains.values().iterator().next();
        } else {
            throw new CayenneRuntimeException("More than 1 domain is configured; use 'getDomain(String name)' instead.");
        }
    }

    /**
     * Unregisters DataDomain matching <code>name<code> from
     * this Configuration object. Note that any domain database
     * connections remain open, and it is a responsibility of a
     * caller to clean it up.
     */
    public void removeDomain(String name) {
        dataDomains.remove(name);
		logObj.debug("removed domain: " + name);
    }

	/**
	 * Returns a list of registered DataDomain objects.
	 * @deprecated Since 1.0 beta1; use #getDomains() instead.
	 */
	public List getDomainList() {
		return new ArrayList(this.getDomains());
	}

	/**
	 * Returns an unmodifiable collection of registered DataDomain objects.
	 */
	public Collection getDomains() {
		return dataDomainsRef;
	}

    /**
     * Returns the ignoringLoadFailures.
     * @return boolean
     */
    public boolean isIgnoringLoadFailures() {
        return ignoringLoadFailures;
    }

    /**
     * Sets the ignoringLoadFailures.
     * @param ignoringLoadFailures The ignoringLoadFailures to set
     */
    public void setIgnoringLoadFailures(boolean ignoringLoadFailures) {
        this.ignoringLoadFailures = ignoringLoadFailures;
    }

    /**
     * Returns the loadStatus.
     * @return ConfigStatus
     */
    public ConfigStatus getLoadStatus() {
        return loadStatus;
    }

	/**
	 * @HH todo
	 */
	public ConfigLoaderDelegate getLoaderDelegate() {
		return new RuntimeLoadDelegate(this, loadStatus, Configuration.getLoggingLevel());
	}

}