package sonar.ui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;

/** The Characters page with one character lost, to see how it reads. */
public class LostShot {
    public static void main(String[] a) throws Exception {
        LiveModel m = new LiveModel();
        String[][] who = {{"Wizard", "291", "20", "0"}, {"Bard", "2950", "20", "0"},
                {"Archer", "2129", "20", "0"}, {"Rogue", "3570", "20", "lost"}};
        for (int i = 0; i < who.length; i++) {
            LiveModel.CharSummary c = new LiveModel.CharSummary();
            c.id = i + 1;
            c.className = who[i][0];
            c.fame = Long.parseLong(who[i][1]);
            c.level = Integer.parseInt(who[i][2]);
            c.seasonal = i % 2 == 0;
            c.current = i == 0;
            c.petName = "Great Tiger";
            c.skin = sonar.game.enums.CharacterClass.idByName(who[i][0]);
            if ("lost".equals(who[i][3])) c.lost = System.currentTimeMillis() - 2 * 3600_000L;
            for (int s = 0; s < 8; s++) {
                c.maxStat[s] = 100;
                c.stat[s] = s < 5 ? 100 : 80;
            }
            m.chars.add(c);
        }
        CharactersPane pane = new CharactersPane();
        pane.show(m);
        int w = 900, h = 330;
        pane.setSize(w, h);
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = SonarTheme.quality(img.createGraphics());
        g.setColor(SonarTheme.BG);
        g.fillRect(0, 0, w, h);
        Method p = Pane.class.getDeclaredMethod("paintContent", Graphics2D.class, int.class);
        p.setAccessible(true);
        p.invoke(pane, g, w - 20);
        g.dispose();
        javax.imageio.ImageIO.write(img, "png", new java.io.File(a[0]));
        System.out.println("drew " + m.chars.size() + " characters");
        System.exit(0);
    }
}
