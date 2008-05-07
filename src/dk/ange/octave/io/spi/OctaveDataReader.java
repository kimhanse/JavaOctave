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

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.spi.ServiceRegistry;

import dk.ange.octave.type.OctaveType;

/**
 * Interface for the IO handler that can read and write OctaveTypes
 */
public abstract class OctaveDataReader {

    private static Map<String, OctaveDataReader> readers = null;

    /**
     * @param type
     * @return The OctaveDataReader or null if it does not exist
     */
    public static OctaveDataReader getOctaveDataReader(final String type) {
        initIfNecessary();
        return readers.get(type);
    }

    private static synchronized void initIfNecessary() {
        if (readers == null) {
            readers = new HashMap<String, OctaveDataReader>();
            final Iterator<OctaveDataReader> sp = ServiceRegistry.lookupProviders(OctaveDataReader.class);
            while (sp.hasNext()) {
                final OctaveDataReader odr = sp.next();
                readers.put(odr.octaveType(), odr);
            }
        }
    }

    /**
     * Could be "scalar" or "string"
     * 
     * @return the OctaveType that this IO handler loads and saves
     */
    public abstract String octaveType();

    /**
     * @param reader
     *                the Reader to read from, will not close reader
     * @return the value read
     */
    public abstract OctaveType read(BufferedReader reader);

}
