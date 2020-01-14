package edu.iris.dmc.station;

/*
 Station xml/seed converter
Copyright (C) 2019  IRIS

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXParseException;

import ch.qos.logback.classic.Level;
import edu.iris.dmc.station.converter.MetadataFileFormatConverter;
import edu.iris.dmc.station.converter.SeedToXmlFileConverter;
import edu.iris.dmc.station.converter.XmlToSeedFileConverter;
import picocli.CommandLine;
import picocli.CommandLine.IExecutionExceptionHandler;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParseResult;
import picocli.CommandLine.Spec;

public class Application implements Callable<Integer> {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	private boolean debug;

	@Spec
	private CommandSpec spec;

	@Option(names = { "?", "-h", "--help" }, usageHelp = true, description = "display this help and exit")
	private boolean help;

	@Option(names = { "-v", "--verbose" }, description = { "Specify multiple -v options to increase verbosity.",
			"For example, `-v -v -v` or `-vvv`" })
	private boolean[] verbose = new boolean[0];

	@Option(names = { "-c", "--continueonerror" }, description = {
			"Ignore errors and exception and move to the next file." })
	private boolean continueOnError;

	@Option(names = { "--org", "--organization" }, description = { "The organization writing this document." })
	private String organization;

	@Option(names = { "--label" }, description = { "An optional label that can be used to identify this document." })
	private String label;

	@Option(names = { "-V", "--version" }, versionHelp = true, description = "Print version info")
	private boolean versionRequested;

	@Parameters(arity = "1..*", description = "Any number of input files SEED|XML")
	private List<File> source;

	private File target;

	@Option(names = { "-o", "--output" }, description = "Output file or directory, default is System.out")
	public void setTarget(File target) {
		if (target.isDirectory() && !target.exists()) {
			try {
				target.createNewFile();
			} catch (IOException e) {
				throw new ParameterException(spec.commandLine(),
						"A problem occurred while creating the output file. Details: " + e.toString());
			}
		}
	}

	public static void main(String[] args) throws Exception {

		IExecutionExceptionHandler errorHandler = new IExecutionExceptionHandler() {
			public int handleExecutionException(Exception ex, CommandLine commandLine, ParseResult parseResult) {
				commandLine.getErr().println(ex.getMessage());
				return commandLine.getCommandSpec().exitCodeOnExecutionException();
			}
		};

		Integer status = new CommandLine(new Application()).setExecutionExceptionHandler(errorHandler).execute(args);
		System.exit(status);
	}

	@Override
	public Integer call() {
		Level l = Level.toLevel(Integer.MAX_VALUE, Level.OFF);
		if (verbose != null) {
			switch (verbose.length) {
			case 1:
				l = Level.ERROR;
				break;
			case 2:
				l = Level.WARN;
				break;
			case 3:
				l = Level.INFO;
				break;
			case 4:
				l = Level.DEBUG;
				break;
			default:
				l = Level.OFF;
			}
		}
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
		root.setLevel(l);
		try {
			Map<String, String> config = new HashMap<>();
			if(organization!=null) {
				config.put("organization", organization);
			}
			if(label!=null) {
				config.put("label", label);
			}
			convert(source, target, config);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return 0;
	}

	private void convert(List<File> list, File target, Map<String, String> config) throws Exception {
		for (File source : list) {
			if(logger.isDebugEnabled()) {
				logger.debug("Converting {} -> {}",source.getAbsolutePath(),target.getAbsolutePath());
			}
			convert(source, target, config);
		}
	}

	private void convert(File source, File target, Map<String, String> config) throws Exception {
		if (source == null || !source.isFile() || source.isHidden()) {
			throw new IOException("Couldn't process file " + source);
		}

		if (source.isDirectory()) {
			File[] listOfFiles = source.listFiles();
			for (File f : listOfFiles) {
				convert(f, target, config);
			}
		} else {
			if (source.length() == 0) {
				throw new IOException("Couldn't process empty file " + source);
			}
			MetadataFileFormatConverter<File> converter = null;
			String extension = null;
			if (source.getName().toLowerCase().endsWith("xml")) {
				converter = XmlToSeedFileConverter.getInstance();
				extension = "dataless";
			} else {
				converter = SeedToXmlFileConverter.getInstance();
				extension = "xml";
			}
			if (target == null) {
				target = new File(source.getPath() + ".converted." + extension);
			} else {
				if (target.isDirectory()) {
					target = new File(target.getPath() + "/" + source.getName() + ".converted." + extension);
				}
			}
			try {
				if (debug) {
					logger.debug("{} -> {}", source.getName(), target.getName());
				}
				converter.convert(source, target, config);
			} catch (Exception e) {
				StringBuilder messageBuilder = new StringBuilder("Exception parsing file: " + source.getName() + "\n");
				if (e.getCause() instanceof SAXParseException) {
					SAXParseException s = (SAXParseException) e.getCause();
					messageBuilder.append("Error when validating XML against FDSN-Station XSD schema\n" + "lineNumber: "
							+ s.getLineNumber() + ";\ncolumnNumber: " + s.getColumnNumber() + ";\n"
							+ s.getMessage().substring(s.getMessage().indexOf(":") + 2));
				} else {
					messageBuilder.append(e.getMessage());
				}

				if (continueOnError) {
					logger.error(messageBuilder.toString());
				} else {
					throw new RuntimeException(messageBuilder.toString(), e);
				}
			}
		}
	}

	static class ManifestVersionProvider implements picocli.CommandLine.IVersionProvider {
		public String[] getVersion() throws Exception {

			try (InputStream resourceAsStream = this.getClass().getResourceAsStream("/version.properties");) {
				Properties prop = new Properties();
				prop.load(resourceAsStream);

				prop.getProperty("groupId");
				prop.get("artifactId");
				prop.get("version");
				prop.get("build.date");

				return new String[] { prop.getProperty("groupId") + " >> " + prop.getProperty("version") + " ["
						+ prop.getProperty("build.date") + "]" };

			} catch (IOException ex) {
				return new String[] { "Unable to read from version: " + ex };
			}
		}
	}
}
