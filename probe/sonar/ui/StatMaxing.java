package sonar.ui;

/**
 * The potion arithmetic, checked against characters Sonar actually saw.
 * <p>
 * Every case below is a real player read off the wire on 14 Oct 2026, with the numbers
 * exactly as the game sent them: the stat total, what their equipment was lending, and
 * the class the eight figures identify beyond doubt. They are kept because the first
 * version of this sum was wrong in a way no invented example would have caught - it took
 * a Huntress maxed on all eight stats, exalted to the hilt, to show that the exalt bonus
 * was being counted five times over and measured against a ceiling it has no business
 * raising. A quiet server never produced one; a crowded one produced her in a minute.
 *
 *   javac -encoding UTF-8 -cp run/Sonar.jar -d probe probe/sonar/ui/StatMaxing.java
 *   cd run
 *   java -cp "Sonar.jar;../probe" sonar.ui.StatMaxing
 */
public final class StatMaxing {

    /** life, mana, attack, defence, speed, dexterity, vitality, wisdom - Sonar's order throughout. */
    private static final String[] NAMES = {"HP", "MP", "ATT", "DEF", "SPD", "DEX", "VIT", "WIS"};

    private static final class Seen {
        final String who, cls;
        final int[] total, boost, expect;

        Seen(String who, String cls, int[] total, int[] boost, int[] expect) {
            this.who = who;
            this.cls = cls;
            this.total = total;
            this.boost = boost;
            this.expect = expect;
        }
    }

    private static final Seen[] REAL = {
            // Fully maxed and fully exalted on health - the case that exposed the fault.
            // Her health without equipment is 750, which is the Huntress maximum exactly,
            // so nothing is owed on any of the eight.
            new Seen("AcaPro", "Huntress",
                    new int[]{950, 425, 70, 46, 55, 65, 57, 66},
                    new int[]{200, 75, 5, 21, 5, 5, 17, 6},
                    new int[]{0, 0, 0, 0, 0, 0, 0, 0}),

            // A Knight some way off on health and mana and finished everywhere else.
            // 800 - 700 = 100 health, which is twenty potions; 300 - 280 = 20 mana, four.
            new Seen("SHEBAR", "Knight",
                    new int[]{795, 365, 52, 78, 62, 52, 97, 55},
                    new int[]{95, 85, 2, 38, 12, 2, 22, 5},
                    new int[]{20, 4, 0, 0, 0, 0, 0, 0}),

            // A Druid wearing a blood ritual that trades a fifth of her health away, and
            // short only on speed: 60 - 53 = 7, and a speed potion is worth one apiece.
            // The ritual changes nothing here, which is the point - it never touches the
            // ceiling potions reach, nor the figure the game reports.
            new Seen("Arubaantje", "Druid",
                    new int[]{795, 335, 69, 49, 65, 84, 59, 84},
                    new int[]{45, -15, 9, 24, 12, 19, 19, 9},
                    new int[]{0, 0, 0, 0, 7, 0, 0, 0}),
    };

    public static void main(String[] args) throws Exception {
        sonar.assets.IdToAsset.reloadAssets();
        int checks = 0, failed = 0;

        for (Seen s : REAL) {
            int classId = sonar.game.enums.CharacterClass.idByName(s.cls);
            int[] caps = sonar.game.enums.CharacterClass.getStats(classId);
            if (caps == null) {
                System.out.println("FAIL " + s.who + ": no stat table for " + s.cls);
                failed++;
                continue;
            }
            for (int i = 0; i < 8; i++) {
                int got = GearPane.potionsToMax(caps, i, s.total[i], s.boost[i]);
                checks++;
                boolean ok = got == s.expect[i];
                if (!ok) failed++;
                System.out.println((ok ? "PASS " : "FAIL ")
                        + String.format("%-11s %-9s %-4s own %4d of %4d  ->  %2d potions (expected %d)",
                        s.who, s.cls, NAMES[i], s.total[i] - s.boost[i], caps[i], got, s.expect[i]));
            }
        }

        // the class tables these rest on are the game's own, so a rebalance moves them and
        // this says so rather than quietly measuring against last season's ceilings
        System.out.println();
        System.out.println("Huntress health ceiling reads " + sonar.game.enums.CharacterClass
                .getStats(sonar.game.enums.CharacterClass.idByName("Huntress"))[0] + ", expected 750");
        System.out.println();
        System.out.println(failed == 0
                ? "all " + checks + " checks pass"
                : failed + " of " + checks + " checks FAILED");
        System.exit(failed == 0 ? 0 : 1);
    }
}
