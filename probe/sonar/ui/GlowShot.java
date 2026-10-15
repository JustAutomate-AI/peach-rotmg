package sonar.ui;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * The gear tiles at every enchant tier, drawn the way GearPane draws them, old look
 * beside new. The point of the comparison is the edge: whether the glow reads as the
 * tile's own outline or as a separate halo behind a grey one.
 */
public final class GlowShot {

    private static final String[] NAMES = {"no enchants", "Uncommon", "Rare", "Legendary", "Divine"};

    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        SonarTheme.install();
        sonar.assets.IdToAsset.reloadAssets();
        int[] kit = LiveDemo.kitFor("Knight");
        System.out.println("kit ids: " + java.util.Arrays.toString(kit));

        int tile = 56, gap = 14, pad = 22, labelH = 20;
        int block = 4 * tile + 3 * gap;
        int w = pad * 3 + block * 2 + 70;
        int h = pad * 2 + 26 + 5 * (tile + labelH + 10);
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = SonarTheme.quality(img.createGraphics());
        g.setColor(SonarTheme.SURFACE);
        g.fillRect(0, 0, w, h);

        g.setFont(SonarTheme.bold(13f));
        g.setColor(SonarTheme.MUTED);
        g.drawString("before", pad, pad + 12);
        g.drawString("after", pad * 2 + block + 70, pad + 12);

        for (int tier = 0; tier <= 4; tier++) {
            int y = pad + 26 + tier * (tile + labelH + 10);
            for (int half = 0; half < 2; half++) {
                int ox = half == 0 ? pad : pad * 2 + block + 70;
                for (int i = 0; i < 4; i++) {
                    int x = ox + i * (tile + gap);
                    g.setColor(new Color(255, 255, 255, 12));
                    g.fill(SonarTheme.round(x, y, tile, tile, 12));
                    if (tier == 0) {
                        plainBorder(g, x, y, tile);
                    } else if (half == 0) {
                        oldGlow(g, tier, x, y, tile);
                    } else {
                        Glyph.enchantGlow(g, tier, x, y, tile, 12);
                    }
                    int ip = Math.max(5, tile / 7);
                    if (kit[i] > 0) Glyph.item(g, kit[i], x + ip, y + ip, tile - 2 * ip);
                }
            }
            g.setFont(SonarTheme.font(11.5f));
            g.setColor(SonarTheme.MUTED);
            String label = NAMES[tier];
            g.drawString(label, pad + block + 14, y + tile / 2f + 4);
        }
        g.dispose();
        ImageIO.write(img, "png", new File(args[0]));
        System.out.println("wrote " + args[0]);
    }

    private static void plainBorder(Graphics2D g, int x, int y, int tile) {
        g.setColor(SonarTheme.LINE_STRONG);
        g.setStroke(new BasicStroke(1f));
        g.draw(SonarTheme.round(x + 0.8, y + 0.8, tile - 1.6, tile - 1.6, 12));
    }

    /** What it looked like before: rings floating outside, and a grey border of its own. */
    private static void oldGlow(Graphics2D g, int tier, int x, int y, int tile) {
        Color glow = CalcPane.tierColor(tier);
        for (int ring = 3; ring >= 1; ring--) {
            g.setColor(new Color(glow.getRed(), glow.getGreen(), glow.getBlue(), 24 + (3 - ring) * 26));
            g.setStroke(new BasicStroke(1.6f));
            g.draw(SonarTheme.round(x - ring, y - ring, tile + ring * 2, tile + ring * 2, 6 + ring));
        }
        plainBorder(g, x, y, tile);
    }
}
