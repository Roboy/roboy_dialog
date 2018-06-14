package roboy.util;

import java.util.List;

public class FileLineReader {
    public static RandomList<String> readFile(String path) {
        RandomList<String> result = new RandomList<>();
        List<String> phrasesFromFile = IO.readLinesFromUtf8File(path);
        if (phrasesFromFile == null) {
            return result;
        }
        // ignore empty strings
        for (String s : phrasesFromFile) {
            if (s.trim().length() > 0) {
                // add spaces to prevent cases when someone forgets to add a space in between
                result.add(" " + s + " ");
            }
        }
        return result;
    }
}
