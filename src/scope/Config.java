/**
 * Config.java
 */
package scope;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Configuration class
 *
 * @author Steve Sampson, January 2020
 */
public final class Config {

    public static final Color NORMAL_WHITE = new Color(231, 231, 231);
    public static final Color HIGH_WHITE = new Color(239, 239, 239);
    public static final Color LOW_WHITE = new Color(66, 66, 66);
    //
    public static final String STATION_LON = "station.longitude";
    public static final String STATION_LAT = "station.latitude";
    public static final String STATION_ALT = "station.alt";
    public static final String STATION_NAME = "station.name";
    //
    public static final String SCREEN_HEIGHT = "screen.height";
    public static final String SCREEN_WIDTH = "screen.width";
    public static final String SCREEN_SCALE = "screen.scale";
    //
    public static final String DISP_INSTRM_TRACK = "display.track";
    public static final String DISP_INSTRM_VECTOR = "display.vector";
    public static final String DISP_INSTRM_BLOCK = "display.block";
    //
    public static final String DISP_INSTRM_LEADER = "display.leader";
    public static final String DISP_INSTRM_TRANS = "display.transition";
    //
    public static final String DISP_INSTRM_ECHOES = "display.echoes";
    public static final String DISP_INSTRM_GND_ECHO = "display.gnd_echo";
    public static final String DISP_INSTRM_ESIZE = "display.echosize";
    public static final String DISP_INSTRM_LOW = "display.low";
    public static final String DISP_INSTRM_HIGH = "display.high";
    public static final String DISP_INSTRM_FONT = "display.font";
    public static final String DISP_INSTRM_TRACK_FONT = "display.track_font";
    public static final String DISP_INSTRM_CA = "display.ca";
    public static final String DISP_INSTRM_CAALT = "display.ca-alt";
    public static final String DISP_INSTRM_CAFLOOR = "display.ca-floor";
    public static final String DISP_INSTRM_CARNG = "display.ca-range";
    public static final String DISP_INSTRM_DIM = "display.dim";
    public static final String DISP_INSTRM_DROP = "display.drop";
    //
    public static final String COLORS_MAP1 = "display.color_map1";
    public static final String COLORS_MAP2 = "display.color_map2";
    public static final String COLORS_MAP3 = "display.color_map3";
    public static final String COLORS_MAP4 = "display.color_map4";
    public static final String COLORS_MAP5 = "display.color_map5";
    public static final String COLORS_MAP6 = "display.color_map6";
    public static final String COLORS_MAP7 = "display.color_map7";
    public static final String COLORS_MAP8 = "display.color_map8";
    public static final String COLORS_MAP9 = "display.color_map9";
    public static final String COLORS_MAP10 = "display.color_map10";
    public static final String COLORS_MAP11 = "display.color_map11";
    public static final String COLORS_MAP12 = "display.color_map12";
    public static final String COLORS_MAP13 = "display.color_map13";
    public static final String COLORS_MAP14 = "display.color_map14";
    public static final String COLORS_MAP15 = "display.color_map15";
    public static final String COLORS_MAP16 = "display.color_map16";
    public static final String COLORS_MAP17 = "display.color_map17";
    public static final String COLORS_MAP18 = "display.color_map18";
    public static final String COLORS_MAP19 = "display.color_map19";
    public static final String COLORS_MAP20 = "display.color_map20";
    public static final String COLORS_MAP21 = "display.color_map21";
    public static final String COLORS_MAP22 = "display.color_map22";
    public static final String COLORS_MAP23 = "display.color_map23";
    public static final String COLORS_MAP24 = "display.color_map24";
    public static final String COLORS_MAP25 = "display.color_map25";
    public static final String COLORS_MAP26 = "display.color_map26";
    public static final String COLORS_MAP27 = "display.color_map27";
    public static final String COLORS_MAP28 = "display.color_map28";
    public static final String COLORS_MAP29 = "display.color_map29";
    public static final String COLORS_MAP30 = "display.color_map30";
    //
    public static final String COLORS_TRACK = "display.color_track";
    public static final String COLORS_TRACK_CLIMB = "display.color_track_climb";
    public static final String COLORS_TRACK_DESCEND = "display.color_track_descend";
    public static final String COLORS_TRACK_HIST = "display.color_track_history";
    public static final String COLORS_TRACK_CLIMB_HIST = "display.color_track_climb_history";
    public static final String COLORS_TRACK_DESCEND_HIST = "display.color_track_descend_history";
    public static final String COLORS_BACK_GND = "display.color_background";
    //
    public static final String MAP_OBJECTS = "display.map_objects";
    public static final String MAP_NAMES = "display.map_names";
    public static final String MAP_VECTORS = "display.map_vectors";
    //
    private final ConcurrentHashMap<String, Boolean> booleanSettings;
    private final ConcurrentHashMap<String, IntegerSetting> integerSettings;
    private final ConcurrentHashMap<String, Color> colorSettings;
    private final ConcurrentHashMap<String, Font> fontSettings;
    //
    private final String userDir;
    private final String fileSeparator;
    private final String homeDir;
    private final String OSConfPath;
    private final Properties Props;
    private final String mapPath;
    private double homeLon;
    private double homeLat;
    private int homeAlt;
    private String homeName;
    private double mapScale;
    private int screenWidth;
    private int screenHeight;
    private FileInputStream in;
    private boolean noProps;
    //
    private final Timestamp sqlTime;
    private final ZuluMillis zulu;
    //
    private Color[] colorMap;
    private String databaseName;
    private String databaseHost;
    private String databasePort;
    private String databaseLogin;
    private String databasePassword;

