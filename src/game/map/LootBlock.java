package game.map;

public class LootBlock extends Block {
	public float animationTimer = 0;
	public LootBlock(int x, int y) {
		super(BlockType.lootBlock, x, y);
	}
}
