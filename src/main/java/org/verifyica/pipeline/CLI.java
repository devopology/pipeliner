/*
 * Copyright (C) 2024-present Verifyica project authors and contributors
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

package org.verifyica.pipeline;

import java.util.List;
import org.verifyica.pipeline.common.Console;
import org.verifyica.pipeline.common.YamlValueException;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/** Class to implement CLI */
public class CLI implements Runnable {

    private static final String PIPELINER_SUPPRESS_TIMESTAMPS = "PIPELINER_SUPPRESS_TIMESTAMPS";

    private static final String PIPELINER_TRACE = "PIPELINER_TRACE";

    private static final String PIPELINER_LOG = "PIPELINER_LOG";

    private final Console console;

    @Option(names = "--version", description = "show version")
    private boolean showVersion;

    @Option(names = "--suppress-timestamps", description = "suppress timestamps")
    private Boolean suppressTimestamps;

    @Option(names = "--trace", description = "enable trace")
    private Boolean trace;

    @Option(names = "--log", description = "enable file logging")
    private Boolean log;

    @Parameters(description = "arguments")
    private List<String> args;

    /** Constructor */
    public CLI() {
        this.console = new Console();
    }

    @Override
    public void run() {
        if (suppressTimestamps != null) {
            console.suppressTimestamps(suppressTimestamps);
        } else {
            String environmentVariable = System.getenv(PIPELINER_SUPPRESS_TIMESTAMPS);
            if (environmentVariable != null) {
                suppressTimestamps =
                        "true".equals(environmentVariable.trim()) || "1".equals(environmentVariable.trim());
                console.suppressTimestamps(suppressTimestamps);
            }
        }

        if (trace != null) {
            console.enableTrace(trace);
        } else {
            String environmentVariable = System.getenv(PIPELINER_TRACE);
            if (environmentVariable != null) {
                trace = "true".equals(environmentVariable.trim()) || "1".equals(environmentVariable.trim());
                console.enableTrace(trace);
            }
        }

        if (log != null) {
            console.enableLogging(log);
        } else {
            String environmentVariable = System.getenv(PIPELINER_LOG);
            if (environmentVariable != null) {
                log = "true".equals(environmentVariable.trim()) || "1".equals(environmentVariable.trim());
                console.enableLogging(log);
            }
        }

        try {
            console.initialize();

            if (showVersion) {
                console.log("@info Verifyica Pipeliner " + Version.getVersion());
                console.log("@info https://github.com/verifyica-team/pipeliner");
                console.close();
                System.exit(0);
            }

            try {
                int exitCode = new Runner(console).run(args);
                console.close();
                System.exit(exitCode);
            } catch (YamlValueException e) {
                console.log("@error message=[%s] exit-code=[%d]", e.getMessage(), 1);
                console.close();
                System.exit(1);
            }
        } catch (Throwable t) {
            t.printStackTrace(System.out);
            System.exit(1);
        }
    }

    /**
     * Main method
     *
     * @param args args
     */
    public static void main(String[] args) {
        new CommandLine(new CLI()).execute(args);
    }
}
