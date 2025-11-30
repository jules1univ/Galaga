package game.entities.enemies;

public class EnemyBee extends Enemy {

    private final int circleX = 325;
    private final int circleY = 200;
    private final int radius = 150;
    private float rotateAngle = 0.f;


    public EnemyBee(float lockX, float lockY, int value, float speed) {
        super(EnemyType.BEE, lockX, lockY, value, speed);
    }

    @Override
    public void update(double dt) {
        this.rotateAngle += this.speed * dt;
        
        this.x = circleX + (float) Math.cos(this.rotateAngle) * radius;
        this.y = circleY + (float) Math.sin(this.rotateAngle) * radius;
        this.angle = (float) Math.toDegrees(this.rotateAngle) + 180;
    }

}
