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
        String[][] who = {{"Jacke","Samurai"},{"Disconiw","Knight"},{"Arthritiss","Archer"},
                {"Nyjacky","Wizard"},{"Epicobil","Necromancer"},{"Winnieeeee","Huntress"},
                {"Nihao","Warrior"},{"XImagiinex","Rogue"}};
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
        BufferedImage img = ResultsImage.renderByBoss(m);
        ImageIO.write(img, "png", new File(a[0]));
        System.out.println("wrote " + a[0] + "  " + img.getWidth() + "x" + img.getHeight());
    }
}
