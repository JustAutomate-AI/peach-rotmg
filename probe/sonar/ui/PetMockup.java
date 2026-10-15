package sonar.ui;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Three places a pet could go, drawn with the real sprites and the real theme so the
 * choice is made by looking rather than by imagining.
 * <p>
 * Nothing here changes the app. Each panel is a mock-up of one placement, rendered the
 * way the page in question already draws itself.
 */
public final class PetMockup {

    /** Real pet skin ids out of the game's own pets.xml, with what the game calls them. */
    private static final Object[][] PETS = {
            {0x7f8c, "Pumpkin Head", "Spooky", "Divine"},
            {0x8068, "Phoenix", "Avian", "Divine"},
            {0x804c, "Baby Dragon", "Reptile", "Rare"},
            {0x8000, "Black Cat", "Feline", "Common"},
            {0x4cb9, "Moon Bunny", "Farm", "Rare"},
            {0x7696, "Spectral Skeledragon", "Spooky", "Divine"},
    };

    private static final String[] WHO = {"Username9", "Username10", "Username12", "Username13", "Username11", "Username15"};
    private static final String[] CLS = {"Samurai", "Knight", "Archer", "Wizard", "Necromancer", "Huntress"};
    private static final int[] SKINS = {0x0342, 0x0347, 0x0343, 0x0344, 0x0344, 0x0343};
    private static final long[] DMG = {1150000, 493000, 468000, 455000, 451000, 341000};

    private static Color rarity(String r) {
        switch (r) {
            case "Divine": return SonarTheme.GOLD;
            case "Legendary": return SonarTheme.VIOLET;
            case "Rare": return new Color(0x6aa8ff);
            default: return SonarTheme.MUTED;
        }
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        SonarTheme.install();
        sonar.assets.IdToAsset.reloadAssets();
        File dir = new File(args.length > 0 ? args[0] : ".");
        dir.mkdirs();
        ImageIO.write(roster(), "png", new File(dir, "pet-a-roster.png"));
        ImageIO.write(characters(), "png", new File(dir, "pet-b-characters.png"));
        ImageIO.write(card(), "png", new File(dir, "pet-c-card.png"));
        System.out.println("three mock-ups written to " + dir.getAbsolutePath());
    }

    private static Graphics2D start(BufferedImage img) {
        Graphics2D g = SonarTheme.quality(img.createGraphics());
        g.setColor(SonarTheme.BG);
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        return g;
    }

    private static void heading(Graphics2D g, String text, String note, int w) {
        g.setFont(SonarTheme.bold(11f));
        g.setColor(SonarTheme.FAINT);
        g.drawString(text, 18, 24);
        g.setFont(SonarTheme.font(11.5f));
        g.setColor(SonarTheme.MUTED);
        g.drawString(note, 18, 42);
        g.setColor(SonarTheme.LINE);
        g.drawLine(18, 54, w - 18, 54);
    }

    /** A: the Players roster, with the pet beside the person. */
    private static BufferedImage roster() {
        int w = 560, row = 54, h = 70 + PETS.length * row + 18;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = start(img);
        heading(g, "A  ·  PLAYERS ROSTER", "the pet beside the person, where you already recognise them on sight", w);
        int y = 70;
        for (int i = 0; i < PETS.length; i++) {
            g.setColor(SonarTheme.alpha(SonarTheme.RAISED, 0.45));
            g.fill(SonarTheme.round(18, y, w - 36, row - 8, 9));
            Glyph.player(g, SKINS[i], 0, 0, 26, y + 6, 34);
            // the pet rides just behind the player, smaller, the way it follows in game
            Glyph.object(g, (Integer) PETS[i][0], 56, y + 16, 24);
            g.setFont(SonarTheme.bold(13f));
            g.setColor(SonarTheme.TEXT);
            g.drawString((String) WHO[i], 92, y + 20);
            g.setFont(SonarTheme.font(11f));
            g.setColor(SonarTheme.MUTED);
            g.drawString(CLS[i] + "  ·  " + PETS[i][1], 92, y + 36);
            g.setFont(SonarTheme.bold(12.5f));
            g.setColor(SonarTheme.accent());
            String d = SonarTheme.compact(DMG[i]);
            g.drawString(d, w - 36 - g.getFontMetrics().stringWidth(d), y + 28);
            y += row;
        }
        g.dispose();
        return img;
    }

    /** B: the Characters page, where the pet's name is already printed as text. */
    private static BufferedImage characters() {
        int w = 560, row = 62, h = 70 + 4 * row + 18;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = start(img);
        heading(g, "B  ·  CHARACTERS", "the page already prints the pet's name - this gives it its face", w);
        String[] cls = {"Wizard", "Necromancer", "Paladin", "Huntress"};
        int[] skins = {0x0344, 0x0344, 0x0347, 0x0343};
        int[] pets = {0x7f8c, 0x8068, 0x8000, 0x4cb9};
        String[] pname = {"Pumpkin Head", "Phoenix", "Black Cat", "Moon Bunny"};
        String[] prare = {"Divine", "Divine", "Common", "Rare"};
        int y = 70;
        for (int i = 0; i < 4; i++) {
            g.setColor(SonarTheme.alpha(SonarTheme.RAISED, 0.45));
            g.fill(SonarTheme.round(18, y, w - 36, row - 8, 9));
            Glyph.player(g, skins[i], 0, 0, 28, y + 8, 38);
            g.setFont(SonarTheme.bold(13.5f));
            g.setColor(SonarTheme.TEXT);
            g.drawString(cls[i], 78, y + 22);
            g.setFont(SonarTheme.font(11f));
            g.setColor(SonarTheme.MUTED);
            g.drawString("Level 20  ·  4,812 fame", 78, y + 38);
            // the pet gets a plate of its own, with its rarity in the colour the game uses
            int px = w - 36 - 150;
            g.setColor(SonarTheme.alpha(SonarTheme.MUTED, 0.10));
            g.fill(SonarTheme.round(px, y + 8, 150, row - 24, 8));
            Glyph.object(g, pets[i], px + 7, y + 13, 26);
            g.setFont(SonarTheme.bold(11.5f));
            g.setColor(SonarTheme.TEXT);
            g.drawString(pname[i], px + 40, y + 24);
            g.setFont(SonarTheme.font(10.5f));
            g.setColor(rarity(prare[i]));
            g.drawString(prare[i], px + 40, y + 38);
            y += row;
        }
        g.dispose();
        return img;
    }

    /** C: the MVP card, with the pet at the character's shoulder. */
    private static BufferedImage card() {
        int w = 560, h = 230;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = start(img);
        heading(g, "C  ·  MVP CARD", "the pet at the shoulder, as it follows them in game", w);
        int y = 76;
        Glyph.player(g, 0x0342, 0, 0, 22, y, 84);
        // tucked at the lower right of the portrait, overlapping it slightly
        Glyph.object(g, 0x7f8c, 78, y + 52, 34);
        int tx = 22 + 84 + 18;
        g.setFont(SonarTheme.bold(24f));
        g.setColor(SonarTheme.TEXT);
        g.drawString("Username9", tx, y + 30);
        g.setFont(SonarTheme.font(13f));
        g.setColor(SonarTheme.MUTED);
        g.drawString("Samurai", tx, y + 52);
        g.setFont(SonarTheme.font(12f));
        g.setColor(SonarTheme.GOLD);
        g.drawString("Pumpkin Head", tx, y + 74);
        g.dispose();
        return img;
    }
}
