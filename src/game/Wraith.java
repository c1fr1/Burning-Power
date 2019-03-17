package game;

import engine.OpenGL.VAO;
import org.joml.Vector2f;

public class Wraith extends Vector2f {
	public static VAO model;
	public float attackTimer;
	
	public Wraith(float x, float y) {
	
	}
	
	public void renderSet() {
	
	}
	
	public static void loadModel() {
		model = new VAO("res/objects/wraith.obj");
	}
}
