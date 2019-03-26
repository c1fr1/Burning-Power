package game.entities;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.Comparator;

import static game.Views.MainView.lightSquare;
import static game.Shaders.lightShader;

public abstract class Particle extends Vector3f {
	public abstract Vector3f getColor();
	public abstract float getSize();
	
	public static void renderParticles(Particle[] particles, Player player) {
		Arrays.sort(particles, new Comparator<Particle>() {
			@Override
			public int compare(Particle o1, Particle o2) {
				return player.distanceSquared(o1) > player.distanceSquared(o2) ? -1 : 1;
			}
		});
		
		Matrix4f mat = player.getCameraMatrix();
		
		lightShader.enable();
		lightSquare.prepareRender();
		for (int i = 0; i < particles.length; ++i) {
			Particle part = particles[i];
			lightShader.setUniform(2, 0, part.getColor());
			lightShader.setUniform(0, 0, player.reverseRotations(new Matrix4f(mat).translate(part).scale(part.getSize())));
			lightSquare.drawTriangles();
		}
		lightSquare.unbind();
	}
}
