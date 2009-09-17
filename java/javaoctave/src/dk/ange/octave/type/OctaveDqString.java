/*
 * Copyright 2007, 2008, 2009 Ange Optimization ApS
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

/**
 * The double quoted string in Octave.
 * 
 * http://www.octave.org/mailing-lists/octave-maintainers/2005/258
 * http://www.octave.org/octave-lists/archive/octave-maintainers.2005/msg00280.html
 * http://osdir.com/ml/gnu.octave.maintainers/2005-04/msg00005.html
 */
public class OctaveDqString extends OctaveType {

    private static final long serialVersionUID = 7228885699924118810L;

    /**
     * The String
     */
    protected String value;

    /**
     * @param string
     */
    public OctaveDqString(final String string) {
        this.value = string;
    }

    /**
     * @return the string
     */
    public String getString() {
        return value;
    }

    /**
     * @param string
     *            the string to set
     */
    public void setString(final String string) {
        this.value = string;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            if (obj instanceof OctaveDqString) {
                final OctaveDqString other = (OctaveDqString) obj;
                return other.equals(obj);
            }
            return false;
        }
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

    @Override
    public OctaveDqString makecopy() {
        return new OctaveDqString(value);
    }

}
