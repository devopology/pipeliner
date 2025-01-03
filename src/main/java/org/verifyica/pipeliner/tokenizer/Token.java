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

package org.verifyica.pipeliner.tokenizer;

import java.util.Objects;

/** Class to implement Token */
public class Token {

    private static final String DOLLAR_CURLY_PREFIX = "${";

    private static final String CURLY_SUFFIX = "}";

    /**
     * Enum to implement token type
     */
    public enum Type {
        /**
         * Text token
         */
        TEXT,
        /**
         * Property token
         */
        PROPERTY,
        /**
         * Environment variable token
         */
        ENVIRONMENT_VARIABLE
    }

    private final Type type;
    private final String text;
    private final String value;

    /**
     * Constructor
     *
     * @param type The token tpe
     * @param text The token text
     */
    public Token(Type type, String text) {
        this.type = type;
        this.text = text;

        // Get the value based on the type

        if (type == Type.PROPERTY) {
            // Text can be ${{ foo }} or ${{foo}}
            this.value = text.substring(3, text.length() - 2).trim();
        } else if (type == Type.ENVIRONMENT_VARIABLE) {
            // Text can be $FOO or ${FOO}
            if (text.startsWith(DOLLAR_CURLY_PREFIX) && text.endsWith(CURLY_SUFFIX)) {
                this.value = text.substring(2, text.length() - 1).trim();
            } else {
                this.value = text.substring(1).trim();
            }
        } else {
            this.value = text;
        }
    }

    /**
     * Method to get the type
     *
     * @return The token type
     */
    public Type getType() {
        return type;
    }

    /**
     * Method to get the text
     *
     * @return The token text
     */
    public String getText() {
        return text;
    }

    /**
     * Method to get the value
     *
     * @return The token value
     */
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Token{" + "type=" + type + ", text='" + text + '\'' + ", value='" + value + '\'' + '}';
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Token token1 = (Token) object;
        return type == token1.type && Objects.equals(text, token1.text) && Objects.equals(value, token1.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, text, value);
    }
}
