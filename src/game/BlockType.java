package game;

public enum BlockType {
	block, empty, unknown;
	public static BlockType generateRandomType() {
		if (Math.random() > 0.5) {
			return block;
		} else {
			return empty;
		}
	}
}
