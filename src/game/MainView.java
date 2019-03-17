package game;

import engine.*;
import engine.Entities.Camera;
import engine.OpenAL.Sound;
import engine.OpenAL.SoundSource;
import engine.OpenGL.*;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.system.CallbackI;

import java.util.ArrayList;

import static game.Shaders.*;
import static game.UserControls.*;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.random;
import static org.joml.Math.sin;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glDisable;

public class MainView extends EnigView {
	public static MainView main;
	
	public Camera playerCamera;
	
	public VAO floorPlane;
	public VAO blockCuboid;
	public VAO lamp;
	
	public Texture tile;
	
	public Map map;
	
	public float[] lightStrengths;
	public Vector2f[] lights;
	public int numLights = 3;
	
	public MainView(EnigWindow window) {
		super(window);
		window.toggleCursorInput();
		floorPlane = new VAO(-250, -250, 500, 500, true);
		blockCuboid = new VAO(-0.5f, 0f, -0.5f, 0.5f, 0.5f, 0.5f);
		lamp = new VAO("res/objects/light.obj");
		playerCamera = new Camera((float) PI * 0.25f, 0.01f, 1000f, window);
		playerCamera.y += 0.5f;
		
		map = new Map();
		
		lightStrengths = new float[] {1f, 10f, 10f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
		lights = new Vector2f[] {new Vector2f(0f, 0f), new Vector2f(0f, -10f), new Vector2f(0f, 0f), new Vector2f(0f, 0f), new Vector2f(0f, 0f), new Vector2f(0f, 0f), new Vector2f(0f, 0f), new Vector2f(0f, 0f), new Vector2f(0f, 0f), new Vector2f(0f, 0f)};
		
		tile = new Texture("res/textures/tile3.png");
		
		for (int x = -25; x < 25; ++x) {
			for (int y = -25; y < 25; ++y) {
				if (isInlightened(x, y)) {
					map.insertBlock(new Block(BlockType.empty, x, y));
				}
			}
		}
	}
	
	public boolean loop() {
		FBO.prepareDefaultRender();
		
		updatePlayerPosition();
		updateCameraRotation();
		renderWalls();
		renderFloor();
		
		if (UserControls.quit(window)) {
			return true;
		}
		return false;
	}
	
	public void renderWalls() {
		Matrix4f mat = playerCamera.getCameraMatrix();
		wallShader.enable();
		
		blockCuboid.prepareRender();
		for (int i = 0; i < map.blocks.size(); ++i) {
			Block b = map.blocks.get(i);
			if (b.type.equals(BlockType.block)) {
				wallShader.setUniform(0, 0, mat.translate(b.x, 0f, b.y, new Matrix4f()));
				blockCuboid.drawTriangles();
			}
		}
		blockCuboid.unbind();
		
		lamp.prepareRender();
		for (int i = 1; i < numLights; ++i) {
			wallShader.setUniform(0, 0, mat.translate(lights[i].x, 0f, lights[i].y, new Matrix4f()));
			lamp.drawTriangles();
		}
		lamp.unbind();
	}
	
	public void updatePlayerPosition() {
		Vector2f offset = new Vector2f(0f, 0f);
		if (UserControls.forward(window)) {
			offset.add((float) sin(playerCamera.getYaw()),(float) -cos(playerCamera.getYaw()));
		}
		if (UserControls.backward(window)) {
			offset.add((float) -sin(playerCamera.getYaw()), (float) cos(playerCamera.getYaw()));
		}
		if (UserControls.left(window)) {
			offset.add((float) -cos(playerCamera.getYaw()), (float) -sin(playerCamera.getYaw()));
		}
		if (UserControls.right(window)) {
			offset.add((float) cos(playerCamera.getYaw()), (float) sin(playerCamera.getYaw()));
		}
		if (offset.lengthSquared() > 0.01f) {
			offset.normalize(0.02f);
			
			int blockX = (int) Math.floor(playerCamera.x + 0.5f);
			int blockY = (int) Math.floor(playerCamera.z + 0.5f);
			
			float intersectT = ((float) blockX - 0.48f - playerCamera.x) / offset.x;
			if (intersectT < 1f) {
				if (intersectT > 0f) {
					if (map.getType(blockX - 1, blockY).equals(BlockType.block)) {
						offset.x *= 0f;
					} else {
						if (playerCamera.z - (float) blockY > 0.48) {
							if (map.getType(blockX - 1, blockY + 1).equals(BlockType.block)) {
								offset.x *= 0;
							}
						} else if (playerCamera.z - (float) blockY < -0.48) {
							if (map.getType(blockX - 1, blockY - 1).equals(BlockType.block)) {
								offset.x *= 0;
							}
						}
					}
				}
			}
			intersectT = ((float) blockX + 0.48f - playerCamera.x) / offset.x;
			if (intersectT < 1f) {
				if (intersectT > 0f) {
					if (map.getType(blockX + 1, blockY).equals(BlockType.block)) {
						offset.x *= 0f;
					} else {
						if (playerCamera.z - (float) blockY > 0.48) {
							if (map.getType(blockX + 1, blockY + 1).equals(BlockType.block)) {
								offset.x *= 0;
							}
						} else if (playerCamera.z - (float) blockY < -0.48) {
							if (map.getType(blockX + 1, blockY - 1).equals(BlockType.block)) {
								offset.x *= 0;
							}
						}
					}
				}
			}
			intersectT = ((float) blockY - 0.48f - playerCamera.z) / offset.y;
			if (intersectT < 1f) {
				if (intersectT > 0f) {
					if (map.getType(blockX, blockY - 1).equals(BlockType.block)) {
						offset.y *= 0f;
					} else {
						if (playerCamera.x + offset.x - (float) blockX > 0.48) {
							if (map.getType(blockX + 1, blockY - 1).equals(BlockType.block)) {
								offset.y *= 0;
							}
						} else if (playerCamera.x + offset.x - (float) blockX < -0.48) {
							if (map.getType(blockX - 1, blockY - 1).equals(BlockType.block)) {
								offset.y *= 0;
							}
						}
					}
				}
			}
			intersectT = ((float) blockY + 0.48f - playerCamera.z) / offset.y;
			if (intersectT < 1f) {
				if (intersectT > 0f) {
					if (map.getType(blockX, blockY + 1).equals(BlockType.block)) {
						offset.y *= 0f;
					} else {
						if (playerCamera.x + offset.x - (float) blockX > 0.48) {
							if (map.getType(blockX + 1, blockY + 1).equals(BlockType.block)) {
								offset.y *= 0;
							}
						} else if (playerCamera.x + offset.x - (float) blockX < -0.48) {
							if (map.getType(blockX - 1, blockY + 1).equals(BlockType.block)) {
								offset.y *= 0;
							}
						}
					}
				}
			}
			playerCamera.translate(offset.x, 0f, offset.y);
			lights[0].x = playerCamera.x;
			lights[0].y = playerCamera.z;
			manageMap(offset);
		}
	}
	
	public void manageMap(Vector2f offset) {
		int px = (int) Math.round(playerCamera.x);
		int py = (int) Math.round(playerCamera.z);
		if (offset.x > 0) {
			if (offset.y > 0) {//Q1
				for (int i = -2; i < 3; ++i) {
					if (!isInlightened(px + i, py - 2)) {
						int idx = map.getIndex(px + i, py - 2);
						if (idx >= 0) {
							map.blocks.remove(idx);
						}
					}
				}
				for (int i = -1; i < 3; ++i) {
					if (!isInlightened(px - 2, py + i)) {
						int idx = map.getIndex(px - 2, py + i);
						if (idx >= 0) {
							map.blocks.remove(idx);
						}
					}
				}
				for (int i = -1; i < 3; ++i) {
					int idx = map.getIndex(px + i, py + 2);
					if (idx == -1) {
						map.generateBlockAt(px + i, py + 2);
					}
				}
				for (int i = -1; i < 2; ++i) {
					int idx = map.getIndex(px + 2, py + i);
					if (idx == -1) {
						map.generateBlockAt(px + 2, py + i);
					}
				}
			} else {//Q4
				for (int i = -1; i < 3; ++i) {
					if (!isInlightened(px + i, py + 2)) {
						int idx = map.getIndex(px + i, py + 2);
						if (idx >= 0) {
							map.blocks.remove(idx);
						}
					}
				}
				for (int i = -2; i < 3; ++i) {
					if (!isInlightened(px - 2, py + i)) {
						int idx = map.getIndex(px - 2, py + i);
						if (idx >= 0) {
							map.blocks.remove(idx);
						}
					}
				}
				for (int i = -1; i < 3; ++i) {
					int idx = map.getIndex(px + i, py - 2);
					if (idx == -1) {
						map.generateBlockAt(px + i, py - 2);
					}
				}
				for (int i = -1; i < 2; ++i) {
					int idx = map.getIndex(px + 2, py + i);
					if (idx == -1) {
						map.generateBlockAt(px + 2, py + i);
					}
				}
			}
		} else {
			if (offset.y > 0) {//Q2
				for (int i = -2; i < 3; ++i) {
					if (!isInlightened(px + i, py - 2)) {
						int idx = map.getIndex(px + i, py - 2);
						if (idx >= 0) {
							map.blocks.remove(idx);
						}
					}
				}
				for (int i = -1; i < 3; ++i) {
					if (!isInlightened(px + 2, py + i)) {
						int idx = map.getIndex(px + 2, py + i);
						if (idx >= 0) {
							map.blocks.remove(idx);
						}
					}
				}
				for (int i = -2; i < 2; ++i) {
					int idx = map.getIndex(px + i, py + 2);
					if (idx == -1) {
						map.generateBlockAt(px + i, py + 2);
					}
				}
				for (int i = -1; i < 2; ++i) {
					int idx = map.getIndex(px - 2, py + i);
					if (idx == -1) {
						map.generateBlockAt(px - 2, py + i);
					}
				}
			} else {//Q3
				for (int i = -2; i < 3; ++i) {
					if (!isInlightened(px + i, py + 2)) {
						int idx = map.getIndex(px + i, py + 2);
						if (idx >= 0) {
							map.blocks.remove(idx);
						}
					}
				}
				for (int i = -2; i < 2; ++i) {
					if (!isInlightened(px + 2, py + i)) {
						int idx = map.getIndex(px + 2, py + i);
						if (idx >= 0) {
							map.blocks.remove(idx);
						}
					}
				}
				for (int i = -2; i < 2; ++i) {
					int idx = map.getIndex(px + i, py - 2);
					if (idx == -1) {
						map.generateBlockAt(px + i, py - 2);
					}
				}
				for (int i = -1; i < 2; ++i) {
					int idx = map.getIndex(px - 2, py + i);
					if (idx == -1) {
						map.generateBlockAt(px - 2, py + i);
					}
				}
			}
		}
	}
	
	public void updateCameraRotation() {
		playerCamera.yaw(-(float) window.cursorXOffset / 1000f);
		playerCamera.pitch(-(float) window.cursorYOffset / 1000f);
		if (playerCamera.getPitch() < (float) -PI / 2) {
			playerCamera.setPitch((float) -PI / 2);
		}
		if (playerCamera.getPitch() > (float) PI / 2) {
			playerCamera.setPitch((float) PI / 2);
		}
	}
	
	public void renderFloor() {
		Matrix4f mat = playerCamera.getCameraMatrix();
		floorShader.enable();
		tile.bind();
		floorShader.setUniform(0, 0, mat);
		floorShader.setUniform(2, 0, new Vector2f(playerCamera.x, playerCamera.z));
		floorShader.setUniform(2, 1, lights);
		floorShader.setUniform(2, 2, lightStrengths);
		floorShader.setUniform(2, 3, numLights);
		floorPlane.fullRender();
	}
	
	public boolean isInlightened(int bx, int by) {
		float x = (float) bx;
		float y = (float) by;
		float dx;
		float dy;
		for (int i = 1; i < numLights; ++i) {
			dx = x - lights[i].x;
			dy = y - lights[i].y;
			if (dx * dx + dy * dy < lightStrengths[i] * lightStrengths[i] + 2f + 1.5f * lightStrengths[i]) {
				return true;
			}
		}
		return false;
	}
	
	public float closestLightDistanceSquared(float x, float z) {
		float ret = Float.POSITIVE_INFINITY;
		float dist;
		for (int i = 0; i < numLights; ++i) {
			dist = lights[i].distanceSquared(x, z);
			if (dist < ret) {
				ret = dist;
			}
		}
		return ret;
	}
	
	public float closestLightDistance(float x, float z) {
		return (float) Math.sqrt(closestLightDistanceSquared(x, z));
	}
}
