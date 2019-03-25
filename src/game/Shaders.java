package game;

import engine.OpenGL.ShaderProgram;

public class Shaders {
	public static ShaderProgram flipShader;
	public static ShaderProgram textureShader;
	public static ShaderProgram floorShader;
	public static ShaderProgram wallShader;
	public static ShaderProgram wraithShader;
	public static ShaderProgram lightShader;
	public static ShaderProgram hpShader;
	public static ShaderProgram lightBarShader;
	public static ShaderProgram deadShader;
	public static ShaderProgram uiShader;
	public static ShaderProgram dropShader;
	public static void createMainShaders() {
		flipShader = new ShaderProgram("flipShader");
		textureShader = new ShaderProgram("textureShader");
		floorShader = new ShaderProgram("floorShader");
		wallShader = new ShaderProgram("wallShader");
		wraithShader = new ShaderProgram("wraithShader");
		lightShader = new ShaderProgram("lightShader");
		hpShader = new ShaderProgram("hpShader");
		lightBarShader = new ShaderProgram("lightBarShader");
		deadShader = new ShaderProgram("deadShader");
		uiShader = new ShaderProgram("uiShader");
		dropShader = new ShaderProgram("dropShader");
	}
}
