package me.barshay.tetris.forms;

import static me.barshay.tetris.engine.ShaderManager.*;
import static me.barshay.tetris.Const.*;
import static org.lwjgl.glfw.GLFW.*;

import me.barshay.tetris.input.Input;
import me.barshay.tetris.utils.Vector4f;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author Michael
 * Собственно идея реализации игры: 
 * игровое поле (стакан) - это массив int[][] (размерность задаётся константами), по умолчанию заполненый нулями,
 * фигура - это массив int[][] по размеру фигуры, содержащий нули и цифры, определяющие цвет фигуры. Массивы прописаны как константы. 
 * Перемещение фигуры в стакане возможно пока не достигнуты границы стакана, и массив фигуры накладывается 
 * на свободные (нулевые) элементы массива поля. Т.е., если в ячейке поля не ноль и мы попытаемся в эту ячейку 
 * переместить элемент массива фигуры, то сумма значений ячеек стакана и фигуры будет не равна элементу фигуры, 
 * и такое перемещение невозможно. Звучит, возможно, запутано, но легко (см. ниже) реализуется.
 * Всю эту математику можно(и наверно нужно) реализовать по-другому - на видео через openGL, но это уже будет не совсем Java)
 * Далее, в соответствие массиву int'ов стакана ставится массив объектов Quad, представляющих из себя квадраты GL, 
 * чей цвет как раз и определяется значением элементов массива стакана. Квадраты неподвижны, мы будем только менять их цвет.
 */
public class Field {
	
	/** Текущая позиция фигуры (левый нижний угол)	 */ 
	private int blockLeft, blockBottom; 
	
	/** Текущая фигура */
	private Block currentBlock;
	
	/** Массив значений цвета ячеек игрового поля */
	private int [][] fieldArray; 
	
	/** Массив ячеек (квадратов) игрового поля */
	private Quad [][] quadFieldArray; 
	
	/** Признак переполнения поля */
	private boolean fieldFull; 
	
	/** Признак конца игры */
	public boolean endOfGame = false; 
	
	/** Инициализация игрового поля, конструктор по умолчанию */
	public Field() {

		/* Загружаем шейдеры */
		loadAll(); 
		
		/* Инициализируем массивы, пока везде нули или NULL */
		fieldArray = new int [ROWS_NUM + ROWS_OFFSET][COLUMNS_NUM];
		quadFieldArray = new Quad [fieldArray.length][fieldArray[0].length];
		
		/* Все Quad чёрного цвета. Координаты задаются здесь, один раз, Quadы не двигаются!*/
		for (int i = 0; i < quadFieldArray.length; i++) {
			for (int j = 0; j < quadFieldArray[i].length; j++) {
				quadFieldArray[i][j] = new Quad(convertCoordX(j), convertCoordY(i), fieldArray[i][j]); 
			}
		}
		
		/* Добавляем новую фигуру в стакан */
		addNewBlock(); 
		
		/* Обновляем цвет ячеек */
		updateQuadField(); 
	}
	
	/** Устанавливает цвета элементов Quad в массиве quadFieldArray в соответствии с массивом fieldArray
	 * Необходимо выполнять после каждого изменения положения фигуры
	 */
	private void updateQuadField() {
		for (int i = 0; i < quadFieldArray.length; i++) {
			for (int j = 0; j < quadFieldArray[i].length; j++) {
				quadFieldArray[i][j].setColor(fieldArray[i][j]); 
			}
		}
	}

