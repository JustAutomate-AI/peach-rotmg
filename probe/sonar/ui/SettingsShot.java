package sonar.ui;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Renders the Settings page offscreen, so the keyword editor can be looked at
 * without opening a window or driving the app to that tab.
 *
 *   javac -cp run/Sonar.jar -d probe probe/sonar/ui/SettingsShot.java
 *   cd run
 *   java -cp "Sonar.jar;../probe" sonar.ui.SettingsShot <out.png>
 */
public final class SettingsShot {

    /**
     * Puts a few words in, so the populated state can be seen. ChatPings reads them
     * from the live GameData, which a headless render has no reason to build, so the
     * field is set directly here rather than booting the backend for a picture.
     */
    private static void seedKeywords() throws Exception {
        sonar.backend.data.GameData data = new sonar.backend.data.GameData();
        java.util.ArrayList<String> words = new java.util.ArrayList<>(
                java.util.Arrays.asList("wand of recompense", "\"loot\"", "oryx"));
        data.setPropList("chatPingMessages", words);
        java.lang.reflect.Field f = sonar.ChatPings.class.getDeclaredField("data");
        f.setAccessible(true);
        f.set(null, data);
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        SonarTheme.install();
        seedKeywords();

        int width = 560, height = 1100;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = SonarTheme.quality(img.getGraphics());
        g.setColor(SonarTheme.BG);
        g.fillRect(0, 0, width, height);
        g.translate(16, 16);

        SettingsPane pane = new SettingsPane();
        pane.paintContent(g, width - 32);
        g.dispose();

        File out = new File(args.length > 0 ? args[0] : "settings.png");
        ImageIO.write(img, "png", out);
        System.out.println("wrote " + out.getAbsolutePath());
    }
}
