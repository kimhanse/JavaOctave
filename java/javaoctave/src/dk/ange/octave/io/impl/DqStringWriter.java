/*
 * Copyright 2008, 2009 Ange Optimization ApS
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
package dk.ange.octave.io.impl;

import java.io.IOException;
import java.io.Writer;

import dk.ange.octave.io.spi.OctaveDataWriter;
import dk.ange.octave.type.OctaveDqString;
import dk.ange.octave.type.OctaveType;

/**
 * The writer of OctaveDqString
 */
public final class DqStringWriter extends OctaveDataWriter {

    @Override
    public Class<? extends OctaveType> javaType() {
        return OctaveDqString.class;
    }

    @Override
    public void write(final Writer writer, final OctaveType octaveType) throws IOException {
        final OctaveDqString octaveString = (OctaveDqString) octaveType;
        final String string = octaveString.getString();
        writer.write("" //
                + "# type: string\n" //
                + "# elements: 1\n" //
                + "# length: " + string.length() + "\n" //
                + string + "\n" //
                + "\n" //
                + "");
    }

}
