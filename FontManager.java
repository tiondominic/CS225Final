import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FontManager {
    private static final Map<String, Font> fontCache = new HashMap<>();

    static {
        loadFontsFromDirectory("assets/fonts");
    }

    private static void loadFontsFromDirectory(String directoryPath) {
        File dir = new File(directoryPath);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".ttf"));
            if (files != null) {
                for (File file : files) {
                    try {
                        Font font = Font.createFont(Font.TRUETYPE_FONT, file);
                        String fontName = file.getName().replace(".ttf", "");
                        fontCache.put(fontName, font);
                        System.out.println("Loaded font: " + fontName);
                    } catch (Exception e) {
                        System.err.println("Failed to load font: " + file.getName());
                    }
                }
            }
        }
    }

    public static Font getFont(String fontName, float size) {
        Font base = fontCache.get(fontName);
        if (base != null) {
            return base.deriveFont(size);
        } else {
            System.err.println("Font not found in cache: " + fontName);
            return new Font("SansSerif", Font.PLAIN, (int) size);
        }
    }
}