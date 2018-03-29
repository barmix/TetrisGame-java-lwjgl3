package me.barshay.tetris.engine;

public class ShaderManager {
	public static Shader shader;
	public static void loadAll() {
		shader = new Shader("resources/shaders/vertex.shader","resources/shaders/fragment.shader");
	}

}
