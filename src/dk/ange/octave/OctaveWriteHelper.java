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
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Reader;
import java.util.Map;

import dk.ange.octave.exception.OctaveIOException;
import dk.ange.octave.type.OctaveType;

final class OctaveWriteHelper {

    private OctaveWriteHelper() {
        throw new UnsupportedOperationException("Do not instantiate");
    }

    /**
     * @param values
     * @return Returns a Reader from which the octave input version of values can be read.
     */
    static public Reader octaveReader(final Map<String, OctaveType> values) {
        final PipedReader pipedReader = new PipedReader();
        final PipedWriter pipedWriter = new PipedWriter();
        try {
            pipedWriter.connect(pipedReader);
        } catch (final IOException e) {
            throw new OctaveIOException(e);
        }
        final ToOctaveMultiWriter toOctaveWriter = new ToOctaveMultiWriter(values, pipedWriter);
        toOctaveWriter.start();
        return pipedReader;
    }

    private static class ToOctaveMultiWriter extends Thread {

        final Map<String, OctaveType> octaveTypes;

        final PipedWriter pipedWriter;

        /**
         * @param octaveTypes
         * @param pipedWriter
         */
        public ToOctaveMultiWriter(final Map<String, OctaveType> octaveTypes, final PipedWriter pipedWriter) {
            this.octaveTypes = octaveTypes;
            this.pipedWriter = pipedWriter;
        }

        @Override
        public void run() {
            try {
                // Enter octave in "read data from input mode"
                pipedWriter.write("load(\"-text\", \"-\")\n");
                // Push the data into octave
                for (final Map.Entry<String, OctaveType> entry : octaveTypes.entrySet()) {
                    final String name = entry.getKey();
                    pipedWriter.write("# name: " + name + "\n");
                    entry.getValue().save(pipedWriter);
                }
                // Exit octave from read data mode
                pipedWriter.write("# name: \n");
                pipedWriter.close();
            } catch (final IOException e1) {
                e1.printStackTrace();
            }
        }
    }

}
