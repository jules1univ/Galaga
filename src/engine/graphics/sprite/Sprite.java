package engine.graphics.sprite;

import engine.utils.Size;
import engine.utils.logger.Log;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Sprite {

    private final BufferedImage image;
    private final Map<Color, Integer> colorMap;

    private Sprite(BufferedImage image, Map<Color, Integer> colorMap) {
        this.image = image;
        this.colorMap = colorMap;
    }

    public static Sprite createSprite(InputStream in) {
        List<String> lines = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            Log.error("Sprite loading failed: %s", e.getMessage());
            return null;
        }

        int height = lines.size();
        int width = lines.get(0).length();

        if (height == 0 || width == 0) {
            Log.error("Sprite dimensions are zero.");
            return null;
        }

        boolean isValidSpriteSize = true;
        for (String line : lines) {
            if (line.length() != width) {
                isValidSpriteSize = false;
                break;
            }
        }
        if (!isValidSpriteSize) {
            Log.error("Sprite size is inconsistent.");
            return null;
        }

        BufferedImage base = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_ARGB);
        Map<Color, Integer> colorMap = new HashMap<>();
        for (int y = 0; y < height; y++) {
            String row = lines.get(y);
            for (int x = 0; x < width; x++) {
                char c = row.charAt(x);
                Color color = charToColor(c);

                colorMap.putIfAbsent(color, 0);
                colorMap.put(color, colorMap.get(color) + 1);
                base.setRGB(x, y, color.getRGB());
            }
        }
        return new Sprite(base, colorMap);
    }

    public static boolean saveSprite(Sprite data, OutputStream out) {
        try {
            for (int y = 0; y < data.image.getHeight(); y++) {
                StringBuilder row = new StringBuilder();
                for (int x = 0; x < data.image.getWidth(); x++) {
                    Color color = new Color(data.image.getRGB(x, y), true);
                    row.append(colorToChar(color));
                }
                row.append('\n');
                out.write(row.toString().getBytes());
            }
            return true;
        } catch (IOException e) {
            Log.error("Sprite saving failed: %s" , e.getMessage());
            return false;
        }
    }

    public static char colorToChar(Color color) {
        if (color.equals(Color.WHITE)) {
            return 'W';
        } else if (color.equals(Color.BLUE)) {
            return 'B';
        } else if (color.equals(Color.RED)) {
            return 'R';
        } else if (color.equals(Color.YELLOW)) {
            return 'Y';
        } else if (color.equals(Color.GREEN)) {
            return 'G';
        } else if (color.equals(Color.CYAN)) {
            return 'C';
        } else if (color.equals(Color.MAGENTA)) {
            return 'M';
        } else if (color.equals(Color.ORANGE)) {
            return 'O';
        } else if (color.equals(Color.PINK)) {
            return 'P';
        } else if (color.equals(Color.LIGHT_GRAY)) {
            return 'L';
        } else if (color.equals(Color.DARK_GRAY)) {
            return 'D';
        } else {
            return 'N';
        }
    }

    public static Color charToColor(char c) {
        return switch (c) {
            case 'W' -> Color.WHITE;
            case 'B' -> Color.BLUE;
            case 'R' -> Color.RED;
            case 'Y' -> Color.YELLOW;
            case 'G' -> Color.GREEN;
            case 'C' -> Color.CYAN;
            case 'M' -> Color.MAGENTA;
            case 'O' -> Color.ORANGE;
            case 'P' -> Color.PINK;
            case 'L' -> Color.LIGHT_GRAY;
            case 'D' -> Color.DARK_GRAY;
            default -> new Color(0, 0, 0, 0); // NOIR/N = transparent
        };
    }

    public Size getSize() {
        if (this.image == null) {
            return Size.of(0, 0);
        }
        return Size.of(this.image.getWidth(), this.image.getHeight());
    }

    public Map<Color, Integer> getColors() {
        return this.colorMap;
    }

    public BufferedImage getImage() {
        return this.image;
    }

}
