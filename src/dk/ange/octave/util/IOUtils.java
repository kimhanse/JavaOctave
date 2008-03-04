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
package dk.ange.octave.util;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * Class for holding static utility functions
 */
public final class IOUtils {

    private static final int BUFFER_SIZE = 8 * 1024;

    private IOUtils() {
        throw new UnsupportedOperationException("Do not instantiate");
    }

    /**
     * @param reader
     * @param writer
     * @return The total chars copied
     * @throws IOException
     */
    public static long copy(Reader reader, Writer writer) throws IOException {
        final char[] buffer = new char[BUFFER_SIZE];
        long total = 0;
        while (true) {
            int charsRead = reader.read(buffer, 0, BUFFER_SIZE);
            if (charsRead == -1) {
                break;
            }
            total += charsRead;
            writer.write(buffer, 0, charsRead);
        }
        return total;
    }

}
