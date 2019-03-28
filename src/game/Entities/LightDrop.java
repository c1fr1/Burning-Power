package game.entities;

import engine.OpenGL.VAO;
import game.map.Map;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;

import static game.Shaders.dropShader;

public class LightDrop extends Vector3f {
	
	public static VAO box;
	
	public float rotation;
	
	public float yvel;
	
	public float scale;
	
	public LightDrop(Wraith wraith) {
		scale = 0.05f + (float) Math.random() * 0.1f;
		rotation = 2f * (float) (Math.random() * Math.PI);
		this.x = wraith.x + (float) Math.random() - 0.5f;
		this.y = (float) Math.random();
		this.z = wraith.z + (float) Math.random() - 0.5f;
	}
	
	public LightDrop(float sx, float sy) {
		scale = 0.05f + (float) Math.random() * 0.1f;
		rotation = 2f * (float) (Math.random() * Math.PI);
		this.x = sx + (float) Math.random() * 0.5f - 0.25f;
		this.y = 0;
		this.z = sy + (float) Math.random() * 0.5f - 0.25f;
	}
	
	public LightDrop() {
		super();
		scale = 0.2f;
		rotation = (float) Math.PI / 4f;
		x = 1f;
	}
	
	public static void renderSet(ArrayList<LightDrop> drops, Map map, Player player) {
		Matrix4f mat = player.getCameraMatrix();
		dropShader.enable();
		dropShader.setUniform(2, 0, map.playerDistances);
		dropShader.setUniform(2, 1, map.lamps);
		dropShader.setUniform(2, 2, map.lampStrengths);
		dropShader.setUniform(2, 3, map.numLamps);
		Matrix4f transformationMatrix;
		box.prepareRender();
		for (int i = 0; i < drops.size(); ++i) {
			transformationMatrix = new Matrix4f().translate(drops.get(i)).rotateY(drops.get(i).rotation).scale(drops.get(i).scale);
			dropShader.setUniform(0, 1, transformationMatrix);
			dropShader.setUniform(0, 0, mat.mul(transformationMatrix, transformationMatrix));
			box.drawTriangles();
		}
		box.unbind();
	}
	
	public static void manageSet(ArrayList<LightDrop> drops, Player player) {
		for (int i = 0; i < drops.size(); ++i) {
			LightDrop d = drops.get(i);
			if (d.manage(player)) {
				player.light += 30 * d.scale * d.scale * d.scale;
				drops.remove(i);
				--i;
			}
		}
		if (player.light > 1) {
			player.light = 1;
		}
	}
	
	public boolean manage(Player player) {
		float dsqr = distanceSquared(player);
		if (dsqr < 0.25f) {
			return true;
		}
		if (dsqr < 2) {
			Vector3f delta = player.sub(this, new Vector3f());
			delta.normalize(0.01f/dsqr);
			delta.y *= 0.25f;
			add(delta);
			yvel = 0;
		}
		yvel -= 0.001f;
		y += yvel;
		if (y < 0) {
			y = 0;
			yvel = 0;
		}
		return false;
	}
	
	public static void loadResources() {
		box = new VAO(-1f, 0f, -1f, 1f, 2f, 1f);
	}
}