	/** Проверяет возможное пересечение полученной после поворота или сдвига фигуры с другими фигурами
	 *  
	 * @param tmpForm 	Фигура, которую надо проверить
	 * @param shiftX	Сдвиг по оси X
	 * @param shiftY	Сдвиг по оси Y
	 * @return			Результат проверки
	 */
		private boolean checkBorders(int[][] tmpForm, int shiftX, int shiftY) { 
		int fieldI = blockBottom - tmpForm.length + shiftY; // индекс верха фигуры в массиве стакана + запрошенное смещение по оси Y
		int fieldJ = blockLeft + shiftX; // индекс левого края фигуры в массиве стакана + запрошенное смещение по оси X
		for (int[] formI : tmpForm) {// перебираем элементы массива фигуры
			for (int formJ : formI) {
				 if (formJ !=0) { // для всех не нулевых элементов фигуры
					// если элемент фигуры пересекается с НЕ нулевым полем стакана, возвращаем false
					if ((fieldArray[fieldI][fieldJ] + formJ) != formJ) return false; 
					
				}
				fieldJ++;
			}
			fieldI++;
			fieldJ = blockLeft + shiftX;
		}
		return true;
	}
	
	
	/** 
	 * Записывает фигуру в массив поля, или "стирает" в зависимости от флага put. 
	 * Основной метод поля. Используется при всех перемещениях фигуры по массиву. 
	 * 
	 * @param form 		Фигура для записи 	
	 * @param shiftX	Сдвиг по оси X
	 * @param shiftY	Сдвиг по оси Y	 
	 * @param put		Флаг записи/стирания
	 * 
	 * Зачем надо стирать фигуру? 
	 * 	1. Перед тем как записать фигуру на новом месте, её надо стереть на старом.
	 * 	2. Перед проверкой возможности сдвига/поворота также надо стереть текущую фигуру, чтобы не мешала. 
	 * 	Подробнее в методе iterate()
	 */
	private void blockToField(int[][] form, int shiftX, int shiftY, boolean put) {
		int fieldI = blockBottom - form.length + shiftY;
		int fieldJ = blockLeft + shiftX;
		for (int[] formI : form) {
			for (int formJ : formI) {
				if (formJ != 0) fieldArray[fieldI][fieldJ] = (put) ? formJ : 0;
				// если put, то записываем фигуру в массив стакана, если нет, то записываем в массив поля нули, "стираем" фигуру.
				fieldJ++;
			}
			fieldI++;
			fieldJ = blockLeft + shiftX;
		}
		blockBottom = fieldI;
		blockLeft = fieldJ;
	}
	
	/**Добавляет новую фигуру */
	private void addNewBlock() {
		currentBlock = new Block();
		blockBottom = ROWS_OFFSET + 1; // на поле будет видно только нижний ряд массива фигуры, остальное будет выше окна
		blockLeft = (fieldArray[0].length - currentBlock.getFormSizeX() + 1)/2; // по горизонтали помещаем фигуру в центр окна
		blockToField(currentBlock.getForm(), 0, 0, true);
	}
	
	/**Один из трёх основных методов,  вызывается в цикле игры независимо от update() и draw().
	 *  
	 * Продвигает пока возможно фигуру вниз по полю. 
	 * Если продвижение невозможно проверяется заполненность линии; 
	 * Добавляется новая фигура, проверяется заполненность поля.  
	 * Подробное описание всех строк метода ниже.
	 */
	public void iterate() {
		blockToField(currentBlock.getForm(), 0, 0, false); // "стираем" текущую фигуру, но её координаты "помнят" переменные blockLeft и blockBottom
		if (blockCanMoveDown()) { // прикладываем на один ряд ниже 
			blockToField(currentBlock.getForm(), 0, 1, true); // если можно сдвинуть вниз, записываем фигуру в массив поля со сдвигом
		} else {
			blockToField(currentBlock.getForm(), 0, 0, true); // если нельзя сдвинуть вниз, записываем фигуру в прежнем положении
			Input.keys[GLFW_KEY_SPACE] = false; // если использовалось ускорение падения, отменяем 
			checkFullLine(); // проверяем заполненность линий
			addNewBlock(); // добавляем новую фигуру
			checkFullField(); // проверяем может ли новая фигура двигаться вниз, если нет, то gameover, поле заполнено
		}
		updateQuadField(); // обновляем цвет ячеек
	}
	
