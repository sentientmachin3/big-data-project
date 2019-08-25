import org.apache.hadoop.fs.Path;
import org.ini4j.Ini;
import java.io.File;
import java.io.IOException;

public class IniParser {
    private static Ini INI_FILE;

    static {
        try {
            INI_FILE = new Ini(new File("config.ini"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Path getInputPath() {
        return new Path(INI_FILE.get("paths", "input"));
    }

    public static Path getOutputPath() {
        return new Path(INI_FILE.get("paths", "output"));
    }
}
