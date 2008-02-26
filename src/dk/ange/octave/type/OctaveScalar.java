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

import dk.ange.octave.OctaveReadHelper;
import dk.ange.octave.exception.OctaveIOException;
import dk.ange.octave.exception.OctaveParseException;

/**
 * @author Kim Hansen
 */
public class OctaveScalar extends OctaveNdMatrix {

    private static final long serialVersionUID = 2221234552189760358L;

    private double value;

    /**
     * @param reader
     */
    public OctaveScalar(final BufferedReader reader) {
        this(reader, true);
    }

    /**
     * @param reader
     * @param close
     *                whether to close the stream. Really should be true by default
     */
    public OctaveScalar(final BufferedReader reader, final boolean close) {
        super(1, 1);
        try {
            String line = reader.readLine();
            final String token = "# type: scalar";
            if (!line.equals(token)) {
                throw new OctaveParseException("Expected <" + token + ">, but got <" + line + ">\n");
            }
            line = reader.readLine();
            value = OctaveReadHelper.parseDouble(line);
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
    public OctaveScalar(final double value) {
        super(1, 1);
        this.value = value;
    }

    @Override
    public boolean equals(final Object thatObject) {
        if (!(thatObject instanceof OctaveScalar)) {
            return false;
        }
        final OctaveScalar that = (OctaveScalar) thatObject;
        return this.value == that.value;
    }

    /**
     * @return Returns the value of this object
     */
    public double getDouble() {
        return value;
    }

    @Override
    public double get(final int... pos) {
        if (pos2ind(pos) != 0) {
            throw new IllegalArgumentException("Can only access pos 0 for OctaveScalar");
        }
        return value;
    }

    @Override
    public void set(final double value, final int... pos) {
        if (pos2ind(pos) != 0) {
            throw new IllegalArgumentException("Can only access pos 0 for OctaveScalar");
        }
        this.value = value;
    }

    @Override
    public double[] getData() {
        // FIXME change value to use double[1] data
        throw new UnsupportedOperationException("Not possible for OctaveScalar");
    }

    @Override
    public void save(final Writer writer) throws IOException {
        writer.write("# type: scalar\n" + value + "\n\n");
    }

    @Override
    public OctaveScalar makecopy() {
        return new OctaveScalar(value);
    }

    /**
     * Sets value
     * 
     * @param value
     */
    public void set(final double value) {
        this.value = value;
    }

}
