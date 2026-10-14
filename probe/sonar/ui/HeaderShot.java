package sonar.ui;

public class HeaderShot {
    public static void main(String[] a) throws Exception {
        LiveModel m = new LiveModel();
        m.instance = "Plagued Nest";
        m.realm = "Nexus";
        m.players = 41;
        m.elapsedMs = 163_000;
        LiveModel.Target boss = new LiveModel.Target();
        boss.name = "Killer Bee Queen";
        boss.objectType = 0x0d7c;
        boss.boss = true;
        boss.segment = true;
        boss.finalBoss = true;
        boss.maxHp = 3_160_733;
        boss.healthLost = 3_160_733;
        boss.firstHit = 1_000;
        boss.lastHit = 60_000;
        boss.fightMs = 59_000;
        String[] names = {"AnActualHorn", "Ryan", "RyanEdmond", "Cartoonnub", "Eth"};
        long[] dmg = {235_797, 232_997, 209_563, 184_405, 163_407};
        for (int i = 0; i < names.length; i++) {
            LiveModel.Row r = new LiveModel.Row();
            r.id = i + 1;
            r.name = names[i];
            r.cls = "Rogue";
            r.skin = 768;
            r.damage = dmg[i];
            boss.rows.add(r);
            boss.total += r.damage;
            m.whole.rows.add(r);
        }
        m.targets.add(boss);
        m.stages.add(boss);
        m.whole.total = 9_041_448;   // the run, trash and all
        LiveModel.buildGroupsForDemo(m);
        java.awt.image.BufferedImage img = ResultsImage.renderByBoss(m);
        javax.imageio.ImageIO.write(img.getSubimage(0, 0, Math.min(460, img.getWidth()), 110), "png", new java.io.File(a[0]));
        System.out.println("boss total " + boss.total + "  run total " + m.whole.total);
        System.exit(0);
    }
}
