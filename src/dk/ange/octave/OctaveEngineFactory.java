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
package dk.ange.octave;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Factory that creates OctaveEngines
 */
public final class OctaveEngineFactory {

    private Writer octaveInputLog = null;

    private Writer errorWriter = new OutputStreamWriter(System.err);

    private File octaveProgram = null;

    private File workingDir = null;

    /**
     * Default constructor
     */
    public OctaveEngineFactory() {
        // Empty constructor
    }

    /**
     * @return a new OctaveEngine
     */
    public OctaveEngine getScriptEngine() {
        return new OctaveEngine(this, octaveInputLog, errorWriter, octaveProgram, workingDir);
    }

    /**
     * @param octaveInputLog
     *                the octaveInputLog to set
     */
    public void setOctaveInputLog(Writer octaveInputLog) {
        this.octaveInputLog = octaveInputLog;
    }

    /**
     * @param errorWriter
     *                the errorWriter to set
     */
    public void setErrorWriter(Writer errorWriter) {
        this.errorWriter = errorWriter;
    }

    /**
     * @param octaveProgram
     *                the octaveProgram to set
     */
    public void setOctaveProgram(File octaveProgram) {
        this.octaveProgram = octaveProgram;
    }

    /**
     * @param workingDir
     *                the workingDir to set
     */
    public void setWorkingDir(File workingDir) {
        this.workingDir = workingDir;
    }

}
