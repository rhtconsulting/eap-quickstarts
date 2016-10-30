/**
 * 
 */
package com.redhat.consulting.eapquickstarts.mutualauth.remoting.ejb.client;

import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

/**
 * @author cadjai
 * 
 */
public class CommandLineArgumentsParserUtils {

	private static final String STR_NEW_LINE = "\n";
	private static final String STR_SEPARATOR = ":";

	@SuppressWarnings("static-access")
	public static Options buildOptions() {
		Options options = new Options();
		Option help = new Option("h", "Help", false, "Usage");
		options.addOption(help);
		Option host = OptionBuilder
				.withArgName("host")
				.withDescription(
						"The remoting host IP or FQDN where the EJBs are deployed ")
				.hasArg(true).isRequired(false).create("host");
		options.addOption(host);
		Option port = OptionBuilder
				.withArgName("port")
				.withDescription(
						"The port of remoting host where the EJBs are deployed ")
				.hasArg(true).isRequired(false).create("port");
		options.addOption(port);
		Option username = OptionBuilder
				.withArgName("username")
				.withDescription(
						"The username for the remoting security realm where the EJBs are deployed ")
				.hasArg(true).isRequired(false).create("username");
		options.addOption(username);
		Option password = OptionBuilder
				.withArgName("password")
				.withDescription(
						"The password for the user on the remoting security realm where the EJBs are deployed ")
				.hasArg(true).isRequired(false).create("password");
		options.addOption(password);
		Option setctxpropsp = OptionBuilder
				.withArgName("setctxpropsp")
				.withDescription(
						"Set this to true to set remoting properties programmatically instead of using a jboss-ejb-client.properties. ")
				.hasArg(true).isRequired(true).create("setctxpropsp");
		options.addOption(setctxpropsp);
		Option usessl = OptionBuilder
				.withArgName("usessl")
				.withDescription(
						"Set this to true to use SSL over remoting. Default is false")
				.hasArg(true).isRequired(true).create("usessl");
		options.addOption(usessl);
		Option keystore = OptionBuilder
				.withArgName("keystore")
				.withDescription(
						"The path to the keystore location if SSL is being used over the remoting connector. ")
				.hasArg(true).isRequired(false).create("keystore");
		options.addOption(keystore);
		Option keystorepw = OptionBuilder
				.withArgName("keystorepw")
				.withDescription(
						"The passwrd of the keystore if SSL is being used over the remoting connector. ")
				.hasArg(true).isRequired(false).create("keystorepw");
		options.addOption(keystorepw);
		Option keystoretype = OptionBuilder
				.withArgName("keystoretype")
				.withDescription(
						"The type of the keystore if SSL is being used over the remoting connector. ")
				.hasArg(true).isRequired(false).create("keystoretype");
		options.addOption(keystoretype);
		Option truststore = OptionBuilder
				.withArgName("truststore")
				.withDescription(
						"The path to the truststore location if SSL is being used over the remoting connector. ")
				.hasArg(true).isRequired(false).create("truststore");
		options.addOption(truststore);
		Option truststorepw = OptionBuilder
				.withArgName("truststorepw")
				.withDescription(
						"The passwrd of the truststore if SSL is being used over the remoting connector. ")
				.hasArg(true).isRequired(false).create("truststorepw");
		options.addOption(truststorepw);
		Option truststoretype = OptionBuilder
				.withArgName("truststoretype")
				.withDescription(
						"The type of the truststore if SSL is being used over the remoting connector. ")
				.hasArg(true).isRequired(false).create("truststoretype");
		options.addOption(truststoretype);
		Option lookupstring = OptionBuilder
				.withArgName("lookupstring")
				.withDescription(
						"The lookup string for the EJB to lookup in case the name of the artifact changed. This is usually the EJB. ")
				.hasArg(true).isRequired(false).create("lookupstring");
		options.addOption(lookupstring);
		return options;
	}

	public static String processArgument(CommandLine cmdLine, String argname) {

		String argval = null;
		if (cmdLine.hasOption(argname)) {
			argval = cmdLine.getOptionValue(argname);
		}
		return argval;
	}

	public static void printHelp(CommandLine cmdLine, Options options) {
		if (null == cmdLine
				|| (cmdLine.hasOption("h") && !cmdLine.getOptionValue("h")
						.isEmpty()) || cmdLine.getArgList().isEmpty()) {
			HelpFormatter hf = new HelpFormatter();
			if (null == options || options.getOptions().isEmpty()) {
				options = CommandLineArgumentsParserUtils.buildOptions();
			}
			hf.printHelp("OptionsTip", options);
			System.exit(0);
		}
	}

	public static void printSysProps(Properties props) {
		System.out.println("keystore path is : "
				+ System.getProperty("javax.net.ssl.keyStore"));
		System.out.println("keystore type is : "
				+ System.getProperty("javax.net.ssl.keyStoreType"));
		System.out.println("truststore path is : "
				+ System.getProperty("javax.net.ssl.trustStore"));
		System.out.println("truststore type is : "
				+ System.getProperty("javax.net.ssl.trustStoreType"));
		System.out.println("SSL debug value is : "
				+ System.getProperty("javax.net.debug"));
		StringBuilder bldr = new StringBuilder(STR_NEW_LINE);
		for (Entry<Object, Object> prop : props.entrySet()) {
			bldr.append((String) prop.getKey()).append(STR_SEPARATOR)
					.append((String) prop.getValue()).append(STR_NEW_LINE);
		}
		System.out.println("JNDI Properties: " + bldr.toString());
		bldr = null;
	}

	public static void printProps(Properties props) {
		for (Entry<Object, Object> prop : props.entrySet()) {
			System.out.println((String) prop.getKey() + ": "
					+ (String) prop.getValue());
		}
	}

	public static void printRawArguments(String[] args, String msg) {
		if (null == msg) {
			msg = "Here is the list of raw arguments you provided \n";
		}
		StringBuilder bldr = new StringBuilder(msg);
		for (int i = 0; i < args.length; i++) {
			bldr.append(args[i]).append(STR_NEW_LINE);
		}
		System.out.println(bldr.toString());
		bldr = null;
	}
}
