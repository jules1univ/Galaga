package engine.utils;

public class AnimatedValue {

    private float value;
    private float target;
    private double duration;

    private double time;

    public AnimatedValue(float value, float target, double duration) {
        this.value = value;
        this.target = target;
        this.duration = duration;
    }

    public void update(double dt) {
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
}
