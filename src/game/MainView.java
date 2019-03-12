package game;

import engine.*;
import engine.Entities.Camera;
import engine.OpenAL.Sound;
import engine.OpenAL.SoundSource;
import engine.OpenGL.*;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import java.util.ArrayList;

import static game.Shaders.*;
import static game.UserControls.*;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.random;
import static org.joml.Math.sin;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glDisable;

public class MainView extends EnigView {
	public static MainView main;
	
	public Camera playerCamera;
	
	public VAO floorPlane;
	
	public Texture tile;
	
	public MainView(EnigWindow window) {
		super(window);
		window.toggleCursorInput();
		floorPlane = new VAO(-250, -250, 500, 500, true);
		playerCamera = new Camera((float) PI * 0.25f, 0.1f, 1000f, window);
		playerCamera.y += 0.5f;
		
		tile = new Texture("res/textures/tile3.png");
	}
	
	public boolean loop() {
		FBO.prepareDefaultRender();
		
		Vector2f offset = new Vector2f(0f, 0f);
		if (UserControls.forward(window)) {
			offset.add((float) sin(playerCamera.getYaw()),(float) -cos(playerCamera.getYaw()));
		}
		if (UserControls.backward(window)) {
			offset.add((float) -sin(playerCamera.getYaw()), (float) cos(playerCamera.getYaw()));
		}
		if (UserControls.left(window)) {
			offset.add((float) -cos(playerCamera.getYaw()), (float) -sin(playerCamera.getYaw()));
		}
		if (UserControls.right(window)) {
			offset.add((float) cos(playerCamera.getYaw()), (float) sin(playerCamera.getYaw()));
		}
		if (offset.lengthSquared() > 0.01f) {
			offset.normalize(0.02f);
			playerCamera.translate(offset.x, 0f, offset.y);
		}
		
		updateCameraRotation();
		renderFloor();
		
		if (UserControls.quit(window)) {
			return true;
		}
		return false;
	}
	
	public void updateCameraRotation() {
		playerCamera.yaw(-(float) window.cursorXOffset / 1000f);
		playerCamera.pitch(-(float) window.cursorYOffset / 1000f);
		if (playerCamera.getPitch() < (float) -PI / 2) {
			playerCamera.setPitch((float) -PI / 2);
		}
		if (playerCamera.getPitch() > (float) PI / 2) {
			playerCamera.setPitch((float) PI / 2);
		}
	}
	
	public void renderFloor() {
		Matrix4f mat = playerCamera.getCameraMatrix();
		floorShader.enable();
		tile.bind();
		floorShader.setUniform(0, 0, mat);
		floorShader.setUniform(2, 0, new Vector2f(0, -1));
		floorShader.setUniform(2, 1, new Vector2f(playerCamera.x, playerCamera.z));
		floorShader.setUniform(2, 2, 10f);
		floorPlane.fullRender();
	}
}
