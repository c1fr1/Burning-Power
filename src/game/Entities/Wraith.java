package game.entities;

import engine.OpenGL.OBJInformation;
import engine.OpenGL.VAO;
import engine.Platform.Ray3f;
import engine.Platform.Simplex2v3d;
import game.views.MainView;
import game.map.Map;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;

import static engine.EnigUtils.getAngle;
import static game.Shaders.wraithShader;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Wraith extends Particle {
	public static VAO model;
	public static Simplex2v3d[] simplices;
	public float attackTimer;
	public float rotation;
	public float perlinTime = 0;
	public float hp = 1;
	public float vhp = 1;
	public Vector3f[] impacts;
	public float[] impactStrengths;
	public static final int maxImpactCount = 5;
	
	public Vector3f target;
	
	public Wraith(float x, float y) {
		this.x = x;
		this.y = 1.1f;
		this.z = y;
		perlinTime = 1000f * (float) Math.random();
		rotation = (float) (Math.random() * Math.PI) * 2f;
		impacts = new Vector3f[maxImpactCount];
		impactStrengths = new float[maxImpactCount];
		
		for (int i = 0; i < maxImpactCount; ++i) {
			impactStrengths[i] = -0.0001f;
			impacts[i] = new Vector3f();
		}
	}
	
	public static void renderSet(ArrayList<Wraith> wraiths, Player player) {
		Matrix4f mat = player.getCameraMatrix();
		
		wraithShader.enable();
		model.prepareRender();
		wraithShader.enable();
		for (int i = 0; i < wraiths.size(); ++i) {
			wraiths.get(i).perlinTime += 0.03f;
			wraithShader.setUniform(2, 0, wraiths.get(i).perlinTime);
			wraithShader.setUniform(2, 1, wraiths.get(i).vhp);
			wraithShader.setUniform(0, 1, (float) Math.sin(wraiths.get(i).perlinTime) / 4);
			wraithShader.setUniform(0, 0, new Matrix4f(mat).translate(wraiths.get(i).x, 0f, wraiths.get(i).z, new Matrix4f()).rotateY(wraiths.get(i).rotation));
			wraithShader.setUniform(2, 2, wraiths.get(i).impacts);
			wraithShader.setUniform(2, 3, wraiths.get(i).impactStrengths);
			
			model.drawTriangles();
		}
		model.unbind();
	}
	
	public void manage(Vector3f player, Map m) {
		findTarget(player, m);
		attackTarget();
		updateHP(m);
		updateImpacts();
	}
	
	public void updateImpacts() {
		for (int i = 0; i < maxImpactCount; ++i) {
			if (impactStrengths[i] > 0) {
				impactStrengths[i] -= 0.01f;
			} else {
				impactStrengths[i] = -0.0000001f;
			}
		}
	}
	
	public void findTarget(Vector3f player, Map m) {
		if (attackTimer <= 0) {
			target = player;
			float dsqr = distanceSquared(player);
			float other;
			for (int i = 0; i < m.numLamps - 1; ++i) {
				other = distanceSquared(m.lampParticles[i]);
				if (other < dsqr) {
					target = m.lampParticles[i];
					dsqr = other;
				}
			}
		}
	}
	
	public void attackTarget() {
		float dx = target.x - x;
		float dy = target.z - z;
		face(dx, dy);
		if (dx * dx + dy * dy < 9 || attackTimer > 0) {
			attackTimer += 0.01;
		} else {
			moveForward();
		}
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
		this.z += -sin(rotation) * 0.008f;
	}
	
	public void updateHP(Map map) {
		hp += 0.003 * (1 - map.getBrightness(this.x, this.z));
		if (hp > 1) {
			hp = 1;
		}
		
		vhp += (hp - vhp) * 0.02f;
	}
	
	public static void manageSet(ArrayList<Wraith> wraiths, Player player, Map map) {
		for (int i = 0; i < wraiths.size(); ++i) {
			wraiths.get(i).manage(player, map);
			if (wraiths.get(i).vhp < 0) {
				for (int j = 0; j < 1 + Math.random() * 5; ++j) {
					MainView.main.drops.add(new LightDrop(wraiths.get(i)));
				}
				wraiths.remove(i);
				--i;
			}
		}
		if (Math.random() < 0.001 + 0.002 * map.spawnFails) {
			Vector2f pos = new Vector2f((float) Math.random() - 0.5f, (float) Math.random() - 0.5f);
			pos.normalize(10f + 5f * (float) Math.random() + map.spawnFails * 2f);
			if (!map.isInlightened(player.x + pos.x, player.z + pos.y)) {
				wraiths.add(new Wraith(player.x + pos.x, player.z + pos.y));
				map.spawnFails = 0;
			}else {
				++map.spawnFails;
			}
		}
	}
	
	public boolean collidesWith(Ray3f point) {
		point.translate(-x, 0f, -z);
		point.rotateY(-rotation);
		for (int i = 0; i < simplices.length; ++i) {
			Vector3f intersection = point.intersectionPoint(simplices[i]);
			if (intersection != null) {
				for (int j = 0; j < impacts.length; ++j) {
					if (impactStrengths[j] < 0) {
						impactStrengths[j] = 1f;
						impacts[j] = intersection;
						return true;
					}
				}
				return true;
			}
		}
		return false;
	}
	
	public static void loadResources() {
		model = new VAO("res/objects/wraith.obj");
		simplices = OBJInformation.getSimplexArray("res/objects/wraithHitbox.obj");
	}
	
	public Vector3f getColor() {
		return new Vector3f(0f, 0f, attackTimer);
	}
	
	public float getSize() {
		return 0.5f;
	}
}
