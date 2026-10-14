package sonar.ui;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Variations on design A, which nests each enchant under the item it is on.
 * All of them keep that nesting; what changes is how the four items are arranged
 * and how hard the grouping is drawn.
 *
 *   javac -encoding UTF-8 -cp run/Sonar.jar -d probe probe/sonar/ui/MvpDesignsA.java
 *   cd run
 *   java -cp "Sonar.jar;../probe" sonar.ui.MvpDesignsA <folder>
 */
public final class MvpDesignsA {

    static final String NL = String.valueOf((char) 10);
    private static final int PAD = 22;

    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        SonarTheme.install();
        sonar.assets.IdToAsset.reloadAssets();
        File dir = new File(args.length > 0 ? args[0] : ".");
        dir.mkdirs();

        // every one of these is a real name out of the game's enchantments.xml, and the
        // ability holds the longest the game has, which is where a column layout gives up
        MvpDesigns.Sample heavy = sample(new String[]{
                "Damage Bonus IV" + NL + "FireRate Bonus II" + NL + "Percentage Life Regeneration III",
                "Summon Power -StatMod Mult Tradeoff III" + NL + "Defense Bonus II",
                "Wisdom Bonus I",
                "Attack Bonus IV" + NL + "Dexterity Bonus II"});
        MvpDesigns.Sample plain = sample(new String[]{"Attack Bonus I", "", "", ""});

