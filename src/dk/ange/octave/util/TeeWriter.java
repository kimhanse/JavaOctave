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
import java.io.Writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Executes the actions on a single writer to multiple writers.
 * 
 * If the list of writers in the constructor is empty everything that is written will be discarted.
 * 
 * If there is thrown one or more exception all writers will still be accessed, the exceptions will be logged and the
 * last exception thrown will be passed on outside.
 * 
 * @author Kim Hansen
 */
public class TeeWriter extends Writer {

    private static final Log log = LogFactory.getLog(TeeWriter.class);

    private final Writer[] writers;

    /**
     * Create a writer that doesn't do anything.
     */
    public TeeWriter() {
        this.writers = new Writer[0];
    }

    /**
     * Create a single writer that writes to multiple writers.
     * 
     * @param writers
     *            the list of writers that should be written to.
     */
    public TeeWriter(final Writer... writers) {
        this.writers = writers;
    }

    @Override
    public void write(final char[] cbuf, final int off, final int len) throws IOException {
        IOException ioe = null;
        for (final Writer writer : writers) {
            try {
                writer.write(cbuf, off, len);
            } catch (final IOException e) {
                log.info("Exception during write()", e);
                ioe = e;
            }
        }
        if (ioe != null) {
            throw ioe;
        }
    }

    @Override
    public void flush() throws IOException {
        IOException ioe = null;
        for (final Writer writer : writers) {
            try {
                writer.flush();
            } catch (final IOException e) {
                log.info("Exception during flush()", e);
                ioe = e;
            }
        }
        if (ioe != null) {
            throw ioe;
        }
    }

    @Override
    public void close() throws IOException {
        IOException ioe = null;
        for (final Writer writer : writers) {
            try {
                writer.close();
            } catch (final IOException e) {
                log.info("Exception during close()", e);
                ioe = e;
            }
        }
        if (ioe != null) {
            throw ioe;
        }
    }

}
