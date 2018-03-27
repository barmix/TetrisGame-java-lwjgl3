package me.barshay.tetris;

import static me.barshay.tetris.Const.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.glfw.Callbacks.*;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import me.barshay.tetris.forms.Field;
import me.barshay.tetris.input.Input;

/**
 * @author Michael
 * ����� ����������! � ������������� � �� ���������, ���������� � ���� ������ ������� ��� ���.
 *  ��� ������ (��, ��� �����!)
 *  �� � �������� ������ ���������, �������� ����� � ������� ����, � ��������� ������ ������ ������:
 *  ����������� �����, � ������� ���������������� ���� window, ������� ���� field, � �����������
 *  � ����������� �������� � ����� ��� ������:
 *    iterate() - ��������� ���� ������ �������� � ������� �� ����; 
 *    update() - ��� �������� ������������ � ��������� �� �� ����;
 *    render() - ������ ���� � ��������� ����. 
 *  ��!
 *  �� ���������� ���������� � ������ Field :)
 *  
 *  ���������� �����:
 *   ������� ����� � ������ ������� ������ �� �����������,
 *   ������� ���� � ����� - �������,
 *   ������ �������� ������� ������,
 *   ESC - �����
 */
public class TetrisGame implements Runnable {
	private Thread thread; 
	private boolean isRun; // ���� ����� ����
	private long window; // ������������� ����
	private Field field; // ���������� ������� ����
	
	public void start() {
		isRun = true;
		thread = new Thread(this,"Tetris");
		thread.start();
	}
	public void run() {
		initWindow(); // �������������� ����, ������������ ���������� lwjgl3 � glfw. �� (�����) ��� � ��� �� ������� �������� ����� ��������. 
		field = new Field(); // ������ ������� ����		
		long lastTime = System.nanoTime();
		double delta = 0.0;
		double ns = 1000000000.0 / 60.0; // � ������� ���� ��������� �������� ����� update() ~60 ��� � ������� 
		long timer = System.currentTimeMillis(); // � ������� timer ����� �������� ����������� ������ �� ����, �.�. ����� field.iterate()
		while(isRun){
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			if (delta >= 1.0) {
				update(); // ����� ������ ~60 ��� � �������
				delta--;
			}
			render(); // render() �������� ��� ������ ����������� �����	
			if (System.currentTimeMillis() - timer > 700) { 
				field.iterate(); // ~ 1 ��� � 0.7���, ����� ������� ����������, ����������� ��������� � �.�.
				timer = System.currentTimeMillis();
			}
			if (glfwWindowShouldClose(window)||field.endOfGame) isRun = false; //����� ����: ���� ������� ���� ��� ������ ESC ��� ���� �����������
		}
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		glfwTerminate();
		glfwSetErrorCallback(null).free();
		
	}
	
	private void initWindow() { // ���������, ����� �� ��� �� ����� ������������� ���������� https://www.lwjgl.org/guide
		GLFWErrorCallback.createPrint(System.err).set();
		if(!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}
		glfwWindowHint(GLFW_RESIZABLE, GL_FALSE); // ���� �� ��������������
		window = glfwCreateWindow(FIELD_WIDTH, FIELD_HEIGHT, TITLE, NULL, NULL); // ������ � ��������� ������������ ����������� � ������ Const
		if (window == NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor()); // �� �������� ��������
		glfwSetWindowPos(window, (vidmode.width()-FIELD_WIDTH) / 2, (vidmode.height() - FIELD_HEIGHT) / 2); // � ��������
		glfwSetKeyCallback(window, new Input()); // ������� ��� ������� � �������� ������ � ��� ����� Input 
		glfwMakeContextCurrent(window);
		glfwShowWindow(window);
		GL.createCapabilities();
	}
	private void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f); // ��� ���� - ������
		field.draw(); // ������������ ������� ����
		glfwSwapBuffers(window);
		if (field.endOfGame) { 
			try { 
				Thread.sleep(1000); // ������ ����� ��� ������
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	private void update() {
		glfwPollEvents(); // ��������� ��������� ������� � ����
		field.update(); // ������������ ������� ������
	}
	
	public static void main(String[] args) {
		new TetrisGame().start();	
	}

}
