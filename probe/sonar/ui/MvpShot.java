package sonar.ui;

import javax.imageio.ImageIO;
import java.io.File;

/**
 * Renders the MVP card from a model built here, so the layout can be looked at
 * without a game running and with a known scenario: one final boss, a couple of
 * minibosses, and a top player wearing gear with enchants on it.
 *
 *   javac -cp run/Sonar.jar -d probe probe/sonar/ui/MvpShot.java
 *   cd run
 *   java -cp "Sonar.jar;../probe" sonar.ui.MvpShot out.png
 */
public final class MvpShot {

    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        SonarTheme.install();

        LiveModel m = new LiveModel();
        m.instance = "Oryx's Sanctuary";
        m.tracked = true;

        LiveModel.Target oryx = boss("Oryx the Mad God 3", true, 10_100_000, 141_000);
        oryx.rows.add(player(1, "Zephyra", "Samurai", 1_149_000, 42_300, 812,
                new String[]{"Damage Bonus IV" + NL + "FireRate Bonus II", "Attack Bonus I", "", "Dexterity Bonus II"}));
        oryx.rows.add(player(2, "Cooosplay", "Knight", 492_000, 18_100, 410,
                new String[]{"", "", "", ""}));

        LiveModel.Target mini = boss("Archbishop Leucoryx", false, 1_200_000, 48_000);
        mini.rows.add(player(1, "Zephyra", "Samurai", 610_000, 21_000, 400,
                new String[]{"", "", "", ""}));

        m.targets.add(oryx);
        m.targets.add(mini);
        m.whole.rows.addAll(oryx.rows);
        m.sumBosses();

        MvpCard.Pick pick = MvpCard.pick(m);
        if (pick == null) {
            System.out.println("no pick");
            return;
        }
        System.out.println("fight:  " + (pick.overall ? "(no boss)" : pick.fight.name));
        System.out.println("player: " + pick.player.name + " " + pick.player.cls + " " + pick.player.damage);

        File out = new File(args.length > 0 ? args[0] : "mvp.png");
        ImageIO.write(MvpCard.render(m, pick), "png", out);
        System.out.println("wrote " + out.getAbsolutePath());
    }

    private static LiveModel.Target boss(String name, boolean last, int maxHp, long fightMs) {
        LiveModel.Target t = new LiveModel.Target();
        t.name = name;
        t.boss = true;
        t.finalBoss = last;
        t.maxHp = maxHp;
        t.healthLost = maxHp;
        t.fightMs = fightMs;
        return t;
    }

    static final String NL = String.valueOf((char) 10);

    /**
     * A player wearing a kit the class can really hold, with the game's own item ids
     * and names, so the pictures on the card are the real ones.
     */
    private static LiveModel.Row player(int id, String name, String cls, long damage, long biggest,
                                        int hits, String[] enchants) throws Exception {
        LiveModel.Row r = new LiveModel.Row();
        r.id = id;
        r.name = name;
        r.cls = cls;
        r.damage = damage;
        r.biggestHit = biggest;
        r.hits = hits;
        r.skin = 768 + id % 3;

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
