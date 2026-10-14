package sonar.ui;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * One layout, five looks.
 * <p>
 * The arrangement here is the chosen one - the two by two grid, every enchant
 * nested under the item it sits on, drawn with the game's own enchant icon. What
 * changes between these is only the paint: background, palette, type and the
 * decoration around the edges, so the five can be judged against each other
 * without any other difference getting in the way.
 *
 *   javac -encoding UTF-8 -cp run/Sonar.jar -d probe probe/sonar/ui/MvpThemes.java
 *   cd run
 *   java -cp "Sonar.jar;../probe" sonar.ui.MvpThemes <folder>
 */
public final class MvpThemes {

    static final String NL = String.valueOf((char) 10);
    private static final int PAD = 22;

    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        SonarTheme.install();
        sonar.assets.IdToAsset.reloadAssets();
        File dir = new File(args.length > 0 ? args[0] : ".");
        dir.mkdirs();

        Sample s = sample();
        Theme[] themes = {new Neon(), new Sunset(), new Holo(), new Trophy(), new Terminal()};
        for (int i = 0; i < themes.length; i++) {
            ImageIO.write(render(s, themes[i]), "png", new File(dir, (i + 1) + "-" + themes[i].name + ".png"));
        }
        System.out.println("wrote " + themes.length + " to " + dir.getAbsolutePath());
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

    /**
     * A kit that could really exist: every enchant below is in the game's own
     * enchantments.xml and is allowed on the slot it is shown in, and the skull
     * carries a thirty-two character name so the width still gets tested.
     */
    private static Sample sample() throws Exception {
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
        s.enchants = new String[]{
                "Damage Bonus IV" + NL + "FireRate Bonus II",
                "Percentage Mana Regeneration III" + NL + "Wisdom Bonus II",
                "Defense Bonus III",
                "Attack Bonus IV" + NL + "Percentage Life Regeneration II"};
        return s;
    }

    // ---------------------------------------------------------------- the looks

    static abstract class Theme {
        String name;
        Color text, muted, faint, line, title, accent, ench, panel, tileBack, tileEdge;
        /** A soft bloom behind the damage figure, which only some of these want. */
        boolean bloom;

        Font base(float size) {
            return SonarTheme.font(size);
        }

        Font strong(float size) {
            return SonarTheme.bold(size);
        }

        abstract void background(Graphics2D g, int w, int h);

        /** Anything drawn over the top of everything else. */
        void overlay(Graphics2D g, int w, int h) {
        }
    }

    /** Black, with the magenta and cyan of an arcade cabinet. */
    static final class Neon extends Theme {
        Neon() {
            name = "neon";
            text = new Color(0xF2ECFF);
            muted = new Color(0x9A8FC4);
            faint = new Color(0x655C8A);
            line = new Color(0x3A2F63);
            title = new Color(0xFF3CAC);
            accent = new Color(0x2DE2E6);
            ench = new Color(0x7CF8C4);
            panel = new Color(24, 16, 48);
            tileBack = new Color(12, 8, 28);
            tileEdge = new Color(0xFF3CAC);
            bloom = true;
        }

        void background(Graphics2D g, int w, int h) {
            g.setPaint(new GradientPaint(0, 0, new Color(0x0C0820), 0, h, new Color(0x16082B)));
            g.fillRect(0, 0, w, h);
            // a faint perspective grid, far enough down that it never fights the text
            g.setColor(new Color(0xFF3CAC & 0xFFFFFF | 0x16000000, true));
            for (int x = 0; x < w; x += 34) g.drawLine(x, h - 90, x, h);
            for (int y = h - 90; y < h; y += 14) g.drawLine(0, y, w, y);
        }

        void overlay(Graphics2D g, int w, int h) {
            g.setColor(new Color(45, 226, 230, 60));
            g.drawRoundRect(5, 5, w - 11, h - 11, 14, 14);
            g.setColor(new Color(255, 60, 172, 40));
            g.drawRoundRect(7, 7, w - 15, h - 15, 12, 12);
        }
    }

