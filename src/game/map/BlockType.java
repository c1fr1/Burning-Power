package game.map;

public enum BlockType {
	block, empty, lootBlock, unknown;
	public boolean isSolid() {
		return this.equals(block);
	}
}
