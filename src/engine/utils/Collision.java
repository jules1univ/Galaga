package engine.utils;

public class Collision {
    public static boolean aabb(Position pA, Size sA, Position pB, Size sB) {
        return pA.getX() < pB.getX() + sB.getWidth()
                && pA.getX() + sA.getWidth() > pB.getX()
                && pA.getY() < pB.getY() + sB.getHeight()
                && pA.getY() + sA.getHeight() > pB.getY();
    }
}
