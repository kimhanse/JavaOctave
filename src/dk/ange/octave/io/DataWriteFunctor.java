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
package dk.ange.octave.io;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import dk.ange.octave.exception.OctaveIOException;
import dk.ange.octave.exec.WriteFunctor;
import dk.ange.octave.type.OctaveType;

/**
 * Write data from OctaveType in a Map
 */
final class DataWriteFunctor implements WriteFunctor {

    private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
            .getLog(DataWriteFunctor.class);

    final Map<String, OctaveType> octaveTypes;

    /**
     * @param octaveTypes
     */
    public DataWriteFunctor(final Map<String, OctaveType> octaveTypes) {
        this.octaveTypes = octaveTypes;
    }

    public void doWrite(final Writer writer) {
        try {
            // Enter octave in "read data from input mode"
            writer.write("load(\"-text\", \"-\")\n");
            // Push the data into octave
            for (final Map.Entry<String, OctaveType> entry : octaveTypes.entrySet()) {
                final String name = entry.getKey();
                OctaveIO.write(writer, name, entry.getValue());
            }
            // Exit octave from read data mode
            writer.write("# name: \n");
            writer.flush();
        } catch (final IOException e) {
            final String message = "Unexpected IOException";
            log.error(message, e);
            throw new OctaveIOException(message, e);
        }
    }

}
