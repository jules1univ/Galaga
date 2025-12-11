package galaga.score;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import engine.utils.logger.Log;

public class Score {
    private int value;
    
    public static Score loadScore(InputStream in)
    {
        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            Log.error("Level loading failed: " + e.getMessage());
            return null;
        }

        if(lines.isEmpty()) {
            return new Score(0);
        }
        int value;
        try {
            value = Integer.parseInt(lines.get(0));
        } catch (NumberFormatException e) {
            Log.error("Score loading failed: " + e.getMessage());
            return null;
        }
        return new Score(value);
    }

    private Score(int value)
    {
        this.value = value;
    }


    public int getValue() {
        return this.value;
    }
}
