package sonar.ui;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * The real damage pane, painted headlessly, so the text over the class bars can be
 * judged from the code that actually draws it rather than from a mock-up of it.
 */
public final class BarText {

    private static final String[][] WHO = {
            {"Username1", "Samurai"}, {"Username2", "Knight"}, {"Username3", "Archer"},
            {"Username4", "Wizard"}, {"Username5", "Necromancer"}, {"Username6", "Huntress"},
            {"Username7", "Warrior"}, {"Username8", "Rogue"}, {"Username19", "Mystic"},
            {"Username17", "Paladin"}, {"Username20", "Priest"}, {"Username23", "Sorcerer"},
    };

    private static final long[] DAMAGE = {
            1_149_000, 40_000, 22_000, 14_000, 9_000, 6_000,
            4_000, 3_000, 2_000, 1_500, 900, 400};

    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        SonarTheme.install();
        sonar.assets.IdToAsset.reloadAssets();

        LiveModel.Target target = new LiveModel.Target();
        target.name = "Oryx 3";
        for (int i = 0; i < WHO.length; i++) {
            LiveModel.Row r = new LiveModel.Row();
            r.id = i + 1;
            r.name = WHO[i][0];
            r.cls = WHO[i][1];
            r.damage = DAMAGE[i];
            r.hits = 120 + i * 7;
            r.me = i == 10;
            int[] kit = LiveDemo.kitFor(r.cls);
            for (int s = 0; s < 4; s++) {
                r.gearIds[s] = kit[s];
                r.loadout[s] = LiveModel.itemName(kit[s]);
                LiveModel.ITEM_IDS.put(r.loadout[s], kit[s]);
            }
            // one of each tier, so the glow is seen on every bar colour too
            r.enchantTier[0] = 1;
            r.enchantTier[1] = 2;
            r.enchantTier[2] = 3;
            r.enchantTier[3] = 4;
            r.enchants[0] = "Damage Bonus I";
            r.enchants[1] = "Damage Bonus II";
            r.enchants[2] = "Damage Bonus III";
            r.enchants[3] = "Damage Bonus IV";
            r.skin = LiveDemo.skinFor(r.cls, i);
            int[] dyes = LiveDemo.dyesFor(i);
            r.dyeMain = dyes[0];
            r.dyeAccessory = dyes[1];
            target.rows.add(r);
        }

        int width = 620, height = 34 * WHO.length + 60;
        DamagePane pane = new DamagePane();
        pane.setSize(width, height);
        pane.show("demo", target, "", "");
        pane.totals(false);

        // the bars ease into place, so they are stepped forward until they have settled
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int frame = 0; frame < 220; frame++) {
            Graphics2D g = SonarTheme.quality(img.createGraphics());
            g.setColor(SonarTheme.BG);
            g.fillRect(0, 0, width, height);
            pane.paintContent(g, width);
            g.dispose();
            Thread.sleep(8);
        }
        ImageIO.write(img, "png", new File(args[0]));
        System.out.println("wrote " + args[0]);
    }
}
