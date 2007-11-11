package dk.ange.octave;

import java.io.Reader;
import java.io.StringReader;

import dk.ange.octave.type.OctaveScalar;

/**
 * @author Kim Hansen
 */
public class RunOctave {

    /**
     * @param args
     * @throws OctaveException
     */
    public static void main(final String[] args) throws OctaveException {
        final Octave octave = new Octave();
        try {
            octave.set("a", new OctaveScalar(42));
            octave.execute("a");
            System.out.println("Java: a = " + new OctaveScalar(octave.get("a")).getDouble());
            octave.execute("a=a+10");
            System.out.println("Java: a = " + new OctaveScalar(octave.get("a")).getDouble());

            final Reader outputReader = octave.executeReader(new StringReader("a\na=a+10;\na"));
            while (true) {
                final int c = outputReader.read();
                if (c == -1) {
                    break;
                }
                System.out.print((char) c);
            }
            outputReader.close();

            octave.close();
        } catch (final Exception e) {
            e.printStackTrace();
        }
        System.out.println("END.");
    }

}
