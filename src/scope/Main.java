package scope;

import java.io.IOException;
import java.util.Locale;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/*
 * Database Track Viewer Program
 *
 * This program displays tracks targets on a 2D Situation Display (SD)
 */
public final class Main {

    private static ScopeFrame f;
    private static TrackDatabase db;
    private static ProcessTracks procTrack;
    //
    private static String configFile = "trackview.conf";
    private static String mapFile = "map.dat";
    private static Config config;

    public static void main(String[] args) {
        /*
         * The user may have a commandline option as to which config file to
         * use, and which map file to use. This is useful if you want more than
         * one Viewer running at a time.
         */

        try {
            switch (args[0]) {
                case "-c":
                case "/c":
                    configFile = args[1];
                    break;
                case "-m":
                case "/m":
                    mapFile = args[1];
                    break;
            }
            switch (args[2]) {
                case "-c":
                case "/c":
                    configFile = args[3];
                    break;
                case "-m":
                case "/m":
                    mapFile = args[3];
                    break;
            }
        } catch (Exception e) {
        }
        
        Locale.setDefault(Locale.US);

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            // Take whatever you get then
        }

        config = new Config(configFile, mapFile);
        System.out.println("Using config file: " + config.getOSConfPath());

        procTrack = new ProcessTracks(config);
        db = new TrackDatabase(procTrack, config);

        Runtime.getRuntime().addShutdownHook(new Shutdown(procTrack, db));

        f = new ScopeFrame(config, db, procTrack);        // Create a new display Panel
        f.setTitle(config.getHomeName());
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(config.getScreenWidth(), config.getScreenHeight());

        f.setVisible(true);

        MapFileParser mfp = new MapFileParser(config);

        try {
            mfp.parse();
            f.setMapData(mfp.getMapData());
        } catch (IOException e) {
            System.out.println("TrackView could not parse MAP data");
            f.setMapData(null);
        }
    }
}
