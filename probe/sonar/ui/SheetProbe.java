package sonar.ui;

import java.awt.*;
import java.awt.image.BufferedImage;

/** A contact sheet of every watched Shatters object's sprite, labelled. */
public class SheetProbe {
    public static void main(String[] a) throws Exception {
        int[] ids = ShattersRadar.watchedForTest();
        int sc = 4, cell = 40 * sc, cols = 6;
        int rows = (ids.length + cols - 1) / cols;
        BufferedImage out = new BufferedImage(cols * cell, rows * (cell + 16), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = out.createGraphics();
        g.setColor(new Color(0x15181f));
        g.fillRect(0, 0, out.getWidth(), out.getHeight());
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        for (int i = 0; i < ids.length; i++) {
            int x = (i % cols) * cell, y = (i / cols) * (cell + 16);
            BufferedImage s = Glyph.entitySprite(ids[i]);
            String note;
            if (s == null) {
                note = "no sprite";
            } else {
                note = s.getWidth() + "x" + s.getHeight();
                int w = s.getWidth() * sc, h = s.getHeight() * sc;
                g.drawImage(s, x + (cell - w) / 2, y + (cell - h) / 2, w, h, null);
            }
            g.setColor(new Color(0x2a2f3a));
            g.drawRect(x, y, cell - 1, cell - 1);
            g.setColor(Color.WHITE);
            g.drawString(ShattersRadar.label(ids[i]), x + 4, y + cell + 12);
            g.setColor(Color.GRAY);
            g.drawString(note, x + cell - 56, y + cell + 12);
        }
        g.dispose();
        javax.imageio.ImageIO.write(out, "png", new java.io.File(a[0]));
        System.out.println("sheet " + out.getWidth() + "x" + out.getHeight());
        System.exit(0);
    }
}
