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
 * @author Esben Mose Hansen
 */
package dk.ange.octave.type;

import java.util.HashMap;
import java.util.Map;

/**
 * 1x1 struct
 */
public class OctaveStruct extends OctaveType {

    private static final long serialVersionUID = 430390185317050230L;

    private final Map<String, OctaveType> data;

    /**
     * Create empty struct
     */
    public OctaveStruct() {
        data = new HashMap<String, OctaveType>();
    }

    /**
     * Create struct from data
     * 
     * @param data
     *                this data will be referenced, not copied
     */
    public OctaveStruct(final Map<String, OctaveType> data) {
        this.data = data;
    }

    /**
     * @param name
     * @param value
     */
    public void set(final String name, final OctaveType value) {
        data.put(name, value);
    }

    /**
     * @param key
     * @return (shallow copy of) value for this key, or null if key isn't there.
     */
    public OctaveType get(final String key) {
        return OctaveType.copy(data.get(key));
    }

    /**
     * @return reference to internal map
     */
    public Map<String, OctaveType> getData() {
        return data;
    }

    @Override
    public OctaveStruct makecopy() {
        return new OctaveStruct(data);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((data == null) ? 0 : data.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final OctaveStruct other = (OctaveStruct) obj;
        if (data == null) {
            if (other.data != null)
                return false;
        } else if (!data.equals(other.data))
            return false;
        return true;
    }

}
