package dk.ange.octave.type;

/**
 * @deprecated Use OctaveDqString, will be gone in 0.4
 */
@Deprecated
public class OctaveString extends OctaveDqString {

    /**
     * @param string
     */
    public OctaveString(String string) {
        super(string);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (OctaveDqString.class != obj.getClass()) {
            final OctaveDqString other = (OctaveDqString) obj;
            if (value == null) {
                if (other.value != null) {
                    return false;
                }
            } else if (!value.equals(other.value)) {
                return false;
            }
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OctaveString other = (OctaveString) obj;
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        return true;
    }

}
