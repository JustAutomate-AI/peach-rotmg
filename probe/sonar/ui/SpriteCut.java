package sonar.ui;

import java.awt.image.BufferedImage;
import java.lang.reflect.*;
import java.nio.ByteBuffer;
import java.nio.file.*;

public class SpriteCut {
    static BufferedImage cut(int[] d) throws Exception {
        Method m = assets.ImageBuffer.class.getDeclaredMethod("getSprite", int[].class);
        m.setAccessible(true);
        return (BufferedImage) m.invoke(null, (Object) d);
    }

    public static void main(String[] a) throws Exception {
        byte[] data = Files.readAllBytes(Paths.get("assets/flatbuffer/spritesheetf"));
        assets.flattbuffer.SpriteSheetRoot root =
                assets.flattbuffer.SpriteSheetRoot.getRootAsSpriteSheetRoot(ByteBuffer.wrap(data));
        // the standing frame of the Void Phantasm
        for (int i = 0; i < root.animatedSpritesLength(); i++) {
            assets.flattbuffer.AnimatedSprite s = root.animatedSprites(i);
            if (!"theShattersChars16x16".equals(s.name()) || s.index() != 18) continue;
            if (s.direction() != 0 || s.action() != 0) continue;
            assets.flattbuffer.Sprite p = s.sprites();
            BufferedImage img = cut(new int[]{(int) p.position().x(), (int) p.position().y(),
                    (int) p.position().w(), (int) p.position().h(), (int) p.aId()});
            int sc = 8;
            BufferedImage big = new BufferedImage(img.getWidth() * sc, img.getHeight() * sc, BufferedImage.TYPE_INT_ARGB);
            java.awt.Graphics2D g = big.createGraphics();
            g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g.drawImage(img, 0, 0, big.getWidth(), big.getHeight(), null);
            g.dispose();
            javax.imageio.ImageIO.write(big, "png", new java.io.File(a[0]));
            System.out.println("cut " + img.getWidth() + "x" + img.getHeight());
            break;
        }
        System.exit(0);
    }
}
