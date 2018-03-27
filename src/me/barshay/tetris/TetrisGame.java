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
 * ƒобро пожаловать! — документацией € не морочилс€, постараюсь в двух словах описать что это.
 *  Ёто тетрис (да, два слова!)
 *  ћы в основном классе программы, описание полей и методов ниже, а структура нашего класса такова:
 *  «апускаетс€ поток, в котором инициализируетс€ окно window, игровое поле field, и выполн€ютс€
 *  с определЄнной частотой в цикле три метода:
 *    iterate() - наполн€ет поле новыми фигурами и двигает их вниз; 
 *    update() - ждЄт действий пользовател€ и реализует их на поле;
 *    render() - рисует поле и обновл€ет окно. 
 *  ¬сЄ!
 *  ¬сЄ интересное происходит в классе Field :)
 *  
 *  ”правление игрой:
 *   стрелки влево и вправо двигают фигуру по горизонтали,
 *   стрелки вниз и вверх - вращают,
 *   пробел ускор€ет падение фигуры,
 *   ESC - выход
 */
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
		initWindow(); // инициализируем окно, используетс€ библиотека lwjgl3 и glfw. ¬сЄ (почти) как у них на главной странице сайта написано. 
		field = new Field(); // создаЄм игровое поле		
		long lastTime = System.nanoTime();
		double delta = 0.0;
		double ns = 1000000000.0 / 60.0; // с помощью этой константы вызываем метод update() ~60 раз в секунду 
		long timer = System.currentTimeMillis(); // с помощью timer задаЄм скорость продвижени€ фигуры по полю, т.е. вызов field.iterate()
		while(isRun){
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			if (delta >= 1.0) {
				update(); // будет вызван ~60 раз в секунду
				delta--;
			}
			render(); // render() работает при каждом прохождении цикла	
			if (System.currentTimeMillis() - timer > 700) { 
				field.iterate(); // ~ 1 раз в 0.7сек, можно сделать переменной, увеличивать сложность и т.д.
				timer = System.currentTimeMillis();
			}
			if (glfwWindowShouldClose(window)||field.endOfGame) isRun = false; //конец игры: если закрыли окно или нажали ESC или поле переполнено
		}
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		glfwTerminate();
		glfwSetErrorCallback(null).free();
		
	}
	
	private void initWindow() { // ѕовторюсь, почти всЄ как на сайте разработчиков библиотеки https://www.lwjgl.org/guide
		GLFWErrorCallback.createPrint(System.err).set();
		if(!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}
		glfwWindowHint(GLFW_RESIZABLE, GL_FALSE); // окно не масштабируемое
		window = glfwCreateWindow(FIELD_WIDTH, FIELD_HEIGHT, TITLE, NULL, NULL); // размер и заголовок определ€ютс€ константами в классе Const
		if (window == NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor()); // на основном мониторе
		glfwSetWindowPos(window, (vidmode.width()-FIELD_WIDTH) / 2, (vidmode.height() - FIELD_HEIGHT) / 2); // в середине
		glfwSetKeyCallback(window, new Input()); // ѕередаЄм все событи€ с нажатием клавиш в наш класс Input 
		glfwMakeContextCurrent(window);
		glfwShowWindow(window);
		GL.createCapabilities();
	}
	private void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f); // фон окна - чЄрный
		field.draw(); // отрисовывает игровое поле
		glfwSwapBuffers(window);
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
		field.update(); // обрабатывает нажати€ клавиш
	}
	
	public static void main(String[] args) {
		new TetrisGame().start();	
	}

}
