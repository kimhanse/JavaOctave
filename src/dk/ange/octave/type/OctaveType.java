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

import java.io.Serializable;

/**
 * Common interface for the octave types.
 */
public abstract class OctaveType implements Serializable {

    /**
     * @param type
     * @return a (shallow) copy of type, or null or type is null
     */
    public static OctaveType copy(final OctaveType type) {
        return (type != null) ? type.makecopy() : null;
    }

    /**
     * @return a copy of this.
     */
    public abstract OctaveType makecopy();

}
