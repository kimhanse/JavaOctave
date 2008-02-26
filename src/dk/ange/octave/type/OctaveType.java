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

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;

import dk.ange.octave.OctaveIO;
import dk.ange.octave.exception.OctaveException;
import dk.ange.octave.exception.OctaveIOException;

/**
 * @author Kim Hansen
 * 
 * Common interface for the octave types.
 */
public abstract class OctaveType implements Serializable {

    @Override
    public String toString() {
        try {
            return toText("ans");
        } catch (final OctaveException e) {
            e.printStackTrace();
            return "[invalid octavetype: " + e.getMessage() + "]";
        }
    }

    /**
     * @param name
     * @return Text to feed to 'load -text -' to define the variable
     */
    // FIXME @deprecate this method
    public String toText(final String name) {
        try {
            final StringWriter writer = new StringWriter();
            OctaveIO.write(writer, name, this);
            return writer.toString();
        } catch (final IOException e) {
            throw new OctaveIOException(e);
        }
    }

    /**
     * @param type
     * @return a (shallow) copy of type, or null or type is null
     */
    public static OctaveType copy(final OctaveType type) {
        return (type != null) ? type.makecopy() : null;
    }

    /**
     * @return a copy of this.
     */
    public abstract OctaveType makecopy();

}
