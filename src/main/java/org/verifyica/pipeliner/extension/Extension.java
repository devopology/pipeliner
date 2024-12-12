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

package org.verifyica.pipeliner.extension;

import java.io.*;
import java.util.*;

/** Class to implement Extension */
public class Extension {

    /** Constructor */
    private Extension() {
        // INTENTIONALLY BLANK
    }

    /**
     * Run the extension
     *
     * @param args Command line arguments
     * @throws Throwable If an error occurs
     */
    public void run(String[] args) throws Throwable {
        Map<String, String> environmentVariables = getEnvironmentVariables();
        Map<String, String> properties = getProperties();

        if (isTraceEnabled()) {
            for (Map.Entry<String, String> entry : environmentVariables.entrySet()) {
                System.out.printf("@trace environment variable [%s] = [%s]%n", entry.getKey(), entry.getValue());
            }

            for (Map.Entry<String, String> entry : properties.entrySet()) {
                System.out.printf("@trace extension property [%s] = [%s]%n", entry.getKey(), entry.getValue());
            }
        }

        System.out.println("This is a sample Java extension");
    }

    /**
     * Main method
     *
     * @param args Command line arguments
     * @throws Throwable If an error occurs
     */
    public static void main(String[] args) throws Throwable {
        new Extension().run(args);
    }

    /**
     * Check if trace is enabled
     *
     * @return trude if trace is enabled, else false
     */
    private boolean isTraceEnabled() {
        return "true".equals(System.getenv("PIPELINER_TRACE"));
    }

    /**
     * Get environment variables
     *
     * @return Map of environment variables
     */
    private static Map<String, String> getEnvironmentVariables() {
        return new TreeMap(System.getenv());
    }

    /**
     * Get properties
     *
     * @return Map of properties
     */
    private static Map<String, String> getProperties() throws Throwable {
        String environmentVariable = System.getenv().get("PIPELINER_STEP_PROPERTIES");
        if (environmentVariable == null || environmentVariable.trim().isEmpty()) {
            return new TreeMap<>();
        }

        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(environmentVariable)) {
            properties.load(fileInputStream);
        }

        Map<String, String> map = new TreeMap<>();
        for (String key : properties.stringPropertyNames()) {
            map.put(key, properties.getProperty(key));
        }

        return map;
    }
}