        ImageIO.write(columns(heavy), "png", new File(dir, "A1-columns.png"));
        ImageIO.write(grid(heavy), "png", new File(dir, "A2-grid.png"));
        ImageIO.write(panels(heavy), "png", new File(dir, "A3-panels.png"));
        ImageIO.write(columns(plain), "png", new File(dir, "A1-columns-plain.png"));
        ImageIO.write(grid(plain), "png", new File(dir, "A2-grid-plain.png"));
        ImageIO.write(panels(plain), "png", new File(dir, "A3-panels-plain.png"));
        System.out.println("wrote six to " + dir.getAbsolutePath());
    }

    private static MvpDesigns.Sample sample(String[] enchants) throws Exception {
        MvpDesigns.Sample s = new MvpDesigns.Sample();
        s.who = "xXLongestNameAllowedXx";
        s.cls = "Necromancer";
        s.fight = "Oryx the Mad God 3";
        s.instance = "Oryx's Sanctuary";
        s.damage = 1_902_775;
        s.bossRun = 2_515_300;
        s.biggest = 55_400;
        s.hits = 1204;
        s.seconds = 120;
        s.healthShare = 18.8;
        s.skin = 0x0344;
        java.lang.reflect.Method kitFor = LiveDemo.class.getDeclaredMethod("kitFor", String.class);
        kitFor.setAccessible(true);
        int[] kit = (int[]) kitFor.invoke(null, "Necromancer");
        for (int i = 0; i < 4; i++) {
            s.gear[i] = kit[i];
            s.names[i] = LiveModel.itemName(kit[i]);
        }
        s.enchants = enchants;
        return s;
    }

    // ---------------------------------------------------------------- bits

    private static void enchantIcon(Graphics2D g, String e, double x, double y, int size) {
        String sheet = EnchantSprites.sheetOf(e);
        int index = EnchantSprites.indexOf(e);
        if (sheet != null && index >= 0) {
            Glyph.sprite(g, sheet, index, x, y, size, 1f);
            return;
        }
        // one Sonar has no icon for still gets a mark, so the column does not gap
        g.setColor(SonarTheme.alpha(SonarTheme.MUTED, 0.5));
        g.fillOval((int) x + size / 2 - 2, (int) y + size / 2 - 2, 4, 4);
    }

    private static List<String> enchantsOf(String raw) {
        List<String> out = new ArrayList<>();
        if (raw == null || raw.isEmpty()) return out;
        for (String e : raw.split(NL)) if (!e.trim().isEmpty()) out.add(e.trim());
        return out;
    }

    /** Breaks a name over as many lines as it needs, so nothing is ever cut. */
    private static List<String> wrap(FontMetrics fm, String text, int width) {
        List<String> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        for (String word : text.split(" ")) {
            String test = line.length() == 0 ? word : line + " " + word;
            if (fm.stringWidth(test) <= width || line.length() == 0) {
                line.setLength(0);
                line.append(test);
            } else {
                lines.add(line.toString());
                line.setLength(0);
                line.append(word);
            }
        }
        if (line.length() > 0) lines.add(line.toString());
        return lines;
    }

    private static Graphics2D start(BufferedImage img) {
        Graphics2D g = SonarTheme.quality(img.createGraphics());
        g.setColor(SonarTheme.BG);
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        return g;
    }

    private static int top(Graphics2D g, MvpDesigns.Sample s, int w) {
        int y = PAD;
        Glyph.action(g, "share-star", PAD, y + 2, 20, SonarTheme.GOLD);
        g.setFont(SonarTheme.bold(15f));
        g.setColor(SonarTheme.GOLD);
        g.drawString("MVP", PAD + 28, y + 17);
        int after = PAD + 28 + g.getFontMetrics().stringWidth("MVP") + 16;
        g.setFont(SonarTheme.font(12.5f));
        g.setColor(SonarTheme.MUTED);
        g.drawString(SonarTheme.fit(s.fight, g.getFontMetrics(), w - PAD - after), after, y + 17);
        g.setColor(SonarTheme.LINE);
        g.drawLine(PAD, y + 32, w - PAD, y + 32);
        y += 50;

        int portrait = 84;
        if (!Glyph.player(g, s.skin, 0, 0, PAD, y, portrait)) {
            g.setColor(new Color(0, 0, 0, 55));
            g.fill(SonarTheme.round(PAD, y, portrait, portrait, 10));
        }
        int tx = PAD + portrait + 18;
        g.setFont(SonarTheme.bold(24f));
        g.setColor(SonarTheme.TEXT);
        g.drawString(SonarTheme.fit(s.who, g.getFontMetrics(), w - tx - PAD), tx, y + 30);
        g.setFont(SonarTheme.font(13f));
        g.setColor(SonarTheme.MUTED);
        g.drawString(s.cls, tx, y + 52);
        y += portrait + 18;

        g.setFont(SonarTheme.bold(32f));
        g.setColor(SonarTheme.accent());
        String dmg = SonarTheme.number(s.damage);
        g.drawString(dmg, PAD, y + 26);
        int a2 = PAD + g.getFontMetrics().stringWidth(dmg) + 10;
        g.setFont(SonarTheme.font(12.5f));
        g.setColor(SonarTheme.MUTED);
        g.drawString("damage to this boss", a2, y + 26);
        String pct = String.format("%.1f%% of its health", s.healthShare);
        g.setFont(SonarTheme.bold(12.5f));
        g.setColor(SonarTheme.TEXT);
        g.drawString(pct, w - PAD - g.getFontMetrics().stringWidth(pct), y + 26);
        g.setFont(SonarTheme.font(12.5f));
        g.setColor(SonarTheme.MUTED);
        g.drawString(SonarTheme.compact(Math.round(s.damage / s.seconds)) + " dps   ·   biggest hit "
                + SonarTheme.compact(s.biggest) + "   ·   " + SonarTheme.number(s.hits) + " hits", PAD, y + 50);
        g.setColor(SonarTheme.FAINT);
        g.drawString(SonarTheme.number(s.bossRun) + " across every boss this run", PAD, y + 70);
        return y + 88;
    }

    private static void bottom(Graphics2D g, MvpDesigns.Sample s, int w, int h) {
        g.setColor(SonarTheme.LINE);
        g.drawLine(PAD, h - PAD - 22, w - PAD, h - PAD - 22);
        g.setFont(SonarTheme.font(11.5f));
        g.setColor(SonarTheme.FAINT);
        g.drawString(s.instance, PAD, h - PAD - 4);
        g.setFont(SonarTheme.bold(11.5f));
        String mark = "SONAR";
        g.drawString(mark, w - PAD - g.getFontMetrics().stringWidth(mark), h - PAD - 4);
    }

    private static void label(Graphics2D g, int y) {
        g.setFont(SonarTheme.bold(11f));
        g.setColor(SonarTheme.FAINT);
        g.drawString("LOADOUT", PAD, y + 10);
    }

    // ---------------------------------------------------------------- A1: columns

    /** The four items across, each its own column with its enchants stacked under it. */
    static BufferedImage columns(MvpDesigns.Sample s) {
        int w = 700, tile = 60, gap = 16, line = 16;
        int col = (w - PAD * 2 - gap * 3) / 4;

        BufferedImage probe = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D pg = SonarTheme.quality(probe.createGraphics());
        FontMetrics nameFm = pg.getFontMetrics(SonarTheme.bold(12f));
        FontMetrics enchFm = pg.getFontMetrics(SonarTheme.font(11f));
        int deepest = 0;
        for (int i = 0; i < 4; i++) {
            int lines = wrap(nameFm, s.names[i], col).size();
            for (String e : enchantsOf(s.enchants[i])) lines += wrap(enchFm, e, col - 18).size();
            deepest = Math.max(deepest, lines);
        }
        pg.dispose();

        int h = PAD + 50 + 84 + 18 + 88 + 22 + tile + 16 + deepest * line + 24 + 32 + PAD;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = start(img);
        int y = top(g, s, w);
        label(g, y);
        y += 22;

        for (int i = 0; i < 4; i++) {
            if (s.gear[i] <= 0) continue;
            int cx = PAD + i * (col + gap);
            double tx = cx + (col - tile) / 2.0;
            Glyph.enchantGlow(g, s.enchants[i], tx, y, tile);
            g.setColor(new Color(0, 0, 0, 60));
            g.fill(SonarTheme.round(tx, y, tile, tile, 9));
            Glyph.item(g, s.gear[i], tx + 3, y + 3, tile - 6);

            int ty = y + tile + 16;
            g.setFont(SonarTheme.bold(12f));
            g.setColor(SonarTheme.TEXT);
            for (String part : wrap(g.getFontMetrics(), s.names[i], col)) {
                g.drawString(part, cx, ty);
                ty += line;
            }
            ty += 4;
            for (String e : enchantsOf(s.enchants[i])) {
                enchantIcon(g, e, cx, ty - 10, 13);
                g.setFont(SonarTheme.font(11f));
                g.setColor(SonarTheme.alpha(SonarTheme.accent(), 0.9));
                // wrapped rather than cut: the longest enchant the game has is 39 characters
                boolean first = true;
                for (String part : wrap(g.getFontMetrics(), e, col - 18)) {
                    g.drawString(part, cx + 17, ty);
                    ty += line;
                    first = false;
                }
                if (first) ty += line;
            }
        }
        bottom(g, s, w, h);
        g.dispose();
        return img;
    }

    // ---------------------------------------------------------------- A2: grid

    /** Two across, two down: half the height of a single column, nesting kept. */
    static BufferedImage grid(MvpDesigns.Sample s) {
        int w = 560, tile = 48, line = 16, gapX = 18;
        int col = (w - PAD * 2 - gapX) / 2;
        int textW = col - tile - 10;

        BufferedImage probe = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D pg = SonarTheme.quality(probe.createGraphics());
        FontMetrics nameFm = pg.getFontMetrics(SonarTheme.bold(12f));
        FontMetrics enchFm = pg.getFontMetrics(SonarTheme.font(11f));
        int[] cell = new int[4];
        for (int i = 0; i < 4; i++) {
            int lines = wrap(nameFm, s.names[i], textW).size();
            for (String e : enchantsOf(s.enchants[i])) lines += wrap(enchFm, e, textW - 18).size();
            cell[i] = Math.max(tile, 2 + lines * line) + 18;
        }
        pg.dispose();
        int blockOne = Math.max(cell[0], cell[1]);
        int blockTwo = Math.max(cell[2], cell[3]);

        int h = PAD + 50 + 84 + 18 + 88 + 22 + blockOne + blockTwo + 10 + 32 + PAD;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = start(img);
        int y = top(g, s, w);
        label(g, y);
        y += 22;

        for (int i = 0; i < 4; i++) {
            if (s.gear[i] <= 0) continue;
            int cx = PAD + (i % 2) * (col + gapX);
            int cy = y + (i < 2 ? 0 : blockOne);
            Glyph.enchantGlow(g, s.enchants[i], cx, cy, tile);
            g.setColor(new Color(0, 0, 0, 60));
            g.fill(SonarTheme.round(cx, cy, tile, tile, 8));
            Glyph.item(g, s.gear[i], cx + 3, cy + 3, tile - 6);

            int tx = cx + tile + 10;
            int ty = cy + 13;
            g.setFont(SonarTheme.bold(12f));
            g.setColor(SonarTheme.TEXT);
            for (String part : wrap(g.getFontMetrics(), s.names[i], textW)) {
                g.drawString(part, tx, ty);
                ty += line;
            }
            ty += 2;
            for (String e : enchantsOf(s.enchants[i])) {
                enchantIcon(g, e, tx, ty - 10, 13);
                g.setFont(SonarTheme.font(11f));
                g.setColor(SonarTheme.alpha(SonarTheme.accent(), 0.9));
                for (String part : wrap(g.getFontMetrics(), e, textW - 18)) {
                    g.drawString(part, tx + 17, ty);
                    ty += line;
                }
            }
        }
        bottom(g, s, w, h);
        g.dispose();
        return img;
    }

    // ---------------------------------------------------------------- A3: panels

    /** Design A's stack, with each item given its own panel so the grouping is drawn, not implied. */
    static BufferedImage panels(MvpDesigns.Sample s) {
        int w = 520, tile = 44, line = 17;
        int total = 0;
        int[] heights = new int[4];
        for (int i = 0; i < 4; i++) {
            heights[i] = Math.max(tile + 16, 30 + enchantsOf(s.enchants[i]).size() * line + 6);
            total += heights[i] + 8;
        }
        int h = PAD + 50 + 84 + 18 + 88 + 22 + total + 14 + 32 + PAD;

        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = start(img);
        int y = top(g, s, w);
        label(g, y);
        y += 22;

        for (int i = 0; i < 4; i++) {
            if (s.gear[i] <= 0) continue;
            int bh = heights[i];
            g.setColor(SonarTheme.alpha(SonarTheme.MUTED, 0.07));
            g.fill(SonarTheme.round(PAD, y, w - PAD * 2, bh, 10));

            double ix = PAD + 10, iy = y + 8;
            Glyph.enchantGlow(g, s.enchants[i], ix, iy, tile);
            g.setColor(new Color(0, 0, 0, 60));
            g.fill(SonarTheme.round(ix, iy, tile, tile, 8));
            Glyph.item(g, s.gear[i], ix + 3, iy + 3, tile - 6);

            int tx = (int) ix + tile + 12;
            g.setFont(SonarTheme.bold(13f));
            g.setColor(SonarTheme.TEXT);
            g.drawString(SonarTheme.fit(s.names[i], g.getFontMetrics(), w - tx - PAD - 10), tx, y + 22);

            int ey = y + 42;
            for (String e : enchantsOf(s.enchants[i])) {
                enchantIcon(g, e, tx, ey - 10, 13);
                g.setFont(SonarTheme.font(11.5f));
                g.setColor(SonarTheme.alpha(SonarTheme.accent(), 0.9));
                g.drawString(e, tx + 18, ey);
                ey += line;
            }
            y += bh + 8;
        }
        bottom(g, s, w, h);
        g.dispose();
        return img;
    }
}