    Config(String conf, String map) {
        sqlTime = new Timestamp(0L);
        zulu = new ZuluMillis();
        colorMap = new Color[30];
        noProps = false;
        booleanSettings = new ConcurrentHashMap<>(25);
        integerSettings = new ConcurrentHashMap<>(25);
        colorSettings = new ConcurrentHashMap<>(25);
        fontSettings = new ConcurrentHashMap<>(25);
        homeLon = 0.0;
        homeLat = 0.0;
        homeAlt = 0;
        homeName = "";
        mapScale = 0.001;
        screenWidth = 0;
        screenHeight = 0;

        Props = new Properties();

        userDir = System.getProperty("user.dir");
        fileSeparator = System.getProperty("file.separator");
        homeDir = userDir + fileSeparator;
        OSConfPath = userDir + fileSeparator + conf;
        mapPath = userDir + fileSeparator + map;

        initProperties(OSConfPath);
    }

    public void initProperties(String filename) {
        String temp;
        String[] token;
        int red, green, blue, v1, v2, v3, v4;

        for (int i = 0; i < 30; i++) {
            colorMap[i] = Color.LIGHT_GRAY; // 192 192 192
        }

        try {
            in = new FileInputStream(filename);
            Props.load(in);
        } catch (IOException e) {
            // File probably doesn't exist
            noProps = true;
        } catch (Exception e1) {
            System.err.println("Config::getProperties exception Loading Properties " + e1.toString());
        }

        temp = Props.getProperty("db.host");
        if (temp == null) {
            databaseHost = "127.0.0.1";
            System.out.println("db.host not set, set to 127.0.0.1");
        } else {
            databaseHost = temp.trim();
        }

        temp = Props.getProperty("db.name");
        if (temp == null) {
            databaseName = "adsb";
            System.out.println("db.name not set, set to adsb");
        } else {
            databaseName = temp.trim();
        }

        temp = Props.getProperty("db.port");
        if (temp == null) {
            databasePort = "3306";
            System.out.println("db.port not set, set to 3306");
        } else {
            databasePort = temp.trim();
        }

        temp = Props.getProperty("db.login");
        if (temp == null) {
            databaseLogin = "adsb-ro";
            System.out.println("db.login not set, set to adsb-ro");
        } else {
            databaseLogin = temp.trim();
        }

        temp = Props.getProperty("db.password");
        if (temp == null) {
            databasePassword = "secret";
            System.out.println("db.password not set, set to secret");
        } else {
            databasePassword = temp.trim();
        }

        temp = Props.getProperty(STATION_ALT, "0").trim();
        Props.setProperty(STATION_ALT, temp);
        try {
            homeAlt = Integer.parseInt(temp);
        } catch (NumberFormatException e) {
            homeAlt = 0;
        }

        temp = Props.getProperty(STATION_LAT, "20.0").trim();
        Props.setProperty(STATION_LAT, temp);
        try {
            homeLat = Double.parseDouble(temp);
        } catch (NumberFormatException e) {
            homeLat = 20.0D;
        }

        temp = Props.getProperty(STATION_LON, "0.0").trim();
        Props.setProperty(STATION_LON, temp);
        try {
            homeLon = Double.parseDouble(temp);
        } catch (NumberFormatException e) {
            homeLon = 0.0D;
        }

        temp = Props.getProperty(STATION_NAME, "TrackViewer-1.91").trim();
        Props.setProperty(STATION_NAME, temp);
        homeName = temp;

        temp = Props.getProperty(SCREEN_HEIGHT, "775").trim();
        Props.setProperty(SCREEN_HEIGHT, temp);
        try {
            screenHeight = Integer.parseInt(temp);
        } catch (NumberFormatException e) {
            screenHeight = 650;
        }

        temp = Props.getProperty(SCREEN_WIDTH, "860").trim();
        Props.setProperty(SCREEN_WIDTH, temp);
        try {
            screenWidth = Integer.parseInt(temp);
        } catch (NumberFormatException e) {
            screenWidth = 860;
        }

        temp = Props.getProperty(SCREEN_SCALE, ".001").trim();
        Props.setProperty(SCREEN_SCALE, temp);
        try {
            mapScale = Double.parseDouble(temp);
        } catch (NumberFormatException e) {
            mapScale = .001D;
        }

        temp = Props.getProperty(DISP_INSTRM_DIM, "90 5 300");
        Props.setProperty(DISP_INSTRM_DIM, temp);
        token = temp.split(" ");
        v1 = Integer.parseInt(token[0].trim());
        v2 = Integer.parseInt(token[1].trim());
        v3 = Integer.parseInt(token[2].trim());

        addIntegerSetting(DISP_INSTRM_DIM, v1, v2, v3);

        temp = Props.getProperty(DISP_INSTRM_DROP, "120 5 300");
        Props.setProperty(DISP_INSTRM_DROP, temp);
        token = temp.split(" ");
        v1 = Integer.parseInt(token[0].trim());
        v2 = Integer.parseInt(token[1].trim());
        v3 = Integer.parseInt(token[2].trim());

        addIntegerSetting(DISP_INSTRM_DROP, v1, v2, v3);

        temp = Props.getProperty(DISP_INSTRM_VECTOR, "1 0 60");
        Props.setProperty(DISP_INSTRM_VECTOR, temp);
        token = temp.split(" ");
        v1 = Integer.parseInt(token[0].trim());
        v2 = Integer.parseInt(token[1].trim());
        v3 = Integer.parseInt(token[2].trim());

        addIntegerSetting(DISP_INSTRM_VECTOR, v1, v2, v3);

        temp = Props.getProperty(DISP_INSTRM_ECHOES, "1 0 60");
        Props.setProperty(DISP_INSTRM_ECHOES, temp);
        token = temp.split(" ");
        v1 = Integer.parseInt(token[0].trim());
        v2 = Integer.parseInt(token[1].trim());
        v3 = Integer.parseInt(token[2].trim());

        addIntegerSetting(DISP_INSTRM_ECHOES, v1, v2, v3);

        temp = Props.getProperty(DISP_INSTRM_ESIZE, "1 1 4");
        Props.setProperty(DISP_INSTRM_ESIZE, temp);
        token = temp.split(" ");
        v1 = Integer.parseInt(token[0].trim());
        v2 = Integer.parseInt(token[1].trim());
        v3 = Integer.parseInt(token[2].trim());

        addIntegerSetting(DISP_INSTRM_ESIZE, v1, v2, v3);

        temp = Props.getProperty(DISP_INSTRM_BLOCK, "0 0 8");
        Props.setProperty(DISP_INSTRM_BLOCK, temp);
        token = temp.split(" ");
        v1 = Integer.parseInt(token[0].trim());
        v2 = Integer.parseInt(token[1].trim());
        v3 = Integer.parseInt(token[2].trim());

        addIntegerSetting(DISP_INSTRM_BLOCK, v1, v2, v3);

        temp = Props.getProperty(DISP_INSTRM_LEADER, "6 0 6");
        Props.setProperty(DISP_INSTRM_LEADER, temp);
        token = temp.split(" ");
        v1 = Integer.parseInt(token[0].trim());
        v2 = Integer.parseInt(token[1].trim());
        v3 = Integer.parseInt(token[2].trim());

        addIntegerSetting(DISP_INSTRM_LEADER, v1, v2, v3);

        temp = Props.getProperty(DISP_INSTRM_TRANS, "180 5 5 600");
        Props.setProperty(DISP_INSTRM_TRANS, temp);
        token = temp.split(" ");
        v1 = Integer.parseInt(token[0].trim());
        v2 = Integer.parseInt(token[1].trim());
        v3 = Integer.parseInt(token[2].trim());
        v4 = Integer.parseInt(token[3].trim());

        addIntegerSetting(DISP_INSTRM_TRANS, v1, v2, v3, v4);

        temp = Props.getProperty(DISP_INSTRM_HIGH, "995 10 0 995");
        Props.setProperty(DISP_INSTRM_HIGH, temp);
        token = temp.split(" ");
        v1 = Integer.parseInt(token[0].trim());
        v2 = Integer.parseInt(token[1].trim());
        v3 = Integer.parseInt(token[2].trim());
        v4 = Integer.parseInt(token[3].trim());

        addIntegerSetting(DISP_INSTRM_HIGH, v1, v2, v3, v4);

        temp = Props.getProperty(DISP_INSTRM_LOW, "0 10 0 995");
        Props.setProperty(DISP_INSTRM_LOW, temp);
        token = temp.split(" ");
        v1 = Integer.parseInt(token[0].trim());
        v2 = Integer.parseInt(token[1].trim());
        v3 = Integer.parseInt(token[2].trim());
        v4 = Integer.parseInt(token[3].trim());

        addIntegerSetting(DISP_INSTRM_LOW, v1, v2, v3, v4);

        temp = Props.getProperty(DISP_INSTRM_CARNG, "5 1 3 40");
        Props.setProperty(DISP_INSTRM_CARNG, temp);
        token = temp.split(" ");
        v1 = Integer.parseInt(token[0].trim());
        v2 = Integer.parseInt(token[1].trim());
        v3 = Integer.parseInt(token[2].trim());
        v4 = Integer.parseInt(token[3].trim());

        addIntegerSetting(DISP_INSTRM_CARNG, v1, v2, v3, v4);

        temp = Props.getProperty(DISP_INSTRM_CAFLOOR, "120 10 10 990");
        Props.setProperty(DISP_INSTRM_CAFLOOR, temp);
        token = temp.split(" ");
        v1 = Integer.parseInt(token[0].trim());
        v2 = Integer.parseInt(token[1].trim());
        v3 = Integer.parseInt(token[2].trim());
        v4 = Integer.parseInt(token[3].trim());

        addIntegerSetting(DISP_INSTRM_CAFLOOR, v1, v2, v3, v4);

        temp = Props.getProperty(DISP_INSTRM_CAALT, "10 10 10 990");

        Props.setProperty(DISP_INSTRM_CAALT, temp);
        token = temp.split(" ");
        v1 = Integer.parseInt(token[0].trim());
        v2 = Integer.parseInt(token[1].trim());
        v3 = Integer.parseInt(token[2].trim());
        v4 = Integer.parseInt(token[3].trim());

        addIntegerSetting(DISP_INSTRM_CAALT, v1, v2, v3, v4);

        temp = Props.getProperty(DISP_INSTRM_FONT, "Franklin Gothic Book,12");
        Props.setProperty(DISP_INSTRM_FONT, temp);
        token = temp.split(",");
        v1 = Integer.parseInt(token[1].trim());

        addFontSetting(DISP_INSTRM_FONT, new Font(token[0], Font.PLAIN, v1));

        temp = Props.getProperty(DISP_INSTRM_TRACK_FONT, "Franklin Gothic Book,12");
        Props.setProperty(DISP_INSTRM_TRACK_FONT, temp);
        token = temp.split(",");
        v1 = Integer.parseInt(token[1].trim());

        addFontSetting(DISP_INSTRM_TRACK_FONT, new Font(token[0], Font.PLAIN, v1));

        temp = Props.getProperty(COLORS_MAP1, "192 192 192");
        Props.setProperty(COLORS_MAP1, temp);
        token = temp.split(" ");
        red = Integer.parseInt(token[0].trim());
        green = Integer.parseInt(token[1].trim());
        blue = Integer.parseInt(token[2].trim());
        colorMap[0] = new Color(red, green, blue);
        addColorSetting(COLORS_MAP1, colorMap[0]);

        temp = Props.getProperty(COLORS_MAP2, "192 192 192");
        Props.setProperty(COLORS_MAP2, temp);
        token = temp.split(" ");
        red = Integer.parseInt(token[0].trim());
        green = Integer.parseInt(token[1].trim());
        blue = Integer.parseInt(token[2].trim());
        colorMap[1] = new Color(red, green, blue);
        addColorSetting(COLORS_MAP2, colorMap[1]);

        temp = Props.getProperty(COLORS_MAP3, "192 192 192");
        Props.setProperty(COLORS_MAP3, temp);
        token = temp.split(" ");
        red = Integer.parseInt(token[0].trim());
        green = Integer.parseInt(token[1].trim());
        blue = Integer.parseInt(token[2].trim());
        colorMap[2] = new Color(red, green, blue);
        addColorSetting(COLORS_MAP3, colorMap[2]);

        temp = Props.getProperty(COLORS_MAP4, "192 192 192");
        Props.setProperty(COLORS_MAP4, temp);
        token = temp.split(" ");
        red = Integer.parseInt(token[0].trim());
        green = Integer.parseInt(token[1].trim());
        blue = Integer.parseInt(token[2].trim());
        colorMap[3] = new Color(red, green, blue);
        addColorSetting(COLORS_MAP4, colorMap[3]);

        temp = Props.getProperty(COLORS_MAP5, "192 192 192");
        Props.setProperty(COLORS_MAP5, temp);
        token = temp.split(" ");
        red = Integer.parseInt(token[0].trim());
        green = Integer.parseInt(token[1].trim());
        blue = Integer.parseInt(token[2].trim());
        colorMap[4] = new Color(red, green, blue);
        addColorSetting(COLORS_MAP5, colorMap[4]);

        temp = Props.getProperty(COLORS_MAP6, "192 192 192");
        Props.setProperty(COLORS_MAP6, temp);
        token = temp.split(" ");
        red = Integer.parseInt(token[0].trim());
        green = Integer.parseInt(token[1].trim());
        blue = Integer.parseInt(token[2].trim());
        colorMap[5] = new Color(red, green, blue);
        addColorSetting(COLORS_MAP6, colorMap[5]);

        temp = Props.getProperty(COLORS_MAP7, "192 192 192");
        Props.setProperty(COLORS_MAP7, temp);
        token = temp.split(" ");
        red = Integer.parseInt(token[0].trim());
        green = Integer.parseInt(token[1].trim());
        blue = Integer.parseInt(token[2].trim());
        colorMap[6] = new Color(red, green, blue);
        addColorSetting(COLORS_MAP7, colorMap[6]);

        temp = Props.getProperty(COLORS_MAP8, "192 192 192");
        Props.setProperty(COLORS_MAP8, temp);
        token = temp.split(" ");
        red = Integer.parseInt(token[0].trim());
        green = Integer.parseInt(token[1].trim());
        blue = Integer.parseInt(token[2].trim());
        colorMap[7] = new Color(red, green, blue);
        addColorSetting(COLORS_MAP8, colorMap[7]);

        temp = Props.getProperty(COLORS_MAP9, "192 192 192");
        Props.setProperty(COLORS_MAP9, temp);
        token = temp.split(" ");
        red = Integer.parseInt(token[0].trim());
        green = Integer.parseInt(token[1].trim());
        blue = Integer.parseInt(token[2].trim());
        colorMap[8] = new Color(red, green, blue);
        addColorSetting(COLORS_MAP9, colorMap[8]);

        temp = Props.getProperty(COLORS_MAP10, "192 192 192");
        Props.setProperty(COLORS_MAP10, temp);
        token = temp.split(" ");
        red = Integer.parseInt(token[0].trim());
        green = Integer.parseInt(token[1].trim());
        blue = Integer.parseInt(token[2].trim());
        colorMap[9] = new Color(red, green, blue);
        addColorSetting(COLORS_MAP10, colorMap[9]);

        temp = Props.getProperty(COLORS_MAP11, "192 192 192");
        Props.setProperty(COLORS_MAP11, temp);
        token = temp.split(" ");
        red = Integer.parseInt(token[0].trim());
        green = Integer.parseInt(token[1].trim());
        blue = Integer.parseInt(token[2].trim());
        colorMap[10] = new Color(red, green, blue);
        addColorSetting(COLORS_MAP11, colorMap[10]);

        temp = Props.getProperty(COLORS_MAP12, "192 192 192");
        Props.setProperty(COLORS_MAP12, temp);
        token = temp.split(" ");
        red = Integer.parseInt(token[0].trim());
        green = Integer.parseInt(token[1].trim());
        blue = Integer.parseInt(token[2].trim());
        colorMap[11] = new Color(red, green, blue);
        addColorSetting(COLORS_MAP12, colorMap[11]);

        temp = Props.getProperty(COLORS_MAP13, "192 192 192");
        Props.setProperty(COLORS_MAP13, temp);
        token = temp.split(" ");
        red = Integer.parseInt(token[0].trim());
        green = Integer.parseInt(token[1].trim());
        blue = Integer.parseInt(token[2].trim());
        colorMap[12] = new Color(red, green, blue);
        addColorSetting(COLORS_MAP13, colorMap[12]);

        temp = Props.getProperty(COLORS_MAP14, "192 192 192");
        Props.setProperty(COLORS_MAP14, temp);
        token = temp.split(" ");
        red = Integer.parseInt(token[0].trim());
        green = Integer.parseInt(token[1].trim());
        blue = Integer.parseInt(token[2].trim());
        colorMap[13] = new Color(red, green, blue);
        addColorSetting(COLORS_MAP14, colorMap[13]);

        temp = Props.getProperty(COLORS_MAP15, "192 192 192");
        Props.setProperty(COLORS_MAP15, temp);
        token = temp.split(" ");
        red = Integer.parseInt(token[0].trim());
        green = Integer.parseInt(token[1].trim());
        blue = Integer.parseInt(token[2].trim());
        colorMap[14] = new Color(red, green, blue);
        addColorSetting(COLORS_MAP15, colorMap[14]);

        temp = Props.getProperty(COLORS_MAP16, "192 192 192");
        Props.setProperty(COLORS_MAP16, temp);
        token = temp.split(" ");
        red = Integer.parseInt(token[0].trim());
        green = Integer.parseInt(token[1].trim());
        blue = Integer.parseInt(token[2].trim());
        colorMap[15] = new Color(red, green, blue);
        addColorSetting(COLORS_MAP16, colorMap[15]);

        temp = Props.getProperty(COLORS_MAP17, "192 192 192");
        Props.setProperty(COLORS_MAP17, temp);
        token = temp.split(" ");
        red = Integer.parseInt(token[0].trim());
        green = Integer.parseInt(token[1].trim());
        colorMap[16] = new Color(red, green, blue);
        addColorSetting(COLORS_MAP17, colorMap[16]);

        temp = Props.getProperty(COLORS_MAP18, "192 192 192");
        Props.setProperty(COLORS_MAP18, temp);
        token = temp.split(" ");
        red = Integer.parseInt(token[0].trim());
        green = Integer.parseInt(token[1].trim());
        blue = Integer.parseInt(token[2].trim());
        colorMap[17] = new Color(red, green, blue);
        addColorSetting(COLORS_MAP18, colorMap[17]);

        temp = Props.getProperty(COLORS_MAP19, "192 192 192");
        Props.setProperty(COLORS_MAP19, temp);
        token = temp.split(" ");
        red = Integer.parseInt(token[0].trim());
        green = Integer.parseInt(token[1].trim());
        blue = Integer.parseInt(token[2].trim());
        colorMap[18] = new Color(red, green, blue);
        addColorSetting(COLORS_MAP19, colorMap[18]);

        temp = Props.getProperty(COLORS_MAP20, "192 192 192");
        Props.setProperty(COLORS_MAP20, temp);
        token = temp.split(" ");
        red = Integer.parseInt(token[0].trim());
        green = Integer.parseInt(token[1].trim());
        blue = Integer.parseInt(token[2].trim());
        colorMap[19] = new Color(red, green, blue);
        addColorSetting(COLORS_MAP20, colorMap[19]);

        temp = Props.getProperty(COLORS_MAP21, "192 192 192");
        Props.setProperty(COLORS_MAP21, temp);
        token = temp.split(" ");
        red = Integer.parseInt(token[0].trim());
        green = Integer.parseInt(token[1].trim());
        blue = Integer.parseInt(token[2].trim());
        colorMap[20] = new Color(red, green, blue);
        addColorSetting(COLORS_MAP21, colorMap[20]);

        temp = Props.getProperty(COLORS_MAP22, "192 192 192");
        Props.setProperty(COLORS_MAP22, temp);
        token = temp.split(" ");
        red = Integer.parseInt(token[0].trim());
        green = Integer.parseInt(token[1].trim());
        blue = Integer.parseInt(token[2].trim());
        colorMap[21] = new Color(red, green, blue);
        addColorSetting(COLORS_MAP22, colorMap[21]);

        temp = Props.getProperty(COLORS_MAP23, "192 192 192");
        Props.setProperty(COLORS_MAP23, temp);
        token = temp.split(" ");
        red = Integer.parseInt(token[0].trim());
        green = Integer.parseInt(token[1].trim());
        blue = Integer.parseInt(token[2].trim());
        colorMap[22] = new Color(red, green, blue);
        addColorSetting(COLORS_MAP23, colorMap[22]);

        temp = Props.getProperty(COLORS_MAP24, "192 192 192");
        Props.setProperty(COLORS_MAP24, temp);
        token = temp.split(" ");
        red = Integer.parseInt(token[0].trim());
        green = Integer.parseInt(token[1].trim());
        blue = Integer.parseInt(token[2].trim());
        colorMap[23] = new Color(red, green, blue);
        addColorSetting(COLORS_MAP24, colorMap[23]);

        temp = Props.getProperty(COLORS_MAP25, "192 192 192");
        Props.setProperty(COLORS_MAP25, temp);
        token = temp.split(" ");
        red = Integer.parseInt(token[0].trim());
        green = Integer.parseInt(token[1].trim());
        blue = Integer.parseInt(token[2].trim());
        colorMap[24] = new Color(red, green, blue);
        addColorSetting(COLORS_MAP25, colorMap[24]);

        temp = Props.getProperty(COLORS_MAP26, "192 192 192");
        Props.setProperty(COLORS_MAP26, temp);
        token = temp.split(" ");
        red = Integer.parseInt(token[0].trim());
        green = Integer.parseInt(token[1].trim());
        blue = Integer.parseInt(token[2].trim());
        colorMap[25] = new Color(red, green, blue);
        addColorSetting(COLORS_MAP26, colorMap[25]);

        temp = Props.getProperty(COLORS_MAP27, "192 192 192");
        Props.setProperty(COLORS_MAP27, temp);
        token = temp.split(" ");
        red = Integer.parseInt(token[0].trim());
        green = Integer.parseInt(token[1].trim());
        blue = Integer.parseInt(token[2].trim());
        colorMap[26] = new Color(red, green, blue);
        addColorSetting(COLORS_MAP27, colorMap[26]);

        temp = Props.getProperty(COLORS_MAP28, "192 192 192");
        Props.setProperty(COLORS_MAP28, temp);
        token = temp.split(" ");
        red = Integer.parseInt(token[0].trim());
        green = Integer.parseInt(token[1].trim());
        blue = Integer.parseInt(token[2].trim());
        colorMap[27] = new Color(red, green, blue);
        addColorSetting(COLORS_MAP28, colorMap[27]);

        temp = Props.getProperty(COLORS_MAP29, "192 192 192");
        Props.setProperty(COLORS_MAP29, temp);
        token = temp.split(" ");
        red = Integer.parseInt(token[0].trim());
        green = Integer.parseInt(token[1].trim());
        blue = Integer.parseInt(token[2].trim());
        colorMap[28] = new Color(red, green, blue);
        addColorSetting(COLORS_MAP29, colorMap[28]);

        temp = Props.getProperty(COLORS_MAP30, "192 192 192");
        Props.setProperty(COLORS_MAP30, temp);
        token = temp.split(" ");
        red = Integer.parseInt(token[0].trim());
        green = Integer.parseInt(token[1].trim());
        blue = Integer.parseInt(token[2].trim());
        colorMap[29] = new Color(red, green, blue);
        addColorSetting(COLORS_MAP30, colorMap[29]);

        temp = Props.getProperty(COLORS_TRACK, "0 255 0");
        Props.setProperty(COLORS_TRACK, temp);
        token = temp.split(" ");
        red = Integer.parseInt(token[0].trim());
        green = Integer.parseInt(token[1].trim());
        blue = Integer.parseInt(token[2].trim());

        addColorSetting(COLORS_TRACK, new Color(red, green, blue));

        temp = Props.getProperty(COLORS_TRACK_CLIMB, "0 255 0");
        Props.setProperty(COLORS_TRACK_CLIMB, temp);
        token = temp.split(" ");
        red = Integer.parseInt(token[0].trim());
        green = Integer.parseInt(token[1].trim());
        blue = Integer.parseInt(token[2].trim());

        addColorSetting(COLORS_TRACK_CLIMB, new Color(red, green, blue));

        temp = Props.getProperty(COLORS_TRACK_DESCEND, "0 255 0");
        Props.setProperty(COLORS_TRACK_DESCEND, temp);
        token = temp.split(" ");
        red = Integer.parseInt(token[0].trim());
        green = Integer.parseInt(token[1].trim());
        blue = Integer.parseInt(token[2].trim());

        addColorSetting(COLORS_TRACK_DESCEND, new Color(red, green, blue));

        temp = Props.getProperty(COLORS_TRACK_HIST, "0 111 0");
        Props.setProperty(COLORS_TRACK_HIST, temp);
        token = temp.split(" ");
        red = Integer.parseInt(token[0].trim());
        green = Integer.parseInt(token[1].trim());
        blue = Integer.parseInt(token[2].trim());

        addColorSetting(COLORS_TRACK_HIST, new Color(red, green, blue));

        temp = Props.getProperty(COLORS_TRACK_CLIMB_HIST, "0 111 0");
        Props.setProperty(COLORS_TRACK_CLIMB_HIST, temp);
        token = temp.split(" ");
        red = Integer.parseInt(token[0].trim());
        green = Integer.parseInt(token[1].trim());
        blue = Integer.parseInt(token[2].trim());

        addColorSetting(COLORS_TRACK_CLIMB_HIST, new Color(red, green, blue));

        temp = Props.getProperty(COLORS_TRACK_DESCEND_HIST, "0 111 0");
        Props.setProperty(COLORS_TRACK_DESCEND_HIST, temp);
        token = temp.split(" ");
        red = Integer.parseInt(token[0].trim());
        green = Integer.parseInt(token[1].trim());
        blue = Integer.parseInt(token[2].trim());

        addColorSetting(COLORS_TRACK_DESCEND_HIST, new Color(red, green, blue));

        temp = Props.getProperty(COLORS_BACK_GND, "0 0 0");
        Props.setProperty(COLORS_BACK_GND, temp);
        token = temp.split(" ");
        red = Integer.parseInt(token[0].trim());
        green = Integer.parseInt(token[1].trim());
        blue = Integer.parseInt(token[2].trim());

        addColorSetting(COLORS_BACK_GND, new Color(red, green, blue));

        temp = Props.getProperty(DISP_INSTRM_GND_ECHO, "false").trim();
        addBooleanSetting(DISP_INSTRM_GND_ECHO, Boolean.parseBoolean(temp));
        Props.setProperty(DISP_INSTRM_GND_ECHO, temp);

        temp = Props.getProperty(DISP_INSTRM_CA, "true").trim();
        addBooleanSetting(DISP_INSTRM_CA, Boolean.parseBoolean(temp));
        Props.setProperty(DISP_INSTRM_CA, temp);

        temp = Props.getProperty(DISP_INSTRM_TRACK, "true").trim();
        addBooleanSetting(DISP_INSTRM_TRACK, Boolean.parseBoolean(temp));
        Props.setProperty(DISP_INSTRM_TRACK, temp);

        temp = Props.getProperty(MAP_OBJECTS, "false").trim();
        addBooleanSetting(MAP_OBJECTS, Boolean.parseBoolean(temp));
        Props.setProperty(MAP_OBJECTS, temp);

        temp = Props.getProperty(MAP_VECTORS, "false").trim();
        addBooleanSetting(MAP_VECTORS, Boolean.parseBoolean(temp));
        Props.setProperty(MAP_VECTORS, temp);

        temp = Props.getProperty(MAP_NAMES, "false").trim();
        addBooleanSetting(MAP_NAMES, Boolean.parseBoolean(temp));
        Props.setProperty(MAP_NAMES, temp);

        /*
         * User doesn't have a valid config file
         * Create one
         */
        if (noProps) {
            saveProperties(filename);
        }
    }

