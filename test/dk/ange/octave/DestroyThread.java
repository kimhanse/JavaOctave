package dk.ange.octave;

/**
 * Helper for TestOctave
 * 
 * @author Kim Hansen
 */
class DestroyThread extends Thread {

    private Octave octave;

    public DestroyThread(Octave octave) {
        this.octave = octave;
    }

    @Override
    public void run() {
        try {
            sleep(1000);
            octave.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
