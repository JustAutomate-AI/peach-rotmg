import sonar.assets.resextractor.ArcaneExtractor;
import java.io.File;
import java.util.List;

public class ArcaneRun {
    public static void main(String[] a) throws Exception {
        File shared = new File(a[0]);
        File out = new File(a[1]);
        long t0 = System.currentTimeMillis();
        List<ArcaneExtractor.Effect> got = ArcaneExtractor.extract(shared, out);
        long ms = System.currentTimeMillis() - t0;
        System.out.println();
        System.out.printf("%-24s %-8s %-7s %-9s %s%n", "KEY", "FRAMES", "CELLS", "SCROLLER", "TEXTURE");
        for (ArcaneExtractor.Effect e : got) {
            System.out.printf("%-24s %-8d %-7d %-9s %s%n", e.key, e.frames, e.cells, e.scroller, e.texture);
        }
        System.out.println();
        System.out.println(got.size() + " effects extracted in " + ms + " ms");
    }
}
