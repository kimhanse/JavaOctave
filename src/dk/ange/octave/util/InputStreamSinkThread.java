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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A Thread that read data from an {@link InputStream} and throws it away
 * 
 * @author Kim Hansen
 */
public final class InputStreamSinkThread extends Thread {

    private static final Log log = LogFactory.getLog(InputStreamSinkThread.class);

    private static final int BUFFERSIZE = 4 * 1024;

    private final InputStream inputStream;

    /**
     * Will create a thread that reads from inputStream and discards the read data until inputStream reaches EOF. Then
     * it will close inputStream and finish. There is no reason to wait() for this thread.
     * 
     * @param inputStream
     */
    public InputStreamSinkThread(final InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public void run() {
        final byte[] b = new byte[BUFFERSIZE];
        while (true) {
            int len;
            try {
                len = inputStream.read(b);
            } catch (final IOException e) {
                log.info("Error when reading from inputStream", e);
                return;
            }
            if (len == -1) {
                break;
            }
        }
        try {
            inputStream.close();
        } catch (final IOException e) {
            log.info("Error when closing inputStream", e);
            return;
        }
        log.debug("InputStreamSinkThread finished without error");
    }

}