    /** A dusk gradient behind warm pinks and amber. */
    static final class Sunset extends Theme {
        Sunset() {
            name = "sunset";
            text = new Color(0xFFF1E6);
            muted = new Color(0xC9A3B5);
            faint = new Color(0x8E6C84);
            line = new Color(0x583C5A);
            title = new Color(0xFFC857);
            accent = new Color(0xFF7B54);
            ench = new Color(0xFFD9A0);
            panel = new Color(255, 255, 255, 16);
            tileBack = new Color(28, 12, 34);
            tileEdge = new Color(0xFF9E7A);
            bloom = true;
        }

        void background(Graphics2D g, int w, int h) {
            g.setPaint(new LinearGradientPaint(new Point(0, 0), new Point(0, h),
                    new float[]{0f, 0.55f, 1f},
                    new Color[]{new Color(0x241036), new Color(0x4A1B46), new Color(0x82304A)}));
            g.fillRect(0, 0, w, h);
            // a low sun behind the footer
            g.setPaint(new RadialGradientPaint(new Point(w / 2, h), w / 2f,
                    new float[]{0f, 1f},
                    new Color[]{new Color(255, 150, 90, 70), new Color(255, 150, 90, 0)}));
            g.fillRect(0, h / 2, w, h / 2);
        }
    }

    /** Frosted glass over slate: quiet, and the easiest of the five to read. */
    static final class Holo extends Theme {
        Holo() {
            name = "holo";
            text = new Color(0xF4F8FF);
            muted = new Color(0x9FB0C9);
            faint = new Color(0x6E7F99);
            line = new Color(255, 255, 255, 34);
            title = new Color(0x8FD8FF);
            accent = new Color(0x62E8C8);
            ench = new Color(0xBFE6FF);
            panel = new Color(255, 255, 255, 20);
            tileBack = new Color(255, 255, 255, 26);
            tileEdge = new Color(255, 255, 255, 70);
        }

        void background(Graphics2D g, int w, int h) {
            g.setPaint(new GradientPaint(0, 0, new Color(0x1B2435), w, h, new Color(0x0E141F)));
            g.fillRect(0, 0, w, h);
            g.setPaint(new RadialGradientPaint(new Point(w - 60, 40), w * 0.8f,
                    new float[]{0f, 1f},
                    new Color[]{new Color(98, 232, 200, 42), new Color(98, 232, 200, 0)}));
            g.fillRect(0, 0, w, h);
        }
    }

    /** Charcoal and gold leaf: the one that looks like a trophy rather than a readout. */
    static final class Trophy extends Theme {
        Trophy() {
            name = "trophy";
            text = new Color(0xF6EEDD);
            muted = new Color(0xB3A287);
            faint = new Color(0x7D7059);
            line = new Color(0x4A3F2C);
            title = new Color(0xE8C35A);
            accent = new Color(0xF0D282);
            ench = new Color(0xCBB08A);
            panel = new Color(255, 230, 170, 14);
            tileBack = new Color(20, 17, 12);
            tileEdge = new Color(0xA8894A);
        }

        void background(Graphics2D g, int w, int h) {
            g.setPaint(new GradientPaint(0, 0, new Color(0x16130E), 0, h, new Color(0x0C0A07)));
            g.fillRect(0, 0, w, h);
            g.setPaint(new RadialGradientPaint(new Point(w / 2, 90), w * 0.75f,
                    new float[]{0f, 1f},
                    new Color[]{new Color(232, 195, 90, 30), new Color(232, 195, 90, 0)}));
            g.fillRect(0, 0, w, h);
        }

        void overlay(Graphics2D g, int w, int h) {
            g.setColor(new Color(168, 137, 74, 110));
            g.drawRoundRect(8, 8, w - 17, h - 17, 10, 10);
        }
    }

