package main.java;

import org.apache.hadoop.fs.Path;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IniParser {
    private static Ini INI_FILE;

    static {
        try {
            INI_FILE = new Ini(new File("res/config.ini"));
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

    public static List<String> getAuthorsPaths() {
        ArrayList<String> paths = new ArrayList<>();
        Section section = INI_FILE.get("authors");
        for (String authName:section.keySet()) {
            paths.add(section.get(authName));
        }

        return paths;
    }
}
