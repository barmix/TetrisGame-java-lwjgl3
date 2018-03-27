package me.barshay.tetris.engine;

public class ShaderManager {
	public static Shader shader;
	public static void loadAll() {
		
		shader = new Shader("src/me/barshay/tetris/shaders/vertex.shader","src/me/barshay/tetris/shaders/fragment.shader");
	}

}
