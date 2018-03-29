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


public class TetrisGame implements Runnable {
	private Thread thread; 
	private boolean isRun; // флаг конца игры
	private long window; // идентификатор окна
	private Field field; // собственно игровое поле
	
	public void start() {
		isRun = true;
		thread = new Thread(this,"Tetris");
		thread.start();
	}
	public void run() {
		initWindow(); // инициализируем окно, используется библиотека lwjgl3 и glfw. Всё (почти) как у них на главной странице сайта написано. 
		field = new Field(); // создаём игровое поле		
		long lastTime = System.nanoTime();
		double delta = 0.0;
		double ns = 1000000000.0 / 60.0; // с помощью этой константы устанавливаем интервал вызова метода update().
		long timer = System.currentTimeMillis(); // с помощью timer задаём скорость продвижения фигуры по полю, т.е. вызов field.iterate()
		while(isRun){
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			if (delta >= 1.0) {
				update(); // будет вызван ~60 раз в секунду
				delta--;
			}
			render(); // render() работает при каждом прохождении цикла	
			if (System.currentTimeMillis() - timer > 700) { // true ~1 раз в 0.7сек, можно сделать переменной, увеличивать скорость и т.д.
				field.iterate();
				timer = System.currentTimeMillis();
			}
			if (glfwWindowShouldClose(window)||field.endOfGame) isRun = false; //конец игры: если закрыли окно или нажали ESC или поле переполнено
		}
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		glfwTerminate();
		glfwSetErrorCallback(null).free();
		
	}
	
	private void initWindow() { // Повторюсь, почти всё как на сайте разработчиков библиотеки https://www.lwjgl.org/guide
		GLFWErrorCallback.createPrint(System.err).set();
		if(!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}
		glfwWindowHint(GLFW_RESIZABLE, GL_FALSE); // окно не масштабируемое
		window = glfwCreateWindow(FIELD_WIDTH, FIELD_HEIGHT, TITLE, NULL, NULL); // размер и заголовок определяются константами в классе Const
		if (window == NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor()); // на основном мониторе
		glfwSetWindowPos(window, (vidmode.width()-FIELD_WIDTH) / 2, (vidmode.height() - FIELD_HEIGHT) / 2); // в середине
		glfwSetKeyCallback(window, new Input()); // Передаём все события с нажатием клавиш в наш класс Input 
		glfwMakeContextCurrent(window);
		glfwShowWindow(window);
		GL.createCapabilities();
	}
	private void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f); // фон окна - чёрный
		field.draw(); // готовит картинку игрового поля
		glfwSwapBuffers(window); // выводит в окно
		if (field.endOfGame) { 
			try { 
				Thread.sleep(1000); // просто пауза при выходе
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	private void update() {
		glfwPollEvents(); // разрешает обработку событий в окне
		field.update(); // обрабатывает нажатия клавиш
	}
	
	public static void main(String[] args) {
		new TetrisGame().start();	
	}

}
