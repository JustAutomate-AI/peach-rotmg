import sonar.ui.ArcaneStyle;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import java.util.List;

/**
 * Renders chosen styles the way Sonar draws them, large, for comparison against the
 * game's own wardrobe preview. Two views each: the character wearing it, and the
 * effect on its own so the art can be checked without the character in the way.
 */
public class ArcaneCompare {

    static final String[] WANTED = {
        "High Blue Fire Flipbook Style",
        "High Lime Inferno Flipbook Style",
        "High Blue Explosion Flipbook Style",
        "High Matrix Flipbook Style",
        "High Glitch Flipbook Style",
        "High Little Ghosties Flipbook Style",
        "High Binary Flipbook Style",
        "High Twin Sword Flipbook Style",
        "High Snowfall Flipbook Style",
        "High Shamrock Blessing Flipbook Style",
        "High Blue Lightning Flipbook Style",
        "High Bubble Scroller Style",
    };

    public static void main(String[] a) throws Exception {
        System.setProperty("java.awt.headless", "true");
        System.setProperty("sonar.arcane", "1");           // the gate is off by default
        Class<?> glyph = Class.forName("sonar.ui.Glyph");
        Method styled = glyph.getDeclaredMethod("playerStyled", Graphics2D.class, int.class,
            int.class, int.class, int.class, long.class, double.class, double.class, int.class, double.class);
        styled.setAccessible(true);
        Method plain = glyph.getDeclaredMethod("player", Graphics2D.class, int.class, int.class,
            int.class, double.class, double.class, int.class);
        plain.setAccessible(true);
        Class<?> art = Class.forName("sonar.ui.ArcaneArt");
        Method overlay = art.getDeclaredMethod("overlay", Graphics2D.class, ArcaneStyle.class,
            long.class, Rectangle.class, double.class);
        overlay.setAccessible(true);
        Class.forName("sonar.ui.SonarTheme").getDeclaredMethod("install").invoke(null);
        sonar.assets.IdToAsset.reloadAssets();

        Map<String, ArcaneStyle> byName = new LinkedHashMap<>();
        for (ArcaneStyle s : ArcaneStyle.all().values()) byName.put(s.id, s);

        List<ArcaneStyle> show = new ArrayList<>();
        for (String w : WANTED) {
            ArcaneStyle s = byName.get(w);
            if (s == null) System.out.println("NOT FOUND: " + w);
            else show.add(s);
        }
        File out = new File(a[0]);
        out.mkdirs();

        final int SKIN = 0x0342;       // a Samurai, as a neutral stand-in
        final int BOX = 160;           // the character drawn at 160px; the effect reaches 320
        final int CELL = 340, PAD = 10;

        // per-style frames: character+effect on the left, effect alone on the right
        for (ArcaneStyle s : show) {
            ArcaneStyle.Effect e = s.drawable();
            int frames = Math.max(1, Math.round(e.framerate));
            for (int f = 0; f < 30; f++) {
                long t = (long) (f * (1000.0 / 30.0));
                BufferedImage img = new BufferedImage(CELL * 2, CELL, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = img.createGraphics();
                g.setColor(new Color(0x14, 0x0c, 0x2b, 255));
                g.fillRect(0, 0, CELL * 2, CELL);
                g.setColor(new Color(0x1b1138));
                g.fillRect(CELL, 0, CELL, CELL);
                double bx = (CELL - BOX) / 2.0, by = (CELL - BOX) / 2.0;
                // left: as Sonar draws a character wearing it, full spread allowed
                styled.invoke(null, g, SKIN, 0, 0, s.objectType, t, bx, by, BOX, 0.5);
                // right: the same effect with no character under it
                Graphics2D g2 = (Graphics2D) g.create();
                g2.translate(CELL, 0);
                overlay.invoke(null, g2, s, t, new Rectangle((int) bx, (int) by, BOX, BOX), 0.5);
                g2.dispose();
                g.dispose();
                ImageIO.write(img, "png", new File(out,
                    s.id.replaceAll("[^A-Za-z0-9]", "_") + String.format("-%02d.png", f)));
            }
            System.out.printf("%-42s %-9s %-10s %3.0f fps  scale=%.2f offsetY=%.2f  %s%n",
                s.id, e.family, e.colour < 0 ? "art colour" : String.format("#%06x", e.colour),
                e.framerate, e.scale, e.offsetY, e.art);
        }
        System.out.println();
        System.out.println(show.size() + " styles rendered, 30 frames each");
    }
}
