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

import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.CyclicBarrier;

import dk.ange.octave.exception.OctaveIOException;

/**
 * Thread that writes to the octave process
 */
final class OctaveWriterThread extends OctaveBaseThread {

    private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
            .getLog(OctaveWriterThread.class);

    private final Writer processWriter;

    private WriteFunctor writeFunctor;

    /**
     * A factory that returns a named and running thread.
     * 
     * @param barrier
     * @param processWriter
     * @return the constructed and started Thread
     */
    static OctaveWriterThread factory(final CyclicBarrier barrier, final Writer processWriter) {
        final OctaveWriterThread writerThread = new OctaveWriterThread(barrier, processWriter);
        writerThread.setName(Thread.currentThread().getName() + "-OctaveWriterThread");
        writerThread.start();
        return writerThread;
    }

    private OctaveWriterThread(final CyclicBarrier barrier, final Writer processWriter) {
        super(barrier);
        this.processWriter = processWriter;
    }

    @Override
    protected void doStuff() {
        // Write to process
        try {
            writeFunctor.doWrites(processWriter);
            processWriter.write("\nprintf(\"%s\\n\", \"" + spacer + "\");\n");
            processWriter.flush();
            log.debug("Has written all");
        } catch (final IOException e) {
            final String message = "Unexpected IOException";
            log.debug(message, e);
            throw new OctaveIOException(message, e);
        }
    }

    void setWriteFunctor(final WriteFunctor writeFunctor) {
        assert this.writeFunctor == null ^ writeFunctor == null;
        this.writeFunctor = writeFunctor;
    }

}
