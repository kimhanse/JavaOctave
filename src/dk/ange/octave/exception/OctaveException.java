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
 * Base exception for the JavaOctave project
 */
public abstract class OctaveException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor inherited from RuntimeException
     */
    public OctaveException() {
        // Do nothing
    }

    /**
     * Constructor inherited from RuntimeException
     * 
     * @param message
     */
    public OctaveException(final String message) {
        super(message);
    }

    /**
     * Constructor inherited from RuntimeException
     * 
     * @param cause
     */
    public OctaveException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructor inherited from RuntimeException
     * 
     * @param message
     * @param cause
     */
    public OctaveException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Set to true on exceptions thrown from an Octave object when the reason for the exception is that the object has
     * been asked to destroy its octave process.
     */
    private boolean destroyed = false;

    /**
     * @return destroyed
     */
    public boolean isDestroyed() {
        return destroyed;
    }

    /**
     * @param destroyed
     */
    public void setDestroyed(final boolean destroyed) {
        this.destroyed = destroyed;
    }

}
