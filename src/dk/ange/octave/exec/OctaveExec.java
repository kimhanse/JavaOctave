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
package dk.ange.octave.exec;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import dk.ange.octave.exception.OctaveException;
import dk.ange.octave.exception.OctaveIOException;
import dk.ange.octave.util.InputStreamSinkThread;
import dk.ange.octave.util.NoCloseWriter;
import dk.ange.octave.util.ReaderWriterPipeThread;
import dk.ange.octave.util.TeeWriter;

/**
 * The object connecting to the octave process
 */
public final class OctaveExec {

    private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
            .getLog(OctaveExec.class);

    private static final String[] CMD_ARRAY = { "octave", "--no-history", "--no-init-file", "--no-line-editing",
            "--no-site-file", "--silent" };

    private final Process process;

    private final Writer processWriter;

    private final BufferedReader processReader;

    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    /*
     * TODO We should wait() on this thread before stderrLog is close()'d
     */
    private final Thread errorStreamThread;

    private boolean destroyed = false;

    /**
     * Will start the octave process.
     * 
     * @param stdinLog
     *                This writer will capture all that is written to the octave process via stdin, if null the data
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
    public OctaveExec(final Writer stdinLog, final Writer stderrLog, final File octaveProgram,
            final String[] environment, final File workingDir) {
        final String[] cmdArray;
        if (octaveProgram == null) {
            cmdArray = CMD_ARRAY;
        } else {
            cmdArray = CMD_ARRAY.clone();
            cmdArray[0] = octaveProgram.getPath();
        }
        try {
            process = Runtime.getRuntime().exec(cmdArray, environment, workingDir);
        } catch (final IOException e) {
            throw new OctaveIOException(e);
        }
        // Connect stderr
        if (stderrLog == null) {
            new InputStreamSinkThread(process.getErrorStream()).start();
            errorStreamThread = null;
        } else {
            errorStreamThread = new ReaderWriterPipeThread(new InputStreamReader(process.getErrorStream()), stderrLog);
            errorStreamThread.setName(Thread.currentThread().getName() + "-ErrorPipe");
            errorStreamThread.start();
        }
        // Connect stdout
        processReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        // Connect stdin
        if (stdinLog == null) {
            processWriter = new OutputStreamWriter(process.getOutputStream());
        } else {
            processWriter = new TeeWriter(new NoCloseWriter(stdinLog),
                    new OutputStreamWriter(process.getOutputStream()));
        }
    }

    /*
     * New execute
     */

    private final Random random = new Random();

    private String generateSpacer() {
        return "-=+X+=- Octave.java spacer -=+X+=- " + random.nextLong() + " -=+X+=-";
    }

    /**
     * @param input
     * @param output
     */
    public void eval(final WriteFunctor input, final ReadFunctor output) {
        final String spacer = generateSpacer();
        final Future<Integer> writerFuture = executor.submit(new OctaveWriterCallable(processWriter, input, spacer));
        final Future<Integer> readerFuture = executor.submit(new OctaveReaderCallable(processReader, output, spacer));
        final RuntimeException writerException = getFromFuture(writerFuture);
        final RuntimeException readerException = getFromFuture(readerFuture);
        if (writerException != null) {
            throw writerException;
        }
        if (readerException != null) {
            throw readerException;
        }
    }

    private RuntimeException getFromFuture(final Future<Integer> future) {
        try {
            future.get();
        } catch (final InterruptedException e) {
            final String message = "Should not happen";
            log.error(message, e);
            return new RuntimeException(message, e);
        } catch (final ExecutionException e) {
            if (e.getCause() instanceof OctaveException) {
                final OctaveException oe = (OctaveException) e.getCause();
                return reInstantiateException(oe);
            }
            final String message = "Should not happen";
            log.error(message, e);
            return new RuntimeException(message, e);
        } catch (final RuntimeException e) {
            final String message = "Should not happen";
            log.error(message, e);
            return new RuntimeException(message, e);
        }
        return null;
    }

    private OctaveException reInstantiateException(final OctaveException inException) {
        final OctaveException outException;
        try {
            outException = inException.getClass().getConstructor(String.class, Throwable.class).newInstance(
                    inException.getMessage(), inException);
        } catch (final Exception e) {
            throw new IllegalStateException("Should not happen", e);
        }
        if (isDestroyed()) {
            outException.setDestroyed(true);
        }
        return outException;
    }

    /**
     * @param writeFunctor
     * @param output
     */
    public void execute(final WriteFunctor writeFunctor, final Writer output) {
        eval(writeFunctor, new WriterReadFunctor(output));
    }

    private synchronized void setDestroyed(final boolean destroyed) {
        this.destroyed = destroyed;
    }

    private synchronized boolean isDestroyed() {
        return destroyed;
    }

    /**
     * Kill the octave process without remorse
     */
    public void destroy() {
        setDestroyed(true);
        executor.shutdownNow();
        process.destroy();
        try {
            processWriter.close();
        } catch (final IOException e) {
            throw new OctaveIOException(e);
        }
    }

    /**
     * Close the octave process in an orderly fasion.
     */
    public void close() {
        try {
            // TODO rewrite this to use eval() and some specialiced Functors
            processWriter.write("exit\n");
            processWriter.close();
            final String read = processReader.readLine();
            if (read != null) {
                throw new OctaveIOException("Expected reader to be closed: " + read);
            }
            processReader.close();
        } catch (final IOException e) {
            final OctaveIOException octaveException = new OctaveIOException("reader error", e);
            if (isDestroyed()) {
                octaveException.setDestroyed(true);
            }
            throw octaveException;
        } finally {
            executor.shutdown();
        }
    }

}
