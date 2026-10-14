package sonar.ui;

import java.awt.image.BufferedImage;

/**
 * A full Oryx's Sanctuary at the player cap: the Archbishop and Oryx 3, with
 * the party thinning between them the way it really does.
 */
public class OryxRun {
    private static final String[] CLASSES = {
            "Rogue", "Wizard", "Knight", "Archer", "Paladin", "Necromancer", "Samurai", "Ninja",
            "Huntress", "Mystic", "Warrior", "Priest", "Sorcerer", "Trickster", "Bard", "Summoner"};

    public static void main(String[] a) throws Exception {
        int players = Integer.parseInt(a[1]);
        LiveModel m = new LiveModel();
        m.instance = "Oryx's Sanctuary";
        m.realm = "USEast";
        m.elapsedMs = 11 * 60_000 + 20_000;
        m.tracked = true;
        m.players = players;
        m.maxPlayers = 85;
        m.deaths = 23;
        m.hasPlayer = true;
        m.myName = "Geksa";

        // name, id, miniboss, health, first hit, last hit, share of the party present
        Object[][] fights = {
                {"Archbishop Leucoryx", 0x19de, true, 1_850_000L, 42_000L, 104_000L, 1.00},
                {"Oryx the Mad God 3", 0xb133, false, 10_100_000L, 232_000L, 373_000L, 0.81},
        };
        java.util.Random random = new java.util.Random(7);
        for (Object[] f : fights) {
            LiveModel.Target t = new LiveModel.Target();
            t.name = (String) f[0];
            t.objectType = (Integer) f[1];
            t.miniboss = (Boolean) f[2];
            t.boss = true;
            t.segment = true;
            t.maxHp = (int) Math.min(Integer.MAX_VALUE, (Long) f[3]);
            t.healthLost = (Long) f[3];
            t.firstHit = (Long) f[4];
            t.lastHit = (Long) f[5];
            t.fightMs = t.lastHit - t.firstHit;
            t.finalBoss = t.name.startsWith("Oryx the Mad God");
            int here = Math.max(1, (int) Math.round(players * (Double) f[6]));
            for (int i = 0; i < here; i++) {
                LiveModel.Row r = new LiveModel.Row();
                r.id = i + 1;
                r.name = NAMES[i % NAMES.length] + (i >= NAMES.length ? String.valueOf(i / NAMES.length + 1) : "");
                r.cls = CLASSES[i % CLASSES.length];
                r.skin = 768 + i % 19;
                r.dyeMain = i % 3 == 0 ? 0x01f0f8ff : 0;
                double share = Math.pow(0.93, i) * (0.75 + random.nextDouble() * 0.5);
                r.damage = Math.max(700, Math.round(((Long) f[3]) * 0.09 * share));
                t.rows.add(r);
                t.total += r.damage;
            }
            t.rows.sort((p, q) -> Long.compare(q.damage, p.damage));
            m.targets.add(t);
            m.stages.add(t);
            for (LiveModel.Row r : t.rows) {
                m.whole.rows.add(r);
                m.whole.total += r.damage;
            }
        }
        // the run also chewed through a sanctuary full of trash
        m.whole.total = m.whole.total * 160 / 100;
        LiveModel.buildGroupsForDemo(m);
        BufferedImage img = ResultsImage.renderByBoss(m);
        javax.imageio.ImageIO.write(img, "png", new java.io.File(a[0]));
        System.out.println(players + " players -> " + img.getWidth() + "x" + img.getHeight()
                + "   ratio " + String.format("%.2f", img.getWidth() / (double) img.getHeight()));
        System.exit(0);
    }

    private static final String[] NAMES = {
            "Zephyra", "Cooosplay", "ZBlab", "VisualBug", "Mistral", "MrMegaMix", "Jox", "Zendelski",
            "Shivaloma", "Kozhler", "RazerTo", "Perturbed", "ChiiLlll", "Fdsfsfa", "Kyoto", "Badadvice",
            "WiZardhekk", "Lordeath", "Ikeno", "ScarletRose", "DarkMw", "DeadGreg", "PootPigeon",
            "Majesty", "Trashboytt", "BurnsBoi", "Kadenkad", "KJB", "Miaushan", "Keljhan", "Jacke",
            "WryChaos", "KillaWag", "IHC", "Scrupter", "Soulthz", "Slumppp", "JuiceMePls", "XinW",
            "Dayklab", "Software", "CoalNecro", "Asstaroth", "oxy", "Vorkath", "AstroFP"};
}
