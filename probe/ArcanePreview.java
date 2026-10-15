import sonar.ui.ArcaneStyle;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import java.util.List;

/**
 * Renders the styles the way Sonar would draw them, as a sequence of frames so the
 * motion can be checked, plus the damage-row case where an effect must not reach the bar.
 */
public class ArcanePreview {

    static Method styled, player;

    static boolean styled(Graphics2D g, int skin, int type, long t, double x, double y, int size, double bleed)
            throws Exception {
        return (Boolean) styled.invoke(null, g, skin, 0, 0, type, t, x, y, size, bleed);
    }

    public static void main(String[] a) throws Exception {
        System.setProperty("java.awt.headless", "true");
        Class<?> glyph = Class.forName("sonar.ui.Glyph");
        styled = glyph.getDeclaredMethod("playerStyled", Graphics2D.class, int.class, int.class,
                int.class, int.class, long.class, double.class, double.class, int.class, double.class);
        styled.setAccessible(true);
        Class<?> theme = Class.forName("sonar.ui.SonarTheme");
        theme.getDeclaredMethod("install").invoke(null);
        sonar.assets.IdToAsset.reloadAssets();

        File out = new File(a[0]);
        out.mkdirs();

        // one style per distinct art key, so every strip is seen at least once
        Map<String, ArcaneStyle> perArt = new LinkedHashMap<>();
        for (ArcaneStyle s : ArcaneStyle.all().values()) {
            ArcaneStyle.Effect e = s.drawable();
            if (e != null) perArt.putIfAbsent(e.art, s);
        }
        List<ArcaneStyle> show = new ArrayList<>(perArt.values());
        show.sort(Comparator.comparing(s -> s.drawable().art));
        System.out.println("showing " + show.size() + " effects, one per art key");

        final int SKIN = 0x0342, CELL = 96, COLS = 8, LABEL = 16;
        int rows = (show.size() + COLS - 1) / COLS;
        int w = COLS * CELL, h = rows * (CELL + LABEL) + 8;

        for (int f = 0; f < 30; f++) {
            long t = (long) (f * (1000.0 / 30.0));
            BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = img.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(new Color(0x0a0616));
            g.fillRect(0, 0, w, h);
            for (int i = 0; i < show.size(); i++) {
                ArcaneStyle s = show.get(i);
                int cx = (i % COLS) * CELL, cy = (i / COLS) * (CELL + LABEL);
                g.setColor(new Color(0x1b1138));
                g.fillRoundRect(cx + 4, cy + 4, CELL - 8, CELL - 8, 8, 8);
                // the portrait at 40px inside a 96px cell leaves room for the full spread
                styled(g, SKIN, s.objectType, t, cx + CELL / 2.0 - 20, cy + CELL / 2.0 - 20, 40, 0.5);
                g.setColor(new Color(0x9a8fc4));
                g.setFont(new Font("Dialog", Font.PLAIN, 9));
                String art = s.drawable().art;
                g.drawString(art.length() > 17 ? art.substring(0, 17) : art, cx + 6, cy + CELL + 9);
            }
            g.dispose();
            ImageIO.write(img, "png", new File(out, String.format("grid-%02d.png", f)));
        }
        System.out.println("wrote 30 grid frames");

        // the damage row: an effect over a class-coloured bar, with and without a leash
        ArcaneStyle fire = null;
        for (ArcaneStyle s : ArcaneStyle.all().values()) {
            if ("morefire".equals(s.drawable() == null ? null : s.drawable().art)
                    && s.drawable().colour == 0x00FF00) { fire = s; break; }
        }
        if (fire == null) for (ArcaneStyle s : ArcaneStyle.all().values())
            if (s.drawable() != null && "morefire".equals(s.drawable().art)) { fire = s; break; }
        System.out.println("damage-row example: " + fire);

        final int ROW = 34, W = 520;
        for (int f = 0; f < 30; f++) {
            long t = (long) (f * (1000.0 / 30.0));
            BufferedImage img = new BufferedImage(W, ROW * 2 + 34, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = img.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(new Color(0x0a0616));
            g.fillRect(0, 0, W, img.getHeight());
            for (int k = 0; k < 2; k++) {
                int y = 8 + k * (ROW + 10);
                // the class-coloured bar, as DamagePane draws it
                g.setPaint(new GradientPaint(0, 0, new Color(0x1f6f4a), 300, 0, new Color(0x3fd99a)));
                g.fillRoundRect(0, y, 300, ROW, 11, 11);
                double bleed = k == 0 ? 0.5 : 0.0;
                int px = k == 0 ? 30 : 22;     // shifted left when it must not touch the bar
                styled(g, SKIN, fire.objectType, t, px, y + ROW / 2.0 - 9, 18, bleed);
                g.setColor(Color.WHITE);
                g.setFont(new Font("Dialog", Font.PLAIN, 11));
                g.drawString(k == 0 ? "bleed 0.5  - effect spreads onto the bar"
                                    : "bleed 0    - effect kept inside the portrait", 320, y + 21);
            }
            g.dispose();
            ImageIO.write(img, "png", new File(out, String.format("row-%02d.png", f)));
        }
        System.out.println("wrote 30 row frames");
    }
}
