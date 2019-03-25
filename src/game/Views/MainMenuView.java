package game.Views;

import engine.EnigView;
import engine.OpenGL.EnigWindow;
import engine.OpenGL.FBO;
import engine.OpenGL.Texture;
import game.Main;
import game.UserControls;
import org.joml.Matrix4f;

import static game.Shaders.uiShader;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MainMenuView extends EnigView {
	
	private Texture titleText;
	private Texture playText;
	private Texture quitText;
	
	private boolean pendingUP;
	
	public MainMenuView(EnigWindow swindow) {
		super(swindow);
		titleText = new Texture("res/textures/titleText.png");
		playText = new Texture("res/textures/play.png");
		quitText = new Texture("res/textures/quit.png");
	}
	
	public boolean loop() {
		FBO.prepareDefaultRender();
		
		if (window.mouseButtons[GLFW_MOUSE_BUTTON_LEFT] > 0) {
			pendingUP = true;
		}
		
		uiShader.enable();
		uiShader.setUniform(2, 0, window.cursorXFloat, window.cursorYFloat);
		Main.screenObj.prepareRender();
		
		renderTitle();
		
		if (renderPlayButton()) {
			return false;
		}
		
		if (renderQuitButton()) {
			if (	window.mouseButtons[GLFW_MOUSE_BUTTON_LEFT] == 0 &&
					pendingUP) {
				return true;
			}
		}
		
		Main.screenObj.unbind();
		
		if (window.mouseButtons[GLFW_MOUSE_BUTTON_LEFT] == 0) {
			pendingUP = false;
		}
		
		if (UserControls.quit(window)) {
			return true;
		}
		return false;
	}
	public void renderTitle() {
		uiShader.setUniform(2, 1, 1f, 1f, 1f);
		uiShader.setUniform(0, 0, new Matrix4f(Main.squareCam).scale(29f / 1.1f, 10f, 10f).translate(0f, 3.7f, 0f));
		titleText.bind();
		Main.screenObj.drawTriangles();
	}
	public boolean renderPlayButton() {
		if (	window.cursorYFloat > -0.1 &&
				window.cursorYFloat < 0.1 &&
				window.cursorXFloat * 50 * window.getAspectRatio() > -40 * window.getAspectRatio() - 10f &&
				window.cursorXFloat * 50 * window.getAspectRatio() < -40 * window.getAspectRatio()) {
			if (window.mouseButtons[GLFW_MOUSE_BUTTON_LEFT] == 0 && pendingUP) {
				window.toggleCursorInput();
				MainView.main.runLoop();
				window.toggleCursorInput();
				pendingUP = false;
				return true;
			}
			uiShader.setUniform(2, 1, 1f, 1f, 1f);
		} else {
			uiShader.setUniform(2, 1, 0.5f, 0.5f, 0.5f);
		}
		uiShader.setUniform(0, 0, new Matrix4f(Main.squareCam).translate(-40f * window.getAspectRatio(), 0f, 0f).scale(10f, 5f, 1f));
		playText.bind();
		Main.screenObj.drawTriangles();
		return false;
	}
	public boolean renderQuitButton() {
		boolean ret = false;
		if (	window.cursorYFloat < -0.1 &&
				window.cursorYFloat > -0.3 &&
				window.cursorXFloat * 50 * window.getAspectRatio() > -40 * window.getAspectRatio() - 10f &&
				window.cursorXFloat * 50 * window.getAspectRatio() < -40 * window.getAspectRatio()) {
			uiShader.setUniform(2, 1, 1f, 1f, 1f);
			ret = true;
		} else {
			uiShader.setUniform(2, 1, 0.5f, 0.5f, 0.5f);
		}
		uiShader.setUniform(0, 0, new Matrix4f(Main.squareCam).translate(-40f * window.getAspectRatio(), -10f, 0f).scale(10f, 5f, 1f));
		quitText.bind();
		Main.screenObj.drawTriangles();
		return ret;
	}
}
