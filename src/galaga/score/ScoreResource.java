package galaga.score;

import java.io.InputStream;

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
        InputStream in = this.getResourceData();
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
}
