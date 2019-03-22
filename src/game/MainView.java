package game;

import engine.*;
import engine.OpenGL.*;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;

import static engine.EnigUtils.clearMatrix;
import static game.Shaders.lightShader;
import static java.lang.Math.PI;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MainView extends EnigView {
	public static MainView main;
	
	public static float z = 0;
	
	public Player player;
	
	public Map map;
	
	public static VAO lightSquare;
	
	public ArrayList<Wraith> wraiths;
	
	public ArrayList<Projectile> playerProjectiles;
	public ArrayList<Projectile> wraithProjectiles;
	
	public MainView(EnigWindow window) {
		super(window);
		window.toggleCursorInput();
		
		player = new Player(window);
		player.y += 0.5f;
		
		map = new Map();
		
		lightSquare = new VAO(-0.5f, -0.5f, 1f, 1f);
		
		playerProjectiles = new ArrayList<>();
		wraithProjectiles = new ArrayList<>();
		wraiths = new ArrayList<>();
		wraiths.add(new Wraith(5, 0));
		
		for (int x = -25; x < 25; ++x) {
			for (int y = -25; y < 25; ++y) {
				if (map.isInlightened(x, y)) {
					map.insertBlock(new Block(BlockType.empty, x, y));
				}
			}
		}
	}
	
	public boolean loop() {
		FBO.prepareDefaultRender();
		
		player.manage(window, map);
		map.render(player);
		
		manageProjectiles();
		
		Wraith.renderSet(wraiths, player);
		Projectile.renderSet(playerProjectiles, player, 1, 1, 1);
		Projectile.renderSet(wraithProjectiles, player, 0f, 0f, 1f);
		
		if (UserControls.quit(window)) {
			return true;
		}
		return false;
	}
	
	
	public void manageProjectiles() {
		if (window.mouseButtons[GLFW_MOUSE_BUTTON_LEFT] == 1) {
			playerProjectiles.add(new Projectile(player));
		}
		if (window.keys[GLFW_KEY_SPACE] == 1) {
			playerProjectiles.add(new Projectile(player));
		}
		Projectile proj;
		for (int i = 0; i < playerProjectiles.size(); ++i) {
			proj = playerProjectiles.get(i);
			
			proj.move(5 * deltaTime);
			
			float brightness = map.getPlayerBasedBrightness(player, proj.x, proj.z);
			brightness = 1 - (1 - brightness) * (1 - brightness);
			if (brightness < 0.8) {
				proj.brightness -= 0.01f * (brightness + 0.87);
			}
			
			if (proj.brightness < 0 ||
				proj.y < 0 ||
				map.isSolid(proj.x, proj.z)) {
				
				playerProjectiles.remove(i);
				--i;
				continue;
			}
			
			for (int j = 0; j < wraiths.size(); ++j) {
				if (wraiths.get(j).collidesWith(proj)) {
					wraiths.get(j).hp -= 0.5 * proj.brightness;
					
					if (wraiths.get(j).vhp < 0) {
						wraiths.remove(j);
					}
					
					playerProjectiles.remove(i);
					--i;
					break;
				}
			}
		}
		
		for (int i = 0; i < wraiths.size(); ++i) {
			Wraith w = wraiths.get(i);
			if (w.attackTimer > 1) {
				w.attackTimer = -0.25f;
				wraithProjectiles.add(new Projectile(w));
			}
		}
		
		for (int i = 0; i < wraithProjectiles.size(); ++i) {
			proj = wraithProjectiles.get(i);
			
			proj.move(5 * deltaTime);
			
			float brightness = map.getPlayerBasedBrightness(player, proj.x, proj.z);
			brightness = 1 - (1 - brightness) * (1 - brightness);
			if (brightness < 0.87) {
				proj.brightness -= 0.01f * (brightness + 0.87);
			}
			
			if (proj.brightness < 0 ||
				proj.y < 0 ||
				map.isSolid(proj.x, proj.z)) {
				
				wraithProjectiles.remove(i);
				--i;
				continue;
			}
			
			if (proj.distanceSquared(player) < 0.25f) {
				player.hp -= 0.01f;
				
				wraithProjectiles.remove(i);
				--i;
				continue;
			}
		}
	}
}
