import sonar.ui.ArcaneStyle;
public class GateCheck {
    public static void main(String[] a) throws Exception {
        java.lang.reflect.Method canDraw = Class.forName("sonar.ui.ArcaneArt")
                .getDeclaredMethod("canDraw", ArcaneStyle.class);
        canDraw.setAccessible(true);
        ArcaneStyle fire = null;
        for (ArcaneStyle s : ArcaneStyle.all().values())
            if (s.drawable() != null) { fire = s; break; }
        System.out.println("sonar.arcane = " + System.getProperty("sonar.arcane"));
        System.out.println("styles parsed   : " + ArcaneStyle.all().size());
        System.out.println("enabled()       : " + Class.forName("sonar.ui.ArcaneArt")
                .getDeclaredMethod("enabled").invoke(null));
        System.out.println("canDraw(" + (fire == null ? "-" : fire.drawable().art) + ") : "
                + canDraw.invoke(null, fire));
    }
}
