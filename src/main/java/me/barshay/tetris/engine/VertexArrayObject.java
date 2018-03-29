package me.barshay.tetris.engine;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static me.barshay.tetris.utils.Utilites.*;

public class VertexArrayObject {
	public static final int VERTEX_ATTRIB = 0;
	private int vaoID;
	public VertexArrayObject(float[] vertices, byte[] indices) {
		createArrayObject(vertices,indices);
		
	}
	
	public void createArrayObject(float[] vertices, byte[] indices) {
		vaoID = glGenVertexArrays();
		glBindVertexArray(vaoID);
		createVerticesBuffer(vertices);
		createIndicesBuffer(indices);
		glBindVertexArray(0);
		
	}
	
	public void createVerticesBuffer(float[] vertices) {
		int vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER,vbo);
		glBufferData(GL_ARRAY_BUFFER,createFloatBuffer(vertices),GL_STATIC_DRAW);
		glVertexAttribPointer(VERTEX_ATTRIB,3,GL_FLOAT,false,0,0);
		glBindBuffer(GL_ARRAY_BUFFER,0);
	}
	
	public void createIndicesBuffer(byte[] indices) {
		int ibo = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,ibo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER,createByteBuffer(indices),GL_STATIC_DRAW);
		
	}
	public int getVaoID() {
		return this.vaoID;
	}
}
