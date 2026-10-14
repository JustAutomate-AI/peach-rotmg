package sonar.ui;

import java.lang.reflect.Field;

/** Checks the view clamp behaves the way the minimap does on big and small maps. */
public class ClampProbe {
    static void place(float x, float y, int mw, int mh) throws Exception {
        for (String[] f : new String[][]{{"playerX", "" + x}, {"playerY", "" + y}}) {
            Field fl = ShattersRadar.class.getDeclaredField(f[0]);
            fl.setAccessible(true);
            fl.setFloat(null, Float.parseFloat(f[1]));
        }
        Field w = ShattersRadar.class.getDeclaredField("mapWidth");
        Field h = ShattersRadar.class.getDeclaredField("mapHeight");
        w.setAccessible(true); h.setAccessible(true);
        w.setInt(null, mw); h.setInt(null, mh);
    }

    public static void main(String[] a) throws Exception {
        double view = 120;   // tiles across the minimap

        // a realm: thousands of tiles, player nowhere near an edge -> view sits on the player
        place(1024, 1024, 2048, 2048);
        double[] c = ShattersRadar.viewCentre(view, view);
        double[] me = ShattersRadar.playerFromView(view, view);
        System.out.printf("realm, mid-map   view (%.0f,%.0f)  you are (%+.0f,%+.0f) from centre%n", c[0], c[1], me[0], me[1]);

        // the same realm, hard against the west edge -> the view stops, you move left of centre
        place(20, 1024, 2048, 2048);
        c = ShattersRadar.viewCentre(view, view);
        me = ShattersRadar.playerFromView(view, view);
        System.out.printf("realm, at edge   view (%.0f,%.0f)  you are (%+.0f,%+.0f) from centre%n", c[0], c[1], me[0], me[1]);

        // a small map: the whole thing fits, so the view never moves and you roam inside it
        place(30, 95, 140, 140);
        c = ShattersRadar.viewCentre(view, view);
        me = ShattersRadar.playerFromView(view, view);
        System.out.printf("small map        view (%.0f,%.0f)  you are (%+.0f,%+.0f) from centre%n", c[0], c[1], me[0], me[1]);
        System.out.printf("   -> at 3.99 px/tile that draws you %+.0f,%+.0f px off the middle%n",
                me[0] * 3.99, me[1] * 3.99);
        System.exit(0);
    }
}
