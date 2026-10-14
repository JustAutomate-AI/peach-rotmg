package sonar.ui;

public class TotalProbe {
    static java.awt.image.BufferedImage[] made = new java.awt.image.BufferedImage[2];
    static int n = 0;

    public static void main(String[] x) throws Exception {
        for (long d : new long[]{400_000, 500_000}) {
            LiveModel m = new LiveModel();
            m.instance = "Plagued Nest";
            LiveModel.Target boss = new LiveModel.Target();
            boss.name = "Killer Bee Queen";
            boss.boss = true; boss.segment = true; boss.finalBoss = true;
            boss.firstHit = 1_000; boss.lastHit = 60_000; boss.fightMs = 59_000;
            boss.maxHp = 3_000_000; boss.healthLost = 3_000_000;
            LiveModel.Row r = new LiveModel.Row();
            r.name = "Geksa"; r.cls = "Rogue"; r.damage = d;
            boss.rows.add(r); boss.total = d;
            m.targets.add(boss); m.stages.add(boss);
            m.whole.rows.add(r); m.whole.total = 9_000_000;
            LiveModel.buildGroupsForDemo(m);
            java.awt.image.BufferedImage img = ResultsImage.renderByBoss(m);
            long sum = 0;
            for (LiveModel.Target t : m.stages) sum += t.total;
            made[n++] = img;
            System.out.println("row=" + d + " stageTotal=" + sum + " img=" + img.getWidth() + "x" + img.getHeight());
        }
        int diff = 0;
        for (int y = 0; y < made[0].getHeight(); y++)
            for (int xx = 0; xx < made[0].getWidth(); xx++)
                if (made[0].getRGB(xx, y) != made[1].getRGB(xx, y)) diff++;
        System.out.println("pixels differing: " + diff);
        System.exit(0);
    }
}
