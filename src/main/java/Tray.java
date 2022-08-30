import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Tray {
    public Tray() {
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }

        PopupMenu popup = new PopupMenu();
        TrayIcon trayIcon;
        try {
            BufferedImage trayIconImage = ImageIO.read(Objects.requireNonNull(Tray.class.getResource("icon.png")));
            int trayIconWidth = new TrayIcon(trayIconImage).getSize().width;
            trayIcon = new TrayIcon(trayIconImage.getScaledInstance(trayIconWidth, -1, Image.SCALE_SMOOTH), "PC Commander Server", popup);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SystemTray tray = SystemTray.getSystemTray();

        MenuItem exitItem = new MenuItem("   Exit   ");
        exitItem.addActionListener(e -> System.exit(0));
        popup.add(exitItem);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
        }
    }
}
