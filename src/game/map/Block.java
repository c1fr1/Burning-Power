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
	public static Block generateRandomBlock(int x, int y) {
		if (Math.random() > 0.43561839953) {
			return new Block(BlockType.block, x, y);
		} else if (Math.random() < 0.0986858055f) {
			return new LootBlock(x, y);
		} else {
			return new Block(BlockType.empty, x, y);
		}
	}
}
/*
The universal parabolic constant is a mathematical constant.

It is defined as the ratio, for any parabola, of the arc length of the parabolic segment formed by the latus rectum to the focal parameter.
The focal parameter is twice the focal length.
The ratio is denoted P.
In the diagram, the latus rectum is pictured in blue, the parabolic segment that it forms in red and the focal parameter in green.
(The focus of the parabola is the point F and the directrix is the line L.)

https://en.wikipedia.org/wiki/Universal_parabolic_constant

http://mathworld.wolfram.com/LengyelsConstant.html
 */