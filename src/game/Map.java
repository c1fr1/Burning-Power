package game;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;

public class Map {
	public ArrayList<Block> blocks;
	public Map() {
		blocks = new ArrayList<>();
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
	
	/*public void manageMap(Vector3f player, Vector2f offset) {
		int px = (int) Math.round(player.x);
		int py = (int) Math.round(player.z);
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
	}*/
}
