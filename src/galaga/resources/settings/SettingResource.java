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
    public boolean load() {
        InputStream in = this.getResourceInput();
        if (in == null) {
            return false;
        }
        Setting setting = Setting.load(in);
        if (setting == null) {
            return false;
        }
        this.onLoadComplete(setting);
        return true;
    }

    @Override
    public boolean write(Setting data) {
        return false;
    }

}
