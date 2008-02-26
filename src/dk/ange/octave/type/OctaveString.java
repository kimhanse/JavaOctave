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
 * @author kim
 */
package dk.ange.octave.type;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

import dk.ange.octave.OctaveReadHelper;
import dk.ange.octave.exception.OctaveIOException;
import dk.ange.octave.exception.OctaveParseException;

/**
 * http://www.octave.org/mailing-lists/octave-maintainers/2005/258
 * http://www.octave.org/octave-lists/archive/octave-maintainers.2005/msg00280.html
 */
public class OctaveString extends OctaveType {

    private static final long serialVersionUID = 7228885699924118810L;

    private String value;

    /**
     * @param reader
     */
    public OctaveString(final BufferedReader reader) {
        this(reader, true);
    }

    /**
     * @param reader
     * @param close
     *                whether to close the stream.
     */
    public OctaveString(final BufferedReader reader, final boolean close) {
        try {
            String line;
            line = OctaveReadHelper.readerReadLine(reader);
            if (!line.equals("# type: string")) {
                throw new OctaveParseException("Wrong type of variable");
            }
            line = OctaveReadHelper.readerReadLine(reader);
            if (!line.equals("# elements: 1")) {
                throw new OctaveParseException("Only implementet for single-line strings '" + line + "'");
            }
            line = OctaveReadHelper.readerReadLine(reader);
            if (!line.startsWith("# length: ")) {
                throw new OctaveParseException("Parse error in String");
            }
            value = OctaveReadHelper.readerReadLine(reader);

            if (close) {
                reader.close();
            }
        } catch (final IOException e) {
            throw new OctaveIOException(e);
        }
    }

    /**
     * @param value
     */
    public OctaveString(final String value) {
        this.value = value;
    }

    /**
     * TODO move this to a dedicated OctaveDataWriter
     * 
     * @param writer
     * @throws IOException
     */
    public void save(final Writer writer) throws IOException {
        writer.write("" //
                + "# type: string\n" //
                + "# elements: 1\n" //
                + "# length: " + value.length() + "\n" //
                + value + "\n" //
                + "\n" //
                + "");
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
