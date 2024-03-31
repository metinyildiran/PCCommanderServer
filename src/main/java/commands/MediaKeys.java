/*
 * Author: Aidan Evans
 * Date: 3/23/2019
 */

package commands;

public class MediaKeys {
    //loads library from "MediaKeys.dll"
    static {
        System.loadLibrary("MediaKeys");
    }

    public static native void volumeMute();

    public static native void volumeDown();

    public static native void volumeUp();

    public static native void songPrevious();

    public static native void songNext();

    public static native void songPlayPause();

    public static native void mediaStop();

    public static void executeMediaKey(String command) {
        switch (command) {
            case "play/stop" -> MediaKeys.songPlayPause();
            case "previous" -> MediaKeys.songPrevious();
            case "next" -> MediaKeys.songNext();
            case "volume_up" -> MediaKeys.volumeUp();
            case "volume_down" -> MediaKeys.volumeDown();
        }
    }
}