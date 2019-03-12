package game;

import engine.OpenGL.ShaderProgram;

public class Shaders {
	public static ShaderProgram flipShader;
	public static ShaderProgram textureShader;
	public static ShaderProgram floorShader;
	public static void createMainShaders() {
		flipShader = new ShaderProgram("flipShader");
		textureShader = new ShaderProgram("textureShader");
		floorShader = new ShaderProgram("floorShader");
	}
}
