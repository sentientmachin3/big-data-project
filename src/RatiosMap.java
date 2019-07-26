import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

public class RatiosMap {
    private HashMap<String, Float> map;

    public RatiosMap(BufferedReader reader) {
        map = new HashMap<>();
        HashMap<String, Integer> temp = new HashMap<>();
        String line;
        int words;

        try {
            while ((line = reader.readLine()) != null) {
                temp.put(line.split(" = ")[0], Integer.parseInt(line.split(" = ")[1]));
            }

        } catch (IOException e) {
            System.out.println(e.getCause());
        }

        for (String s : temp.keySet()) {
            if (!s.equals("nwords"))
                map.put(s, (float) (temp.get(s) / temp.get("nwords")));
        }
    }

    public HashMap<String, Float> getMap() {
        return map;
    }

    public void append(String key, float value) {
        map.put(key, value);
    }


}
