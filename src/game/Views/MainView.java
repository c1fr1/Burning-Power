package game.views;

import engine.*;
import engine.OpenGL.*;
import engine.Platform.Ray3f;
import game.*;
import game.entities.*;
import game.map.Block;
import game.map.BlockType;
import game.map.LootBlock;
import game.map.Map;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;

import static engine.EnigUtils.clearMatrix;
import static game.Shaders.dropShader;
import static game.Shaders.hpShader;
import static game.Shaders.lightBarShader;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MainView extends EnigView {
	public static MainView main;
	
	public Player player;
	
	public Map map;
	
	public FBO mainFrameBuffer;
	
	public static VAO lightSquare;
	public static VAO hpBar;
	
	public ArrayList<Wraith> wraiths;
	
	public ArrayList<LightDrop> drops;
	public ArrayList<Spear> spearDrops;
	
	public ArrayList<Projectile> playerProjectiles;
	public ArrayList<Projectile> wraithProjectiles;
	public ArrayList<Spear> playerSpears;
	
	public MainView(EnigWindow window) {
		super(window);
		window.toggleCursorInput();
		
		player = new Player(window);
		player.y += 0.5f;
		
		map = new Map();
		
		mainFrameBuffer = new FBO(new Texture(window.getWidth(), window.getHeight()));
		
		lightSquare = new VAO(-0.5f, -0.5f, 1f, 1f);
		hpBar = new VAO(-1f, 0.98f, 2f, 0.02f);
		
		playerProjectiles = new ArrayList<>();
		wraithProjectiles = new ArrayList<>();
		playerSpears = new ArrayList<>();
		drops = new ArrayList<>();
		wraiths = new ArrayList<>();
		spearDrops = new ArrayList<>();
		wraiths.add(new Wraith(5, 0));
		
		for (int x = -25; x < 25; ++x) {
			for (int y = -25; y < 25; ++y) {
				if (map.isInlightened(x, y)) {
					map.insertBlock(new Block(BlockType.empty, x, y));
				}
			}
		}
	}
	
	public void reset() {
		Main.gameOverView.timer = 1;
		player.hp = 1;
		player.x = 0f;
		player.z = 0f;
		wraiths = new ArrayList<>();
		wraithProjectiles = new ArrayList<>();
		playerProjectiles = new ArrayList<>();
	}
	
	public boolean loop() {
		
		manageScene();
		
		prepRender();
		renderScene();
		checkDeath();
		
		if (UserControls.quit(window)) {
			return true;
		}
		return false;
	}
	
	public void manageScene() {
		player.manage(window, map);
		managePlayerProjectiles();
		manageSpears();
		manageWraithProjectiles();
		LightDrop.manageSet(drops, player);
		Wraith.manageSet(wraiths, player, map);
		manageSpearDrops();
		checkLootDrop();
	}
	
	public void prepRender() {
		if (player.hp > 0) {
			FBO.prepareDefaultRender();
		}else {
			mainFrameBuffer.prepareForTexture();
		}
	}
	
	public void renderScene() {
		renderHPBar();
		renderLightBar();
		map.render(player);
		Wraith.renderSet(wraiths, player);
		LightDrop.renderSet(drops, map, player);
		Spear.renderSet(playerSpears, player.getCameraMatrix(), map);
		renderParticles();
		renderSpearDrops();
	}
	
	public void checkDeath() {
		if (player.hp <= 0) {
			window.toggleCursorInput();
			Main.gameOverView.runLoop();
			window.toggleCursorInput();
			reset();
		}
	}
	
	public void renderParticles() {
		Particle[] renderSet = new Particle[wraithProjectiles.size() + playerProjectiles.size() + wraiths.size() + map.numLamps - 1];
		for (int i = 0; i < wraithProjectiles.size(); ++i) {
			renderSet[i] = wraithProjectiles.get(i);
		}
		for (int i = 0; i < playerProjectiles.size(); ++i) {
			renderSet[i + wraithProjectiles.size()] = playerProjectiles.get(i);
		}
		for (int i = 0; i < wraiths.size(); ++i) {
			renderSet[i + wraithProjectiles.size() + playerProjectiles.size()] = wraiths.get(i);
		}
		for (int i = 0; i < map.numLamps - 1; ++i) {
			renderSet[i + wraithProjectiles.size() + playerProjectiles.size() + wraiths.size()] = map.lampParticles[i];
		}
		Particle.renderParticles(renderSet, player);
	}
	
	public void renderHPBar() {
		hpShader.enable();
		hpShader.setUniform(2, 0, player.hp);
		hpBar.fullRender();
	}
	
	public void renderLightBar() {
		lightBarShader.enable();
		lightBarShader.setUniform(2, 0, player.light - 0.05f * player.attackTimer);
		hpBar.fullRender();
	}
	
	public void checkLootDrop() {
		Block b = map.blocks.get(map.getIndex((int) Math.round(player.x), (int) Math.round(player.z)));
		if (b.type.equals(BlockType.lootBlock)) {
			LootBlock lb = (LootBlock) b;
			lb.animationTimer += 0.005f;
			if (lb.animationTimer >= 1f) {
				for (int i = -2; i < Math.random() * 3; ++i) {
					drops.add(new LightDrop(Math.round(player.x), Math.round(player.z)));
				}
				for (int i = -2; i < Math.random() * 3; ++i) {
					spearDrops.add(new Spear(Math.round(player.x), Math.round(player.z)));
				}
				lb.type = BlockType.empty;
			}
		}
	}
	
	public void managePlayerProjectiles() {
		if (player.selectedWeapon == 0) {
			if (window.mouseButtons[GLFW_MOUSE_BUTTON_LEFT] > 0) {
				if (player.light > 0.05) {
					if (player.attackTimer < 1) {
						player.attackTimer += 0.02000001f;
					}
				}
			} else {
				if (player.attackTimer > 1) {
					player.attackTimer = 0f;
					playerProjectiles.add(new Projectile(player));
					player.light -= 0.05f;
				} else {
					player.attackTimer -= 0.1f;
					if (player.attackTimer < 0) {
						player.attackTimer = 0f;
					}
				}
			}
		} else {
			player.attackTimer = 0;
		}
		Projectile proj;
		for (int i = 0; i < playerProjectiles.size(); ++i) {
			proj = playerProjectiles.get(i);
			
			proj.move(0.08f);
			
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
				if (wraiths.get(j).collidesWith(new Ray3f(proj, proj.direction.mul(0.08f, new Vector3f())))) {
					wraiths.get(j).hp -= 0.5 * proj.brightness;
					playerProjectiles.remove(i);
					--i;
					break;
				}
			}
		}
	}
	
	public void manageSpears() {
		if (player.selectedWeapon == 1) {
			if (window.mouseButtons[GLFW_MOUSE_BUTTON_LEFT] == 1) {
				if (player.spearCount > 0) {
					playerSpears.add(new Spear(player));
					--player.spearCount;
				}
			}
		}
		for (int i = 0; i < playerSpears.size(); ++i) {
			Spear s = playerSpears.get(i);
			s.delta.y -= 0.0005f;
			s.xRotation = (float) Math.asin(-s.delta.y / s.delta.length());
			s.start.add(s.delta);
			if (s.start.y < 0 ||
				map.isSolid(s.start.x, s.start.z)) {
				playerSpears.remove(i);
				--i;
				continue;
			}
			for (int j = 0; j < wraiths.size(); ++j) {
				Wraith w = wraiths.get(j);
				if (w.collidesWith(new Ray3f(s))) {
					wraiths.get(j).hp -= 0.6;
					playerSpears.remove(i);
					--i;
					break;
				}
			}
		}
	}
	
	public void manageSpearDrops() {
		for (int i = 0; i < spearDrops.size(); ++i) {
			Spear s = spearDrops.get(i);
			float dsqr = s.start.distanceSquared(player);
			if (dsqr < 0.01f) {
				++player.spearCount;
				spearDrops.remove(i);
				continue;
			}
			if (dsqr < 2) {
				Vector3f delta = player.sub(s.start, new Vector3f());
				delta.normalize(0.01f/dsqr);
				delta.y *= 0.25f;
				s.start.add(delta);
				if (s.start.y < 0.4f) {
					s.delta.y = 0;
				}
			}
			s.delta.y -= 0.0005f;
			s.start.y += s.delta.y;
			if (s.start.y < 0) {
				s.start.y = 0;
				s.delta.y = 0;
			}
		}
	}
	
	public void renderSpearDrops() {
		Matrix4f mat = player.getCameraMatrix();
		dropShader.enable();
		dropShader.setUniform(2, 0, map.playerDistances);
		dropShader.setUniform(2, 1, map.lamps);
		dropShader.setUniform(2, 2, map.lampStrengths);
		dropShader.setUniform(2, 3, map.numLamps);
		Matrix4f transformationMatrix;
		Spear.spear.prepareRender();
		for (int i = 0; i < spearDrops.size(); ++i) {
			Spear s = spearDrops.get(i);
			transformationMatrix = new Matrix4f().translate(s.start.x, s.start.y, s.start.z).rotateZ(-(float) Math.PI / 2);
			dropShader.setUniform(0, 1, transformationMatrix);
			dropShader.setUniform(0, 0, mat.mul(transformationMatrix, transformationMatrix).scale( 0.1f));
			Spear.spear.drawTriangles();
		}
		Spear.spear.unbind();
	}
	
	public void manageWraithProjectiles() {
		Projectile proj;
		for (int i = 0; i < wraiths.size(); ++i) {
			Wraith w = wraiths.get(i);
			if (w.attackTimer > 1) {
				w.attackTimer = -0.25f;
				wraithProjectiles.add(new Projectile(w));
			}
		}
		
		for (int i = 0; i < wraithProjectiles.size(); ++i) {
			proj = wraithProjectiles.get(i);
			
			proj.move(0.08f);
			
			float brightness = map.getPlayerBasedBrightness(player, proj.x, proj.z);
			brightness = 1 - (1 - brightness) * (1 - brightness);
			if (brightness < 0.87) {
				proj.brightness -= 0.005f * (brightness + 0.87);
			}
			
			if (proj.distanceSquared(player) < 0.20f) {
				player.hp -= 0.05f;
				
				wraithProjectiles.remove(i);
				--i;
				continue;
			}
			for (int j = 0; j < map.numLamps - 1; ++j) {
				if (proj.distanceSquared(map.lampParticles[j]) < 0.2f) {
					map.lampParticles[j].hp -= 0.100001f;
					
					if (map.lampParticles[j].hp <= 0) {
						map.removeLight(j + 1);
					}
					
					wraithProjectiles.remove(i);
					--i;
					break;
				}
			}
		}
	}
}
