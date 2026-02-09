package galaga.resources.score;

import java.io.InputStream;
import java.io.OutputStream;

import engine.resource.Resource;
import engine.resource.ResourceAlias;
import engine.resource.ResourceCallback;

public final class ScoreResource extends Resource<Score> {
    public static final String NAME = "score";

    public ScoreResource(ResourceAlias alias, ResourceCallback callback) {
        super(alias, callback);
    }

    @Override
    public Score read(InputStream in) {
        return Score.load(in);
    }

    @Override
    public boolean write(Score data) {
        OutputStream out = this.getResourceOutput();
        if (out == null) {
            return false;
        }
        return Score.saveScore(data, out);
    }
}
