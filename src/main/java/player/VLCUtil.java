package player;

import uk.co.caprica.vlcj.discovery.NativeDiscovery;

import java.io.File;
import java.io.IOException;

public class VLCUtil {

    private static String OS = System.getProperty("os.name").toLowerCase();

    /**
     * VLC lib discovery method for current OS.
     */
    public static void discover() {
        String osName = "";

        if (isMac()) osName = "Running on OS X";
        if (isUnix()) osName = "Running on Unix";
        if (isWindows()) osName = "Running on Windows";

        System.out.println(osName);

        new NativeDiscovery().discover();
    }

    /**
     * Checks if VLC is installed
     *
     * @return false if not.
     */
    public static boolean isVlcInstalled() {
        String vlc = getVLCPath();
        if (vlc != null) {
            File libDir = new File(vlc);
            if (libDir.exists())
                return true;
        }
        return false;
    }

    public static String getVLCPath() {
        if (isWindows()) {
            String path = getVLCPathWindows();
            if (!path.isEmpty()) {
                File vlcFolder = new File(path);
                return (new File(vlcFolder, "vlc.exe")).toString();
            }
        } else if (isMac()) {
            return "/Applications/VLC.app/Contents/MacOS/VLC";
        } else if (isUnix()) {
            return "/usr/bin/vlc";
        }
        return null;
    }

    /**
     * Returns x32 or x64 VLC install path.
     * @return
     */
    public static String getVLCPathWindows() {
        //TODO: VLC player may be installed into different folder.
        String arch = System.getProperty("os.arch");

        if (arch.indexOf("64") >= 0) {
            return "C://Program Files//VideoLAN//VLC";
        }

        return "C://Program Files (x86)//VideoLAN//VLC";
    }

    /**
     * Run external VLC player and play media
     *
     * @param media media to play
     */
    public static void runVLC(String media) {
        ProcessBuilder pb = new ProcessBuilder(VLCUtil.getVLCPath(), media);
        try {
            Process start = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isWindows() {
        return (OS.contains("win"));
    }

    public static boolean isMac() {
        return (OS.contains("mac"));
    }

    public static boolean isUnix() {
        return (OS.contains("nux"));
    }
}