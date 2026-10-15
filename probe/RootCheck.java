import java.io.File;
import java.lang.reflect.Method;

/** Which folder an update would be unpacked over, from each layout. */
public class RootCheck {
    public static void main(String[] a) throws Exception {
        Method m = Class.forName("sonar.ui.Updater").getDeclaredMethod("installRoot");
        m.setAccessible(true);
        System.out.println("working dir : " + new File("").getAbsoluteFile());
        System.out.println("install root: " + m.invoke(null));
    }
}
