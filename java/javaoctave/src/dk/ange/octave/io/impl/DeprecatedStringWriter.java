package dk.ange.octave.io.impl;

import dk.ange.octave.type.OctaveString;
import dk.ange.octave.type.OctaveType;

/**
 * Write the old OctaveString
 */
@Deprecated
public class DeprecatedStringWriter extends DqStringWriter {

    @Override
    public Class<? extends OctaveType> javaType() {
        return OctaveString.class;
    }

}