    public void saveProperties(String filename) {
        FileWriter fout = null;
        BufferedWriter bout = null;
        String line;

        try {
            in.close();
        } catch (IOException e) {
        }

        try {
            fout = new FileWriter(filename);
            bout = new BufferedWriter(fout);

            bout.write("#\r\n# TrackViewer Configuration File\r\n#\r\n# ");
            sqlTime.setTime(zulu.getUTCTime());
            bout.write(sqlTime.toString() + "\r\n#\r\n");

            bout.write("db.host = " + databaseHost + "\r\n");
            bout.write("db.port = " + databasePort + "\r\n");
            bout.write("db.name = " + databaseName + "\r\n");
            bout.write("db.login = " + databaseLogin + "\r\n");
            bout.write("db.password = " + databasePassword + "\r\n");
            bout.write("station.name = " + homeName + "\r\n");
            bout.write("station.alt = " + Integer.toString(homeAlt) + "\r\n");
            bout.write("station.latitude = " + Double.toString(homeLat) + "\r\n");
            bout.write("station.longitude = " + Double.toString(homeLon) + "\r\n");
            bout.write("screen.height = " + Integer.toString(screenHeight) + "\r\n");
            bout.write("screen.width = " + Integer.toString(screenWidth) + "\r\n");
            bout.write("screen.scale = " + Double.toString(mapScale) + "\r\n");

            for (Enumeration e = booleanSettings.keys(); e.hasMoreElements();) {
                String key = (String) e.nextElement();
                boolean val = booleanSettings.get(key);
                line = key + " = " + Boolean.toString(val);
                bout.write(line + "\r\n");
            }

            for (Enumeration e = integerSettings.keys(); e.hasMoreElements();) {
                String key = (String) e.nextElement();
                IntegerSetting val = integerSettings.get(key);

                if (key.equals(DISP_INSTRM_DIM) || key.equals(DISP_INSTRM_DROP)
                        || key.equals(DISP_INSTRM_BLOCK) || key.equals(DISP_INSTRM_VECTOR)
                        || key.equals(DISP_INSTRM_LEADER) || key.equals(DISP_INSTRM_ECHOES)
                        || key.equals(DISP_INSTRM_ESIZE)) {
                    line = key + " = " + Integer.toString(val.value)
                            + " " + Integer.toString(val.min)
                            + " " + Integer.toString(val.max);
                } else {
                    line = key + " = " + Integer.toString(val.value)
                            + " " + Integer.toString(val.step)
                            + " " + Integer.toString(val.min)
                            + " " + Integer.toString(val.max);
                }

                bout.write(line + "\r\n");
            }

            for (Enumeration e = colorSettings.keys(); e.hasMoreElements();) {
                String key = (String) e.nextElement();
                Color color = colorSettings.get(key);
                line = key + " = " + Integer.toString(color.getRed())
                        + " " + Integer.toString(color.getGreen())
                        + " " + Integer.toString(color.getBlue());

                bout.write(line + "\r\n");
            }

            for (Enumeration e = fontSettings.keys(); e.hasMoreElements();) {
                String key = (String) e.nextElement();
                Font font = fontSettings.get(key);
                line = key + " = " + font.getFontName() + "," + Integer.toString(font.getSize());

                bout.write(line + "\r\n");
            }
        } catch (IOException e) {
        }

        try {
            bout.flush();
            fout.close();
        } catch (IOException e) {
        }
    }

