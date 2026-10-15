package sonar.ui;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * The headline figure every FactsPane opens with, drawn in every theme, at the zoom the
 * panes actually use. The old way is drawn beside the new one so the edges can be
 * compared rather than described.
 */
public final class NumberCrisp {

    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        SonarTheme.install();
        String[] themes = SonarTheme.themeNames();
        double[] zooms = {1.0, 1.4};
        int cellW = 240, cellH = 64;
        int w = cellW * 2 * zooms.length + 150, h = cellH * themes.length + 40;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D top = SonarTheme.quality(img.createGraphics());

        for (int t = 0; t < themes.length; t++) {
            SonarTheme.setTheme(t);
            int y = 30 + t * cellH;
            top.setColor(SonarTheme.SURFACE);
            top.fillRect(0, y - 24, w, cellH);
            top.setFont(SonarTheme.font(11f));
            top.setColor(SonarTheme.MUTED);
            top.drawString(themes[t] + (SonarTheme.vivid() ? "  (vivid)" : ""), cellW * 2 * zooms.length + 12, y + 8);

            for (int z = 0; z < zooms.length; z++) {
                for (int mode = 0; mode < 2; mode++) {
                    int x = (z * 2 + mode) * cellW + 14;
                    Graphics2D g = SonarTheme.quality((Graphics2D) img.getGraphics());
                    if (mode == 0) {
                        // what it was: subpixel text, and a bloom starting against the glyph
                        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                    }
                    g.translate(x, y);
                    g.scale(zooms[z], zooms[z]);
                    g.setFont(SonarTheme.bold(30f));
                    if (mode == 0) old(g, "173", 0, 30, SonarTheme.accent());
                    else SonarTheme.glow(g, "173", 0, 30, SonarTheme.accent());
                    g.dispose();
                }
            }
        }
        top.setFont(SonarTheme.bold(11f));
        top.setColor(Color.WHITE);
        for (int z = 0; z < zooms.length; z++) {
            top.drawString("before  @" + zooms[z] + "x", (z * 2) * cellW + 14, 18);
            top.drawString("after  @" + zooms[z] + "x", (z * 2 + 1) * cellW + 14, 18);
        }
        top.dispose();
        ImageIO.write(img, "png", new File(args[0]));
        System.out.println("wrote " + args[0] + " for " + themes.length + " themes");
    }

    /** The bloom as it was: four copies a pixel out, sitting on the glyph's own edge. */
    private static void old(Graphics2D g, String text, float x, float y, Color c) {
        if (SonarTheme.vivid()) {
            g.setColor(SonarTheme.alpha(c, 0.16));
            for (int r = 3; r >= 1; r--) {
                g.drawString(text, x - r, y);
                g.drawString(text, x + r, y);
                g.drawString(text, x, y - r);
                g.drawString(text, x, y + r);
            }
        }
        g.setColor(c);
        g.drawString(text, x, y);
    }
}
