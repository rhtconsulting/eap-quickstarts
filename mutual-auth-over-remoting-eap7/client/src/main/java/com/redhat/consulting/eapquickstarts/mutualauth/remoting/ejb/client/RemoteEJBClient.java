/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.redhat.consulting.eapquickstarts.mutualauth.remoting.ejb.client;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jboss.ejb.client.EJBClientConfiguration;
import org.jboss.ejb.client.EJBClientContext;
import org.jboss.ejb.client.PropertiesBasedEJBClientConfiguration;
import org.jboss.ejb.client.remoting.ConfigBasedEJBClientContextSelector;

import com.redhat.consulting.eapquickstarts.mutualauth.remoting.ejb.server.stateful.RemoteCounter;
import com.redhat.consulting.eapquickstarts.mutualauth.remoting.ejb.server.stateless.RemoteCalculator;



/**
 * A sample program which acts a remote client for a EJB deployed on JBoss EAP
 * server. This program shows how to lookup stateful and stateless beans via
 * JNDI and then invoke on them
 *
 * @author Jaikiran Pai
 */
public class RemoteEJBClient {

	private static final String STR_STATEFUL_SUFFIX = "?stateful";
	private static final String STR_SLASH_SEPARATOR = "/";
	private static final String STR_EXCLAM_SEPARATOR = "!";
	private static final String STR_NEW_LINE = "\n";
	private static final String STR_EJB_PREFFIX = "ejb:/";
	private static final String DEFAULT_TIMEOUT = "60000";

	private static Properties props = new Properties();
	private static Properties iniCtxProps = new Properties();
	private String[] args = null;
	private Options options = new Options();

	private String host = null;
	private String port = null;
	private String ks = null;
	private String ksp = null;
	private String kst = null;
	private String ts = null;
	private String tsp = null;
	private String tst = null;
	private String username = null;
	private String password = null;
	private boolean setRemoteEJBProps = false;
	private boolean useSSLOnRemoteEJBProps = false;
	private static String lookupString = "mutualauth-remoting-ejb-server-side";
	private static String calcBeanString = "CalculatorBean";
	private static String ctrBeanString = "CounterBean";

	private static Context ctx = null;

	{
		if (null == options || options.getOptions().isEmpty()) {
			options = CommandLineArgumentsParserUtils.buildOptions();
		}
	}

	public RemoteEJBClient(String[] args) {
		this.args = args;
	}

	public static void main(String[] args) throws Exception {

		RemoteEJBClient client = new RemoteEJBClient(args);

		if (null == args || args.length == 0) {
			CommandLineArgumentsParserUtils.printHelp(null, client.options);
		}

		System.out.println("argument lenght is : " + args.length + STR_NEW_LINE);

		CommandLineArgumentsParserUtils.printRawArguments(args, null);

		client.parseInputs(client.args);

		client.initLookupContextProps();

		// Invoke a stateless bean
		client.invokeStatelessBean();

		// Invoke a stateful bean
		client.invokeStatefulBean();

	}

	private void parseInputs(String[] args) {
		CommandLineParser parser = new GnuParser();
		CommandLine cmdLine;

		try {
			cmdLine = parser.parse(options, args);
			CommandLineArgumentsParserUtils.printRawArguments(args,
					"List of original arguments right after they have been parsed \n");
			if (cmdLine.getArgs().length != 0) {
				System.out.print("unexpected Exception occurred while processing pased in arguments: ");
				CommandLineArgumentsParserUtils.printHelp(cmdLine, options);
			}
			processOptions(cmdLine);

		} catch (ParseException e) {
			e.printStackTrace(System.out);
		}
	}

