/*
 * Copyright (C) 2024-present Pipeliner project authors and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.verifyica.pipeliner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import org.verifyica.pipeliner.common.Console;
import org.verifyica.pipeliner.common.Environment;
import org.verifyica.pipeliner.execution.Context;
import org.verifyica.pipeliner.execution.Pipeline;
import org.verifyica.pipeliner.execution.PipelineFactory;
import org.verifyica.pipeliner.model.PipelineDefinitionException;
import org.verifyica.pipeliner.model.support.EnvironmentVariable;
import org.verifyica.pipeliner.model.support.Property;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/** Class to implement Pipeliner */
@SuppressWarnings("PMD.EmptyCatchBlock")
@CommandLine.Command(name = "pipeliner")
public class Pipeliner implements Runnable {

    private static final String PIPELINER_PROPERTIES = "/pipeliner.properties";

    private static final String VERSION_KEY = "version";

    private static final String VERSION_UNKNOWN = "unknown";

    private static final String PIPELINER_TIMESTAMPS = "PIPELINER_TIMESTAMPS";

    private static final String PIPELINER_MINIMAL = "PIPELINER_MINIMAL";

    @Option(
            names = {"--information", "--info"},
            description = "show information")
    private boolean optionInformation;

    @Option(
            names = {"--version", "--ver"},
            description = "show version")
    private boolean optionVersion;

    @Option(names = "--timestamps", description = "enable timestamps")
    private boolean optionTimestamps;

    @Option(names = "--trace", description = "enable trace logging")
    private boolean optionTrace;

    @Option(
            names = {"--minimal", "--min"},
            description = "enable minimal output")
    private boolean optionMinimal;

    @Option(names = "--validate", description = "validate pipeline file")
    private boolean optionValidate;

    @Parameters(description = "filenames")
    private List<String> argumentFilenames;

    @Option(
            names = {"--env", "-E"},
            description = "specify environment variables in key=value format",
            split = ",")
    private final Map<String, String> commandLineEnvironmentVariables = new HashMap<>();

    @Option(
            names = {"--with", "-P"},
            description = "specify properties in key=value format",
            split = ",")
    private final Map<String, String> commandLineProperties = new HashMap<>();

    @Option(names = "--with-file", description = "specify property files", split = ",")
    private final List<String> commandLinePropertiesFiles = new ArrayList<>();

    @Option(
            names = {"-h", "--help"},
            usageHelp = true,
            description = "Display this help message.")
    private boolean helpRequested;

    private final Console console;
    private final List<File> files;

    /** Constructor */
    public Pipeliner() {
        console = Console.getInstance();
        files = new ArrayList<>();
    }

    /**
     * Method to get the Console
     *
     * @return the Console
     */
    private Console getConsole() {
        return console;
    }

