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
package dk.ange.octave;

/**
 * @author Kim Hansen
 * 
 * Exception from the Octave interface. Most likely caused by the octave process exiting because of a timeout, out of
 * memory error or programming error.
 * 
 * Can also be caused by an OctaveType constructor when the octave variable has the wrong type. This error might be
 * moved to a subclass called OctaveTypeException.
 */
public class OctaveException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public OctaveException() {
        super();
    }

    /**
     * @param message
     */
    public OctaveException(final String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public OctaveException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public OctaveException(final Throwable cause) {
        super(cause);
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
