package galaga.resources.settings;

import java.io.InputStream;

import engine.resource.Resource;
import engine.resource.ResourceAlias;
import engine.resource.ResourceCallback;

public final class SettingResource extends Resource<Setting> {

    public static final String NAME = "setting";

    public SettingResource(ResourceAlias alias, ResourceCallback callback) {
        super(alias, callback);
    }

    @Override
    public Setting read(InputStream in) {
        return Setting.load(in);
    }

    @Override
    public boolean write(Setting data) {
        return false;
    }


}
