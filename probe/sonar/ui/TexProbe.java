package sonar.ui;

public class TexProbe {
    public static void main(String[] a) throws Exception {
        int[] ids = ShattersRadar.watchedForTest();
        for (int id : ids) {
            String n = null; int ix = -1; String err = "";
            try { n = assets.IdToAsset.getObjectTextureName(id, 0); } catch (Throwable t) { err = t.toString(); }
            try { ix = assets.IdToAsset.getObjectTextureIndex(id, 0); } catch (Throwable t) { err += " " + t; }
            System.out.printf("%#07x %-20s -> %s : %d %s%n", id, ShattersRadar.label(id), n, ix, err);
        }
        System.exit(0);
    }
}
