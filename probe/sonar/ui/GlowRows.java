package sonar.ui;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * The gear tiles as the damage list actually draws them: small, on top of a
 * class-coloured bar. This is the case that matters, because a glow of the same family
 * as the bar underneath it is the one that disappears.
 */
public final class GlowRows {

    private static final String[] CLASSES = {
            "Samurai", "Knight", "Archer", "Wizard", "Necromancer",
            "Huntress", "Warrior", "Rogue", "Mystic", "Paladin"};

    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        SonarTheme.install();
        sonar.assets.IdToAsset.reloadAssets();

        final int ROW = 34, GEAR = 18, GEAR_GAP = 3, W = 620;
        int h = 14 + CLASSES.length * (ROW + 5);
        BufferedImage img = new BufferedImage(W, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = SonarTheme.quality(img.createGraphics());
        g.setColor(SonarTheme.BG);
        g.fillRect(0, 0, W, h);

        int[] kit = LiveDemo.kitFor("Knight");
        // one tier per slot, so every tier is seen against every class colour
        int[] tiers = {1, 2, 3, 4};

        for (int c = 0; c < CLASSES.length; c++) {
            double y = 8 + c * (ROW + 5);
            String cls = CLASSES[c];
            // the bar, exactly as DamagePane paints it
            Shape track = SonarTheme.round(0, y, W, ROW, 11);
            g.setColor(new Color(255, 255, 255, 10));
            g.fill(track);
            double barWidth = W * (0.95 - c * 0.07);
            Color dark = SonarTheme.classColor(cls, false), light = SonarTheme.classColor(cls, true);
            g.setPaint(new GradientPaint(0, 0, dark, (float) Math.max(60, barWidth), 0, light));
            Shape old = g.getClip();
            g.clip(track);
            g.fill(SonarTheme.round(0, y, barWidth, ROW, 11));
            g.setClip(old);

            int x = 150;
            double top = y + (ROW - GEAR) / 2.0;
            for (int i = 0; i < 4; i++) {
                g.setColor(new Color(0, 0, 0, 70));
                g.fill(SonarTheme.round(x, top, GEAR, GEAR, 6));
                Glyph.enchantGlow(g, tiers[i], x, top, GEAR, 6);
                if (kit[i] > 0) Glyph.item(g, kit[i], x + 2, top + 2, GEAR - 4);
                x += GEAR + GEAR_GAP;
            }
            g.setFont(SonarTheme.bold(12f));
            g.setColor(Color.WHITE);
            g.drawString(cls, 14, (float) (y + ROW / 2.0 + 4));
            g.setFont(SonarTheme.font(10.5f));
            g.setColor(new Color(255, 255, 255, 190));
            g.drawString("Uncommon  Rare  Legendary  Divine", x + 10, (float) (y + ROW / 2.0 + 4));
        }
        g.dispose();
        ImageIO.write(img, "png", new File(args[0]));
        System.out.println("wrote " + args[0]);
    }
}