	/**
	 * Проверяет возможность новой (только что появившейся на поле) фигуры двигаться вниз.
	 * Если фигура не может двигаться, то поле заполнено - ставим признак fieldFull = true; 
	 * 
	 */
	private void checkFullField() {
		blockToField(currentBlock.getForm(), 0, 0, false);
		if (!blockCanMoveDown()) fieldFull = true; // если новая фигура не может двигаться вниз, то поле заполнено!
		blockToField(currentBlock.getForm(), 0, 0, true);

	}

	private void checkFullLine() {
		boolean del;
		for (int i = blockBottom - currentBlock.getFormSizeY(); i < blockBottom; i++) {
			del = true;
			for (int j = 0; j < fieldArray[i].length; j++)  	
				if (fieldArray[i][j] == 0) del = false; // если в ряду есть "нулевые" элементы, то ряд стирать нельзя 
			if (del) deleteLine(i); 
		}	
	}
	
	/**
	 * Стирает ряд delline, переписывая массив со сдвигом на одну строку, верхняя строка заполняется нулями.
	 * 
	 * @param delLine Номер строки в массиве fieldArray, которую нужно стереть.
	 */
	private void deleteLine(int delLine) {
		for (int i = delLine; i >= 0; i--)
			for (int j = 0; j < fieldArray[0].length; j++)
				fieldArray[i][j] = (i!=0) ? fieldArray[i-1][j] : 0; 
	}

	/**
	 * Проверяет возможнось сдвига фигуры вниз.
	 * 
	 * @return 	true если есть пространство для сдвига фигуры вниз
	 */
	private boolean blockCanMoveDown() {
		if (blockBottom == fieldArray.length) return false; // если достигнут низ стакана	
		return checkBorders(currentBlock.getForm(), 0, 1); // проверяет пересечение с другими фигурами при сдвиге на 1 по оси Y
	}

	/**
	 * Проверяет возможнось вращения фигуры.
	 * 
	 * Работает аналогично методу blockCanMoveDown(), только сначала делает клон текущей фигуры, 
	 * поворачивает его, проверяет новые границы и пересечение с другими фигурами.
	 * 
	 * @param direction Направление вращения: вправо или влево
	 * 
	 * @return true если есть пространство для вращения фигуры
	 */
	private boolean blockCanRotate(String direction) {
		Block tmpBlock = new Block(currentBlock);
		tmpBlock.formFlip(direction); 
		if ((blockBottom - tmpBlock.getFormSizeY() < 0)||(blockLeft + tmpBlock.getFormSizeX() > fieldArray[0].length)) return false;	
		return checkBorders(tmpBlock.getForm(), 0, 0);
	}
	
	/**
	 * Проверяет возможнось сдвига фигуры по горизонтали.
	 * 
	 * Работает аналогично методу blockCanMoveDown(), только сначала распознаёт запрошенный сдвиг, 
	 * потом проверяет новые границы и пересечение с другими фигурами.
	 * 
	 * @param direction Направление сдвига: вправо или влево
	 * 
	 * @return true если есть пространство для сдвига фигуры
	 */
	private boolean blockCanShift(String direction) {
		int shiftX = (direction == "right") ? 1 : -1;
		if ((blockLeft + shiftX < 0)||(blockLeft + currentBlock.getFormSizeX() +shiftX > fieldArray[0].length)) return false;	
		return checkBorders(currentBlock.getForm(), shiftX, 0);
	}

