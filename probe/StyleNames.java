import sonar.ui.ArcaneStyle;
import java.util.*;

public class StyleNames {
    public static void main(String[] a) {
        Map<String, List<ArcaneStyle>> byArt = new TreeMap<>();
        for (ArcaneStyle s : ArcaneStyle.all().values()) {
            ArcaneStyle.Effect e = s.drawable();
            if (e != null) byArt.computeIfAbsent(e.art, k -> new ArrayList<>()).add(s);
        }
        for (Map.Entry<String, List<ArcaneStyle>> en : byArt.entrySet()) {
            List<ArcaneStyle> l = en.getValue();
            l.sort(Comparator.comparing(s -> s.id));
            ArcaneStyle.Effect e = l.get(0).drawable();
            System.out.printf("%-22s %3d styles  family=%-8s colour=%-8s intensity=%-5s framerate=%-5s scale=%-4s offsetY=%s%n",
                en.getKey(), l.size(), e.family,
                e.colour < 0 ? "art" : String.format("#%06x", e.colour),
                e.intensity, e.framerate, e.scale, e.offsetY);
            for (int i = 0; i < Math.min(3, l.size()); i++) System.out.println("        " + l.get(i).id);
        }
    }
}
