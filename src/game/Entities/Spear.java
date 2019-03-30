package game.entities;

import engine.OpenGL.VAO;
import engine.Platform.Ray3f;
import game.map.Map;
import org.joml.Matrix4f;

import java.util.ArrayList;

import static game.Shaders.dropShader;

public class Spear extends Ray3f {
	
	public static VAO spear;
	
	public float yRotation;
	public float xRotation;
	
	public Spear(Player player) {
		super(player.x, player.y, player.z, (float) Math.sin(player.getYaw()) * (float) Math.cos(player.getPitch()), -(float) Math.sin(player.getPitch()), -(float) Math.cos(player.getYaw()) * (float) Math.cos(player.getPitch()));
		delta.mul(0.1f);
		yRotation = player.getYaw();
		xRotation = player.getPitch();
	}
	public Spear(float x, float z) {
		super(x, (float) Math.random() * 0.5f, z, 0f, 0f, 0f);
	}
	
	public static void renderSet(ArrayList<Spear> spears, Matrix4f mat, Map map) {
		dropShader.enable();
		dropShader.setUniform(2, 0, map.playerDistances);
		dropShader.setUniform(2, 1, map.lamps);
		dropShader.setUniform(2, 2, map.lampStrengths);
		dropShader.setUniform(2, 3, map.numLamps);
		Matrix4f transformationMatrix;
		spear.prepareRender();
		for (int i = 0; i < spears.size(); ++i) {
			Spear s = spears.get(i);
			transformationMatrix = new Matrix4f().translate(s.start.x, s.start.y, s.start.z).rotateY((float) Math.PI / 2f - s.yRotation).rotateZ(-s.xRotation);
			dropShader.setUniform(0, 1, transformationMatrix);
			dropShader.setUniform(0, 0, mat.mul(transformationMatrix, transformationMatrix).scale( 0.1f));
			spear.drawTriangles();
		}
		spear.unbind();
	}
	
	public static void loadResources() {
		spear = new VAO("res/objects/spear.obj");
	}
}
//http://mathworld.wolfram.com/Alladi-GrinsteadConstant.html
