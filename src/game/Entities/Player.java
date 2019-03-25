package game.Entities;

import engine.Entities.Camera;
import engine.OpenGL.EnigWindow;
import game.UserControls;
import game.map.BlockType;
import game.map.Map;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static org.joml.Math.sin;

public class Player extends Camera {
	public float hp = 1;
	public float light = 1.000001f;
	public float attackTimer = 0f;
	
	public Player(EnigWindow window) {
		super((float) PI * 0.25f, 0.01f, 1000f, window);
	}
	
	public Vector2f manage(EnigWindow window, Map map) {
		updateRotation(window);
		
		Vector2f offset = getMovementOffset(window);
		
		if (offset.lengthSquared() > 0.01f) {
			offset.normalize(0.02f);
			
			preventCollision(offset, map);
			
			translate(offset.x, 0f, offset.y);
			
			setPlayerLight(map);
			map.manage(this, offset);
		}
		return offset;
	}
	
	public Vector2f getMovementOffset(EnigWindow window) {
		Vector2f ret = new Vector2f();
		if (UserControls.forward(window)) {
			ret.add((float) sin(getYaw()),(float) -cos(getYaw()));
		}
		if (UserControls.backward(window)) {
			ret.add((float) -sin(getYaw()), (float) cos(getYaw()));
		}
		if (UserControls.left(window)) {
			ret.add((float) -cos(getYaw()), (float) -sin(getYaw()));
		}
		if (UserControls.right(window)) {
			ret.add((float) cos(getYaw()), (float) sin(getYaw()));
		}
		return ret;
	}
	
	public Vector2f preventCollision(Vector2f offset, Map map) {
		int blockX = (int) Math.floor(x + 0.5f);
		int blockY = (int) Math.floor(z + 0.5f);
		
		float intersectT = ((float) blockX - 0.48f - x) / offset.x;
		if (intersectT < 1f) {
			if (intersectT > 0f) {
				if (map.getType(blockX - 1, blockY).equals(BlockType.block)) {
					offset.x *= 0f;
				} else {
					if (z - (float) blockY > 0.48) {
						if (map.getType(blockX - 1, blockY + 1).equals(BlockType.block)) {
							offset.x *= 0;
						}
					} else if (z - (float) blockY < -0.48) {
						if (map.getType(blockX - 1, blockY - 1).equals(BlockType.block)) {
							offset.x *= 0;
						}
					}
				}
			}
		}
		intersectT = ((float) blockX + 0.48f - x) / offset.x;
		if (intersectT < 1f) {
			if (intersectT > 0f) {
				if (map.getType(blockX + 1, blockY).equals(BlockType.block)) {
					offset.x *= 0f;
				} else {
					if (z - (float) blockY > 0.48) {
						if (map.getType(blockX + 1, blockY + 1).equals(BlockType.block)) {
							offset.x *= 0;
						}
					} else if (z - (float) blockY < -0.48) {
						if (map.getType(blockX + 1, blockY - 1).equals(BlockType.block)) {
							offset.x *= 0;
						}
					}
				}
			}
		}
		intersectT = ((float) blockY - 0.48f - z) / offset.y;
		if (intersectT < 1f) {
			if (intersectT > 0f) {
				if (map.getType(blockX, blockY - 1).equals(BlockType.block)) {
					offset.y *= 0f;
				} else {
					if (x + offset.x - (float) blockX > 0.48) {
						if (map.getType(blockX + 1, blockY - 1).equals(BlockType.block)) {
							offset.y *= 0;
						}
					} else if (x + offset.x - (float) blockX < -0.48) {
						if (map.getType(blockX - 1, blockY - 1).equals(BlockType.block)) {
							offset.y *= 0;
						}
					}
				}
			}
		}
		intersectT = ((float) blockY + 0.48f - z) / offset.y;
		if (intersectT < 1f) {
			if (intersectT > 0f) {
				if (map.getType(blockX, blockY + 1).equals(BlockType.block)) {
					offset.y *= 0f;
				} else {
					if (x + offset.x - (float) blockX > 0.48) {
						if (map.getType(blockX + 1, blockY + 1).equals(BlockType.block)) {
							offset.y *= 0;
						}
					} else if (x + offset.x - (float) blockX < -0.48) {
						if (map.getType(blockX - 1, blockY + 1).equals(BlockType.block)) {
							offset.y *= 0;
						}
					}
				}
			}
		}
		return offset;
	}
	
	public void setPlayerLight(Map map) {
		map.lamps[0].x = x;
		map.lamps[0].y = z;
	}
	
	public void updateRotation(EnigWindow window) {
		yaw(-(float) window.cursorXOffset / 1000f);
		pitch(-(float) window.cursorYOffset / 1000f);
		if (getPitch() < (float) -PI / 2) {
			setPitch((float) -PI / 2);
		}
		if (getPitch() > (float) PI / 2) {
			setPitch((float) PI / 2);
		}
	}
	
	public Matrix4f reverseRotations(Matrix4f mat) {
		return mat.rotateZYX(-roll, -yaw, -pitch);
	}
}
