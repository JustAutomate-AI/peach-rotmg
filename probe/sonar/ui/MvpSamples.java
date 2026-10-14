package sonar.ui;

import javax.imageio.ImageIO;
import java.io.File;

/**
 * Renders the MVP card across the cases it has to survive, so the layout can be
 * judged without waiting for a run that happens to produce each one.
 *
 *   javac -cp run/Sonar.jar -d probe probe/sonar/ui/MvpSamples.java
 *   cd run
 *   java -cp "Sonar.jar;../probe" sonar.ui.MvpSamples <folder>
 */
public final class MvpSamples {

    static final String NL = String.valueOf((char) 10);

    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        SonarTheme.install();
        // the character sprites need the object list loaded, which the app does at startup
        sonar.assets.IdToAsset.reloadAssets();
        File dir = new File(args.length > 0 ? args[0] : ".");
        dir.mkdirs();

        // 1. the ordinary case: a final boss, a fully enchanted kit
        LiveModel a = run("Oryx's Sanctuary");
        LiveModel.Target oryx = boss(a, "Oryx the Mad God 3", true, 10_100_000, 141_000);
        add(oryx, player(1, "Zephyra", "Samurai", 1_149_000, 42_300, 812,
                new String[]{"Damage Bonus IV" + NL + "FireRate Bonus II", "Attack Bonus I", "Defense Bonus II", "Dexterity Bonus II"}));
        add(oryx, player(2, "Cooosplay", "Knight", 492_000, 18_100, 410, blank()));
        LiveModel.Target bishop = boss(a, "Archbishop Leucoryx", false, 1_200_000, 48_000);
        add(bishop, player(1, "Zephyra", "Samurai", 610_000, 21_000, 400, blank()));
        finish(a);
        write(dir, "1-standard.png", a);

        // 2. no enchants on anything - the common case for a newer player
        LiveModel b = run("The Shatters");
        LiveModel.Target king = boss(b, "The Forgotten King", true, 1_400_000, 96_000);
        add(king, player(1, "Grumbles", "Warrior", 388_400, 9_120, 622, blank()));
        finish(b);
        write(dir, "2-no-enchants.png", b);

        // 3. a long name and long item names, which is where a layout usually breaks
        LiveModel c = run("Lost Halls");
        LiveModel.Target marble = boss(c, "Marble Colossus", true, 2_000_000, 120_000);
        add(marble, player(1, "xXLongestNameAllowedXx", "Necromancer", 1_902_775, 55_400, 1_204,
                new String[]{"Damage Bonus IV" + NL + "FireRate Bonus II" + NL + "Percentage Life Regeneration III",
                        "Percentage Mana Regeneration III" + NL + "Wisdom Bonus II", "Defense Bonus III", "Attack Bonus IV"}));
        finish(c);
        write(dir, "3-long-names.png", c);

        // 4. a run with no boss at all - the fallback, which has to say so
        LiveModel d = run("Realm of the Mad God");
        add(d.whole, player(1, "Pistacio", "Huntress", 74_300, 3_100, 212, blank()));
        d.tracked = true;
        write(dir, "4-no-boss.png", d);

        // 5. a close one - the final boss decides it, not the run total
        LiveModel e = run("Cultist Hideout");
        LiveModel.Target mini = boss(e, "Malus", false, 600_000, 40_000);
        LiveModel.Target last = boss(e, "The Voice", true, 1_000_000, 90_000);
        add(mini, player(1, "Carry", "Paladin", 520_000, 14_000, 500, blank()));
        add(mini, player(2, "Burst", "Wizard", 60_000, 9_000, 90, blank()));
        add(last, player(1, "Carry", "Paladin", 300_000, 12_000, 320, blank()));
        add(last, player(2, "Burst", "Wizard", 640_000, 48_900, 410,
                new String[]{"Damage Bonus IV", "", "", "Attack Bonus II"}));
        finish(e);
        write(dir, "5-final-boss-decides.png", e);

