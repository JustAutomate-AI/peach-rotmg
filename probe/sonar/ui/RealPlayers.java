package sonar.ui;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Three things Sonar does to a player, checked against players it really saw.
 * <p>
 * Each of these paths ends up on the card people share, and each was until now only
 * ever exercised against demo characters whose skins, gear and enchants were chosen by
 * whoever wrote the demo - which is close to marking your own homework. These run
 * against a census taken off a live server instead.
 * <ul>
 *   <li>Every look drawn: skin and both dyes through the same call the roster makes.
 *       This is the path that once drew a grey box on every character in the app.</li>
 *   <li>Every item named. A blank here is a blank slot on a shared card.</li>
 *   <li>Every enchant decoded and matched to the game's own icon. This one fails
 *       quietly - a code it cannot read simply shows as no enchants at all - so
 *       nothing but counting will show it.</li>
 * </ul>
 *
 *   javac -encoding UTF-8 -cp run/Sonar.jar -d probe probe/sonar/ui/RealPlayers.java
 *   cd run
 *   java -cp "Sonar.jar;../probe" sonar.ui.RealPlayers ../census3.csv [sheet.png]
 */
public final class RealPlayers {

    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        SonarTheme.install();
        sonar.assets.IdToAsset.reloadAssets();

        File csv = new File(args.length > 0 ? args[0] : "census3.csv");
        if (!csv.isFile()) {
            System.out.println("no census at " + csv.getAbsolutePath());
            System.exit(2);
        }

        List<String[]> rows = read(csv);
        if (rows.isEmpty()) {
            System.out.println("census is empty");
            System.exit(2);
        }
        Map<String, Integer> col = new LinkedHashMap<>();
        String[] head = rows.get(0);
        for (int i = 0; i < head.length; i++) col.put(head[i].trim(), i);
        for (String needed : new String[]{"skin", "dye1", "dye2", "gear0", "ench0"}) {
            if (!col.containsKey(needed)) {
                System.out.println("this census has no '" + needed + "' column - collect with the newer build");
                System.exit(2);
            }
        }

        Set<String> looks = new LinkedHashSet<>();
        Set<Integer> items = new LinkedHashSet<>();
        Set<String> codes = new LinkedHashSet<>();
        for (int r = 1; r < rows.size(); r++) {
            String[] f = rows.get(r);
            if (f.length < head.length) continue;
            looks.add(f[col.get("skin")] + ":" + f[col.get("dye1")] + ":" + f[col.get("dye2")]);
            for (int i = 0; i < 4; i++) {
                int id = num(f[col.get("gear" + i)]);
                if (id > 0) items.add(id);
                String code = f[col.get("ench" + i)];
                if (code != null && !code.isEmpty()) codes.add(code);
            }
        }

        System.out.println("census: " + (rows.size() - 1) + " readings");
        System.out.println("  distinct looks   : " + looks.size());
        System.out.println("  distinct items   : " + items.size());
        System.out.println("  distinct enchants: " + codes.size());
        System.out.println();

        int failed = 0;
        failed += checkLooks(looks, args.length > 1 ? new File(args[1]) : null);
        failed += checkItems(items);
        failed += checkEnchants(codes);

