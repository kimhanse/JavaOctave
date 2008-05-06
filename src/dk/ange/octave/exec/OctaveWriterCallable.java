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
import java.util.concurrent.Callable;

import dk.ange.octave.exception.OctaveIOException;

/**
 * Callable that writes to the octave process
 */
final class OctaveWriterCallable implements Callable<Integer> {

    private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
            .getLog(OctaveWriterCallable.class);

    private final Writer processWriter;

    private final WriteFunctor writeFunctor;

    private final String spacer;

    /**
     * @param processWriter
     * @param writeFunctor
     * @param spacer
     */
    public OctaveWriterCallable(final Writer processWriter, final WriteFunctor writeFunctor, final String spacer) {
        this.processWriter = processWriter;
        this.writeFunctor = writeFunctor;
        this.spacer = spacer;
    }

    public Integer call() throws Exception {
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
        return null;
    }

}
