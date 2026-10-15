package sonar.ui;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/** Renders a few enemies' own sprites to PNGs, for mocking a selector up against real art. */
public final class EnemyIcons {

    private static final String[] WANTED = {
            "shtrs The Forgotten King", "shtrs Twilight Archmage", "shtrs Bridge Sentinel",
            "Shatters A22", "Shatters A13", "Shatters Stone Idol", "Shatters Void Phantasm",
            "shtrs Inferno", "shtrs Blizzard", "Stone Judoka", "Shatters Royal Jester",
            "Oryx the Mad God 3", "Archbishop Leucoryx", "Treasurer Gemsbok"};

    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        SonarTheme.install();
        sonar.assets.IdToAsset.reloadAssets();
        Map<String, Integer> byName = new HashMap<>();
        for (int id = 0; id < 0x10000; id++) {
            String n = sonar.assets.IdToAsset.getObjectIdName(id);
            if (n != null && !n.isEmpty()) byName.putIfAbsent(n, id);
        }
        System.out.println("indexed " + byName.size() + " object names");
        File out = new File(args[0]);
        out.mkdirs();
        int size = 96;
        for (String name : WANTED) {
            Integer id = byName.get(name);
            if (id == null) { System.out.println("no id for " + name); continue; }
            BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            boolean drew = Glyph.object(g, id, 0, 0, size);
            g.dispose();
            if (!drew) { System.out.println("no sprite for " + name + " (id " + id + ")"); continue; }
            String file = name.replaceAll("[^A-Za-z0-9]+", "-").toLowerCase() + ".png";
            ImageIO.write(img, "png", new File(out, file));
            System.out.println("wrote " + file + "  id=" + id);
        }
    }
}
