package sonar.ui;

import java.awt.*;

/** Samples the ambient glow and the panel top in every theme, to see if any purple survives. */
public class ThemeShot {
    public static void main(String[] a) {
        String[] names = SonarTheme.themeNames();
        System.out.printf("%-10s %-18s %-18s %s%n", "theme", "ground", "glow", "hue of glow");
        for (int i = 0; i < names.length; i++) {
            SonarTheme.setTheme(i);
            Color bg = SonarTheme.BG, glow = SonarTheme.lift(0.09), top = SonarTheme.lift(0.02);
            float[] hsb = Color.RGBtoHSB(glow.getRed(), glow.getGreen(), glow.getBlue(), null);
            float[] bh = Color.RGBtoHSB(bg.getRed(), bg.getGreen(), bg.getBlue(), null);
            System.out.printf("%-10s #%06x            #%06x            %5.1f  (ground %5.1f)  panelTop #%06x%n",
                    names[i], bg.getRGB() & 0xffffff, glow.getRGB() & 0xffffff,
                    hsb[0] * 360, bh[0] * 360, top.getRGB() & 0xffffff);
        }
        System.exit(0);
    }
}
