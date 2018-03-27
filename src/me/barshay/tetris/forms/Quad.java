package me.barshay.tetris.forms;
import static me.barshay.tetris.Const.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import me.barshay.tetris.Colors;
import me.barshay.tetris.engine.VertexArrayObject;
import me.barshay.tetris.utils.Vector3f;
import me.barshay.tetris.utils.Vector4f;
public class Quad {
	private int vaoID;
	private int count;
	private float[] vertices;
	private byte[] indices;
	private VertexArrayObject vao;
	
	public Vector3f position;
	private Vector4f color;
	
	public Quad(float posX, float posY, int num) { //инициализируем четырёхугольник
		this.color = Colors.getColorByNumber(num).getColorVec4(); // цвет определяем по номеру в Enum BlocksColors
		this.vertices = new float[] { // массив вершин
				posX, posY,0f,
				posX, posY + Q_HEIGHT,0f,
				posX + Q_WIDTH, posY ,0f,
				posX + Q_WIDTH, posY + Q_HEIGHT,0f
		};
		this.indices = new byte[] { // порядок обхода вершин (два треугольника)
				0,1,2,
				1,2,3
		};
		this.count = vertices.length;
		this.position = new Vector3f(posX, posY, 0.0f);
		this.vao = new VertexArrayObject(this.vertices, this.indices); // собственно создаём объект VAO OpenGL
		this.vaoID = vao.getVaoID();
	}
	
	public void setColor(Vector4f color) {
		this.color = color;
	}
	
	public void setColor(int num) {
		this.color = Colors.getColorByNumber(num).getColorVec4();
	}

	public Vector4f getColor() {
		return this.color;
	}
	
	public Vector3f getPosition() {
		return this.position;
	}
	
	public void draw() {
		glBindVertexArray(this.vaoID);
		glEnableVertexAttribArray(0);
		glDrawElements(GL_TRIANGLES,count,GL_UNSIGNED_BYTE,0);
		glDisableVertexAttribArray(0);
		glBindVertexArray(0);
	}

}
