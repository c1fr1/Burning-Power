package game;

import engine.OpenGL.VAO;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static engine.EnigUtils.getAngle;
import static game.MainView.lightSquare;
import static game.Shaders.lightShader;
import static game.Shaders.wraithShader;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Wraith extends Vector2f {
	public static VAO model;
	public float attackTimer;
	public float rotation;
	public float perlinTime = 0;
	public float hp = 1;
	public float vhp = 1;
	
	public Wraith(float x, float y) {
		super(x, y);
		perlinTime = 1000f * (float) Math.random();
		rotation = (float) (Math.random() * Math.PI) * 2f;
	}
	
	public static void renderSet(ArrayList<Wraith> wraiths, Player player) {
		Matrix4f mat = player.getCameraMatrix();
		
		manageSet(wraiths, player);
		
		wraithShader.enable();
		model.prepareRender();
		wraithShader.enable();
		for (int i = 0; i < wraiths.size(); ++i) {
			wraiths.get(i).manage(player);
			if (wraiths.get(i).vhp < 0) {
				wraiths.remove(i);
				--i;
				continue;
			}
			wraiths.get(i).perlinTime += 0.03f;
			wraithShader.setUniform(2, 0, wraiths.get(i).perlinTime);
			wraithShader.setUniform(2, 1, wraiths.get(i).vhp);
			wraithShader.setUniform(0, 1, (float) Math.sin(wraiths.get(i).perlinTime) / 4);
			wraithShader.setUniform(0, 0, new Matrix4f(mat).translate(wraiths.get(i).x, 0f, wraiths.get(i).y, new Matrix4f()).rotateY(wraiths.get(i).rotation));
			model.drawTriangles();
		}
		model.unbind();
		
		lightShader.enable();
		lightSquare.prepareRender();
		for (int i = 0; i < wraiths.size(); ++i) {
			Wraith w = wraiths.get(i);
			if (w.attackTimer > 0) {
				lightShader.setUniform(2, 0, 0f, 0f, w.attackTimer);
				lightShader.setUniform(0, 0, player.reverseRotations(new Matrix4f(mat).translate(w.x, 1.1f, w.y).scale(0.5f)));
				lightSquare.drawTriangles();
			}
		}
		lightSquare.unbind();
	}
	
	public void manage(Player player) {
		float dx = player.x - x;
		float dy = player.z - y;
		face(dx, dy);
		if (dx * dx + dy * dy < 9 || attackTimer > 0) {
			attackTimer += 0.01;
		} else {
			moveForward();
		}
		updateHP();
	}
	
	public void face(float dx, float dy) {
		
		float targetAngle = -getAngle((float) atan2(dy, dx));
		
		float dtheta = (targetAngle - rotation) % (2f * (float) Math.PI);
		if (dtheta < 0) {
			dtheta += 2f * Math.PI;
		}
		
		if (dtheta > Math.PI) {
			rotation -= 0.01f;
			if (dtheta > (float) Math.PI * 2 - 0.01) {
				rotation = targetAngle;
			}
		}else {
			rotation += 0.01f;
			if (rotation < 0.01) {
				rotation = targetAngle;
			}
		}
		
		rotation = getAngle(rotation);
	}
	
	public void moveForward() {
		this.x += cos(rotation) * 0.008f;
		this.y += -sin(rotation) * 0.008f;
	}
	
	public void updateHP() {
		hp += 0.002;
		if (hp > 1) {
			hp = 1;
		}
		
		vhp += (hp - vhp) * 0.02f;
	}
	
	public static void manageSet(ArrayList<Wraith> wraiths, Player position) {
		if (Math.random() < 0.001) {
			Vector2f pos = new Vector2f((float) Math.random() - 0.5f, (float) Math.random() - 0.5f);
			pos.normalize(10f + 5f * (float) Math.random());
			wraiths.add(new Wraith(position.x + pos.x, position.z + pos.y));
		}
	}
	
	public boolean collidesWith(Vector3f point) {
		if (point.distanceSquared(new Vector3f(x, 0.4f, y)) < 0.25) {
			return true;
		} else {
			return false;
		}
	}
	
	public static void loadResources() {
		model = new VAO("res/objects/wraith.obj");
	}
}
