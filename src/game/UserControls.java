package game;

import engine.OpenGL.EnigWindow;

import static org.lwjgl.glfw.GLFW.*;

public class UserControls {
	public static int[] forward = new int[] {GLFW_KEY_W};
	public static int[] backward = new int[] {GLFW_KEY_S};
	public static int[] left = new int[] {GLFW_KEY_A};
	public static int[] right = new int[] {GLFW_KEY_D};
	public static int[] placeLamp = new int[] {GLFW_KEY_L};
	public static int[] quit = new int[] {GLFW_KEY_ESCAPE};
	
	public static float sensitivity = 1f/500f;
	
	public static boolean forward(EnigWindow window) {
		for (int i:forward) {
			if (window.keys[i] > 0) {
				return true;
			}
		}
		return false;
	}
	public static boolean backward(EnigWindow window) {
		for (int i:backward) {
			if (window.keys[i] > 0) {
				return true;
			}
		}
		return false;
	}
	public static boolean left(EnigWindow window) {
		for (int i:left) {
			if (window.keys[i] > 0) {
				return true;
			}
		}
		return false;
	}
	public static boolean right(EnigWindow window) {
		for (int i:right) {
			if (window.keys[i] > 0) {
				return true;
			}
		}
		return false;
	}
	public static boolean placeLamp(EnigWindow window) {
		for (int i:placeLamp) {
			if (window.keys[i] == 1) {
				return true;
			}
		}
		return false;
	}
	public static boolean quit(EnigWindow window) {
		for (int i:quit) {
			if (window.keys[i] == 1) {
				return true;
			}
		}
		return false;
	}
	public static boolean checkI(EnigWindow window, int i) {
		if (window.keys[GLFW_KEY_0 + i] == 1) {
			return true;
		}
		return false;
	}
}
	
	
	
/*
	if commonCamera is true, the camera rotation for most 3d games will be used, if it is false, this alternate method is used:

	every frame the new rotation matrix is set to the rotation matrix from relative mouse position changes (from the last frame),
	will be multiplied by the old rotation matrix.
	R(xnew-xold)*R(ynew-yold)*R(znew-zold)*oldRotationMatrix.
	
	oh btw
	this variable has been moved from this file, to the Camera file, so you can have different cameras with different types of cameras
																																		 */