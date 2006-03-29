package dk.ange.octave;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import dk.ange.octave.type.OctaveScalar;

public class RunOctave {

    /**
     * @param args
     * @throws OctaveException
     * @throws IOException
     */
    public static void main(String[] args) throws OctaveException {
        Octave octave = new Octave();
        try {
            octave.set("a", new OctaveScalar(42));
            octave.execute("a");
            System.out.println("Java: a = "
                    + new OctaveScalar(octave.get("a")).getDouble());
            octave.execute("a=a+10");
            System.out.println("Java: a = "
                    + new OctaveScalar(octave.get("a")).getDouble());

            Reader outputReader = octave.executeReader(new StringReader(
                    "a\na=a+10;\na"));
            while (true) {
                int c = outputReader.read();
                if (c == -1)
                    break;
                System.out.print((char) c);
            }
            outputReader.close();

            octave.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("END.");
    }

}
