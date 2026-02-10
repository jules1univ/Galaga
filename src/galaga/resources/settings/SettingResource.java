package galaga.resources.settings;

import java.io.InputStream;
import java.io.OutputStream;

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
        return Setting.createSetting(in);
    }

    @Override
    public boolean write(Setting data, OutputStream out) {
        return Setting.saveSetting(data, out);
    }


}
