package game.level;

import engine.resource.Resource;
import engine.resource.ResourceAlias;
import engine.resource.ResourceCallback;
import java.io.InputStream;

public class LevelResource extends Resource<Level>{
    public static final String NAME = "level";
    
    public LevelResource(ResourceAlias alias, ResourceCallback callback) {
        super(alias, callback);
    }

    @Override
    public boolean load() {
        InputStream in = this.getResourceData();
        if(in == null) {
            return false;
        }
        Level level = Level.createLevel(in);
        if(level == null) {
            return false;
        }
        this.onLoadComplete(level);
        return true;
    }
    
}
