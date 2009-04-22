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
import java.io.Reader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A Thread that read data from a Reader and throws it away
 * 
 * @author Kim Hansen
 */
public class ReaderSinkThread extends Thread {

    private static final Log log = LogFactory.getLog(ReaderSinkThread.class);

    private static final int BUFFERSIZE = 4 * 1024;

    private final Reader reader;

    /**
     * Will create a thread that reads from reader and discards the read data until reader reaches EOF. Then it will
     * close reader and finish. There is no reason to wait() for this thread.
     * 
     * @param reader
     */
    public ReaderSinkThread(final Reader reader) {
        this.reader = reader;
    }

    @Override
    public void run() {
        final char[] b = new char[BUFFERSIZE];
        while (true) {
            int len;
            try {
                len = reader.read(b);
            } catch (final IOException e) {
                log.info("Error when reading from reader", e);
                return;
            }
            if (len == -1) {
                break;
            }
        }
        try {
            reader.close();
        } catch (final IOException e) {
            log.info("Error when closing reader", e);
            return;
        }
        log.debug("ReaderSinkThread finished without error");

    }

}
