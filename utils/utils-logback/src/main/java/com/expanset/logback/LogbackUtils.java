package com.expanset.logback;

import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;

/**
 * Utilities for logback logging engine.
 */
public final class LogbackUtils {

	/**
	 * Setup Logback with configuration file.
	 * @param configurationFile Logback configuration file
	 * @throws Exception Configuration error.
	 */
	public static void install(String configurationFile)
			throws Exception {
		final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		context.reset();
		final JoranConfigurator configurator = new JoranConfigurator();
		configurator.setContext(context);
		configurator.doConfigure(configurationFile);

		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();		
	}

	/**
	 * Releases all Logback resources.
	 */
	public static void uninstall() {
		SLF4JBridgeHandler.uninstall();
		
		final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		context.stop();
	}
	
	private LogbackUtils() {}
}
