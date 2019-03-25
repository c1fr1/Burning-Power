package game.Views;

import engine.EnigView;
import engine.OpenGL.EnigWindow;
import engine.OpenGL.FBO;
import engine.OpenGL.Texture;
import engine.OpenGL.VAO;
import game.Main;
import game.UserControls;
import org.joml.Matrix4f;

import static game.Shaders.deadShader;
import static game.Shaders.flipShader;
import static game.Shaders.uiShader;

public class GameOverView extends EnigView {
	
	private Texture background;
	private Texture gameOverTexture;
	
	public float timer = 1;
	
	public GameOverView(EnigWindow window, Texture background) {
		super(window);
		this.background = background;
		gameOverTexture = new Texture("res/textures/gameOverText.png");
	}
	
	public boolean loop() {
		FBO.prepareDefaultRender();
		timer -= deltaTime * 0.5f;
		if (timer < 0.5) {
			timer = 0.5f;
		}
		
		deadShader.enable();
		deadShader.setUniform(2, 0, timer);
		background.bind();
		Main.screenObj.fullRender();
		
		uiShader.enable();
		uiShader.setUniform(0, 0, new Matrix4f(Main.squareCam).scale(25f));
		uiShader.setUniform(2, 0, window.cursorXFloat, window.cursorYFloat);
		gameOverTexture.bind();
		Main.screenObj.fullRender();
		
		
		if (UserControls.quit(window)) {
			return true;
		}
		return false;
	}
}
