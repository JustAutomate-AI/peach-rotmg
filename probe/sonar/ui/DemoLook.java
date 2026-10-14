package sonar.ui;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Every demo player's portrait, drawn large, with what the game calls the skin
 * and the dyes underneath. This is the check the demo never had: a box here
 * means the sprite pipeline failed for that player.
 */
public final class DemoLook {

    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        SonarTheme.install();
        sonar.assets.IdToAsset.reloadAssets();

        // the demo's own row builder, called directly, so nothing here invents a player
        java.lang.reflect.Method build = LiveDemo.class.getDeclaredMethod("row", int.class, long.class);
        build.setAccessible(true);
        java.lang.reflect.Field players = LiveDemo.class.getDeclaredField("PLAYERS");
        players.setAccessible(true);
        int count = ((String[][]) players.get(null)).length;
        java.util.List<LiveModel.Row> rows = new java.util.ArrayList<>();
        for (int p = 0; p < count; p++) rows.add((LiveModel.Row) build.invoke(null, p, 250_000L));

        int cols = 5, cell = 190, head = 46;
        int rowsN = (rows.size() + cols - 1) / cols;
        BufferedImage img = new BufferedImage(cols * cell, head + rowsN * cell, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = SonarTheme.quality(img.createGraphics());
        g.setColor(SonarTheme.BG);
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        g.setFont(SonarTheme.bold(16f));
        g.setColor(SonarTheme.accent());
        g.drawString("Demo party portraits - " + rows.size() + " players", 16, 28);

        int ok = 0;
        for (int i = 0; i < rows.size(); i++) {
            LiveModel.Row r = rows.get(i);
            int x = (i % cols) * cell, y = head + (i / cols) * cell;
            g.setColor(SonarTheme.alpha(SonarTheme.RAISED, 0.5));
            g.fill(SonarTheme.round(x + 8, y + 8, cell - 16, cell - 16, 10));
            boolean drew = Glyph.player(g, r.skin, r.dyeMain, r.dyeAccessory, x + 51, y + 18, 88);
            if (drew) ok++;
            g.setFont(SonarTheme.bold(12f));
            g.setColor(drew ? SonarTheme.TEXT : SonarTheme.DANGER);
            g.drawString(SonarTheme.fit(r.name + "  " + r.cls, g.getFontMetrics(), cell - 28), x + 16, y + 124);
            g.setFont(SonarTheme.font(10.5f));
            g.setColor(SonarTheme.MUTED);
            String skin = Wardrobe.skinName(r.skin);
            g.drawString(SonarTheme.fit(skin.isEmpty() ? "default look" : skin, g.getFontMetrics(), cell - 28), x + 16, y + 140);
            g.setColor(SonarTheme.FAINT);
            String dye = (r.dyeMain == 0 ? "" : Wardrobe.dyeName(r.dyeMain))
                    + (r.dyeMain != 0 && r.dyeAccessory != 0 ? " + " : "")
                    + (r.dyeAccessory == 0 ? "" : Wardrobe.dyeName(r.dyeAccessory));
            g.drawString(SonarTheme.fit(dye.isEmpty() ? "no dye" : dye, g.getFontMetrics(), cell - 28), x + 16, y + 155);
            if (!drew) {
                g.setColor(SonarTheme.DANGER);
                g.drawString("DID NOT DRAW", x + 16, y + 170);
            }
        }
        g.dispose();
        ImageIO.write(img, "png", new File(args.length > 0 ? args[0] : "demo-look.png"));
        System.out.println(ok + " of " + rows.size() + " portraits drew");
        System.exit(ok == rows.size() ? 0 : 1);
    }
}
