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
package dk.ange.octave.io.spi;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.spi.ServiceRegistry;

import dk.ange.octave.type.OctaveType;

/**
 * Interface for the IO handler that can read and write OctaveTypes
 */
public abstract class OctaveDataWriter {

    private static Map<Class<? extends OctaveType>, OctaveDataWriter> writers;

    /**
     * @param clazz
     * @return The OctaveDataWriter or null if it does not exist
     */
    public static OctaveDataWriter getOctaveDataWriter(final Class<? extends OctaveType> clazz) {
        initIfNecessary();
        return writers.get(clazz);
    }

    private static synchronized void initIfNecessary() {
        if (writers == null) {
            writers = new HashMap<Class<? extends OctaveType>, OctaveDataWriter>();
            final Iterator<OctaveDataWriter> sp = ServiceRegistry.lookupProviders(OctaveDataWriter.class);
            while (sp.hasNext()) {
                final OctaveDataWriter odw = sp.next();
                writers.put(odw.javaType(), odw);
            }
        }
    }

    /**
     * Could be OctaveScalar or OctaveMatrix
     * 
     * @return the OctaveType that this IO handler loads and saves
     */
    public abstract Class<? extends OctaveType> javaType();

    /**
     * @param writer
     *                the Writer to write to
     * @param octaveType
     *                the value to write
     * @throws IOException
     */
    public abstract void write(Writer writer, OctaveType octaveType) throws IOException;

}
