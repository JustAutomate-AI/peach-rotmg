package sonar.ui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.*;

/** Which classes and skins Sonar can actually draw, and which come out blank. */
public class SkinProbe {
    public static void main(String[] a) throws Exception {
        // every class, by the object type the game gives a player of it
        Map<Integer, String> names = new LinkedHashMap<>();
        String xml = new String(java.nio.file.Files.readAllBytes(
                java.nio.file.Paths.get("assets/xml/players.xml")), java.nio.charset.StandardCharsets.UTF_8);
        java.util.regex.Matcher m = java.util.regex.Pattern
                .compile("<Object\s+type=\"0x([0-9a-fA-F]+)\"\s+id=\"([^\"]*)\"").matcher(xml);
        while (m.find()) names.put(Integer.parseInt(m.group(1), 16), m.group(2));
        int okC = 0, badC = 0;
        List<String> bad = new ArrayList<>();
        for (Map.Entry<Integer, String> e : names.entrySet()) {
            if (draws(e.getKey())) okC++;
            else { badC++; bad.add(e.getValue() + " (" + Integer.toHexString(e.getKey()) + ")"); }
        }
        System.out.println("classes drawable: " + okC + "  blank: " + badC);
        for (String s : bad) System.out.println("   BLANK class " + s);

        int okS = 0, badS = 0;
        List<String> badSkins = new ArrayList<>();
        Set<Integer> seen = new HashSet<>();
        for (Integer c : names.keySet()) {
            for (Wardrobe.Skin s : Wardrobe.skins(c)) {
                if (!seen.add(s.id)) continue;
                if (draws(s.id)) okS++;
                else { badS++; if (badSkins.size() < 25) badSkins.add(s.name + " (" + Integer.toHexString(s.id) + ")"); }
            }
        }
        System.out.println("skins drawable: " + okS + "  blank: " + badS);
        for (String s : badSkins) System.out.println("   BLANK skin " + s);
        System.exit(0);
    }

    static boolean draws(int skin) {
        BufferedImage img = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        boolean ok;
        try { ok = Glyph.player(g, skin, 0, 0, 4, 4, 32); } catch (Throwable t) { ok = false; }
        g.dispose();
        return ok;
    }
}
