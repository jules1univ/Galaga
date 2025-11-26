package engine.entity;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public final class EntityManager {

    private static EntityManager inst = null;
    private HashMap<Integer, Entity<?>> entities = new HashMap<>();

    private EntityManager() {
    }

    public static EntityManager getInstance() {
        if (inst == null) {
            inst = new EntityManager();
        }
        return inst;
    }

    public EntityManager add(Entity<?> e) {
        this.entities.put(e.getId(), e);
        return this;
    }

    public EntityManager remove(int id) {
        this.entities.remove(id);
        return this;
    }

    public Entity<?> get(int id) {
        return this.entities.get(id);
    }

    public boolean init() {
        return this.entities.values().stream().allMatch(e -> e.init());
    }

    public void update(double dt) {
        this.entities.values().forEach(e -> e.update(dt));
    }

    public void draw() {
        this.entities.values().forEach(e -> e.draw());
    }

    @SuppressWarnings("unchecked")
    public <Type extends Enum<Type>> List<Entity<Type>> filterByType(Type type) {
        return this.entities.values().stream()
                .filter(e -> e.getType() == type)
                .map(e -> (Entity<Type>) e)
                .collect(Collectors.toList());
    }

    public List<Entity<?>> filterByCollision(double x, double y, double width, double height) {
        return this.entities.values().stream().filter(e -> e.collideWith(x, y, width, height))
                .collect(Collectors.toList());
    }

    public List<Entity<?>> filterByCollision(Entity<?> e) {
        return this.filterByCollision(e.x, e.y, e.width, e.height);
    }
}
