package engine.graphics.sprite;

import engine.utils.Size;
import engine.utils.logger.Log;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Sprite {

    private final BufferedImage image;
    private Map<Color, Integer> colorMap;

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
        } catch (Exception e) {
            Log.error("Sprite loading failed - " + e.getMessage());
            return null;
        }

        int height = lines.size();
        int width = lines.get(0).length();

        if (height == 0 || width == 0) {
            Log.error("Sprite dimensions are zero.");
            return null;
        }

        boolean isValidSpriteSize = lines.stream().filter(line -> line.length() == width).toList()
                .size() == height;
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

    private static Color charToColor(char c) {
        return switch (c) {
            case 'W' -> Color.WHITE;
            case 'B' -> Color.BLUE;
            case 'R' -> Color.RED;
            case 'Y' -> Color.YELLOW;
            case 'G' -> Color.GREEN;
            default -> new Color(0, 0, 0, 0); // BLACK = transparent
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
