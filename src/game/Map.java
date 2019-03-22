package game;

import engine.Entities.Camera;
import engine.OpenGL.Texture;
import engine.OpenGL.VAO;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;

import static game.MainView.lightSquare;
import static game.Shaders.floorShader;
import static game.Shaders.lightShader;
import static game.Shaders.wallShader;

public class Map {
	public static VAO floorPlane;
	public static VAO blockCuboid;
	public static VAO lamp;
	
	public static Texture tile;
	
	public ArrayList<Block> blocks;
	public float[] lightStrengths;
	public Vector2f[] lights;
	public int numLights = 3;
	
	public Map() {
		blocks = new ArrayList<>();
		lightStrengths = new float[] {1f, 15f, 15f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
		lights = new Vector2f[] {new Vector2f(0f, 0f), new Vector2f(0f, -10f), new Vector2f(0f, 0f), new Vector2f(0f, 0f), new Vector2f(0f, 0f), new Vector2f(0f, 0f), new Vector2f(0f, 0f), new Vector2f(0f, 0f), new Vector2f(0f, 0f), new Vector2f(0f, 0f)};
	}
	
	public void render(Player playerCamera) {
		Matrix4f mat = playerCamera.getCameraMatrix();
		renderWalls(mat);
		renderLamps(playerCamera, mat);
		renderFloor(playerCamera, mat);
	}
	
	public void renderFloor(Camera playerCamera, Matrix4f mat) {
		floorShader.enable();
		tile.bind();
		floorShader.setUniform(0, 0, mat);
		floorShader.setUniform(2, 0, new Vector2f(playerCamera.x, playerCamera.z));
		floorShader.setUniform(2, 1, lights);
		floorShader.setUniform(2, 2, lightStrengths);
		floorShader.setUniform(2, 3, numLights);
		floorPlane.fullRender();
	}
	
	public void renderWalls(Matrix4f mat) {
		wallShader.enable();
		
		blockCuboid.prepareRender();
		for (int i = 0; i < blocks.size(); ++i) {
			Block b = blocks.get(i);
			if (b.type.equals(BlockType.block)) {
				wallShader.setUniform(0, 0, mat.translate(b.x, 0f, b.y, new Matrix4f()));
				blockCuboid.drawTriangles();
			}
		}
		blockCuboid.unbind();
	}
	
	public void renderLamps(Player player, Matrix4f mat) {
		lamp.prepareRender();
		for (int i = 1; i < numLights; ++i) {
			wallShader.setUniform(0, 0, mat.translate(lights[i].x, 0f, lights[i].y, new Matrix4f()));
			lamp.drawTriangles();
		}
		lamp.unbind();
		
		lightShader.enable();
		lightSquare.prepareRender();
		lightShader.setUniform(2, 0, 1f, 1f, 1f);
		lightShader.setUniform(0, 1, 0.001f);
		for (int i = 1; i < numLights; ++i) {
			lightShader.setUniform(0, 0, player.reverseRotations(new Matrix4f(mat).translate(lights[i].x, 5.3f, lights[i].y).scale(2f)));
			lightSquare.drawTriangles();
		}
		lightSquare.unbind();
		
	}
	
	public int getIndex(int x, int y) {
		if (blocks.size() == 0) {
			return -1;
		}
		int lowBound = 0;
		int highBound = blocks.size() - 1;
		Block temp = blocks.get((blocks.size() - 1) / 2);
		while (lowBound != highBound) {
			temp = blocks.get((lowBound + highBound) / 2);
			if (temp.x == x) {
				if (temp.y == y) {
					return (lowBound + highBound) / 2;
				}
				if (temp.y < y) {
					int nLowBound = (lowBound + highBound) / 2;
					if (nLowBound == lowBound) {
						lowBound += 1;
					} else {
						lowBound = nLowBound;
					}
				}
				if (temp.y > y) {
					int nHighBound = (lowBound + highBound) / 2;
					if (nHighBound == highBound) {
						nHighBound -= 1;
					}else {
						highBound = nHighBound;
					}
					continue;
				}
			}
			if (temp.x < x) {
				int nLowBound = (lowBound + highBound) / 2;
				if (nLowBound == lowBound) {
					lowBound += 1;
				} else {
					lowBound = nLowBound;
				}
				continue;
			}
			if (temp.x > x) {
				int nHighBound = (lowBound + highBound) / 2;
				if (nHighBound == highBound) {
					nHighBound -= 1;
				}else {
					highBound = nHighBound;
				}
				continue;
			}
		}
		temp = blocks.get(lowBound);
		if (temp.x == x && temp.y == y) {
			return lowBound;
		}
		return -1;
	}
	
	public BlockType getType(float x, float y) {
		return getType(Math.round(x), Math.round(y));
	}
	
	public BlockType getType(int x, int y) {
		if (blocks.size() == 0) {
			return BlockType.unknown;
		}
		int lowBound = 0;
		int highBound = blocks.size() - 1;
		Block temp = blocks.get((blocks.size() - 1) / 2);
		while (lowBound != highBound) {
			temp = blocks.get((lowBound + highBound) / 2);
			if (temp.x == x) {
				if (temp.y == y) {
					return temp.type;
				}
				if (temp.y < y) {
					int nLowBound = (lowBound + highBound) / 2;
					if (nLowBound == lowBound) {
						lowBound += 1;
					} else {
						lowBound = nLowBound;
					}
				}
				if (temp.y > y) {
					int nHighBound = (lowBound + highBound) / 2;
					if (nHighBound == highBound) {
						nHighBound -= 1;
					}else {
						highBound = nHighBound;
					}
					continue;
				}
			}
			if (temp.x < x) {
				int nLowBound = (lowBound + highBound) / 2;
				if (nLowBound == lowBound) {
					lowBound += 1;
				} else {
					lowBound = nLowBound;
				}
				continue;
			}
			if (temp.x > x) {
				int nHighBound = (lowBound + highBound) / 2;
				if (nHighBound == highBound) {
					nHighBound -= 1;
				}else {
					highBound = nHighBound;
				}
				continue;
			}
		}
		temp = blocks.get(lowBound);
		if (temp.x == x && temp.y == y) {
			return temp.type;
		}
		return BlockType.unknown;
	}
	
	public boolean isSolid(float x, float y) {
		return getType(x, y).isSolid();
	}
	
	public boolean isSolid(int x, int y) {
		return getType(x, y).isSolid();
	}
	
	public BlockType generateBlockAt(int x, int y) {
		Block b;
		if (getType(x + 1, y).equals(BlockType.block)) {
			b = new Block(BlockType.generateRandomType(), x, y);
		} else if (getType(x - 1, y).equals(BlockType.block)) {
			b = new Block(BlockType.generateRandomType(), x, y);
		} else if (getType(x, y + 1).equals(BlockType.block)) {
			b = new Block(BlockType.generateRandomType(), x, y);
		} else if (getType(x, y - 1).equals(BlockType.block)) {
			b = new Block(BlockType.generateRandomType(), x, y);
		} else if (getType(x - 1, y - 1).equals(BlockType.block)) {
			b = new Block(BlockType.empty, x, y);
		} else if (getType(x - 1, y + 1).equals(BlockType.block)) {
			b = new Block(BlockType.empty, x, y);
		} else if (getType(x + 1, y - 1).equals(BlockType.block)) {
			b = new Block(BlockType.empty, x, y);
		} else if (getType(x + 1, y + 1).equals(BlockType.block)) {
			b = new Block(BlockType.empty, x, y);
		} else {
			b = new Block(BlockType.generateRandomType(), x, y);
		}
		insertBlock(b);
		return b.type;
	}
	
	public void insertBlock(Block block) {
		if (blocks.size() == 0) {
			blocks.add(block);
			return;
		}
		int lowBound = 0;
		int highBound = blocks.size() - 1;
		Block temp = blocks.get((blocks.size() - 1) / 2);
		while (lowBound != highBound) {
			temp = blocks.get((lowBound + highBound) / 2);
			if (temp.x == block.x) {
				if (temp.y < block.y) {
					int nLowBound = (lowBound + highBound) / 2;
					if (nLowBound == lowBound) {
						lowBound += 1;
					} else {
						lowBound = nLowBound;
					}
					continue;
				}
				if (temp.y > block.y) {
					int nHighBound = (lowBound + highBound) / 2;
					if (nHighBound == highBound) {
						nHighBound -= 1;
					}else {
						highBound = nHighBound;
					}
					continue;
				}
			}
			if (temp.x < block.x) {
				int nLowBound = (lowBound + highBound) / 2;
				if (nLowBound == lowBound) {
					lowBound += 1;
				} else {
					lowBound = nLowBound;
				}
				continue;
			}
			if (temp.x > block.x) {
				int nHighBound = (lowBound + highBound) / 2;
				if (nHighBound == highBound) {
					nHighBound -= 1;
				}else {
					highBound = nHighBound;
				}
				continue;
			}
		}
		temp = blocks.get(lowBound);
		if (compareBlocks(block.x, block.y, temp)) {
			blocks.add(lowBound, block);
		}else {
			blocks.add(lowBound + 1, block);
		}
	}
	
	private boolean compareBlocks(int x, int y, Block temp) {
		if (temp.x == x) {
			if (temp.y < y) {
				return false;
			}
			if (temp.y > y) {
				return true;
			}
		}
		if (temp.x < x) {
			return false;
		}
		if (temp.x > x) {
			return true;
		}
		return false;
	}
	
	public void manage(Vector3f player, Vector2f offset) {
		int px = (int) Math.round(player.x);
		int py = (int) Math.round(player.z);
		if (offset.x > 0) {
			if (offset.y > 0) {//Q1
				for (int i = -2; i < 3; ++i) {
					if (!isInlightened(px + i, py - 2)) {
						int idx = getIndex(px + i, py - 2);
						if (idx >= 0) {
							blocks.remove(idx);
						}
					}
				}
				for (int i = -1; i < 3; ++i) {
					if (!isInlightened(px - 2, py + i)) {
						int idx = getIndex(px - 2, py + i);
						if (idx >= 0) {
							blocks.remove(idx);
						}
					}
				}
				for (int i = -1; i < 3; ++i) {
					int idx = getIndex(px + i, py + 2);
					if (idx == -1) {
						generateBlockAt(px + i, py + 2);
					}
				}
				for (int i = -1; i < 2; ++i) {
					int idx = getIndex(px + 2, py + i);
					if (idx == -1) {
						generateBlockAt(px + 2, py + i);
					}
				}
			} else {//Q4
				for (int i = -1; i < 3; ++i) {
					if (!isInlightened(px + i, py + 2)) {
						int idx = getIndex(px + i, py + 2);
						if (idx >= 0) {
							blocks.remove(idx);
						}
					}
				}
				for (int i = -2; i < 3; ++i) {
					if (!isInlightened(px - 2, py + i)) {
						int idx = getIndex(px - 2, py + i);
						if (idx >= 0) {
							blocks.remove(idx);
						}
					}
				}
				for (int i = -1; i < 3; ++i) {
					int idx = getIndex(px + i, py - 2);
					if (idx == -1) {
						generateBlockAt(px + i, py - 2);
					}
				}
				for (int i = -1; i < 2; ++i) {
					int idx = getIndex(px + 2, py + i);
					if (idx == -1) {
						generateBlockAt(px + 2, py + i);
					}
				}
			}
		} else {
			if (offset.y > 0) {//Q2
				for (int i = -2; i < 3; ++i) {
					if (!isInlightened(px + i, py - 2)) {
						int idx = getIndex(px + i, py - 2);
						if (idx >= 0) {
							blocks.remove(idx);
						}
					}
				}
				for (int i = -1; i < 3; ++i) {
					if (!isInlightened(px + 2, py + i)) {
						int idx = getIndex(px + 2, py + i);
						if (idx >= 0) {
							blocks.remove(idx);
						}
					}
				}
				for (int i = -2; i < 2; ++i) {
					int idx = getIndex(px + i, py + 2);
					if (idx == -1) {
						generateBlockAt(px + i, py + 2);
					}
				}
				for (int i = -1; i < 2; ++i) {
					int idx = getIndex(px - 2, py + i);
					if (idx == -1) {
						generateBlockAt(px - 2, py + i);
					}
				}
			} else {//Q3
				for (int i = -2; i < 3; ++i) {
					if (!isInlightened(px + i, py + 2)) {
						int idx = getIndex(px + i, py + 2);
						if (idx >= 0) {
							blocks.remove(idx);
						}
					}
				}
				for (int i = -2; i < 2; ++i) {
					if (!isInlightened(px + 2, py + i)) {
						int idx = getIndex(px + 2, py + i);
						if (idx >= 0) {
							blocks.remove(idx);
						}
					}
				}
				for (int i = -2; i < 2; ++i) {
					int idx = getIndex(px + i, py - 2);
					if (idx == -1) {
						generateBlockAt(px + i, py - 2);
					}
				}
				for (int i = -1; i < 2; ++i) {
					int idx = getIndex(px - 2, py + i);
					if (idx == -1) {
						generateBlockAt(px - 2, py + i);
					}
				}
			}
		}
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
	
	public float getPlayerBasedBrightness(Vector3f pos, float x, float z) {
		float ret = 0;
		float dist;
		for (int i = 0; i < numLights; ++i) {
			dist = (float) Math.pow(Math.E, -2 * lights[i].distanceSquared(x, z) / (lightStrengths[i] * lightStrengths[i])) - 0.135335283237f;
			dist *= (float) Math.pow(Math.E, -2 * lights[i].distanceSquared(pos.x, pos.z) / (lightStrengths[i] * lightStrengths[i])) - 0.135335283237f;
			if (dist > ret) {
				ret = dist;
			}
		}
		return ret;
	}
	
	public float getBrightness(float x, float z) {
		float ret = 0;
		float dist;
		for (int i = 0; i < numLights; ++i) {
			dist = (float) Math.pow(Math.E, -2 * lights[i].distanceSquared(x, z) / (lightStrengths[i] * lightStrengths[i])) - 0.135335283237f;
			if (dist > ret) {
				ret = dist;
			}
		}
		return ret;
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
	
	public static void loadResources() {
		floorPlane = new VAO(-250, -250, 500, 500, true);
		blockCuboid = new VAO(-0.5f, 0f, -0.5f, 0.5f, 0.5f, 0.5f);
		lamp = new VAO("res/objects/light.obj");
		
		tile = new Texture("res/textures/tile3.png");
	}
}
