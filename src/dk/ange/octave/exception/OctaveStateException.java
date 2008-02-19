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
package dk.ange.octave.exception;

/**
 * Exception thrown when the internal state of the Octave object is broken
 */
public class OctaveStateException extends OctaveException {

    private static final long serialVersionUID = 3900418745562546024L;

    /**
     * Constructor inherited from OctaveException
     */
    public OctaveStateException() {
        // Do nothing
    }

    /**
     * Constructor inherited from OctaveException
     * 
     * @param message
     */
    public OctaveStateException(final String message) {
        super(message);
    }

    /**
     * Constructor inherited from OctaveException
     * 
     * @param cause
     */
    public OctaveStateException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructor inherited from OctaveException
     * 
     * @param message
     * @param cause
     */
    public OctaveStateException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
