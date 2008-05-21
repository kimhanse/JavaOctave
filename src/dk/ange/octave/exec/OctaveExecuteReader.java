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
/**
 * @author Kim Hansen
 */
package dk.ange.octave.exec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import dk.ange.octave.util.StringUtil;

/**
 * Reader that passes the reading on to the output from the octave process until the spacer reached, then it returns
 * EOF.
 */
final class OctaveExecuteReader extends Reader {

    private static final Log log = LogFactory.getLog(OctaveExecuteReader.class);

    private final BufferedReader octaveReader;

    private final String spacer;

    private StringBuffer buffer;

    private boolean firstLine = true;

    private boolean eof = false;

    /**
     * This reader will read from octaveReader until a single line equal() spacer is read, after that this reader will
     * return eof. When this reader is closed it will update the state of octave to NONE.
     * 
     * @param octaveReader
     * @param spacer
     */
    public OctaveExecuteReader(final BufferedReader octaveReader, final String spacer) {
        this.octaveReader = octaveReader;
        this.spacer = spacer;
    }

    @Override
    public int read(final char[] cbuf, final int off, final int len) throws IOException {
        if (eof) {
            return -1;
        }
        if (buffer == null) {
            final String line = octaveReader.readLine();
            if (log.isTraceEnabled()) {
                log.trace("octaveReader.readLine() = " + StringUtil.jQuote(line));
            }
            if (line == null) {
                throw new IOException("Pipe to octave-process broken");
            }
            if (spacer.equals(line)) {
                eof = true;
                return -1;
            }
            buffer = new StringBuffer(line.length() + 1);
            if (firstLine) {
                firstLine = false;
            } else {
                buffer.append('\n');
            }
            buffer.append(line);
        }
        final int charsRead = Math.min(buffer.length(), len);
        buffer.getChars(0, charsRead, cbuf, off);
        if (charsRead == buffer.length()) {
            buffer = null;
        } else {
            buffer.delete(0, charsRead);
        }
        return charsRead;
    }

    @Override
    public void close() throws IOException {
        if (read() != -1) {
            throw new IOException("read hasn't finished");
        }
        if (octaveReader.ready()) {
            throw new IOException("octaveReader is ready()");
        }
        log.debug("Reader closed()");
    }

}
