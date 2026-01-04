package engine.utils;

public final class Collision {
    public static boolean aabb(Position pA, Size sA, Position pB, Size sB) {
        return pA.getX() < pB.getX() + sB.getWidth()
                && pA.getX() + sA.getWidth() > pB.getX()
                && pA.getY() < pB.getY() + sB.getHeight()
                && pA.getY() + sA.getHeight() > pB.getY();
    }

    public static boolean circle(Position cA, float rA, Position cB, float rB) {
        float dx = cA.getX() - cB.getX();
        float dy = cA.getY() - cB.getY();
        float dist = dx * dx + dy * dy;
        float radSum = rA + rB;
        return dist < radSum * radSum;
    }
}
