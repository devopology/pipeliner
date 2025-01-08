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

package org.verifyica.pipeliner.common;

/** Class to implement SignatureException */
public class SignatureException extends Exception {

    /**
     * Constructor
     *
     * @param message message
     */
    public SignatureException(String message) {
        super(message);
    }

    /**
     * Constructor
     *
     * @param message message
     * @param cause cause
     */
    public SignatureException(String message, Throwable cause) {
        super(message, cause);
    }
}
