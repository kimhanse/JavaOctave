package dk.ange.octave.type;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import dk.ange.octave.OctaveException;

public class OctaveStruct extends OctaveType {

    private Map<String, OctaveType> data;

    public OctaveStruct() {
        data = new HashMap<String, OctaveType>();
    }

    @Override
    public void toOctave(Writer writer, String name) throws OctaveException {
        try {
            // FIXME This will break with nested structs
            String tmp_var_name = "octave_java_tmp_struct";
            boolean tmp_var_used = false;
            writer.write(name + "=struct();\n");
            for (Map.Entry<String, OctaveType> e : data.entrySet()) {
                writer.write("clear " + tmp_var_name + ";\n");
                e.getValue().toOctave(writer, tmp_var_name);
                tmp_var_used = true;
                writer.write(name + '.' + e.getKey() + "=" + tmp_var_name
                        + ";\n");

            }
            if (tmp_var_used) {
                writer.write("clear " + tmp_var_name + ";\n");
            }
        } catch (IOException e) {
            throw new OctaveException(e);
        }
    }

    public void set(String key, OctaveType value) {
        data.put(key, value);
    }

}