        System.out.println();
        System.out.println(failed == 0 ? "everything the census saw is drawn, named and decoded"
                : failed + " problems found");
        System.exit(failed == 0 ? 0 : 1);
    }

    /** Draws every look the census saw, and says how many came back as nothing. */
    private static int checkLooks(Set<String> looks, File sheet) throws Exception {
        int cell = 64, cols = 24;
        int rows = (looks.size() + cols - 1) / cols;
        BufferedImage img = new BufferedImage(cols * cell, Math.max(1, rows) * cell, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = SonarTheme.quality(img.createGraphics());
        g.setColor(SonarTheme.BG);
        g.fillRect(0, 0, img.getWidth(), img.getHeight());

        int drew = 0, blank = 0, i = 0;
        List<String> missing = new ArrayList<>();
        for (String look : looks) {
            String[] p = look.split(":");
            int skin = num(p[0]), main = num(p[1]), accessory = num(p[2]);
            int x = (i % cols) * cell, y = (i / cols) * cell;
            if (Glyph.player(g, skin, main, accessory, x + 8, y + 8, 48)) {
                drew++;
            } else {
                blank++;
                if (missing.size() < 12) missing.add(look);
                g.setColor(SonarTheme.DANGER);
                g.drawRect(x + 8, y + 8, 48, 48);
            }
            i++;
        }
        g.dispose();
        if (sheet != null) ImageIO.write(img, "png", sheet);

        System.out.println("LOOKS   " + drew + " of " + looks.size() + " drew"
                + (blank == 0 ? "" : "   " + blank + " DID NOT"));
        for (String m : missing) System.out.println("          did not draw: skin:dye:accessory = " + m);
        if (sheet != null) System.out.println("          sheet written to " + sheet.getAbsolutePath());
        return blank;
    }

    /** Names every item the census saw. */
    private static int checkItems(Set<Integer> items) {
        int named = 0;
        List<Integer> blank = new ArrayList<>();
        for (int id : items) {
            String name = LiveModel.itemName(id);
            if (name != null && !name.isEmpty()) named++;
            else blank.add(id);
        }
        System.out.println("ITEMS   " + named + " of " + items.size() + " named"
                + (blank.isEmpty() ? "" : "   " + blank.size() + " BLANK"));
        for (int i = 0; i < Math.min(12, blank.size()); i++) System.out.println("          no name for id " + blank.get(i));
        return blank.size();
    }

    /**
     * Decodes every enchant the census saw and looks each one up in the game's icon table.
     * A code that will not decode is counted apart from an enchant that decodes but has no
     * icon: the first is Sonar failing to read the game, the second is the game having an
     * enchant Sonar's icon table does not cover, and they want different fixes.
     */
    private static int checkEnchants(Set<String> codes) {
        int decoded = 0, undecodable = 0, enchants = 0, noIcon = 0;
        List<String> bad = new ArrayList<>();
        Set<String> iconless = new LinkedHashSet<>();
        for (String code : codes) {
            List<String> names = new ArrayList<>();
            try {
                for (Short id : sonar.game.ParseEnchants.extractEnchantIds(code)) {
                    String name = sonar.game.ParseEnchants.ENCHANTS.get(id);
                    if (name != null && !name.isEmpty()) names.add(name);
                }
                decoded++;
            } catch (RuntimeException e) {
                undecodable++;
                if (bad.size() < 8) bad.add(code);
                continue;
            }
            for (String name : names) {
                enchants++;
                if (EnchantSprites.indexOf(name) < 0) {
                    noIcon++;
                    iconless.add(name);
                }
            }
        }
        System.out.println("ENCHANT " + decoded + " of " + codes.size() + " codes decoded"
                + (undecodable == 0 ? "" : "   " + undecodable + " UNREADABLE"));
        for (String b : bad) System.out.println("          could not read: " + b);
        System.out.println("        " + (enchants - noIcon) + " of " + enchants + " enchants have the game's own icon"
                + (noIcon == 0 ? "" : "   " + noIcon + " WITHOUT"));
        int shown = 0;
        for (String n : iconless) {
            System.out.println("          no icon for: " + n);
            if (++shown >= 10) break;
        }
        return undecodable + iconless.size();
    }

    private static int num(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    /** A small CSV reader: the census quotes only where it has to. */
    private static List<String[]> read(File f) throws Exception {
        List<String[]> out = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
            for (String line; (line = r.readLine()) != null; ) {
                if (line.isEmpty()) continue;
                List<String> cells = new ArrayList<>();
                StringBuilder cur = new StringBuilder();
                boolean quoted = false;
                for (int i = 0; i < line.length(); i++) {
                    char c = line.charAt(i);
                    if (quoted) {
                        if (c == '"' && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                            cur.append('"');
                            i++;
                        } else if (c == '"') {
                            quoted = false;
                        } else {
                            cur.append(c);
                        }
                    } else if (c == '"') {
                        quoted = true;
                    } else if (c == ',') {
                        cells.add(cur.toString());
                        cur.setLength(0);
                    } else {
                        cur.append(c);
                    }
                }
                cells.add(cur.toString());
                out.add(cells.toArray(new String[0]));
            }
        }
        return out;
    }
}