    /** Phosphor on black, scanlines and all. */
    static final class Terminal extends Theme {
        Terminal() {
            name = "terminal";
            text = new Color(0xD6FFD6);
            muted = new Color(0x5FA86A);
            faint = new Color(0x3C6B44);
            line = new Color(0x1F4426);
            title = new Color(0xFFB000);
            accent = new Color(0x33FF66);
            ench = new Color(0x8CFFA8);
            panel = new Color(0, 255, 102, 14);
            tileBack = new Color(4, 14, 7);
            tileEdge = new Color(0x2E7A3C);
            bloom = true;
        }

        // monospace runs wider per character, so it is set back a little to keep the
        // same lines fitting the same widths
        Font base(float size) {
            return new Font(Font.MONOSPACED, Font.PLAIN, Math.round(size * 0.9f));
        }

        Font strong(float size) {
            return new Font(Font.MONOSPACED, Font.BOLD, Math.round(size * 0.9f));
        }

        void background(Graphics2D g, int w, int h) {
            g.setColor(new Color(0x030703));
            g.fillRect(0, 0, w, h);
        }

        void overlay(Graphics2D g, int w, int h) {
            g.setColor(new Color(0, 0, 0, 30));
            for (int y = 0; y < h; y += 3) g.drawLine(0, y, w, y);
            g.setColor(new Color(51, 255, 102, 55));
            g.drawRect(6, 6, w - 13, h - 13);
        }
    }

    // ---------------------------------------------------------------- the drawing

    private static List<String> enchantsOf(String raw) {
        List<String> out = new ArrayList<>();
        if (raw == null || raw.isEmpty()) return out;
        for (String e : raw.split(NL)) if (!e.trim().isEmpty()) out.add(e.trim());
        return out;
    }

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

    private static void enchantIcon(Graphics2D g, Theme t, String e, double x, double y, int size) {
        String sheet = EnchantSprites.sheetOf(e);
        int index = EnchantSprites.indexOf(e);
        if (sheet != null && index >= 0) {
            Glyph.sprite(g, sheet, index, x, y, size, 1f);
            return;
        }
        g.setColor(t.faint);
        g.fillOval((int) x + size / 2 - 2, (int) y + size / 2 - 2, 4, 4);
    }

    /** The damage figure, with a bloom behind it on the themes that asked for one. */
    private static void bigNumber(Graphics2D g, Theme t, String s, int x, int y) {
        if (t.bloom) {
            g.setColor(new Color(t.accent.getRed(), t.accent.getGreen(), t.accent.getBlue(), 38));
            for (int r = 3; r >= 1; r--) {
                g.drawString(s, x - r, y);
                g.drawString(s, x + r, y);
                g.drawString(s, x, y - r);
                g.drawString(s, x, y + r);
            }
        }
        g.setColor(t.accent);
        g.drawString(s, x, y);
    }

