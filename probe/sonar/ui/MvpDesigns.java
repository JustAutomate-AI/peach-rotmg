package sonar.ui;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Three ways the MVP card could lay its information out, rendered side by side so
 * one can be chosen before any of it goes into the app.
 *
 * All three use the game's own enchant icons rather than a rarity gem, and none of
 * them truncate an enchant name.
 *
 *   javac -cp run/Sonar.jar -d probe probe/sonar/ui/MvpDesigns.java
 *   cd run
 *   java -cp "Sonar.jar;../probe" sonar.ui.MvpDesigns <folder>
 */
public final class MvpDesigns {

    static final String NL = String.valueOf((char) 10);
    private static final int PAD = 22;

    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        SonarTheme.install();
        sonar.assets.IdToAsset.reloadAssets();
        File dir = new File(args.length > 0 ? args[0] : ".");
        dir.mkdirs();

        Sample s = heavy();
        ImageIO.write(designA(s), "png", new File(dir, "A-rows.png"));
        ImageIO.write(designB(s), "png", new File(dir, "B-tiles.png"));
        ImageIO.write(designC(s), "png", new File(dir, "C-ledger.png"));

        // the same three with a plain kit, to see them when there is little to show
        Sample p = plain();
        ImageIO.write(designA(p), "png", new File(dir, "A-rows-plain.png"));
        ImageIO.write(designB(p), "png", new File(dir, "B-tiles-plain.png"));
        ImageIO.write(designC(p), "png", new File(dir, "C-ledger-plain.png"));
        System.out.println("wrote six samples to " + dir.getAbsolutePath());
    }

    // ---------------------------------------------------------------- the data

    static final class Sample {
        String who, cls, fight, instance;
        long damage, bossRun, biggest;
        int hits, skin;
        double seconds, healthShare;
        int[] gear = new int[4];
        String[] names = new String[4];
        String[] enchants = new String[4];
    }

    private static Sample heavy() throws Exception {
        Sample s = base();
        s.enchants = new String[]{
                "Damage Bonus IV" + NL + "FireRate Bonus II" + NL + "Piercing Bonus I",
                "Health Bonus III" + NL + "Defense Bonus II",
                "Wisdom Bonus I",
                "Attack Bonus IV" + NL + "Dexterity Bonus II"};
        return s;
    }

    private static Sample plain() throws Exception {
        Sample s = base();
        s.who = "Grumbles";
        s.cls = "Warrior";
        s.enchants = new String[]{"Attack Bonus I", "", "", ""};
        return s;
    }

    private static Sample base() throws Exception {
        Sample s = new Sample();
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
        return s;
    }

    // ---------------------------------------------------------------- shared bits

    /** The game's own icon for one enchant, at the size asked for. */
    private static void enchantIcon(Graphics2D g, String enchant, double x, double y, int size) {
        String sheet = EnchantSprites.sheetOf(enchant);
        int index = EnchantSprites.indexOf(enchant);
        if (sheet == null || index < 0) return;
        Glyph.sprite(g, sheet, index, x, y, size, 1f);
    }

    private static List<String> enchantsOf(String raw) {
        List<String> out = new ArrayList<>();
        if (raw == null || raw.isEmpty()) return out;
        for (String e : raw.split(NL)) if (!e.trim().isEmpty()) out.add(e.trim());
        return out;
    }

    private static Graphics2D start(BufferedImage img) {
        Graphics2D g = SonarTheme.quality(img.createGraphics());
        g.setColor(SonarTheme.BG);
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        return g;
    }

    /** Star, title and fight, measured so nothing collides. */
    private static int heading(Graphics2D g, Sample s, int w, int y) {
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
        return y + 50;
    }

    private static void footer(Graphics2D g, Sample s, int w, int h) {
        g.setColor(SonarTheme.LINE);
        g.drawLine(PAD, h - PAD - 22, w - PAD, h - PAD - 22);
        g.setFont(SonarTheme.font(11.5f));
        g.setColor(SonarTheme.FAINT);
        g.drawString(s.instance, PAD, h - PAD - 4);
        g.setFont(SonarTheme.bold(11.5f));
        String mark = "SONAR";
        g.drawString(mark, w - PAD - g.getFontMetrics().stringWidth(mark), h - PAD - 4);
    }

    // ---------------------------------------------------------------- A: rows

    /**
     * One row per item: the icon, its name, and its enchants listed beneath with the
     * game's own symbol beside each. Nothing is cut; the card grows instead.
     */
    static BufferedImage designA(Sample s) {
        int w = 520, portrait = 84, tile = 40, line = 17;
        int rows = 0;
        for (int i = 0; i < 4; i++) rows += 1 + Math.max(0, enchantsOf(s.enchants[i]).size());
        int h = PAD + 50 + portrait + 20 + 86 + 26 + rows * line + 24 + 32 + PAD;

        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = start(img);
        int y = heading(g, s, w, PAD);
        y = who(g, s, y, portrait, w);
        y = numbers(g, s, y, w);

        g.setFont(SonarTheme.bold(11f));
        g.setColor(SonarTheme.FAINT);
        g.drawString("LOADOUT", PAD, y + 10);
        y += 20;

        for (int i = 0; i < 4; i++) {
            if (s.gear[i] <= 0) continue;
            Glyph.enchantGlow(g, s.enchants[i], PAD, y, tile);
            g.setColor(new Color(0, 0, 0, 60));
            g.fill(SonarTheme.round(PAD, y, tile, tile, 7));
            Glyph.item(g, s.gear[i], PAD + 2, y + 2, tile - 4);

            g.setFont(SonarTheme.bold(12.5f));
            g.setColor(SonarTheme.TEXT);
            g.drawString(s.names[i], PAD + tile + 12, y + 14);

            int ey = y + 32;
            for (String e : enchantsOf(s.enchants[i])) {
                enchantIcon(g, e, PAD + tile + 12, ey - 10, 13);
                g.setFont(SonarTheme.font(11.5f));
                g.setColor(SonarTheme.alpha(SonarTheme.accent(), 0.9));
                g.drawString(e, PAD + tile + 30, ey);
                ey += line;
            }
            y = Math.max(y + tile + 8, ey + 4);
        }
        footer(g, s, w, h);
        g.dispose();
        return img;
    }

    // ---------------------------------------------------------------- B: tiles

    /**
     * Gear as four large tiles across, each with its name and enchant symbols under
     * it. Enchant names sit in a legend beneath so long ones never squeeze a column.
     */
    static BufferedImage designB(Sample s) {
        int w = 560, portrait = 84, tile = 72, gap = 14, line = 17;
        int legend = 0;
        for (int i = 0; i < 4; i++) legend += enchantsOf(s.enchants[i]).size();
        int h = PAD + 50 + portrait + 20 + 86 + 26 + tile + 22 + 14 + legend * line + 20 + 32 + PAD;

        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = start(img);
        int y = heading(g, s, w, PAD);
        y = who(g, s, y, portrait, w);
        y = numbers(g, s, y, w);

        g.setFont(SonarTheme.bold(11f));
        g.setColor(SonarTheme.FAINT);
        g.drawString("LOADOUT", PAD, y + 10);
        y += 20;

        for (int i = 0; i < 4; i++) {
            double gx = PAD + i * (tile + gap);
            if (s.gear[i] <= 0) continue;
            Glyph.enchantGlow(g, s.enchants[i], gx, y, tile);
            g.setColor(new Color(0, 0, 0, 60));
            g.fill(SonarTheme.round(gx, y, tile, tile, 9));
            Glyph.item(g, s.gear[i], gx + 4, y + 4, tile - 8);

            // the symbols for this item, along its bottom edge
            List<String> es = enchantsOf(s.enchants[i]);
            for (int k = 0; k < es.size(); k++) {
                enchantIcon(g, es.get(k), gx + 3 + k * 15, y + tile - 15, 14);
            }
            g.setFont(SonarTheme.font(10f));
            g.setColor(SonarTheme.MUTED);
            String nm = SonarTheme.fit(s.names[i], g.getFontMetrics(), tile + gap - 4);
            g.drawString(nm, (float) gx, (float) (y + tile + 14));
        }
        y += tile + 30;

        g.setColor(SonarTheme.LINE);
        g.drawLine(PAD, y, w - PAD, y);
        y += 16;
        for (int i = 0; i < 4; i++) {
            for (String e : enchantsOf(s.enchants[i])) {
                enchantIcon(g, e, PAD, y - 10, 13);
                g.setFont(SonarTheme.font(11.5f));
                g.setColor(SonarTheme.alpha(SonarTheme.accent(), 0.9));
                g.drawString(e, PAD + 18, y);
                g.setColor(SonarTheme.FAINT);
                g.setFont(SonarTheme.font(10.5f));
                String on = "on " + s.names[i];
                g.drawString(on, w - PAD - g.getFontMetrics().stringWidth(on), y);
                y += line;
            }
        }
        footer(g, s, w, h);
        g.dispose();
        return img;
    }

    // ---------------------------------------------------------------- C: ledger

    /**
     * The numbers given the top half as a block, the loadout as a tidy two-column
     * ledger underneath: symbol, enchant, and the item it is on.
     */
    static BufferedImage designC(Sample s) {
        int w = 520, portrait = 96, line = 19;
        int rows = 0;
        for (int i = 0; i < 4; i++) rows += Math.max(1, enchantsOf(s.enchants[i]).size());
        int h = PAD + 50 + portrait + 24 + 24 + rows * line + 22 + 32 + PAD;

        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = start(img);
        int y = heading(g, s, w, PAD);

        // portrait left, the figures stacked to its right
        if (!Glyph.player(g, s.skin, 0, 0, PAD, y, portrait)) {
            g.setColor(new Color(0, 0, 0, 55));
            g.fill(SonarTheme.round(PAD, y, portrait, portrait, 10));
        }
        int tx = PAD + portrait + 18;
        g.setFont(SonarTheme.bold(22f));
        g.setColor(SonarTheme.TEXT);
        g.drawString(SonarTheme.fit(s.who, g.getFontMetrics(), w - tx - PAD), tx, y + 22);
        g.setFont(SonarTheme.font(12.5f));
        g.setColor(SonarTheme.MUTED);
        g.drawString(s.cls, tx, y + 42);

        g.setFont(SonarTheme.bold(27f));
        g.setColor(SonarTheme.accent());
        String dmg = SonarTheme.number(s.damage);
        g.drawString(dmg, tx, y + 76);
        g.setFont(SonarTheme.font(11.5f));
        g.setColor(SonarTheme.MUTED);
        g.drawString(String.format("on this boss  ·  %.1f%% of it", s.healthShare),
                tx + g.getFontMetrics(SonarTheme.bold(27f)).stringWidth(dmg) + 10, y + 76);
        g.setFont(SonarTheme.font(11.5f));
        g.setColor(SonarTheme.FAINT);
        g.drawString(SonarTheme.compact(Math.round(s.damage / s.seconds)) + " dps   ·   biggest "
                + SonarTheme.compact(s.biggest) + "   ·   " + SonarTheme.number(s.hits) + " hits"
                + "   ·   " + SonarTheme.number(s.bossRun) + " all bosses", tx, y + 94);
        y += portrait + 20;

        g.setColor(SonarTheme.LINE);
        g.drawLine(PAD, y, w - PAD, y);
        y += 20;

        for (int i = 0; i < 4; i++) {
            if (s.gear[i] <= 0) continue;
            List<String> es = enchantsOf(s.enchants[i]);
            int block = Math.max(1, es.size());
            // the item once, against its own enchants
            Glyph.item(g, s.gear[i], PAD, y - 12, 16);
            g.setFont(SonarTheme.font(11.5f));
            g.setColor(SonarTheme.MUTED);
            g.drawString(SonarTheme.fit(s.names[i], g.getFontMetrics(), 196), PAD + 22, y);
            for (int k = 0; k < block; k++) {
                int ey = y + k * line;
                if (k < es.size()) {
                    enchantIcon(g, es.get(k), PAD + 244, ey - 10, 13);
                    g.setFont(SonarTheme.font(11.5f));
                    g.setColor(SonarTheme.alpha(SonarTheme.accent(), 0.9));
                    g.drawString(es.get(k), PAD + 262, ey);
                } else {
                    g.setFont(SonarTheme.font(11.5f));
                    g.setColor(SonarTheme.FAINT);
                    g.drawString("—", PAD + 262, ey);
                }
            }
            y += block * line + 4;
        }
        footer(g, s, w, h);
        g.dispose();
        return img;
    }

    // ---------------------------------------------------------------- A/B shared

    private static int who(Graphics2D g, Sample s, int y, int portrait, int w) {
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
        return y + portrait + 18;
    }

    private static int numbers(Graphics2D g, Sample s, int y, int w) {
        g.setFont(SonarTheme.bold(32f));
        g.setColor(SonarTheme.accent());
        String dmg = SonarTheme.number(s.damage);
        g.drawString(dmg, PAD, y + 26);
        int after = PAD + g.getFontMetrics().stringWidth(dmg) + 10;
        g.setFont(SonarTheme.font(12.5f));
        g.setColor(SonarTheme.MUTED);
        g.drawString("damage to this boss", after, y + 26);
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
}
