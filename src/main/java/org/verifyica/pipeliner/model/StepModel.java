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

package org.verifyica.pipeliner.model;

import static java.lang.String.format;

import org.verifyica.pipeliner.execution.support.Shell;

/** Class to implement StepModel */
public class StepModel extends Model {

    private String shell;
    private String run;

    /** Constructor */
    public StepModel() {
        super();

        this.shell = "bash";
    }

    /**
     * Method to set the shell
     *
     * @param shell shell
     */
    public void setShell(String shell) {
        if (shell != null) {
            this.shell = shell;
        }
    }

    /**
     * Method to get the shell
     *
     * @return the shell
     */
    public String getShell() {
        return shell;
    }

    /**
     * Method to set the run
     *
     * @param run run
     */
    public void setRun(String run) {
        if (run != null) {
            this.run = run.trim();
        }
    }

    /**
     * Method to get the run
     *
     * @return the run
     */
    public String getRun() {
        return run;
    }

    @Override
    public void validate() {
        validateName();
        validateId();
        validateEnabled();
        validateEnv();
        validateWith();
        validateWorkingDirectory();
        validateShell();
        validateRun();
    }

    /**
     * Method to validate the shell
     */
    private void validateShell() {
        if (shell == null) {
            throw new PipelineDefinitionException(format("%s -> shell is null", this));
        }

        if (shell.isEmpty()) {
            throw new PipelineDefinitionException(format("%s -> shell is blank", this));
        }

        if (Shell.decode(shell) == Shell.INVALID) {
            throw new PipelineDefinitionException(
                    format("%s -> shell=[%s] is invalid. Must be [bash] or [sh]", this, shell));
        }
    }

    /**
     * Method to validate the run
     */
    private void validateRun() {
        if (run == null) {
            throw new PipelineDefinitionException(format("%s -> run is null", this));
        }

        if (run.isEmpty()) {
            throw new PipelineDefinitionException(format("%s -> run is blank", this));
        }
    }

    @Override
    public String toString() {
        return "@step " + super.toString();
    }
}
