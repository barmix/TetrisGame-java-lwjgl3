package me.barshay.tetris.input;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

public class Input extends GLFWKeyCallback{
	
	public static boolean [] keys = new boolean[65536]; // �� ��������� ��� �������� false
	@Override
	public void invoke(long window, int key, int scancode, int action, int mods) {
		keys[key] = action == GLFW.GLFW_PRESS; // ������� ������� ���������� true, ���� ������� ������ (�� ��������, � �� ������, ������ ������)
	}
}
