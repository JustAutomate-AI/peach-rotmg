package sonar.ui;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;

/** Styles drawn the new way - clipped to the character - beside the old free quad. */
public final class MaskCheck {
    public static void main(String[] a) throws Exception {
        System.setProperty("java.awt.headless", "true");
        System.setProperty("sonar.arcane", "1");
        SonarTheme.install();
        sonar.assets.IdToAsset.reloadAssets();
        String[] want = {"High Blue Fire Flipbook Style", "High Lime Inferno Flipbook Style",
                "High Matrix Flipbook Style", "High Little Ghosties Flipbook Style",
                "High Snowfall Flipbook Style", "Blue Hologram Style"};
        Map<String, ArcaneStyle> by = new LinkedHashMap<>();
        for (ArcaneStyle s : ArcaneStyle.all().values()) by.put(s.id, s);
        List<ArcaneStyle> show = new ArrayList<>();
        for (String w : want) if (by.containsKey(w)) show.add(by.get(w));
        final int SKIN = 0x0342, BOX = 120, CELL = 150;
        int w = show.size() * CELL, h = CELL + 26;
        for (int f = 0; f < 30; f++) {
            long t = (long) (f * (1000.0 / 30.0));
            BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = SonarTheme.quality(img.createGraphics());
            g.setColor(new Color(45, 45, 45));
            g.fillRect(0, 0, w, h);
            for (int i = 0; i < show.size(); i++) {
                int x = i * CELL;
                Glyph.playerStyled(g, SKIN, 0, 0, show.get(i).objectType, t,
                        x + (CELL - BOX) / 2.0, 8, BOX, 0);
                g.setFont(SonarTheme.font(10f));
                g.setColor(SonarTheme.MUTED);
                ArcaneStyle.Effect e = show.get(i).drawable();
                g.drawString(e == null ? show.get(i).effects.get(0).family.toString() : e.art, x + 8, CELL + 16);
            }
            g.dispose();
            ImageIO.write(img, "png", new File(a[0] + String.format("-%02d.png", f)));
        }
        System.out.println("wrote 30 frames for " + show.size() + " styles");
    }
}
