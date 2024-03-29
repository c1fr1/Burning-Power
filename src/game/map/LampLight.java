package game.map;

import game.entities.Particle;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class LampLight extends Particle {
	
	public float hp = 1;
	
	public LampLight(Vector2f position) {
		x = position.x;
		y = 5.3f;
		z = position.y;
	}
	
	public Vector3f getColor() {
		return new Vector3f(1f * hp, 1f * hp, 1f * hp * 2f);
	}
	
	public float getSize() {
		return 2f;
	}
}
