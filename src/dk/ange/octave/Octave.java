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
package dk.ange.octave;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Collections;
import java.util.Map;

import dk.ange.octave.type.OctaveType;

/**
 * Interface object
 */
public final class Octave {

    private final OctaveExec octaveExec;

    private final OctaveIO octaveIO;

    /**
     * Will start the octave process.
     * 
     * @param stdinLog
     *                This writer will capture all that is written to the octave process via stdin, if null the data
     *                will not be captured.
     * @param stdoutLog
     *                This writer will capture all that is written from the octave process on stdout, if null the data
     *                will not be captured.
     * @param stderrLog
     *                This writer will capture all that is written from the octave process on stderr, if null the data
     *                will not be captured.
     * @param octaveProgram
     *                This is the path to the octave program, if it is null the program 'octave' will be assumed to be
     *                in the PATH.
     * @param environment
     *                The environment for the octave process, if null the process will inherit the environment for the
     *                virtual mashine.
     * @param workingDir
     *                This will be the working dir for the octave process, if null the process will inherit the working
     *                dir of the current process.
     */
    public Octave(final Writer stdinLog, final Writer stdoutLog, final Writer stderrLog, final File octaveProgram,
            final String[] environment, final File workingDir) {
        octaveExec = new OctaveExec(stdinLog, stdoutLog, stderrLog, octaveProgram, environment, workingDir);
        octaveIO = new OctaveIO(octaveExec);
    }

    /**
     * Will start the octave process in a standard environment.
     * 
     * @param stdinLog
     *                This writer will capture all that is written to the octave process via stdin, if null the data
     *                will not be captured.
     * @param stdoutLog
     *                This writer will capture all that is written from the octave process on stdout, if null the data
     *                will not be captured.
     * @param stderrLog
     *                This writer will capture all that is written from the octave process on stderr, if null the data
     *                will not be captured.
     */
    public Octave(final Writer stdinLog, final Writer stdoutLog, final Writer stderrLog) {
        this(stdinLog, stdoutLog, stderrLog, null, null, null);
    }

    /**
     * Will start the octave process with its output connected to System.out and System.err.
     */
    public Octave() {
        this(null, new OutputStreamWriter(System.out), new OutputStreamWriter(System.err));
    }

    /**
     * @param command
     */
    public void execute(final Reader command) {
        octaveExec.execute(command);
    }

    /**
     * @param command
     */
    public void execute(final String command) {
        octaveExec.execute(command);
    }

    /**
     * @param values
     */
    public void set(final Map<String, OctaveType> values) {
        octaveIO.set(values);
    }

    /**
     * Convenience overload
     * 
     * @param name
     * @param value
     */
    public void set(final String name, final OctaveType value) {
        set(Collections.singletonMap(name, value));
    }

    /**
     * @param <T>
     * @param name
     * @return Returns the value of the variable from octave
     */
    @SuppressWarnings("unchecked")
    public <T extends OctaveType> T get(final String name) {
        return (T) octaveIO.get(name); // Cast works around bug in Sun javac 1.5
    }

    /**
     * Close the octave process in an orderly fasion.
     */
    public void close() {
        octaveExec.close();
    }

    /**
     * @return Returns always true, return value is needed in order for this to be used in assert statements. If there
     *         was an error OctaveException would be thrown.
     */
    public boolean check() {
        return octaveExec.check();
    }

    /**
     * Kill the octave process without remorse
     */
    public void destroy() {
        octaveExec.destroy();
    }

}
