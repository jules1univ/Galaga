package engine.resource;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResourceAlias {

    private static Set<String> aliases = new HashSet<>();

    private final String name;
    private final File path;
    private final URI url;
    private ResourceVariant variant = null;

    public static ResourceAlias file(String name, String path, String url) {
        if (aliases.contains(name)) {
            throw new IllegalArgumentException("Alias already exists: " + name);
        }
        aliases.add(name);

        ResourceAlias alias = new ResourceAlias(name, path, url);
        return alias;
    }

    public static <E extends Enum<E>> List<ResourceAlias> folder(String prefix, int from, int to, String path, String url) {
        List<ResourceAlias> alias = new ArrayList<>();
        for (int i = from; i <= to; i++) {
            String name = String.format(prefix, i);
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

    public static <E extends Enum<E>> List<ResourceAlias> folder(Class<E> enumClass, String path, String url, ResourceVariant... variants) {
        List<ResourceAlias> aliasFinal = new ArrayList<>();

        for (E enumConst : enumClass.getEnumConstants()) {
            String name = enumConst.name().toLowerCase();
            List<ResourceAlias> alias = ResourceAlias.file(name, String.format(path, name), String.format(url, name)).variant(variants);
            aliasFinal.addAll(alias);
        }
        return aliasFinal;
    }

    public List<ResourceAlias> variant(ResourceVariant... variants) {
        List<ResourceAlias> alias = new ArrayList<>();
        for (ResourceVariant vrt : variants) {
            alias.add(new ResourceAlias(this, vrt));
        }
        return alias;
    }

    private ResourceAlias(String name, String path, String url) {
        this.name = name;
        // try {
        //     ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        //     this.path = new File(classLoader.getResource(path).toURI());
        // } catch (Exception e) {
        //     throw new IllegalArgumentException("Resource path is invalid: " + path);
        // }
        this.path = new File(new File(".").getAbsolutePath(), path);
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

    public String getFullName() {
        if (this.variant != null) {
            return this.name + this.variant.getName();
        }
        return this.name;
    }

    public ResourceVariant getVariant() {
        return this.variant;
    }
}
