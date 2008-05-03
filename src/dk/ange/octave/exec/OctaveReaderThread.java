/*
 * Copyright 2008 Ange Optimization ApS
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
/**
 * @author Kim Hansen
 */
package dk.ange.octave.exec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.concurrent.CyclicBarrier;

import dk.ange.octave.exception.OctaveIOException;

/**
 * Thread that reads from the octave process
 */
final class OctaveReaderThread extends OctaveBaseThread {

    private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
            .getLog(OctaveReaderThread.class);

    private final BufferedReader processReader;

    private ReadFunctor readFunctor;

    /**
     * A factory that returns a named and running thread.
     * 
     * @param barrier
     * @param processReader
     * @return the constructed and started Thread
     */
    static OctaveReaderThread factory(final CyclicBarrier barrier, final BufferedReader processReader) {
        final OctaveReaderThread readThread = new OctaveReaderThread(barrier, processReader);
        readThread.setName(Thread.currentThread().getName() + "-OctaveReaderThread");
        readThread.start();
        return readThread;
    }

    private OctaveReaderThread(final CyclicBarrier barrier, final BufferedReader processReader) {
        super(barrier);
        this.processReader = processReader;
    }

    @Override
    protected void doStuff() {
        try {
            // Read from process
            final Reader reader = new OctaveExecuteReader(processReader, spacer);
            readFunctor.doReads(reader);
            reader.close();
        } catch (final IOException e) {
            final String message = "Unexpected IOException";
            log.debug(message, e);
            throw new OctaveIOException(message, e);
        }
    }

    void setReadFunctor(final ReadFunctor readFunctor) {
        assert this.readFunctor == null ^ readFunctor == null;
        this.readFunctor = readFunctor;
    }

}
