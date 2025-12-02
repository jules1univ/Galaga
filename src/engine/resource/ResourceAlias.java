package engine.resource;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResourceAlias {
    private static Set<String> aliases = new HashSet<>();

    private String name;
    private File path;
    private URI url;
    private ResourceVariant variant = null;

    public static ResourceAlias file(String name, String path, String url) {
        if (aliases.contains(name)) {
            throw new IllegalArgumentException("Alias already exists: " + name);
        }
        aliases.add(name);

        ResourceAlias alias = new ResourceAlias(name, path, url);
        return alias;
    }

    
    public static <E extends Enum<E>> List<ResourceAlias> folder(String[] names, String path, String url) {
        List<ResourceAlias> alias = new ArrayList<>();
        for (String name : names) {
            alias.add(ResourceAlias.file(name, String.format(path, name), String.format(url, name)));
        }
        return alias;
    }

    public static <E extends Enum<E>> List<ResourceAlias> folder(Class<E> enumClass, String path, String url) {
        List<ResourceAlias> alias = new ArrayList<>();
        for (E enumConst : enumClass.getEnumConstants()) {
            String name = enumConst.name().toLowerCase();
            alias.add(ResourceAlias.file(name, String.format(path, name), String.format(url, name)));
        }
        return alias;
    }

    public static <E extends Enum<E>>  List<ResourceAlias> folder(Class<E> enumClass, String path, String url, ResourceVariant ...variants) {
        List<ResourceAlias> aliasFinal = new ArrayList<>();

        for (E enumConst : enumClass.getEnumConstants()) {
            String name = enumConst.name().toLowerCase();
            List<ResourceAlias> alias = ResourceAlias.file(name, String.format(path, name), String.format(url, name)).variant(variants);
            aliasFinal.addAll(alias);
        }
        return aliasFinal;
    }

    
    public List<ResourceAlias> variant(ResourceVariant ...variants)
    {   
        List<ResourceAlias> alias = new ArrayList<>();
        for (ResourceVariant variant : variants) {
            alias.add(new ResourceAlias(this, variant));
        }
        return alias;
    }

    private ResourceAlias(String name, String path, String url) {
        this.name = name;
        this.path = new File(path);
        this.url = URI.create(url);
    }

    private ResourceAlias(ResourceAlias base, ResourceVariant variant) {
        this.name = base.name;
        this.path = base.path;
        this.url = base.url;
        this.variant = variant;
    }


    public String getName() {
        return this.name;
    }

    public File getPath() {
        return this.path;
    }

    public URI getUrl() {
        return this.url;
    }


    public ResourceVariant getVariant() {
        return this.variant;
    }
}
