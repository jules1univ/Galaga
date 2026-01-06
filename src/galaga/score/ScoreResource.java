package galaga.score;

import java.io.InputStream;
import java.io.OutputStream;

import engine.resource.Resource;
import engine.resource.ResourceAlias;
import engine.resource.ResourceCallback;

public class ScoreResource extends Resource<Score> {
   public static final String NAME = "score";
    
    public ScoreResource(ResourceAlias alias, ResourceCallback callback) {
        super(alias, callback);
    }

    @Override
    public boolean load() {
        InputStream in = this.getResourceInput();
        if(in == null) {
            return false;
        }
        Score score = Score.loadScore(in);
        if(score == null) {
            return false;
        }
        this.onLoadComplete(score);
        return true;
    }

    
    @Override
    public boolean write(Score data) {
        OutputStream out = this.getResourceOutput();
        if(out == null) {
            return false;
        }
        return Score.saveScore(data, out);
    }
}
