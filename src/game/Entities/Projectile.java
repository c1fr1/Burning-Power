package game.entities;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;

import static game.Views.MainView.lightSquare;
import static game.Shaders.lightShader;

public class Projectile extends Particle {
	
	public Vector3f direction;
	
	private Vector3f color;
	
	public float brightness = 1;
	
	public Projectile(Player player) {
		x = player.x;
		y = player.y;
		z = player.z;
		color = new Vector3f(1f, 1f, 1f);
		Vector4f fullDirection = new Vector4f(0, 0, 1, 1).mul(player.rotationMatrix);
		direction = new Vector3f((float) Math.sin(player.getYaw()) * (float) Math.cos(player.getPitch()), -(float) Math.sin(player.getPitch()), -(float) Math.cos(player.getYaw()) * (float) Math.cos(player.getPitch()));
	}
	
	public Projectile(Wraith wraith) {
		this.x = wraith.x;
		this.y = 1.1f;
		this.z = wraith.z;
		color = new Vector3f(0f, 0f, 1f);
		direction = wraith.target.sub(wraith, new Vector3f());
		direction.normalize();
		float length = (float) Math.sqrt(direction.x * direction.x + direction.z * direction.z);
		direction.x = (float) Math.cos(wraith.rotation) * length;
		direction.z = (float) -Math.sin(wraith.rotation) * length;
	}
	
	public static void renderSet(ArrayList<Projectile> projectiles, Player player, float r, float g, float b) {
		Matrix4f mat = player.getCameraMatrix();
		
		lightShader.enable();
		lightSquare.prepareRender();
		for (int i = 0; i < projectiles.size(); ++i) {
			lightShader.setUniform(2, 0, r, g, b);
			lightShader.setUniform(0, 0, player.reverseRotations(new Matrix4f(mat).translate(projectiles.get(i)).scale(projectiles.get(i).brightness * 0.5f)));
			lightSquare.drawTriangles();
		}
		lightSquare.unbind();
	}
	
	public void move(float speed) {
		add(direction.mul(speed, new Vector3f()));
	}
	
	public Vector3f getColor() {
		return color;
	}
	
	public float getSize() {
		return 0.5f * brightness;
	}
}
