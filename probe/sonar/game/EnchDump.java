package sonar.game;

/** Lists the plain "X Bonus N" enchants, which are the ones worth putting on a demo roster. */
public final class EnchDump {
    public static void main(String[] a) throws Exception {
        System.setProperty("java.awt.headless", "true");
        sonar.assets.IdToAsset.reloadAssets();
        java.util.TreeSet<String> plain = new java.util.TreeSet<>();
        for (String name : ParseEnchants.ENCHANTS.values()) {
            if (name != null && name.matches("[A-Za-z]+ Bonus (I|II|III|IV)")) plain.add(name);
        }
        System.out.println(String.join(" | ", plain));
    }
}