	/**
	 * Тут всё максимально очевидно, на мой взгляд. 
	 * 
	 * При запросе движения фигуры проверяем возможность, двигаем (или возвращаем обратно), применяем изменения.
	 * Потом обнуляем нажатие кнопки,
	 */
	public void update() {
 

		if (Input.keys[GLFW_KEY_RIGHT]) {
			blockToField(currentBlock.getForm(), 0, 0, false);
			if (blockCanShift(RIGHT)) { 
				blockToField(currentBlock.getForm(), 1, 0, true);
			}else {
				blockToField(currentBlock.getForm(), 0, 0, true);
			}
			updateQuadField();
			Input.keys[GLFW_KEY_RIGHT] = false;
		}
		if (Input.keys[GLFW_KEY_LEFT]) {
			blockToField(currentBlock.getForm(), 0, 0, false);
			if (blockCanShift(LEFT)) { 
				blockToField(currentBlock.getForm(), -1, 0, true);
			}else {
				blockToField(currentBlock.getForm(), 0, 0, true);
			}
			updateQuadField();
			Input.keys[GLFW_KEY_LEFT] = false;
		}
		if (Input.keys[GLFW_KEY_UP]) {
			blockToField(currentBlock.getForm(), 0, 0, false);
			if (blockCanRotate(RIGHT)) currentBlock.formFlip(RIGHT);
			blockToField(currentBlock.getForm(), 0, 0, true);
			updateQuadField();	
			Input.keys[GLFW_KEY_UP] = false;
		}
		if (Input.keys[GLFW_KEY_DOWN]) {
			blockToField(currentBlock.getForm(), 0, 0, false);
			if (blockCanRotate(LEFT)) currentBlock.formFlip(LEFT);
			blockToField(currentBlock.getForm(), 0, 0, true);
			updateQuadField();	
			Input.keys[GLFW_KEY_DOWN] = false;
		}
		if (Input.keys[GLFW_KEY_SPACE]) { //Ускорение падения фигуры с помощью внеочередного вызова метода iterate()
			this.iterate();
		}
		if (Input.keys[GLFW_KEY_ESCAPE]) {
			endOfGame = true; // Выход по Escape
		}
	}


	public void draw() {
		Vector4f tmpColor; 
		int index = 0;
		for(Quad [] quadArr : quadFieldArray) {
			for(Quad quad : quadArr) {
				if (fieldFull) { // рисуем "клетчатый флаг", если поле заполнено )
					tmpColor = (index % 2 == 0)? VEC4_BLACK : VEC4_WHITE;
					quad.setColor(tmpColor);
				}
				drawQuad(quad); // отрисовывает текущий квадрат
				index++;
			}
			index++;
		}
		if (fieldFull) endOfGame = true;
	}

	private void drawQuad(Quad quad) {
		shader.start();
		shader.setUniform4f("color", quad.getColor()); // в шейдер передаётся цвет текущего Quada
		quad.draw();
		shader.stop();
	}
	
	/**
	 * Методы ниже преобразуют индексы массива в кординаты окна для отрисовки ячейки в правильном месте.
	 * 
	 * Как это работает? 
	 * "Координаты" в массиве начинаются в верхнем левом углу с индексов [0][0]. 
	 * Координаты OpenGL в окне от -1.0f до 1.0f по X и по Y, точка [0,0] находится в центре.
	 * Методы ConvertCoord добавляют такое смещение индексам массива с помощью констант размера ячейки,
	 * чтобы ячейка по индексам массива, например, [0][0] отрисовывалась в окне начиная с координаты [-1.0f, 1.0f] и т.д.
	 * Константа ROWS_OFFSET - дополнительный сдвиг по Y в невидимую область окна, чтобы фигура появлялась в окне постепенно.    
	 * 	  
	 * @param i,j	Принимает индексы массива fieldArray j(ось X) и i(ось Y)
	 * @return	Возвращает координаты OpenGL X и Y в окне для Quad
	 */
	private float convertCoordY(int i) { 
		return -(float)((i + 1 - ROWS_OFFSET) * Q_HEIGHT) + 1.0f; 
	}

	private float convertCoordX(int j) {
		return (float)(j * Q_WIDTH) - 1.0f; 
	}
}