    public Properties getProperties() {
        return Props;
    }

    public Color[] getColorMap() {
        return colorMap;
    }

    /**
     * Getter to return the database login name
     *
     * @return a string Representing the database login name
     */
    public String getDatabaseLogin() {
        return databaseLogin;
    }

    /**
     * Getter to return the database login password
     *
     * @return a string Representing the database login password
     */
    public String getDatabasePassword() {
        return databasePassword;
    }

    /**
     * Getter to return the database connection URL
     *
     * @return a string Representing the database URL
     */
    public String getDatabaseURL() {
        return "jdbc:mysql://" + databaseHost + ":" + databasePort + "/" + databaseName;
    }

    /**
     * Method to return the home directory
     *
     * @return a string Representing the home directory
     */
    public String getHomeDir() {
        return homeDir;
    }

    public String getOSConfPath() {
        return OSConfPath;
    }

    public String getMapPath() {
        return mapPath;
    }

    public int getHomeAlt() {
        return homeAlt;
    }

    public double getHomeLat() {
        return homeLat;
    }

    public void setHomeLat(double lat) {
        homeLat = lat;
        Props.setProperty("station.latitude", Double.toString(lat));
    }

    public double getHomeLon() {
        return homeLon;
    }

    public void setHomeLon(double lon) {
        homeLon = lon;
        Props.setProperty("station.longitude", Double.toString(lon));
    }

