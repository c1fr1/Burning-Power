package game.map;

import engine.Entities.Camera;
import engine.OpenGL.Texture;
import engine.OpenGL.VAO;
import game.entities.Player;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;

import static game.Shaders.floorShader;
import static game.Shaders.lootDropShader;
import static game.Shaders.wallShader;

public class Map {
	public static VAO floorPlane;
	public static VAO blockCuboid;
	public static VAO lamp;
	public static VAO lootBlock;
	
	public static Texture tile;
	
	public ArrayList<Block> blocks;
	public float[] lampStrengths;
	public Vector2f[] lamps;
	public LampLight[] lampParticles;
	public int numLamps = 3;
	
	public float[] playerDistances;
	
	public int spawnFails = 0;
	
	public Map() {
		blocks = new ArrayList<>();
		lampStrengths = new float[] {5f, 15f, 15f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
		lamps = new Vector2f[] {new Vector2f(0f, 0f), new Vector2f(0f, -10f), new Vector2f(0f, 0f), new Vector2f(0f, 0f), new Vector2f(0f, 0f), new Vector2f(0f, 0f), new Vector2f(0f, 0f), new Vector2f(0f, 0f), new Vector2f(0f, 0f), new Vector2f(0f, 0f)};
		lampParticles = new LampLight[9];
		lampParticles[0] = new LampLight(lamps[1]);
		lampParticles[1] = new LampLight(lamps[2]);
		playerDistances = new float[10];
		playerDistances[0] = 1;
	}
	
	public void generateXSet(int minX, int maxX, int y) {
		for (int i = minX; i <= maxX; ++i) {
			if (getIndex(i, y) == -1) {
				generateBlockAt(i, y);
			}
		}
	}
	
	public void generateYSet(int x, int minY, int maxY) {
		for (int i = minY; i <= maxY; ++i) {
			if (getIndex(x, i) == -1) {
				generateBlockAt(x, i);
			}
		}
	}
	
	public void removeXSet(int minX, int maxX, int y) {
		for (int i = minX; i <= maxX; ++i) {
			if (!isInlightened(i, y)) {
				int index = getIndex(i, y);
				if (index >= 0) {
					blocks.remove(index);
				}
			}
		}
	}
	
	public void removeYSet(int x, int minY, int maxY) {
		for (int i = minY; i <= maxY; ++i) {
			if (!isInlightened(x, i)) {
				int index = getIndex(x, i);
				if (index >= 0) {
					blocks.remove(index);
				}
			}
		}
	}
	
	public void manage(Vector3f player) {
		int px = (int) Math.round(player.x);
		int py = (int) Math.round(player.z);
		
		int ri = Math.round(lampStrengths[0]);
		
		removeXSet(px - ri - 2, px + ri + 1,py + ri + 2);
		removeXSet(px - ri - 1, px + ri + 2,py - ri - 2);
		removeYSet(px + ri + 2, py - ri - 2,py + ri + 1);
		removeYSet(px - ri - 2, py - ri - 1,py + ri + 2);
		
		
		generateXSet(px - ri - 1, px + ri,py + ri + 1);
		generateXSet(px - ri, px + ri + 1,py - ri - 1);
		generateYSet(px + ri + 1, py - ri - 1,py + ri);
		generateYSet(px - ri - 1, py - ri,py + ri + 1);
	}
	
	public void render(Player playerCamera) {
		Matrix4f mat = playerCamera.getCameraMatrix();
		renderWalls(mat);
		renderLamps(playerCamera, mat);
		renderLootDrops(mat);
		renderFloor(playerCamera, mat);
	}
	
	public void renderFloor(Camera playerCamera, Matrix4f mat) {
		
		for (int i = 1; i < numLamps; ++i) {
			playerDistances[i] = getBrightness(playerCamera.x, playerCamera.z, i);
		}
		
		floorShader.enable();
		tile.bind();
		floorShader.setUniform(0, 0, mat);
		floorShader.setUniform(2, 0, playerDistances);
		floorShader.setUniform(2, 1, lamps);
		floorShader.setUniform(2, 2, lampStrengths);
		floorShader.setUniform(2, 3, numLamps);
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
	
	public void renderLootDrops(Matrix4f mat)  {
		lootDropShader.enable();
		lootBlock.prepareRender();
		lootDropShader.setUniform(2, 0, playerDistances);
		lootDropShader.setUniform(2, 1, lamps);
		lootDropShader.setUniform(2, 2, lampStrengths);
		lootDropShader.setUniform(2, 3, numLamps);
		for (int i = 0; i < blocks.size(); ++i) {
			Block b = blocks.get(i);
			if (b.type.equals(BlockType.lootBlock)) {
				lootDropShader.setUniform(0, 0, mat.translate(b.x, 0.25f, b.y, new Matrix4f()));
				lootDropShader.setUniform(0, 1, new Matrix4f().translate(b.x, 0.25f, b.y));
				lootDropShader.setUniform(0, 2, ((LootBlock) b).animationTimer);
				lootBlock.drawTriangles();
			}
		}
		lootBlock.unbind();
	}
	
	public void renderLamps(Player player, Matrix4f mat) {
		lamp.prepareRender();
		for (int i = 1; i < numLamps; ++i) {
			wallShader.setUniform(0, 0, mat.translate(lamps[i].x, 0f, lamps[i].y, new Matrix4f()));
			lamp.drawTriangles();
		}
		lamp.unbind();
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
			b = Block.generateRandomBlock(x, y);
		} else if (getType(x - 1, y).equals(BlockType.block)) {
			b = Block.generateRandomBlock(x, y);
		} else if (getType(x, y + 1).equals(BlockType.block)) {
			b = Block.generateRandomBlock(x, y);
		} else if (getType(x, y - 1).equals(BlockType.block)) {
			b = Block.generateRandomBlock(x, y);
		} else if (getType(x - 1, y - 1).equals(BlockType.block)) {
			b = new Block(BlockType.empty, x, y);
		} else if (getType(x - 1, y + 1).equals(BlockType.block)) {
			b = new Block(BlockType.empty, x, y);
		} else if (getType(x + 1, y - 1).equals(BlockType.block)) {
			b = new Block(BlockType.empty, x, y);
		} else if (getType(x + 1, y + 1).equals(BlockType.block)) {
			b = new Block(BlockType.empty, x, y);
		} else {
			b = Block.generateRandomBlock(x, y);
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
	
	public boolean isInlightened(int bx, int by) {
		float x = (float) bx;
		float y = (float) by;
		float dx;
		float dy;
		for (int i = 1; i < numLamps; ++i) {
			dx = x - lamps[i].x;
			dy = y - lamps[i].y;
			if (dx * dx + dy * dy < lampStrengths[i] * lampStrengths[i] + 2f + 1.5f * lampStrengths[i]) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isInlightened(float x, float y) {
		float dx;
		float dy;
		for (int i = 1; i < numLamps; ++i) {
			dx = x - lamps[i].x;
			dy = y - lamps[i].y;
			if (dx * dx + dy * dy < lampStrengths[i] * lampStrengths[i] + 2f + 1.5f * lampStrengths[i]) {
				return true;
			}
		}
		return false;
	}
	
	public float getPlayerBasedBrightness(Vector3f pos, float x, float z) {
		float ret = 0;
		float dist;
		for (int i = 0; i < numLamps; ++i) {
			dist = (float) Math.pow(Math.E, -2 * lamps[i].distanceSquared(x, z) / (lampStrengths[i] * lampStrengths[i])) - 0.135335283237f;
			dist *= (float) Math.pow(Math.E, -2 * lamps[i].distanceSquared(pos.x, pos.z) / (lampStrengths[i] * lampStrengths[i])) - 0.135335283237f;
			if (dist > ret) {
				ret = dist;
			}
		}
		return ret;
	}
	
	public float getBrightness(float x, float z) {
		float ret = 0;
		float dist;
		for (int i = 0; i < numLamps; ++i) {
			dist = (float) Math.pow(Math.E, -2 * lamps[i].distanceSquared(x, z) / (lampStrengths[i] * lampStrengths[i])) - 0.135335283237f;
			if (dist > ret) {
				ret = dist;
			}
		}
		return ret;
	}
	
	public float getBrightness(float x, float z, int i) {
		return (float) Math.pow(Math.E, -2 * lamps[i].distanceSquared(x, z) / (lampStrengths[i] * lampStrengths[i])) - 0.135335283237f;
	}
	
	public float closestLightDistanceSquared(float x, float z) {
		float ret = Float.POSITIVE_INFINITY;
		float dist;
		for (int i = 0; i < numLamps; ++i) {
			dist = lamps[i].distanceSquared(x, z);
			if (dist < ret) {
				ret = dist;
			}
		}
		return ret;
	}
	
	public float closestLightDistance(float x, float z) {
		return (float) Math.sqrt(closestLightDistanceSquared(x, z));
	}
	
	public void removeLight(int index) {
		for (int i = index; i < numLamps; ++i) {
			if (i + 1 < lamps.length) {
				lampStrengths[i] = lampStrengths[i + 1];
				lamps[i] = lamps[i + 1];
				lampParticles[i - 1] = lampParticles[i];
			}
		}
		--numLamps;
	}
	
	public static void loadResources() {
		floorPlane = new VAO(-250, -250, 500, 500, true);
		blockCuboid = new VAO(-0.5f, 0f, -0.5f, 0.5f, 0.5f, 0.5f);
		lamp = new VAO("res/objects/light.obj");
		lootBlock = new VAO("res/objects/lootDrop.obj");
		tile = new Texture("res/textures/tile3.png");
	}
	
	public void generateLamp(float x, float y) {
		lampStrengths[numLamps] = 15f;
		lamps[numLamps] = new Vector2f(x, y);
		lampParticles[numLamps - 1] = new LampLight(lamps[numLamps]);
		
		for (int bx = (int) Math.floor(x - 15f); bx <= (int) Math.ceil(x + 15f); ++bx) {
			for (int by = (int) Math.floor(y - 15f); by <= (int) Math.ceil(y + 15f); ++by) {
				if (isInlightened(bx, by)) {
					if (getIndex(bx, by) == -1) {
						generateBlockAt(bx, by);
					}
				}
			}
		}
		
		++numLamps;
	}
}
