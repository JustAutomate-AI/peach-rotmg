package sonar.ui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

/** Renders the overlay with one mark of every kind, to see how the minimap would read. */
public class OverlayShot {
    public static void main(String[] a) throws Exception {
        int w = 260, h = 260;
        int[] ids = ShattersRadar.watchedForTest();

        // plant the player and a spread of marks around them, the way a first room would look
        Field px = ShattersRadar.class.getDeclaredField("playerX"), py = ShattersRadar.class.getDeclaredField("playerY");
        px.setAccessible(true); py.setAccessible(true);
        px.setFloat(null, 100f); py.setFloat(null, 100f);
        List<ShattersRadar.Mark> list = new ArrayList<>();
        Constructor<ShattersRadar.Mark> c = ShattersRadar.Mark.class.getDeclaredConstructor(int.class, float.class, float.class);
        c.setAccessible(true);
        for (int i = 0; i < ids.length; i++) {
            double ang = Math.PI * 2 * i / ids.length;
            double r = ShattersRadar.tier(ids[i]) == ShattersRadar.OBJECTIVE ? 90 : 150;
            list.add(c.newInstance(ids[i], (float) (100 + Math.cos(ang) * r), (float) (100 + Math.sin(ang) * r)));
        }
        list.sort(java.util.Comparator.comparingInt(m -> -ShattersRadar.tier(m.type)));
        Field mk = ShattersRadar.class.getDeclaredField("marks");
        mk.setAccessible(true);
        mk.set(null, list);

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
        System.out.println(list.size() + " marks drawn");
        System.exit(0);
    }
}
