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

const fs = require('fs');
const path = require('path');
const os = require('os');

/**
 * Custom Exception Class to simulate IpcException
 */
class IpcException extends Error {
    constructor(message, cause) {
        super(message);
        this.name = "IpcException";
        this.cause = cause;
    }
}

/**
 * Class to implement Ipc (Inter-process communication)
 */
class Ipc {

    static BUFFER_SIZE_BYTES = 16384;
    static TEMPORARY_DIRECTORY_PREFIX = "pipeliner-ipc-";
    static TEMPORARY_DIRECTORY_SUFFIX = "";
    
    /**
     * Constructor
     */
    constructor() {
        // INTENTIONALLY BLANK (Singleton pattern)
    }

    /**
     * Send the properties
     *
     * @param {string} ipcFilePath Path to the IPC file
     * @param {Map} map A Map of properties to be written
     * @throws {IpcException} If an error occurs
     */
    static write(ipcFilePath, map) {
        try {
            const writeStream = fs.createWriteStream(ipcFilePath, { flags: 'w', encoding: 'utf8' });
            const properties = new Map(map);

            writeStream.write('# IpcMap\n');
            properties.forEach((value, key) => {
                writeStream.write(`${key}=${value}\n`);
            });

            writeStream.end();
        } catch (e) {
            fs.unlinkSync(ipcFilePath); // Remove file if error occurs
            throw new IpcException("failed to write IPC file", e);
        }
    }

    /**
     * Receive the properties
     *
     * @param {string} ipcFilePath Path to the IPC file
     * @returns {Map} A Map of properties
     * @throws {IpcException} If an error occurs
     */
    static read(ipcFilePath) {
        try {
            const data = fs.readFileSync(ipcFilePath, { encoding: 'utf8' });
            const map = new Map();

            data.split('\n').forEach(line => {
                if (line.trim() && !line.startsWith('#')) {
                    const [key, value] = line.split('=');
                    if (key && value) {
                        map.set(key.trim(), value.trim());
                    }
                }
            });

            return map;
        } catch (e) {
            throw new IpcException("failed to read IPC file", e);
        }
    }

    /**
     * Create a new IPC file
     *
     * @returns {string} The path of the new IPC file
     * @throws {IpcException} If an error occurs
     */
    static createIpcFile() {
        try {
            const tempFilePath = path.join(os.tmpdir(), `${Ipc.TEMPORARY_DIRECTORY_PREFIX}${Date.now()}${Ipc.TEMPORARY_DIRECTORY_SUFFIX}`);
            fs.writeFileSync(tempFilePath, '', { flag: 'w' }); // Create empty file
            
            // Simulating POSIX file permissions - Not directly supported in Node.js like Java
            fs.chmodSync(tempFilePath, 0o600);  // rw-------

            // Register cleanup on exit
            process.on('exit', () => Ipc.cleanup(tempFilePath));

            return tempFilePath;
        } catch (e) {
            throw new IpcException("failed to create IPC file", e);
        }
    }

    /**
     * Cleanup the IPC file
     *
     * @param {string} ipcFilePath Path to the IPC file
     */
    static cleanup(ipcFilePath) {
        if (ipcFilePath && fs.existsSync(ipcFilePath)) {
            fs.unlinkSync(ipcFilePath); // Remove the file
        }
    }
}

module.exports = Ipc;

