package test.java;

import main.java.IniParser;
import org.junit.*;

import java.util.ArrayList;
import java.util.Arrays;

public class IniParserTest {
    ArrayList<String> expected;

    @Before
    public void setUp() {
        expected = new ArrayList<>(Arrays.asList("/user/root/authorship/input/arthur-conan-doyle.txt",
                "/user/root/authorship/input/dante-alighieri.txt", "/user/root/authorship/input/herman-melville.txt"));
    }

    @Test
    public void getAuthorsPathsTest() {
        Assert.assertEquals(expected, IniParser.getAuthorsPaths());
    }
}
