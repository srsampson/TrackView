package scope;

public final class Shutdown extends Thread {

    private final ProcessTracks procTrack;
    private final TrackDatabase db;

    public Shutdown(ProcessTracks s1, TrackDatabase s2) {
        procTrack = s1;
        db = s2;
    }

    @Override
    public void run() {
        System.out.println("Shutdown started");

        procTrack.close();
        db.close();

        System.runFinalization();
    }
}
