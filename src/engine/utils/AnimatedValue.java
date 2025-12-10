package engine.utils;

public final class AnimatedValue {

    private final float target;
    private final float duration;
    private float value;
    
    private float time;

    public AnimatedValue(float value, float target, float duration) {
        this.value = value;
        this.target = target;
        this.duration = duration;
    }

    public void update(float dt) {
        this.time += dt;
        if (this.time > this.duration) {
            this.time = this.duration;
        }

        // smoothstep ease in out
        float t = (float) (this.time / this.duration);
        t = t * t * (3 - 2 * t);

        this.value = this.value + t * (this.target - this.value);
    }

    public float getValue() {
        return this.value;
    }

    public boolean isFinished() {
        return this.time >= this.duration;
    }

    public void reset(float value) {
        this.value = value;
        this.time = 0.f;
    }
}
