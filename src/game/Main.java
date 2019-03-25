package game;

import engine.OpenAL.SoundSource;
import engine.OpenGL.EnigWindow;
import engine.OpenGL.VAO;
import game.Entities.LightDrop;
import game.Entities.Wraith;
import game.Views.GameOverView;
import game.Views.MainMenuView;
import game.Views.MainView;
import game.map.Map;
import org.joml.Matrix4f;

import java.io.IOException;

public class Main {
	public static VAO screenObj;
	public static SoundSource source;
	public static GameOverView gameOverView;
	public static MainMenuView mainMenuView;
	public static Matrix4f squareCam;
	public static void main(String[] args) {
		if (args.length == 0) {
			String os = System.getProperty("os.name");
			System.out.println("Operating System: " + os);
			if (os.contains("mac") || os.contains("Mac")) {
				System.out.println("in order to get a stack trace, run with\njava -jar 'Burning Power.jar' noReRun -XstartOnFirstThread");
				try {
					Runtime.getRuntime().exec(new String[]{"java", "-XstartOnFirstThread", "-jar", "'Flaming Resistance.jar'", "noReRun"});
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else {
				runGame();
			}
		}else if (args[0].equals("noReRun")) {
			runGame();
		}
	}
	public static void runGame() {
		EnigWindow.runOpeningSequence = false;
		EnigWindow window = new EnigWindow("Burning Power", "res/textures/icon.png");
		squareCam = window.getSquarePerspectiveMatrix(100);
		source = new SoundSource();
		loadResources();
		screenObj = new VAO(-1, -1, 2, 2);
		window.fps = 60;
		MainView.main = new MainView(window);
		gameOverView = new GameOverView(window, MainView.main.mainFrameBuffer.getBoundTexture());
		mainMenuView = new MainMenuView(window);
		window.toggleCursorInput();
		mainMenuView.runLoop();
		window.terminate();
	}
	
	public static void loadResources() {
		Map.loadResources();
		Wraith.loadResources();
		LightDrop.loadResources();
		Shaders.createMainShaders();
	}
}
