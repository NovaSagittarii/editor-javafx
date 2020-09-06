package editor.util;

import okio.BufferedSource;
import okio.Okio;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    public static List<String> readLines(File file) throws IOException {
        List<String> content = new ArrayList<>();

        try (BufferedSource source = Okio.buffer(Okio.source(file))) {
            while (!source.exhausted()) {
                content.add(source.readUtf8LineStrict(1024L));
            }
        }

        return content;
    }
}
