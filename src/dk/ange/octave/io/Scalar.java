/*
 * Copyright 2008 Ange Optimization ApS
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
package dk.ange.octave.io;

import java.io.BufferedReader;
import java.io.Writer;

import dk.ange.octave.type.OctaveScalar;
import dk.ange.octave.type.OctaveType;

/**
 * The IO handler for OctaveScalar
 */
public final class Scalar implements OctaveIOHandler {

    public String format() {
        return "text";
    }

    public Class<? extends OctaveType> type() {
        return OctaveScalar.class;
    }

    public OctaveType read(BufferedReader reader) {
        return new OctaveScalar(reader);
    }

    public void write(Writer writer, OctaveType data) {
        // FIXME data.save(???, writer);
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not implemented");
    }

}
