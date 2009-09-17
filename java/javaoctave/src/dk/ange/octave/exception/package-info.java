package dk.ange.octave.exception;

/**
 * The exceptions here are still very close tied to the implementation 
 * of the JavaOctave package, it would be better if they were more related 
 * to what the use of the Octave object needed.
 * <p>
 * I need more experience with the current exception before I do the rewrite.
 * <p>
 * I expect to get down to three kinds of exception:
 * <ul>
 * <li> internal errors in the Octave object
 * <li> lost connection to octave process
 * <li> user error, this should be recoverable
 * <ul>
 * The reason that it could be nice to distinguish between the different types 
 * of error is that the user error could be recoverable in some cases.
 */
