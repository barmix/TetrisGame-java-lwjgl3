package me.barshay.tetris.engine;
import static org.lwjgl.opengl.GL20.*;
import me.barshay.tetris.utils.Vector3f;
import me.barshay.tetris.utils.Vector4f;

import static me.barshay.tetris.utils.Utilites.*;
public class Shader {
	private int programID;
	private int vertexShaderID;
	private int fragmentShaderID;
	public Shader(String vertexFile, String fragmentFile) {
		vertexShaderID = loadShader(vertexFile, GL_VERTEX_SHADER);
		fragmentShaderID = loadShader(fragmentFile, GL_FRAGMENT_SHADER);
		programID = glCreateProgram();
		glAttachShader(programID, vertexShaderID);
		glAttachShader(programID, fragmentShaderID);
		glLinkProgram(programID);
		glValidateProgram(programID);
	}
	public int getID() {
		return this.programID;
	}
	public int getUniform(String name) {
		int result = glGetUniformLocation(programID, name);
		if (result == -1) System.err.println("Could not find uniform variable: "+ name);
		return result;
	}
	public void setUniform3f(String name, Vector3f position) {
		glUniform3f(getUniform(name),position.x,position.y,position.z);
	}
	public void setUniform4f(String name, Vector4f color) {
		glUniform4f(getUniform(name),color.r,color.g,color.b,color.a);
	}
	public void start() {
		glUseProgram(programID);
	}
	public void stop() {
		glUseProgram(0);
	}
}