	private void processOptions(CommandLine cmdLine) {
		this.host = CommandLineArgumentsParserUtils.processArgument(cmdLine, "host");
		this.port = CommandLineArgumentsParserUtils.processArgument(cmdLine, "port");
		this.username = CommandLineArgumentsParserUtils.processArgument(cmdLine, "username");
		this.password = CommandLineArgumentsParserUtils.processArgument(cmdLine, "password");
		this.setRemoteEJBProps = Boolean
				.parseBoolean(CommandLineArgumentsParserUtils.processArgument(cmdLine, "setctxpropsp"));
		this.useSSLOnRemoteEJBProps = Boolean
				.parseBoolean(CommandLineArgumentsParserUtils.processArgument(cmdLine, "usessl"));
		this.ks = CommandLineArgumentsParserUtils.processArgument(cmdLine, "keystore");
		this.ksp = CommandLineArgumentsParserUtils.processArgument(cmdLine, "keystorepw");
		this.kst = CommandLineArgumentsParserUtils.processArgument(cmdLine, "keystoretype");
		this.ts = CommandLineArgumentsParserUtils.processArgument(cmdLine, "truststore");
		this.tsp = CommandLineArgumentsParserUtils.processArgument(cmdLine, "truststorepw");
		this.tst = CommandLineArgumentsParserUtils.processArgument(cmdLine, "truststoretype");
		// only override the lookup string if passed in.
		String lkpStrg = CommandLineArgumentsParserUtils.processArgument(cmdLine, "lookupstring");
		if (null != lkpStrg && !lkpStrg.isEmpty()) {
			lookupString = lkpStrg;
		}
		printProcessedOptions();
	}

	private void printProcessedOptions() {
		System.out.println("The parameters entered are: " + STR_NEW_LINE);
		System.out.println("host: " + this.host + STR_NEW_LINE);
		System.out.println("port: " + this.port + STR_NEW_LINE);
		System.out.println("SetRemotingProps: " + this.setRemoteEJBProps + STR_NEW_LINE);
		System.out.println("TuseSSLOnRemoteEJBProps: " + this.useSSLOnRemoteEJBProps + STR_NEW_LINE);
		System.out.println("ks: " + this.ks + STR_NEW_LINE);
		System.out.println("ksp: " + this.ksp + STR_NEW_LINE);
		System.out.println("kst: " + this.kst + STR_NEW_LINE);
		System.out.println("ts: " + this.ts + STR_NEW_LINE);
		System.out.println("tsp: " + this.tsp + STR_NEW_LINE);
		System.out.println("tst: " + this.tst + STR_NEW_LINE);
		System.out.println("lookup string: " + lookupString + STR_NEW_LINE);
	}

	private static Context getInitialContext() throws NamingException {
		if (null == ctx) {
			ctx = new InitialContext(iniCtxProps);
			// ctx = new InitialContext(props);
		}
		return ctx;
	}

	private void initSSLParams() {
		System.setProperty("javax.net.debug", "ssl,handshake");
		System.setProperty("javax.net.ssl.keyStore", this.ks);
		System.setProperty("javax.net.ssl.keyStoreType", this.kst);
		System.setProperty("javax.net.ssl.keyStorePassword", this.ksp);
		System.setProperty("javax.net.ssl.trustStore", this.ts);
		System.setProperty("javax.net.ssl.trustStoreType", this.tst);
		System.setProperty("javax.net.ssl.trustStorePassword", this.tsp);
		System.setProperty("https.protocols", "TLSv1.1,TLSv1.2");
	}

	private void initLookupContextProps() {
		iniCtxProps.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
		if (this.useSSLOnRemoteEJBProps) {
			initSSLParams();
			CommandLineArgumentsParserUtils.printSysProps(props);
		}

		if (!this.setRemoteEJBProps) {
			return;
		}
		props.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
		props.put("remote.connections", "default");
		props.put("remote.connection.default.host", this.host);
		props.put("remote.connection.default.port", this.port);
		props.put("remote.connection.default.timeout", DEFAULT_TIMEOUT);
		props.put("remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOPLAINTEXT", "false");

		if (this.useSSLOnRemoteEJBProps) {

			props.put("remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED", "true");
			props.put("remote.connectionprovider.create.options.org.xnio.Options.SSL_STARTTLS", "true");
			props.put("remote.connection.default.protocol", "https-remoting");
			props.put("remote.connection.default.connect.options.org.xnio.Options.SSL_STARTTLS", "true");
			props.put("remote.connection.default.connect.options.org.xnio.Options.SSL_ENABLED", "true");
			props.put("remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS", "false");
			props.put("remote.connection.default.connect.options.org.xnio.Options.SASL_DISALLOWED_MECHANISMS",
					"JBOSS-LOCAL-USER");

		} else {
			props.put("remote.connection.default.protocol", "http-remoting");
			props.put("remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED", "false");
			props.put("remote.connection.default.connect.options.org.xnio.Options.SSL_ENABLED", "false");
			props.put("remote.connection.default.connect.options.org.xnio.Options.SSL_STARTTLS", "false");
			props.put("remote.connection.default.username", this.username);
			props.put("remote.connection.default.password", this.password);
		}
		final EJBClientConfiguration cc = new PropertiesBasedEJBClientConfiguration(props);
		final ConfigBasedEJBClientContextSelector sel = new ConfigBasedEJBClientContextSelector(cc);
		EJBClientContext.setSelector(sel);
		CommandLineArgumentsParserUtils.printSysProps(props);
		CommandLineArgumentsParserUtils.printProps(props);
	}