        // 6. the Shatters: three main bosses, the middle one missed entirely
        LiveModel f = run("The Shatters");
        LiveModel.Target sentinel = boss(f, "The Bridge Sentinel", false, 250_000, 61_000);
        sentinel.objectType = sprite(0x714b, "The Bridge Sentinel");
        LiveModel.Target archmage = boss(f, "Twilight Archmage", false, 300_000, 74_000);
        archmage.objectType = sprite(0x715d, "Twilight Archmage");
        LiveModel.Target fk = boss(f, "The Forgotten King", true, 500_000, 128_000);
        fk.objectType = sprite(0x716f, "The Forgotten King");
        add(sentinel, player(1, "Zephyra", "Samurai", 96_000, 7_100, 210,
                new String[]{"Damage Bonus IV", "", "", ""}));
        add(sentinel, player(2, "Cooosplay", "Knight", 141_000, 9_200, 260, blank()));
        add(fk, player(1, "Zephyra", "Samurai", 232_400, 18_900, 420,
                new String[]{"Damage Bonus IV" + NL + "FireRate Bonus II", "Attack Bonus I", "Defense Bonus II", "Dexterity Bonus II"}));
        add(fk, player(2, "Cooosplay", "Knight", 150_000, 11_000, 310, blank()));
        finish(f);
        write(dir, "6-shatters-three-bosses.png", f);

        System.out.println("done");
    }

    /**
     * An object id, checked against what the game says it is before it is used.
     * <p>
     * The three Shatters ids here were typed from memory the first time and were a
     * Quest Chest, Septavius the Ghost God and a Lair Skeleton Mage - so the card
     * showed three confidently wrong sprites and nothing complained. An id is only
     * ever a number; this makes it say its own name out loud.
     */
    private static int sprite(int id, String expected) {
        String actual = sonar.assets.IdToAsset.objectName(id);
        if (actual == null || !actual.equals(expected)) {
            throw new IllegalStateException("0x" + Integer.toHexString(id) + " is \"" + actual
                    + "\", not \"" + expected + "\"");
        }
        return id;
    }

    private static String[] blank() {
        return new String[]{"", "", "", ""};
    }

    private static LiveModel run(String name) {
        LiveModel m = new LiveModel();
        m.instance = name;
        m.tracked = true;
        return m;
    }

    private static LiveModel.Target boss(LiveModel m, String name, boolean last, int maxHp, long fightMs) {
        LiveModel.Target t = new LiveModel.Target();
        t.name = name;
        t.boss = true;
        t.finalBoss = last;
        t.maxHp = maxHp;
        t.healthLost = maxHp;
        t.fightMs = fightMs;
        m.targets.add(t);
        return t;
    }

    private static void add(LiveModel.Target t, LiveModel.Row r) {
        t.rows.add(r);
    }

    private static void finish(LiveModel m) {
        for (LiveModel.Target t : m.targets) {
            for (LiveModel.Row r : t.rows) if (m.whole.row(r.id) == null) m.whole.rows.add(r);
        }
        m.sumBosses();
    }

    private static void write(File dir, String name, LiveModel m) throws Exception {
        MvpCard.Pick pick = MvpCard.pick(m);
        if (pick == null) {
            System.out.println(name + ": no card");
            return;
        }
        File out = new File(dir, name);
        ImageIO.write(MvpCard.render(m, pick), "png", out);
        System.out.println(name + "  ->  " + (pick.overall ? "(no boss)" : pick.fight.name)
                + "  ·  " + pick.player.name);
    }

    private static LiveModel.Row player(int id, String name, String cls, long damage, long biggest,
                                        int hits, String[] enchants) throws Exception {
        return player(id, name, cls, damage, biggest, hits, enchants, SKINS.getOrDefault(cls, 0x0344));
    }

    /** Real skin ids out of the game's own skins.xml, so the portrait is a character. */
    private static final java.util.Map<String, Integer> SKINS = new java.util.HashMap<>();
    static {
        SKINS.put("Rogue", 0x0342);
        SKINS.put("Wizard", 0x0344);
        SKINS.put("Warrior", 0x0346);
        SKINS.put("Knight", 0x0347);
        SKINS.put("Samurai", 0x0342);
        SKINS.put("Necromancer", 0x0344);
        SKINS.put("Huntress", 0x0343);
        SKINS.put("Paladin", 0x0347);
    }

    private static LiveModel.Row player(int id, String name, String cls, long damage, long biggest,
                                        int hits, String[] enchants, int skinId) throws Exception {
        LiveModel.Row r = new LiveModel.Row();
        r.id = id;
        r.name = name;
        r.cls = cls;
        r.damage = damage;
        r.biggestHit = biggest;
        r.hits = hits;
        r.skin = skinId;
        java.lang.reflect.Method kitFor = LiveDemo.class.getDeclaredMethod("kitFor", String.class);
        kitFor.setAccessible(true);
        int[] kit = (int[]) kitFor.invoke(null, cls);
        for (int i = 0; i < 4; i++) {
            r.gearIds[i] = kit[i];
            r.loadout[i] = LiveModel.itemName(kit[i]);
        }
        System.arraycopy(enchants, 0, r.enchants, 0, 4);
        return r;
    }
}
