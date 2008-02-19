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
                    entry.getValue().save(entry.getKey(), pipedWriter);
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
