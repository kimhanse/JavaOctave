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
/**
 * @author Kim Hansen
 */
package dk.ange.octave.type;

/**
 * Scalar that inherits from 1x1 matrix
 */
public class OctaveScalar extends OctaveNdMatrix {

    private static final long serialVersionUID = 2221234552189760358L;

    private double value;

    /**
     * @param value
     */
    public OctaveScalar(final double value) {
        super(1, 1);
        this.value = value;
    }

    /**
     * @return Returns the value of this object
     */
    public double getDouble() {
        return value;
    }

    @Override
    public double get(final int... pos) {
        if (pos2ind(pos) != 0) {
            throw new IllegalArgumentException("Can only access pos 0 for OctaveScalar");
        }
        return value;
    }

    @Override
    public void set(final double value, final int... pos) {
        if (pos2ind(pos) != 0) {
            throw new IllegalArgumentException("Can only access pos 0 for OctaveScalar");
        }
        this.value = value;
    }

    @Override
    public double[] getData() {
        // FIXME change value to use double[1] data
        throw new UnsupportedOperationException("Not possible for OctaveScalar");
    }

    @Override
    public OctaveScalar makecopy() {
        return new OctaveScalar(value);
    }

    /**
     * Sets value
     * 
     * @param value
     */
    public void set(final double value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(value);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OctaveScalar other = (OctaveScalar) obj;
        if (Double.doubleToLongBits(value) != Double.doubleToLongBits(other.value)) {
            return false;
        }
        return true;
    }

}
