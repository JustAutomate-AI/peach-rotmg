package sonar.ui;

import java.awt.image.BufferedImage;

public class CoverProbe {
    public static void main(String[] a) {
        for (int id : ShattersRadar.watchedForTest()) {
            BufferedImage s = Glyph.entitySprite(id);
            if (s == null) { System.out.printf("%-20s none%n", ShattersRadar.label(id)); continue; }
            int on = 0, lit = 0, n = s.getWidth() * s.getHeight();
            for (int y = 0; y < s.getHeight(); y++) for (int x = 0; x < s.getWidth(); x++) {
                int p = s.getRGB(x, y);
                if ((p >>> 24) < 40) continue;
                on++;
                int r = (p >> 16) & 255, g = (p >> 8) & 255, b = p & 255;
                if (Math.max(r, Math.max(g, b)) > 60) lit++;
            }
            System.out.printf("%-20s %2dx%-2d  opaque %3d%%  visible %3d%%%n",
                    ShattersRadar.label(id), s.getWidth(), s.getHeight(), on * 100 / n, lit * 100 / n);
        }
        System.exit(0);
    }
}