	/**
	 * Looks up a stateless bean and invokes on it
	 *
	 * @throws NamingException
	 */
	private void invokeStatelessBean() throws NamingException {
		// Let's lookup the remote stateless calculator
		final RemoteCalculator statelessRemoteCalculator = lookupRemoteStatelessCalculator();
		System.out.println("Obtained a remote stateless calculator for invocation");
		// invoke on the remote calculator
		int a = 204;
		int b = 340;
		System.out.println("Adding " + a + " and " + b + " via the remote stateless calculator deployed on the server");
		int sum = statelessRemoteCalculator.add(a, b);
		System.out.println("Remote calculator returned sum = " + sum);
		if (sum != a + b) {
			throw new RuntimeException(
					"Remote stateless calculator returned an incorrect sum " + sum + " ,expected sum was " + (a + b));
		}
		// try one more invocation, this time for subtraction
		int num1 = 3434;
		int num2 = 2332;
		System.out.println("Subtracting " + num2 + " from " + num1
				+ " via the remote stateless calculator deployed on the server");
		int difference = statelessRemoteCalculator.subtract(num1, num2);
		System.out.println("Remote calculator returned difference = " + difference);
		if (difference != num1 - num2) {
			throw new RuntimeException("Remote stateless calculator returned an incorrect difference " + difference
					+ " ,expected difference was " + (num1 - num2));
		}
		ctx.close();
		ctx = null;
	}

	/**
	 * Looks up a stateful bean and invokes on it
	 *
	 * @throws NamingException
	 */
	private void invokeStatefulBean() throws NamingException {
		// Let's lookup the remote stateful counter
		final RemoteCounter statefulRemoteCounter = lookupRemoteStatefulCounter();
		System.out.println("Obtained a remote stateful counter for invocation");
		// invoke on the remote counter bean
		final int NUM_TIMES = 5;
		System.out.println("Counter will now be incremented " + NUM_TIMES + " times");
		for (int i = 0; i < NUM_TIMES; i++) {
			System.out.println("Incrementing counter");
			statefulRemoteCounter.increment();
			System.out.println("Count after increment is " + statefulRemoteCounter.getCount());
		}
		// now decrementing
		System.out.println("Counter will now be decremented " + NUM_TIMES + " times");
		for (int i = NUM_TIMES; i > 0; i--) {
			System.out.println("Decrementing counter");
			statefulRemoteCounter.decrement();
			System.out.println("Count after decrement is " + statefulRemoteCounter.getCount());
		}
		ctx.close();
		ctx = null;
	}

	/**
	 * Looks up and returns the proxy to remote stateless calculator bean
	 * 
	 * @return
	 * @throws NamingException
	 */
	private RemoteCalculator lookupRemoteStatelessCalculator() throws NamingException {
		getInitialContext();
		String lkpStrg = STR_EJB_PREFFIX + lookupString + STR_SLASH_SEPARATOR + calcBeanString + STR_EXCLAM_SEPARATOR
				+ RemoteCalculator.class.getName();
		System.out.println("The lookup string being used is: " + lkpStrg);
		return (RemoteCalculator) ctx.lookup(lkpStrg);
	}

	/**
	 * Looks up and returns the proxy to remote stateful counter bean
	 * 
	 * @return
	 * @throws NamingException
	 */
	private static RemoteCounter lookupRemoteStatefulCounter() throws NamingException {
		getInitialContext();
		String lkpStrg = STR_EJB_PREFFIX + lookupString + STR_SLASH_SEPARATOR + ctrBeanString + STR_EXCLAM_SEPARATOR
				+ RemoteCounter.class.getName() + STR_STATEFUL_SUFFIX;
		System.out.println("The lookup string being used is: " + lkpStrg);
		return (RemoteCounter) ctx.lookup(lkpStrg);
	}

}
