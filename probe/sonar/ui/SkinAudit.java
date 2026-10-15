package sonar.ui;

import sonar.game.enums.CharacterClass;

import java.util.*;

/**
 * Checks that the skins Sonar offers a class are the ones the game allows it.
 * <p>
 * The wardrobe reads PlayerClassType out of skins.xml and groups by it, with a bucket
 * for the skins any class may wear. This prints what that produces, and flags two things
 * worth a second look: a skin whose own name belongs to a different class, and any skin
 * that somehow reaches more than one class without being in the any-class bucket.
 */
public final class SkinAudit {

    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        sonar.assets.IdToAsset.reloadAssets();

        String[] classes = {"Rogue", "Archer", "Wizard", "Priest", "Warrior", "Knight",
                "Paladin", "Assassin", "Necromancer", "Huntress", "Mystic", "Trickster",
                "Sorcerer", "Ninja", "Samurai", "Bard", "Summoner", "Kensei", "Druid"};

        // which classes each skin id reaches
        Map<Integer, List<String>> reach = new LinkedHashMap<>();
        Map<Integer, String> names = new HashMap<>();
        System.out.printf("%-14s %5s  %s%n", "CLASS", "SKINS", "id");
        for (String cls : classes) {
            int id = CharacterClass.idByName(cls);
            List<Wardrobe.Skin> skins = Wardrobe.skins(id);
            System.out.printf("%-14s %5d  0x%04x%n", cls, skins.size(), id);
            for (Wardrobe.Skin s : skins) {
                reach.computeIfAbsent(s.id, k -> new ArrayList<>()).add(cls);
                names.put(s.id, s.name);
            }
        }

        System.out.println();
        System.out.println("distinct skins offered anywhere: " + reach.size());

        // a skin named after one class but offered to another
        System.out.println();
        System.out.println("=== skins whose name mentions a class that cannot be the wearer ===");
        int suspect = 0;
        for (Map.Entry<Integer, List<String>> e : reach.entrySet()) {
            String name = names.get(e.getKey());
            if (name == null) continue;
            for (String other : classes) {
                if (!name.toLowerCase().contains(other.toLowerCase())) continue;
                List<String> wearers = e.getValue();
                if (wearers.contains(other) && wearers.size() == 1) break;   // named for its own class
                System.out.printf("  0x%04x  %-44s named %-12s offered to %s%n",
                        e.getKey(), name, other,
                        wearers.size() > 6 ? wearers.size() + " classes" : wearers);
                suspect++;
                break;
            }
        }
        if (suspect == 0) System.out.println("  none");

        // how many classes each skin reaches
        System.out.println();
        Map<Integer, Integer> spread = new TreeMap<>();
        for (List<String> v : reach.values()) spread.merge(v.size(), 1, Integer::sum);
        System.out.println("=== how many classes each skin is offered to ===");
        for (Map.Entry<Integer, Integer> e : spread.entrySet()) {
            System.out.printf("  offered to %2d class(es): %4d skins%s%n",
                    e.getKey(), e.getValue(),
                    e.getKey() == classes.length ? "   <- the any-class bucket" : "");
        }
    }
}
