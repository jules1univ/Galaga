package engine.utils;

public final class Time {
    private static double lastTime = 0.0;
    private static double deltaTime = 0.0;
    private final static double TIME_TO_NANO = 1_000_000_000.0;

    public static void update() {
        double currentTime = System.nanoTime() / TIME_TO_NANO;
        deltaTime = currentTime - lastTime;
        lastTime = currentTime;
    }

    public static void reset() {
        lastTime = System.nanoTime() / TIME_TO_NANO;
        deltaTime = 0;
    }

    public static float getDeltaTime() {
        return (float)deltaTime;
    }

    public static float getFrameRate() {
        return (float)(deltaTime == 0 ? 0 : 1.0 / deltaTime);
    }
}
