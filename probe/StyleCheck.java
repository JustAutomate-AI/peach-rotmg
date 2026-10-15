import sonar.ui.ArcaneStyle;
import java.util.*;

public class StyleCheck {
    public static void main(String[] a) {
        Map<Integer, ArcaneStyle> all = ArcaneStyle.all();
        System.out.println("styles parsed: " + all.size() + "   (763 objects minus 2 removers = 761)");
        Map<ArcaneStyle.Family, Integer> byFamily = new EnumMap<>(ArcaneStyle.Family.class);
        Map<Integer, Integer> byCount = new TreeMap<>();
        Set<String> arts = new TreeSet<>();
        int tinted = 0, drawable = 0;
        for (ArcaneStyle s : all.values()) {
            byCount.merge(s.effects.size(), 1, Integer::sum);
            for (ArcaneStyle.Effect e : s.effects) {
                byFamily.merge(e.family, 1, Integer::sum);
                if (e.hasArt()) arts.add(e.art);
                if (e.colour >= 0 && e.family == ArcaneStyle.Family.FLIPBOOK) tinted++;
            }
            if (sonar.ui.ArcaneArt.canDraw(s)) drawable++;
        }
        System.out.println("effect elements by family: " + byFamily);
        System.out.println("   expected {FLIPBOOK=455, SCROLLER=18, PULSE=144, INNER_GLOW=216, HOLOGRAM=36}");
        System.out.println("effects per style: " + byCount);
        System.out.println("flipbook effects carrying their own colour: " + tinted + "   (expected 325)");
        System.out.println("distinct art keys: " + arts.size() + "   (expected 32)");
        System.out.println("styles Sonar can draw art for: " + drawable);
        int missing = 0;
        for (String k : arts) {
            boolean any = false;
            for (ArcaneStyle s : all.values())
                for (ArcaneStyle.Effect e : s.effects)
                    if (k.equals(e.art) && sonar.ui.ArcaneArt.canDraw(s)) { any = true; break; }
            if (!any) { System.out.println("   NO STRIP for " + k); missing++; }
        }
        System.out.println("art keys with no strip: " + missing);
        System.out.println();
        for (ArcaneStyle s : all.values())
            if (s.has(ArcaneStyle.Family.PULSE) && s.has(ArcaneStyle.Family.INNER_GLOW)) {
                System.out.println("combined example: " + s); break;
            }
    }
}
