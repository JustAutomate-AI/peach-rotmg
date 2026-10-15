package sonar.ui;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/** Can Sonar draw a pet at all? Tries every pet skin the game defines. */
public final class PetProbe {
    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        SonarTheme.install();
        sonar.assets.IdToAsset.reloadAssets();

        String xml = new String(java.nio.file.Files.readAllBytes(
                sonar.DataDir.file("assets/xml/pets.xml").toPath()), java.nio.charset.StandardCharsets.UTF_8);
        java.util.regex.Matcher m = java.util.regex.Pattern.compile(
                "<Object type=\"0x([0-9a-fA-F]+)\" id=\"([^\"]+)\">((?:(?!</Object>).)*?)</Object>",
                java.util.regex.Pattern.DOTALL).matcher(xml);
        List<int[]> ids = new ArrayList<>();
        List<String> names = new ArrayList<>();
        while (m.find()) {
            if (!m.group(3).contains("<Class>PetSkin</Class>")) continue;
            ids.add(new int[]{Integer.parseInt(m.group(1), 16)});
            java.util.regex.Matcher d = java.util.regex.Pattern.compile("<DisplayId>([^<]+)</DisplayId>").matcher(m.group(3));
            names.add(d.find() ? d.group(1) : m.group(2));
        }
        System.out.println("pet skins defined: " + ids.size());

        int cell = 56, cols = 26;
        int rows = (ids.size() + cols - 1) / cols;
        BufferedImage img = new BufferedImage(cols * cell, Math.max(1, rows) * cell, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = SonarTheme.quality(img.createGraphics());
        g.setColor(SonarTheme.BG);
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        int drew = 0;
        for (int i = 0; i < ids.size(); i++) {
            int x = (i % cols) * cell, y = (i / cols) * cell;
            if (Glyph.object(g, ids.get(i)[0], x + 8, y + 8, 40)) drew++;
        }
        g.dispose();
        ImageIO.write(img, "png", new File(args.length > 0 ? args[0] : "pets.png"));
        System.out.println("drew " + drew + " of " + ids.size() + " through Glyph.object");
        System.out.println("first few: " + names.subList(0, Math.min(6, names.size())));
    }
}
