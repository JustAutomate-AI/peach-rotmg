package sonar.ui;

import java.lang.reflect.*;
import java.nio.ByteBuffer;
import java.nio.file.*;

/** What the game actually ships for the Shatters objects Sonar marks. */
public class SpriteProbe {
    public static void main(String[] a) throws Exception {
        byte[] data = Files.readAllBytes(Paths.get("assets/flatbuffer/spritesheetf"));
        assets.flattbuffer.SpriteSheetRoot root =
                assets.flattbuffer.SpriteSheetRoot.getRootAsSpriteSheetRoot(ByteBuffer.wrap(data));
        System.out.println("animated sprite entries: " + root.animatedSpritesLength());
        String[][] want = {
                {"theShattersChars16x16", "18"},   // Void Phantasm
                {"theShattersChars32x32", "1"},    // Stone Idol
        };
        for (String[] w : want) {
            int hits = 0;
            StringBuilder modes = new StringBuilder();
            for (int i = 0; i < root.animatedSpritesLength(); i++) {
                assets.flattbuffer.AnimatedSprite s = root.animatedSprites(i);
                if (!w[0].equals(s.name()) || s.index() != Integer.parseInt(w[1])) continue;
                hits++;
                if (hits <= 8) modes.append(" dir=").append(s.direction()).append("/act=").append(s.action())
                        .append("[").append((int) s.sprites().position().w()).append("x")
                        .append((int) s.sprites().position().h()).append(" aId=").append(s.sprites().aId()).append("]");
            }
            System.out.println(w[0] + ":" + w[1] + " -> " + hits + " frames" + modes);
        }
        // and the static ones, through the normal path
        Field f = assets.ImageBuffer.class.getDeclaredField("spriteFlatBuffer");
        f.setAccessible(true);
        Object sfb = f.get(null);
        String[][] stat = {{"theShattersObjects16x16", "157"}, {"theShattersObjects32x32", "18"}, {"theShattersObjects8x8", "752"}};
        for (String[] s : stat) {
            try {
                int[] d = (int[]) assets.SpriteFlatBuffer.class.getMethod("getSpriteData", String.class, int.class)
                        .invoke(sfb, s[0], Integer.parseInt(s[1]));
                System.out.println(s[0] + ":" + s[1] + " -> " + (d == null ? "null" : java.util.Arrays.toString(d)));
            } catch (Exception e) {
                System.out.println(s[0] + ":" + s[1] + " -> " + e.getCause());
            }
        }
        System.exit(0);
    }
}
