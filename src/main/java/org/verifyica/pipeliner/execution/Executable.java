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

package org.verifyica.pipeliner.execution;

import org.verifyica.pipeliner.common.Console;
import org.verifyica.pipeliner.common.Stopwatch;
import org.verifyica.pipeliner.execution.support.Status;

/** Class to implement Executable */
public abstract class Executable {

    private Context context;
    private final Stopwatch stopwatch;
    private int exitCode;

    /** Constructor */
    public Executable() {
        stopwatch = new Stopwatch();
    }

    /**
     * Method to execute
     *
     * @param context executableContext
     */
    public abstract void execute(Context context);

    /**
     * Method to skip
     *
     * @param context executableContext
     * @param status status
     */
    public abstract void skip(Context context, Status status);

    /**
     * Method to prepare
     *
     * @param context context
     */
    protected void prepare(Context context) {
        this.context = context;
    }

    /**
     * Method to get the Context
     *
     * @return the Context
     */
    protected Context getContext() {
        return context;
    }

    /**
     * Method to get the Console
     *
     * @return the Console
     */
    protected Console getConsole() {
        return context.getConsole();
    }

    /**
     * Method to get the ExtensionManager
     *
     * @return the ExtensionManager
     */
    protected ExtensionManager getExtensionManager() {
        return context.getExtensionManager();
    }

    /**
     * Method to get the Stopwatch
     *
     * @return the Stopwatch
     */
    protected Stopwatch getStopwatch() {
        return stopwatch;
    }

    /**
     * Method to set the exit code
     *
     * @param exitCode exitCode
     */
    protected void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    /**
     * Method to get the exit code
     *
     * @return the exit code
     */
    public int getExitCode() {
        return exitCode;
    }
}
