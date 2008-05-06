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
import java.util.concurrent.Callable;

import dk.ange.octave.exception.OctaveIOException;

/**
 * Callable that reads from the octave process
 */
final class OctaveReaderCallable implements Callable<Integer> {

    private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
            .getLog(OctaveReaderCallable.class);

    private final BufferedReader processReader;

    private final ReadFunctor readFunctor;

    private final String spacer;

    /**
     * @param processReader
     * @param readFunctor
     * @param spacer
     */
    public OctaveReaderCallable(final BufferedReader processReader, final ReadFunctor readFunctor, final String spacer) {
        this.processReader = processReader;
        this.readFunctor = readFunctor;
        this.spacer = spacer;
    }

    public Integer call() throws Exception {
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
        return null;
    }

}
