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
package dk.ange.octave.exec;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import dk.ange.octave.util.IOUtils;

/**
 * Reads all from the reader and writes it to the writer
 */
public final class ReaderWriteFunctor implements WriteFunctor {

    private final Reader reader;

    /**
     * @param reader
     */
    public ReaderWriteFunctor(final Reader reader) {
        this.reader = reader;
    }

    public void doWrites(final Writer writer) throws IOException {
        IOUtils.copy(reader, writer);
    }

}
