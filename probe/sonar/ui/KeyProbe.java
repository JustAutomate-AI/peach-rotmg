package sonar.ui;

public class KeyProbe {
    public static void main(String[] a) {
        int[] z = GameKeys.minimapZoom();
        System.out.printf("minimap zoom in  -> VK 0x%02X%n", z[0]);
        System.out.printf("minimap zoom out -> VK 0x%02X%n", z[1]);
        // the two the registry actually holds for this account
        System.out.println("unity 61 (=) maps to VK 0x" + Integer.toHexString(GameKeys.vkOf(61)));
        System.out.println("unity 45 (-) maps to VK 0x" + Integer.toHexString(GameKeys.vkOf(45)));
        System.out.println("unity 97 (a) maps to VK 0x" + Integer.toHexString(GameKeys.vkOf(97)));
        System.out.println("unity 270 (kp+) maps to VK 0x" + Integer.toHexString(GameKeys.vkOf(270)));
        System.exit(0);
    }
}
