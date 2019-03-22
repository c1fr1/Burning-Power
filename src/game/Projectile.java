package game;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;

import static game.MainView.lightSquare;
import static game.Shaders.lightShader;

public class Projectile extends Vector3f {
	
	public Vector3f direction;
	
	public float brightness = 1;
	
	public Projectile(Player player) {
		x = player.x;
		y = player.y;
		z = player.z;
		Vector4f fullDirection = new Vector4f(0, 0, 1, 1).mul(player.rotationMatrix);
		direction = new Vector3f((float) Math.sin(player.getYaw()) * (float) Math.cos(player.getPitch()), -(float) Math.sin(player.getPitch()), -(float) Math.cos(player.getYaw()) * (float) Math.cos(player.getPitch()));
	}
	
	public Projectile(Wraith wraith) {
		this.x = wraith.x;
		this.y = 1.1f;
		this.z = wraith.y;
		direction = new Vector3f((float) Math.cos(wraith.rotation), -0.2f, (float) -Math.sin(wraith.rotation));
		direction.normalize();
	}
	
	public static void renderSet(ArrayList<Projectile> projectiles, Player player, float r, float g, float b) {
		Matrix4f mat = player.getCameraMatrix();
		
		lightShader.enable();
		lightSquare.prepareRender();
		for (int i = 0; i < projectiles.size(); ++i) {
			lightShader.setUniform(2, 0, r, g, b);
			lightShader.setUniform(0, 0, player.reverseRotations(new Matrix4f(mat).translate(projectiles.get(i).x, projectiles.get(i).y, projectiles.get(i).z).scale(projectiles.get(i).brightness * 0.5f)));
			lightSquare.drawTriangles();
		}
		lightSquare.unbind();
	}
	
	public void move(float speed) {
		add(direction.mul(speed, new Vector3f()));
	}
}
