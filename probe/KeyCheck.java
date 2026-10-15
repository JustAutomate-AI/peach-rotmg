import sonar.assets.AssetExtractor;
import sonar.version.Version;

/** The cache key must come out exactly as it did before the rename, or everyone re-extracts. */
public class KeyCheck {
    public static void main(String[] a) throws Throwable {
        System.out.println("app version   Version.VERSION      = " + Version.VERSION);
        System.out.println("cache key     Version.ASSET_FORMAT = " + Version.ASSET_FORMAT);
        String now = AssetExtractor.lastEdited(Version.ASSET_FORMAT);
        String stored = sonar.util.PropertiesManager.getProperty("lastModifiedTime");
        System.out.println();
        System.out.println("stored in sonar.properties : " + stored);
        System.out.println("computed by this build     : " + now);
        System.out.println();
        System.out.println(now.equals(stored)
            ? "MATCH - no install re-extracts because of this change"
            : "DIFFERS - this change would force a re-extraction");
    }
}