    static BufferedImage render(Sample s, Theme t) {
        int w = 560, tile = 48, line = 16, gapX = 18;
        int col = (w - PAD * 2 - gapX) / 2;
        int textW = col - tile - 10;

        BufferedImage probe = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D pg = SonarTheme.quality(probe.createGraphics());
        FontMetrics nameFm = pg.getFontMetrics(t.strong(12f));
        FontMetrics enchFm = pg.getFontMetrics(t.base(11f));
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
        Graphics2D g = SonarTheme.quality(img.createGraphics());
        t.background(g, w, h);

        int y = PAD;

        // heading
        Glyph.action(g, "share-star", PAD, y + 2, 20, t.title);
        g.setFont(t.strong(15f));
        g.setColor(t.title);
        g.drawString("MVP", PAD + 28, y + 17);
        int after = PAD + 28 + g.getFontMetrics().stringWidth("MVP") + 16;
        g.setFont(t.base(12.5f));
        g.setColor(t.muted);
        g.drawString(SonarTheme.fit(s.fight, g.getFontMetrics(), w - PAD - after), after, y + 17);
        g.setColor(t.line);
        g.drawLine(PAD, y + 32, w - PAD, y + 32);
        y += 50;

        // the character
        int portrait = 84;
        if (!Glyph.player(g, s.skin, 0, 0, PAD, y, portrait)) {
            g.setColor(t.tileBack);
            g.fill(SonarTheme.round(PAD, y, portrait, portrait, 10));
        }
        int tx = PAD + portrait + 18;
        g.setFont(t.strong(24f));
        g.setColor(t.text);
        g.drawString(SonarTheme.fit(s.who, g.getFontMetrics(), w - tx - PAD), tx, y + 30);
        g.setFont(t.base(13f));
        g.setColor(t.muted);
        g.drawString(s.cls, tx, y + 52);
        y += portrait + 18;

        // the numbers
        g.setFont(t.strong(32f));
        String dmg = SonarTheme.number(s.damage);
        bigNumber(g, t, dmg, PAD, y + 26);
        int a2 = PAD + g.getFontMetrics().stringWidth(dmg) + 10;
        g.setFont(t.base(12.5f));
        g.setColor(t.muted);
        g.drawString("damage to this boss", a2, y + 26);
        String pct = String.format("%.1f%% of its health", s.healthShare);
        g.setFont(t.strong(12.5f));
        g.setColor(t.text);
        g.drawString(pct, w - PAD - g.getFontMetrics().stringWidth(pct), y + 26);
        g.setFont(t.base(12.5f));
        g.setColor(t.muted);
        g.drawString(SonarTheme.compact(Math.round(s.damage / s.seconds)) + " dps   ·   biggest hit "
                + SonarTheme.compact(s.biggest) + "   ·   " + SonarTheme.number(s.hits) + " hits", PAD, y + 50);
        g.setColor(t.faint);
        g.drawString(SonarTheme.number(s.bossRun) + " across every boss this run", PAD, y + 70);
        y += 88;

        // the loadout, two by two
        g.setFont(t.strong(11f));
        g.setColor(t.faint);
        g.drawString("LOADOUT", PAD, y + 10);
        y += 22;

        for (int i = 0; i < 4; i++) {
            if (s.gear[i] <= 0) continue;
            int cx = PAD + (i % 2) * (col + gapX);
            int cy = y + (i < 2 ? 0 : blockOne);

            g.setColor(t.panel);
            g.fill(SonarTheme.round(cx - 8, cy - 8, col + 8, (i < 2 ? blockOne : blockTwo) - 4, 10));
            g.setColor(t.tileBack);
            g.fill(SonarTheme.round(cx, cy, tile, tile, 8));
            Glyph.item(g, s.gear[i], cx + 3, cy + 3, tile - 6);
            if (!enchantsOf(s.enchants[i]).isEmpty()) {
                g.setColor(t.tileEdge);
                g.draw(SonarTheme.round(cx, cy, tile - 1, tile - 1, 8));
            }

            int ix = cx + tile + 10;
            int iy = cy + 13;
            g.setFont(t.strong(12f));
            g.setColor(t.text);
            for (String part : wrap(g.getFontMetrics(), s.names[i], textW)) {
                g.drawString(part, ix, iy);
                iy += line;
            }
            iy += 2;
            for (String e : enchantsOf(s.enchants[i])) {
                enchantIcon(g, t, e, ix, iy - 10, 13);
                g.setFont(t.base(11f));
                g.setColor(t.ench);
                for (String part : wrap(g.getFontMetrics(), e, textW - 18)) {
                    g.drawString(part, ix + 17, iy);
                    iy += line;
                }
            }
        }

        // the footer
        g.setColor(t.line);
        g.drawLine(PAD, h - PAD - 22, w - PAD, h - PAD - 22);
        g.setFont(t.base(11.5f));
        g.setColor(t.faint);
        g.drawString(s.instance, PAD, h - PAD - 4);
        g.setFont(t.strong(11.5f));
        g.setColor(t.title);
        String mark = "SONAR";
        g.drawString(mark, w - PAD - g.getFontMetrics().stringWidth(mark), h - PAD - 4);

        t.overlay(g, w, h);
        g.dispose();
        return img;
    }
}
