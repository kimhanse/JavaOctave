/*
 * Copyright 2007, 2008 Ange Optimization ApS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dk.ange.octave.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Kim Hansen
 * 
 * A Thread that moves data from an {@link InputStream} to an {@link OutputStream}
 */
public final class IOStreamPipeThread extends Thread {

    private static final Log log = LogFactory.getLog(IOStreamPipeThread.class);

    private static final int BUFFERSIZE = 4 * 1024;

    private final InputStream inputStream;

    private final OutputStream outputStream;

    /**
     * Will create a thread that reads from inputStream and writes to outputStream until inputStream reaches EOF. Then
     * it will close inputStream and finish. Remember to join() this thread before outputStream is closed.
     * 
     * @param inputStream
     * @param outputStream
     */
    public IOStreamPipeThread(final InputStream inputStream, final OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    @Override
    public void run() {
        final byte[] b = new byte[BUFFERSIZE];
        while (true) {
            final int len;
            try {
                len = inputStream.read(b);
            } catch (final IOException e) {
                log.info("Error when reading from inputStream", e);
                return;
            }
            if (len == -1) {
                break;
            }
            try {
                outputStream.write(b, 0, len);
                outputStream.flush();
            } catch (final IOException e) {
                log.info("Error when writing to outputStream", e);
                return;
            }
        }
        try {
            inputStream.close();
            // Don't close outputStream, other programs might use it
        } catch (final IOException e) {
            log.info("Error when closing inputStream", e);
            return;
        }
        log.debug("IOStreamPipeThread finished without error");
    }

}
