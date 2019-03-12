package game;

import java.util.ArrayList;

public class Map {
	private ArrayList<Block> blocks;
	public Map() {
		blocks = new ArrayList<>();
	}
	public BlockType getType(int x, int y) {
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
					lowBound = (lowBound + highBound) / 2;
					continue;
				}
				if (temp.y > y) {
					highBound = (lowBound + highBound) / 2;
					continue;
				}
			}
			if (temp.x < x) {
				lowBound = (lowBound + highBound) / 2;
				continue;
			}
			if (temp.x > x) {
				highBound = (lowBound + highBound) / 2;
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
		int lowBound = 0;
		int highBound = blocks.size() - 1;
		Block temp = blocks.get((blocks.size() - 1) / 2);
		while (lowBound != highBound) {
			temp = blocks.get((lowBound + highBound) / 2);
			if (temp.x == block.x) {
				if (temp.y < block.y) {
					lowBound = (lowBound + highBound) / 2;
					continue;
				}
				if (temp.y > block.y) {
					highBound = (lowBound + highBound) / 2;
					continue;
				}
			}
			if (temp.x < block.x) {
				lowBound = (lowBound + highBound) / 2;
				continue;
			}
			if (temp.x > block.x) {
				highBound = (lowBound + highBound) / 2;
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
}
