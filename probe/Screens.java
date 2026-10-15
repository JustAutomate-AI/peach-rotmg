import java.awt.*;

/** What -Dsonar.monitor=N actually selects on this machine. */
public class Screens {
    public static void main(String[] a) {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = env.getScreenDevices();
        GraphicsDevice primary = env.getDefaultScreenDevice();
        System.out.println("screen devices as Java orders them:");
        for (int i = 0; i < screens.length; i++) {
            Rectangle b = screens[i].getDefaultConfiguration().getBounds();
            System.out.printf("  index %d  (-Dsonar.monitor=%d)  id=%-12s %5dx%-5d at (%d,%d)  %s%n",
                    i, i + 1, screens[i].getIDstring(), b.width, b.height, b.x, b.y,
                    screens[i] == primary ? "<-- PRIMARY" : "");
        }
        String want = System.getProperty("sonar.monitor");
        if (want != null) {
            int idx = Integer.parseInt(want.trim()) - 1;
            if (idx >= 0 && idx < screens.length) {
                Rectangle b = screens[idx].getDefaultConfiguration().getBounds();
                System.out.println();
                System.out.printf("-Dsonar.monitor=%s picks index %d at (%d,%d) which is %s%n",
                        want, idx, b.x, b.y,
                        screens[idx] == primary ? "THE PRIMARY SCREEN" : "a secondary screen");
            }
        }
    }
}
