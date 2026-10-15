package sonar.ui;
import java.awt.*;
import java.awt.image.BufferedImage;
public final class AaProbe {
    public static void main(String[] a) {
        System.setProperty("java.awt.headless", "true");
        SonarTheme.install();
        for (double zoom : new double[]{1.0, 1.4}) {
            BufferedImage[] out = new BufferedImage[2];
            Object[] hints = {RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB,
                              RenderingHints.VALUE_TEXT_ANTIALIAS_ON};
            for (int i = 0; i < 2; i++) {
                out[i] = new BufferedImage(200, 60, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = (Graphics2D) out[i].getGraphics();
                g.setColor(Color.BLACK); g.fillRect(0, 0, 200, 60);
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, hints[i]);
                g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
                g.scale(zoom, zoom);
                g.setFont(SonarTheme.bold(30f));
                g.setColor(Color.WHITE);
                g.drawString("173", 4, 34);
                g.dispose();
            }
            int diff = 0, coloured = 0;
            for (int y = 0; y < 60; y++) for (int x = 0; x < 200; x++) {
                int p = out[0].getRGB(x, y), q = out[1].getRGB(x, y);
                if (p != q) diff++;
                int r = (p >> 16) & 255, gg = (p >> 8) & 255, b = p & 255;
                if (Math.max(Math.max(r, gg), b) - Math.min(Math.min(r, gg), b) > 12) coloured++;
            }
            System.out.printf("zoom %.1fx : %4d pixels differ between LCD and grayscale; %4d colour-fringed pixels under LCD%n",
                    zoom, diff, coloured);
        }
    }
}
