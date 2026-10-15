package sonar.ui;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/** The by-boss results image, with long names on short bars - the case that broke. */
public final class ResultsShot {
    public static void main(String[] a) throws Exception {
        System.setProperty("java.awt.headless", "true");
        SonarTheme.install();
        sonar.assets.IdToAsset.reloadAssets();
        LiveModel m = new LiveModel();
        m.instance = "Lost Halls";
        m.tracked = true;
        String[][] who = {{"Username1","Samurai"},{"Username2","Knight"},{"Username3","Archer"},
                {"Username4","Wizard"},{"Username5","Necromancer"},{"Username6","Huntress"},
                {"Username7","Warrior"},{"Username8","Rogue"}};
        long[] dmg = {1_149_000, 390_000, 345_000, 300_000, 255_000, 210_000, 175_000, 140_000};
        LiveModel.Target t = new LiveModel.Target();
        t.name = "Marble Colossus";
        t.segment = true;
        t.finalBoss = true;
        for (int i = 0; i < who.length; i++) {
            LiveModel.Row r = new LiveModel.Row();
            r.id = i + 1; r.name = who[i][0]; r.cls = who[i][1]; r.damage = dmg[i]; r.hits = 50;
            int[] kit = LiveDemo.kitFor(r.cls);
            for (int k = 0; k < 4; k++) { r.gearIds[k] = kit[k]; r.loadout[k] = LiveModel.itemName(kit[k]); }
            r.skin = LiveDemo.skinFor(r.cls, i);
            int[] dy = LiveDemo.dyesFor(i); r.dyeMain = dy[0]; r.dyeAccessory = dy[1];
            t.rows.add(r);
            t.total += r.damage;
        }
        m.targets.add(t);
        LiveModel.Group grp = new LiveModel.Group();
        grp.kind = "boss"; grp.name = t.name; grp.enemies = 1; grp.target = t;
        m.groups.add(grp);
        m.whole.rows.addAll(t.rows);
        m.whole.total = t.total;
        // two who did not see the end, so the tags beside their names can be looked at
        m.completionKnown = true;
        m.whole.rows.get(2).completed = false;
        m.whole.rows.get(2).leftAtMs = 150_000;
        m.whole.rows.get(5).completed = false;
        m.whole.rows.get(5).leftAtMs = 282_000;
        m.whole.rows.get(5).died = true;
        // the full roster view, which is the one carrying the NEXUS and DIED tags beside names
        BufferedImage img = a.length > 1 && "boss".equals(a[1])
                ? ResultsImage.renderByBoss(m) : ResultsImage.render(m);
        ImageIO.write(img, "png", new File(a[0]));
        System.out.println("wrote " + a[0] + "  " + img.getWidth() + "x" + img.getHeight());
    }
}