    @Override
    public void run() {
        getConsole().enableTimestamps(optionTimestamps);

        String environmentVariable = Environment.getenv(PIPELINER_TIMESTAMPS);
        if (environmentVariable != null) {
            optionTimestamps = Constants.TRUE.equals(environmentVariable.trim())
                    || Constants.ONE.equals(environmentVariable.trim());
            getConsole().enableTimestamps(optionTimestamps);
        }

        getConsole().enableMinimal(optionMinimal);

        environmentVariable = Environment.getenv(PIPELINER_MINIMAL);
        if (environmentVariable != null) {
            optionMinimal = Constants.TRUE.equals(environmentVariable.trim())
                    || Constants.ONE.equals(environmentVariable.trim());
            getConsole().enableMinimal(optionMinimal);
        }

        getConsole().enableTrace(optionTrace);

        environmentVariable = Environment.getenv(Constants.PIPELINER_TRACE);
        if (environmentVariable != null) {
            optionTrace = Constants.TRUE.equals(environmentVariable.trim())
                    || Constants.ONE.equals(environmentVariable.trim());
            getConsole().enableTrace(optionTrace);
        }

        if (getConsole().isTraceEnabled()) {
            getConsole().enableMinimal(false);
        }

        try {
            if (optionInformation) {
                getConsole()
                        .info("@info Verifyica Pipeliner " + getVersion()
                                + " (https://github.com/verifyica-team/pipeliner)");
                getConsole().closeAndExit(0);
            }

            if (optionVersion) {
                System.out.print(getVersion());
                getConsole().closeAndExit(0);
            }

            getConsole()
                    .info("@info Verifyica Pipeliner " + getVersion()
                            + " (https://github.com/verifyica-team/pipeliner)");

            // Validate command line properties files

            for (String commandLinePropertiesFile : commandLinePropertiesFiles) {
                Path filePath = Paths.get(commandLinePropertiesFile);

                if (!Files.exists(filePath)) {
                    getConsole().error("properties file=[%s] doesn't exist", commandLinePropertiesFile);
                    getConsole().closeAndExit(1);
                }

                if (!Files.isReadable(filePath)) {
                    getConsole().error("properties file=[%s] isn't accessible", commandLinePropertiesFile);
                    getConsole().closeAndExit(1);
                }

                if (!Files.isRegularFile(filePath)) {
                    getConsole().error("properties file=[%s] isn't a file", commandLinePropertiesFile);
                    getConsole().closeAndExit(1);
                }
            }

            // Validate command line environment variables

            for (String commandLineEnvironmentVariable : commandLineEnvironmentVariables.keySet()) {
                if (!EnvironmentVariable.isValid(commandLineEnvironmentVariable)) {
                    getConsole()
                            .error("option [-E=%s] is an invalid environment variable", commandLineEnvironmentVariable);
                    getConsole().closeAndExit(1);
                }
            }

            // Validate command line properties

            for (String commandLineProperty : commandLineProperties.keySet()) {
                if (!Property.isValid(commandLineProperty)) {
                    getConsole().error("option [-P=%s] is an invalid property", commandLineProperty);
                    getConsole().closeAndExit(1);
                }
            }

            // Validate filename arguments

            if (argumentFilenames == null || argumentFilenames.isEmpty()) {
                getConsole().error("no filename(s) provided");
                getConsole().closeAndExit(1);
            }

            for (String filename : argumentFilenames) {
                if (filename.trim().isEmpty()) {
                    getConsole().error("filename is blank");
                    getConsole().closeAndExit(1);
                }

                File file = new File(filename);

                if (!file.exists()) {
                    getConsole().error("filename [%s] doesn't exist", filename);
                    getConsole().closeAndExit(1);
                }

                if (!file.canRead()) {
                    getConsole().error("filename [%s] isn't accessible", filename);
                    getConsole().closeAndExit(1);
                }

                if (!file.isFile()) {
                    getConsole().error("filename [%s] isn't a file", filename);
                    getConsole().closeAndExit(1);
                }

                files.add(file);
            }

            // Load properties from properties files and command line property options

            Map<String, String> properties = new TreeMap<>();

            for (String commandLinePropertiesFile : commandLinePropertiesFiles) {
                try {
                    getConsole().info("@info --with-file [%s]", commandLinePropertiesFile);
                    Properties fileProperties = new Properties();
                    fileProperties.load(
                            new File(commandLinePropertiesFile).toURI().toURL().openStream());
                    fileProperties.forEach((key, value) -> properties.put(key.toString(), value.toString()));
                } catch (Throwable t) {
                    if (getConsole().isTraceEnabled()) {
                        t.printStackTrace(System.out);
                    }
                    getConsole().error("failed to load properties from file [%s]", commandLinePropertiesFile);
                    getConsole().closeAndExit(1);
                }
            }

            properties.putAll(commandLineProperties);

            int exitCode = 0;
            PipelineFactory pipelineFactory = new PipelineFactory();

            for (File file : files) {
                getConsole().info("@info filename [%s]", file.getName());

                Pipeline pipeline =
                        pipelineFactory.create(file.getAbsolutePath(), commandLineEnvironmentVariables, properties);

                if (optionValidate) {
                    getConsole().info("@info filename [%s] is valid pipeline", file.getName());
                } else {
                    pipeline.execute(new Context(console));
                }

                exitCode = pipeline.getExitCode();
                if (exitCode != 0) {
                    break;
                }
            }

            getConsole().closeAndExit(exitCode);
        } catch (PipelineDefinitionException e) {
            if (getConsole().isTraceEnabled()) {
                e.printStackTrace(System.out);
            }
            getConsole().error("%s", e.getMessage());
            getConsole().closeAndExit(1);
        } catch (Throwable t) {
            t.printStackTrace(System.out);
            getConsole().closeAndExit(1);
        }
    }

    /**
     * Method to get the version
     *
     * @return the version
     */
    private static String getVersion() {
        String value = VERSION_UNKNOWN;

        try (InputStream inputStream = Pipeliner.class.getResourceAsStream(PIPELINER_PROPERTIES)) {
            if (inputStream != null) {
                Properties properties = new Properties();
                properties.load(inputStream);
                value = properties.getProperty(VERSION_KEY).trim();
            }
        } catch (IOException e) {
            // INTENTIONALLY BLANK
        }

        return value;
    }

    /**
     * Main method
     *
     * @param args args
     */
    public static void main(String[] args) {
        int exitCode = new CommandLine(new Pipeliner()).execute(args);
        System.exit(exitCode);
    }

    /** Debug class */
    private static class Debug {

        private static final String PWD = "PWD";

        private static final String PIPELINER = "/pipeliner";

        /**
         * Main method
         *
         * @param args ignored
         */
        public static void main(String[] args) {
            // SystemEnvironment.getenv().forEach((s, s2) -> System.out.printf("[%s] = [%s]%n", s, s2));

            if (Environment.getenv(Constants.PIPELINER_HOME) == null) {
                Environment.set(Constants.PIPELINER_HOME, Environment.getenv(PWD));
            }

            if (Environment.getenv(Constants.PIPELINER) == null) {
                Environment.set(Constants.PIPELINER, Environment.getenv(Constants.PIPELINER_HOME) + PIPELINER);
            }

            String[] arguments = new String[] {"tests/all.yaml"};
            // String[] arguments = new String[] {"--trace", "test-extensions-1.yaml"};
            // String[] arguments = new String[] {"examples/properties-recursive.yaml"};
            // String[] arguments = new String[] {"examples/js/extension-js.yaml"};
            // String[] arguments = new String[] {"--trace", "tests/test-properties-1.yaml"};
            // String[] arguments = new String[] {"--trace", "tests/test-properties-6.yaml"};

            Pipeliner.main(arguments);
        }
    }
}
