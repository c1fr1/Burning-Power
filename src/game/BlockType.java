package game;

public enum BlockType {
	block, empty, unknown;
	public static BlockType generateRandomType() {
		if (Math.random() > 0.4) {
			return block;
		} else {
			return empty;
		}
	}
	public boolean isSolid() {
		return this.equals(block);
	}
}
