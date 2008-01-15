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
package dk.ange.octave.type;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

import dk.ange.octave.OctaveException;

/**
 * @author kim
 * 
 * http://www.octave.org/mailing-lists/octave-maintainers/2005/258
 * http://www.octave.org/octave-lists/archive/octave-maintainers.2005/msg00280.html
 */
public class OctaveString extends OctaveType {

    private static final long serialVersionUID = 7228885699924118810L;

    private String value;

    /**
     * @param reader
     * @throws OctaveException
     */
    public OctaveString(final BufferedReader reader) throws OctaveException {
        this(reader, true);
    }

    /**
     * @param reader
     * @param close
     *            whether to close the stream.
     * @throws OctaveException
     */
    public OctaveString(final BufferedReader reader, final boolean close) throws OctaveException {
        try {
            String line;
            line = readerReadLine(reader);
            if (!line.equals("# type: string")) {
                throw new OctaveException("Wrong type of variable");
            }
            line = readerReadLine(reader);
            if (!line.equals("# elements: 1")) {
                throw new OctaveException("Only implementet for single-line strings '" + line + "'");
            }
            line = readerReadLine(reader);
            if (!line.startsWith("# length: ")) {
                throw new OctaveException("Parse error in String");
            }
            value = readerReadLine(reader);

            if (close) {
                reader.close();
            }
        } catch (final IOException e) {
            throw new OctaveException(e);
        }
    }

    /**
     * @param value
     */
    public OctaveString(final String value) {
        this.value = value;
    }

    @Override
    public void save(final String name, final Writer writer) throws IOException {
        writer.write("# name: " + name + "\n# type: string\n# elements: 1\n# length: " + value.length() + "\n" + value
                + "\n\n");
    }

    @Override
    public boolean equals(final Object thatObject) {
        if (!(thatObject instanceof OctaveString)) {
            return false;
        }
        final OctaveString that = (OctaveString) thatObject;
        return this.value.equals(that.value);
    }

    @Override
    public OctaveString makecopy() {
        return new OctaveString(value);
    }

}
