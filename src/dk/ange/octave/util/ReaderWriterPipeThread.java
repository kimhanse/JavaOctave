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

/**
 * A Thread that moves data from a Reader to a Writer
 * 
 * @author Kim Hansen
 */
public class ReaderWriterPipeThread extends Thread {

    private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
            .getLog(ReaderWriterPipeThread.class);

    private static final int BUFFERSIZE = 4 * 1024;

    private final Reader reader;

    private Writer writer;

    /**
     * Will create a thread that reads from reader and writes to write until reader reaches EOF. Then the thread will
     * close. Remember to join() this thread before closeing reader or writer.
     * 
     * @param reader
     * @param writer
     * @return Returns the new thread
     */
    public static ReaderWriterPipeThread instantiate(final Reader reader, final Writer writer) {
        final ReaderWriterPipeThread readerWriterPipeThread = new ReaderWriterPipeThread(reader, writer);
        readerWriterPipeThread.setName(Thread.currentThread().getName() + "-ReaderWriterPipeThread");
        readerWriterPipeThread.start();
        return readerWriterPipeThread;
    }

    private ReaderWriterPipeThread(final Reader reader, final Writer writer) {
        this.reader = reader;
        this.writer = writer;
    }

    @Override
    public void run() {
        final char[] b = new char[BUFFERSIZE];
        while (!interrupted()) {
            final int len;
            try {
                len = reader.read(b);
            } catch (final IOException e) {
                log.error("Error when reading from reader", e);
                throw new RuntimeException(e);
            }
            if (len == -1) {
                break;
            }
            try {
                synchronized (this) {
                    if (writer != null) {
                        writer.write(b, 0, len);
                        writer.flush();
                    }
                }
            } catch (final IOException e) {
                log.error("Error when writing to writer", e);
                throw new RuntimeException(e);
            }
        }
        log.debug("ReaderWriterPipeThread finished without error");
    }

    /**
     * @param writer
     *                the writer to set
     */
    public void setWriter(final Writer writer) {
        synchronized (this) {
            this.writer = writer;
        }
    }

    /**
     * Close the thread
     */
    public void close() {
        interrupt();
        try {
            join();
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
