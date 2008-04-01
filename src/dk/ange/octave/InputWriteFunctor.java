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
package dk.ange.octave;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import dk.ange.octave.exception.OctaveIOException;
import dk.ange.octave.util.StringUtil;

/**
 * Write commands read from a Reader
 */
public final class InputWriteFunctor implements WriteFunctor {

    private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
            .getLog(InputWriteFunctor.class);

    private static final int BUFFERSIZE = 4 * 1024;

    private final Reader currentInput;

    /**
     * @param currentInput
     */
    public InputWriteFunctor(final Reader currentInput) {
        this.currentInput = currentInput;
    }

    public void doWrite(final Writer writer) {
        log.debug("Enter doWrite()");
        try {
            final char[] cbuf = new char[BUFFERSIZE];
            while (true) {
                // FIXME change loop to IOUtils.copy()
                final int c = currentInput.read(cbuf);
                if (c < 0) {
                    break;
                }
                if (log.isTraceEnabled()) {
                    log.trace("octaveWriter.write(" + StringUtil.jQuote(cbuf, c) + ", 0, " + c + ")");
                }
                writer.write(cbuf, 0, c);
                writer.flush();
            }
            currentInput.close();
        } catch (final IOException e) {
            final String message = "Unexpected IOException";
            log.error(message, e);
            throw new OctaveIOException(message, e);
        }
    }

}
