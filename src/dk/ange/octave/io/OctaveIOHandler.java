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

import dk.ange.octave.type.OctaveType;

/**
 * Interface for the IO handler that can read and write OctaveTypes
 */
public interface OctaveIOHandler {

    /**
     * Could be 'text' or 'hdf5'
     * 
     * @return the format that this IO handler load and saves
     */
    public String format();

    /**
     * Could be OctaveScalar or OctaveMatrix
     * 
     * @return the OctaveType that this IO handler loads and saves
     */
    public Class<? extends OctaveType> type();

    /**
     * @param reader
     *                the Reader to read from
     * @return the value read
     */
    public OctaveType read(BufferedReader reader);

    /**
     * @param writer
     *                the Writer to write to
     * @param data
     *                the value to write
     */
    public void write(Writer writer, OctaveType data);

}
