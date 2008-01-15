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
import java.io.Writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Kim Hansen
 * 
 * A Thread that moves data from a Reader to a Writer
 */
public class ReaderWriterPipeThread extends Thread {

    private static final Log log = LogFactory.getLog(ReaderWriterPipeThread.class);

    private static final int BUFFERSIZE = 4 * 1024;

    private final Reader reader;

    private final Writer writer;

    /**
     * Will create a thread that reads from reader and writes to write until reader reaches EOF. Then it will close
     * reader and finish. Remember to join() this thread before writer is closed.
     * 
     * @param reader
     * @param writer
     */
    public ReaderWriterPipeThread(final Reader reader, final Writer writer) {
        this.reader = reader;
        this.writer = writer;
    }

    @Override
    public void run() {
        final char[] b = new char[BUFFERSIZE];
        while (true) {
            final int len;
            try {
                len = reader.read(b);
            } catch (final IOException e) {
                log.info("Error when reading from reader", e);
                return;
            }
            if (len == -1) {
                break;
            }
            try {
                writer.write(b, 0, len);
                writer.flush();
            } catch (final IOException e) {
                log.info("Error when writing to writer", e);
                return;
            }
        }
        try {
            reader.close();
            // Don't close writer, other programs might use it
        } catch (final IOException e) {
            log.info("Error when closing reader", e);
            return;
        }
        log.debug("ReaderWriterPipeThread finished without error");
    }

}