    public String getHomeName() {
        return homeName;
    }

    public double getMapScale() {
        return mapScale;
    }

    public void setMapScale(double scale) {
        mapScale = scale;
        Props.setProperty("screen.scale", Double.toString(scale));
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void addBooleanSetting(String name, boolean value) {
        if (name != null && name.length() > 0) {
            try {
                booleanSettings.put(name, value);
            } catch (NullPointerException e) {
                System.err.println("Config::addBooleanSetting Exception during put " + e.toString());
            }
        }
    }

    public void addIntegerSetting(String name, int value, int step, int min, int max) {
        if (name != null && name.length() > 0 && min <= max && value >= min && value <= max) {
            try {
                integerSettings.put(name, new IntegerSetting(value, step, min, max));
            } catch (NullPointerException e) {
                System.err.println("Config::addIntegerSetting Exception during put " + e.toString());
            }
        }
    }

    public void addIntegerSetting(String name, int value, int min, int max) {
        addIntegerSetting(name, value, 1, min, max);
    }

    public void addIntegerSetting(String name, int value) {
        addIntegerSetting(name, value, 1, 0, 32000);
    }

    public void addColorSetting(String name, Color color) {
        if (name != null && name.length() > 0 && color != null) {
            try {
                colorSettings.put(name, color);
            } catch (NullPointerException e) {
                System.err.println("Config::addColorSetting Exception during put " + e.toString());
            }
        }
    }

    public void addFontSetting(String name, Font font) {
        if (name != null && name.length() > 0 && font != null) {
            try {
                fontSettings.put(name, font);
            } catch (NullPointerException e) {
                System.err.println("Config::addFontSetting Exception during put " + e.toString());
            }
        }
    }

    public boolean getBooleanSetting(String name) {
        Boolean obj;

        if (name != null && name.length() > 0) {
            try {
                obj = booleanSettings.get(name);
                return obj;
            } catch (NullPointerException e) {
                System.err.println("Config::getBooleanSetting Exception during get " + e.toString());
            }
        }

        return false;
    }

    public int getIntegerSetting(String name) {
        IntegerSetting obj;

        if (name != null && name.length() > 0) {
            try {
                obj = integerSettings.get(name);
                return obj.value;
            } catch (NullPointerException e) {
                System.err.println("Config::getIntegerSetting Exception during get " + e.toString());
            }
        }

        return 0;
    }

    public Color getColorSetting(String name) {
        Color obj;

        if (name != null && name.length() > 0) {
            try {
                obj = colorSettings.get(name);
                return obj;
            } catch (NullPointerException e) {
                System.err.println("Config::getColorSetting Exception during get " + e.toString());
            }
        }

        return Color.BLACK;
    }

    public Font getFontSetting(String name) {
        Font obj;

        if (name != null && name.length() > 0) {
            try {
                obj = fontSettings.get(name);
                return obj;
            } catch (NullPointerException e) {
                System.err.println("Config::getFontSetting Exception during get " + e.toString());
            }
        }

        return new Font("Franklin Gothic Book", Font.PLAIN, 12);
    }

    public void toggleBooleanSetting(String name) {
        Boolean obj;

        if (name != null && name.length() > 0) {
            try {
                obj = booleanSettings.get(name);
                booleanSettings.put(name, !obj);
            } catch (NullPointerException e) {
                System.err.println("Config::toggleBooleanSetting Exception during get/put " + e.toString());
            }
        }
    }

    public void setBooleanSetting(String name, boolean val) {
        if (name != null && name.length() > 0) {
            try {
                booleanSettings.put(name, val);
            } catch (NullPointerException e) {
                System.err.println("Config::setBooleanSetting Exception during put " + e.toString());
            }
        }
    }

    public int incIntegerSetting(String name) {
        IntegerSetting obj;

        if (name != null && name.length() > 0) {
            try {
                obj = integerSettings.get(name);

                if ((obj.value + obj.step) <= obj.max) {
                    obj.value += obj.step;
                }

                integerSettings.put(name, obj);
                return obj.value;
            } catch (NullPointerException e) {
                System.err.println("Config::incIntegerSetting Exception during get/put " + e.toString());
                return 0;
            }
        }

        return 0;
    }

    public int decIntegerSetting(String name) {
        IntegerSetting obj;

        if (name != null && name.length() > 0) {
            try {
                obj = integerSettings.get(name);

                if ((obj.value - obj.step) >= obj.min) {
                    obj.value -= obj.step;
                }

                integerSettings.put(name, obj);
                return obj.value;
            } catch (NullPointerException e) {
                System.err.println("Config::decIntegerSetting Exception during get/put " + e.toString());
                return 0;
            }
        }

        return 0;
    }

    public void changeColorSetting(String name, Color color) {
        if (name != null && name.length() > 0 && color != null) {
            try {
                colorSettings.put(name, color);
            } catch (NullPointerException e) {
                System.err.println("Config::changeColorSetting Exception during put " + e.toString());
            }
        }
    }

    /**
     * mutable class
     */
    class IntegerSetting {

        int value;
        int step;
        int min;
        int max;

        public IntegerSetting(int val1, int val2, int val3, int val4) {
            value = val1;
            step = val2;
            min = val3;
            max = val4;
        }
    }
}
