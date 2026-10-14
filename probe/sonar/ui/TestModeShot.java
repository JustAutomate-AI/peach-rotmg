package sonar.ui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

/** The overlay in portal-test mode: static portals plus moving enemies. */
public class TestModeShot {
    public static void main(String[] a) throws Exception {
        Field px = ShattersRadar.class.getDeclaredField("playerX"), py = ShattersRadar.class.getDeclaredField("playerY");
        px.setAccessible(true); py.setAccessible(true);
        px.setFloat(null, 100f); py.setFloat(null, 100f);

        Constructor<ShattersRadar.Mark> c = ShattersRadar.Mark.class.getDeclaredConstructor(int.class, float.class, float.class);
        c.setAccessible(true);
        List<ShattersRadar.Mark> list = new ArrayList<>();
        // four portals in a ring, the way a realm's dungeon portals sit apart
        for (int i = 0; i < 4; i++) {
            double ang = Math.PI * 2 * i / 4 + 0.4;
            list.add(c.newInstance(ShattersRadar.PORTAL, (float) (100 + Math.cos(ang) * 95), (float) (100 + Math.sin(ang) * 95)));
        }
        // and a scatter of nearby enemies
        for (int i = 0; i < 7; i++) {
            double ang = Math.PI * 2 * i / 7;
            double r = 32 + (i % 3) * 22;
            list.add(c.newInstance(ShattersRadar.MOVER, (float) (100 + Math.cos(ang) * r), (float) (100 + Math.sin(ang) * r)));
        }
        Field mk = ShattersRadar.class.getDeclaredField("marks");
        mk.setAccessible(true);
        mk.set(null, list);
        ShattersRadar.setPortalTest(true);

        int w = 300, h = 300;
        ShattersOverlayWindow win = new ShattersOverlayWindow();
        win.setSize(w, h);
        win.setCalibrating(Boolean.parseBoolean(a[1]));
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(0x0b1020));
        g.fillRect(0, 0, w, h);
        Method m = ShattersOverlayWindow.class.getDeclaredMethod("paintOverlay", Graphics2D.class);
        m.setAccessible(true);
        m.invoke(win, g.create());
        g.dispose();
        javax.imageio.ImageIO.write(img, "png", new java.io.File(a[0]));
        System.out.println(list.size() + " test marks drawn");
        System.exit(0);
    }
}
