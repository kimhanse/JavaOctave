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
final class OctaveWriterCallable implements Callable<Void> {

    private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
            .getLog(OctaveWriterCallable.class);

    static final String EXCEPTION_MESSAGE_FUNCTOR = "IOException from WriteFunctor";

    static final String EXCEPTION_MESSAGE_SPACER = "IOException when writing spacer";

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

    public Void call() {
        // Write to process
        try {
            writeFunctor.doWrites(processWriter);
        } catch (final IOException e) {
            log.debug(EXCEPTION_MESSAGE_FUNCTOR, e);
            throw new OctaveIOException(EXCEPTION_MESSAGE_FUNCTOR, e);
        }
        try {
            processWriter.write("\nprintf(\"%s\\n\", \"" + spacer + "\");\n");
            processWriter.flush();
        } catch (final IOException e) {
            log.debug(EXCEPTION_MESSAGE_SPACER, e);
            throw new OctaveIOException(EXCEPTION_MESSAGE_SPACER, e);
        }
        log.debug("Has written all");
        return null;
    }

}
