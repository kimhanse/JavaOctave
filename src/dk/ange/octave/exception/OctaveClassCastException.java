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
 * Exception thrown when a cast fails inside the Octave object
 */
public class OctaveClassCastException extends OctaveException {

    private static final long serialVersionUID = -3220354511039186384L;

    /**
     * Constructor inherited from OctaveException
     */
    public OctaveClassCastException() {
        // Do nothing
    }

    /**
     * Constructor inherited from OctaveException
     * 
     * @param message
     */
    public OctaveClassCastException(final String message) {
        super(message);
    }

    /**
     * Constructor inherited from OctaveException
     * 
     * @param cause
     */
    public OctaveClassCastException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructor inherited from OctaveException
     * 
     * @param message
     * @param cause
     */
    public OctaveClassCastException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
