package game.map;

public class Block {
	public BlockType type;
	public int x;
	public int y;
	public Block(BlockType t, int x, int y) {
		type = t;
		this.x = x;
		this.y = y;
	}
}
