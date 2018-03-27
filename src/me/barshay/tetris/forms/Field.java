package me.barshay.tetris.forms;

import static me.barshay.tetris.engine.ShaderManager.*;
import static me.barshay.tetris.Const.*;
import static org.lwjgl.glfw.GLFW.*;

import me.barshay.tetris.input.Input;
import me.barshay.tetris.utils.Vector4f;

/**
 * @author Michael
 * ���������� ���� ���������� ����: 
 * ������� ���� (������) - ��� ������ int[][] (����������� ������� �����������), �� ��������� ���������� ������,
 * ������ - ��� ������ int[][] �� ������� ������, ���������� ���� � �����, ������������ ���� ������. ������� ��������� ��� ���������. 
 * ����������� ������ � ������� �������� ���� �� ���������� ������� �������, � ������ ������ ������������� 
 * �� ��������� (�������) �������� ������� ����. �.�., ���� � ������ ���� �� ���� � �� ���������� � ��� ������ 
 * ����������� ������� ������� ������, �� ����� �������� ����� ������� � ������ ����� �� ����� �������� ������, 
 * � ����� ����������� ����������. ������, ��������, ��������, �� ����� (��. ����) �����������.
 * ��� ��� ���������� �����(� ������� �����) ����������� ��-������� - �� ����� ����� openGL, �� ��� ��� ����� �� ������ Java)
 * �����, � ������������ ������� int'�� ������� �������� ������ �������� Quad, �������������� �� ���� �������� GL, 
 * ��� ���� ��� ��� � ������������ ��������� ��������� ������� �������. �������� ����������, �� ����� ������ ������ �� ����.
 */
public class Field {
	
	/** ������� ������� ������ (����� ������ ����)	 */ 
	private int blockLeft, blockBottom; 
	
	/** ������� ������ */
	private Block currentBlock;
	
	/** ������ �������� ����� ����� �������� ���� */
	private int [][] fieldArray; 
	
	/** ������ ����� (���������) �������� ���� */
	private Quad [][] quadFieldArray; 
	
	/** ������� ������������ ���� */
	private boolean fieldFull; 
	
	/** ������� ����� ���� */
	public boolean endOfGame = false; 
	
	/** ������������� �������� ����, ����������� �� ��������� */
	public Field() { 
		
		/* ��������� ������� */
		loadAll(); 
		
		/* �������������� �������, ���� ����� ���� ��� NULL */
		fieldArray = new int [ROWS_NUM + ROWS_OFFSET][COLUMNS_NUM];
		quadFieldArray = new Quad [fieldArray.length][fieldArray[0].length];
		
		/* ��� Quad ������� �����. ���������� �������� �����, ���� ���, Quad� �� ���������!*/
		for (int i = 0; i < quadFieldArray.length; i++) {
			for (int j = 0; j < quadFieldArray[i].length; j++) {
				quadFieldArray[i][j] = new Quad(convertCoordX(j), convertCoordY(i), fieldArray[i][j]); 
			}
		}
		
		/* ��������� ����� ������ � ������ */
		addNewBlock(); 
		
		/* ��������� ���� ����� */
		updateQuadField(); 
	}
	
	/** ������������� ����� ��������� Quad � ������� quadFieldArray � ������������ � �������� fieldArray
	 * ���������� ��������� ����� ������� ��������� ��������� ������
	 */
	private void updateQuadField() {
		for (int i = 0; i < quadFieldArray.length; i++) {
			for (int j = 0; j < quadFieldArray[i].length; j++) {
				quadFieldArray[i][j].setColor(fieldArray[i][j]); 
			}
		}
	}

	/** ��������� ��������� ����������� ���������� ����� �������� ��� ������ ������ � ������� ��������
	 *  
	 * @param tmpForm 	������, ������� ���� ���������
	 * @param shiftX	����� �� ��� X
	 * @param shiftY	����� �� ��� Y
	 * @return			��������� ��������
	 */
		private boolean checkBorders(int[][] tmpForm, int shiftX, int shiftY) { 
		int fieldI = blockBottom - tmpForm.length + shiftY; // ������ ����� ������ � ������� ������� + ����������� �������� �� ��� Y
		int fieldJ = blockLeft + shiftX; // ������ ������ ���� ������ � ������� ������� + ����������� �������� �� ��� X
		for (int[] formI : tmpForm) {// ���������� �������� ������� ������
			for (int formJ : formI) {
				 if (formJ !=0) { // ��� ���� �� ������� ��������� ������
					// ���� ������� ������ ������������ � �� ������� ����� �������, ���������� false
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
	 * ���������� ������ � ������ ����, ��� "�������" � ����������� �� ����� put. 
	 * �������� ����� ����. ������������ ��� ���� ������������ ������ �� �������. 
	 * 
	 * @param form 		������ ��� ������ 	
	 * @param shiftX	����� �� ��� X
	 * @param shiftY	����� �� ��� Y	 
	 * @param put		���� ������/��������
	 * 
	 * ����� ���� ������� ������? 
	 * 	1. ����� ��� ��� �������� ������ �� ����� �����, � ���� ������� �� ������.
	 * 	2. ����� ��������� ����������� ������/�������� ����� ���� ������� ������� ������, ����� �� ������. 
	 * 	��������� � ������ iterate()
	 */
	private void blockToField(int[][] form, int shiftX, int shiftY, boolean put) {
		int fieldI = blockBottom - form.length + shiftY;
		int fieldJ = blockLeft + shiftX;
		for (int[] formI : form) {
			for (int formJ : formI) {
				if (formJ != 0) fieldArray[fieldI][fieldJ] = (put) ? formJ : 0;
				// ���� put, �� ���������� ������ � ������ �������, ���� ���, �� ���������� � ������ ���� ����, "�������" ������.
				fieldJ++;
			}
			fieldI++;
			fieldJ = blockLeft + shiftX;
		}
		blockBottom = fieldI;
		blockLeft = fieldJ;
	}
	
	/**��������� ����� ������ */
	private void addNewBlock() {
		currentBlock = new Block();
		blockBottom = ROWS_OFFSET + 1; // �� ���� ����� ����� ������ ������ ��� ������� ������, ��������� ����� ���� ����
		blockLeft = (fieldArray[0].length - currentBlock.getFormSizeX() + 1)/2; // �� ����������� �������� ������ � ����� ����
		blockToField(currentBlock.getForm(), 0, 0, true);
	}
	
	/**���� �� ��� �������� �������,  ���������� � ����� ���� ���������� �� update() � draw().
	 *  
	 * ���������� ���� �������� ������ ���� �� ����. 
	 * ���� ����������� ���������� ����������� ������������� �����; 
	 * ����������� ����� ������, ����������� ������������� ����.  
	 * ��������� �������� ���� ����� ������ ����.
	 */
	public void iterate() {
		blockToField(currentBlock.getForm(), 0, 0, false); // "�������" ������� ������, �� � ���������� "������" ���������� blockLeft � blockBottom
		if (blockCanMoveDown()) { // ������������ �� ���� ��� ���� 
			blockToField(currentBlock.getForm(), 0, 1, true); // ���� ����� �������� ����, ���������� ������ � ������ ���� �� �������
		} else {
			blockToField(currentBlock.getForm(), 0, 0, true); // ���� ������ �������� ����, ���������� ������ � ������� ���������
			Input.keys[GLFW_KEY_SPACE] = false; // ���� �������������� ��������� �������, �������� 
			checkFullLine(); // ��������� ������������� �����
			addNewBlock(); // ��������� ����� ������
			checkFullField(); // ��������� ����� �� ����� ������ ��������� ����, ���� ���, �� gameover, ���� ���������
		}
		updateQuadField(); // ��������� ���� �����
	}
	
	/**
	 * ��������� ����������� ����� (������ ��� ����������� �� ����) ������ ��������� ����.
	 * ���� ������ �� ����� ���������, �� ���� ��������� - ������ ������� fieldFull = true; 
	 * 
	 */
	private void checkFullField() {
		blockToField(currentBlock.getForm(), 0, 0, false);
		if (!blockCanMoveDown()) fieldFull = true; // ���� ����� ������ �� ����� ��������� ����, �� ���� ���������!
		blockToField(currentBlock.getForm(), 0, 0, true);

	}

	private void checkFullLine() {
		boolean del;
		for (int i = blockBottom - currentBlock.getFormSizeY(); i < blockBottom; i++) {
			del = true;
			for (int j = 0; j < fieldArray[i].length; j++)  	
				if (fieldArray[i][j] == 0) del = false; // ���� � ���� ���� "�������" ��������, �� ��� ������� ������ 
			if (del) deleteLine(i); 
		}	
	}
	
	/**
	 * ������� ��� delline, ����������� ������ �� ������� �� ���� ������, ������� ������ ����������� ������.
	 * 
	 * @param delLine ����� ������ � ������� fieldArray, ������� ����� �������.
	 */
	private void deleteLine(int delLine) {
		for (int i = delLine; i >= 0; i--)
			for (int j = 0; j < fieldArray[0].length; j++)
				fieldArray[i][j] = (i!=0) ? fieldArray[i-1][j] : 0; 
	}

	/**
	 * ��������� ���������� ������ ������ ����.
	 * 
	 * @return 	true ���� ���� ������������ ��� ������ ������ ����
	 */
	private boolean blockCanMoveDown() {
		if (blockBottom == fieldArray.length) return false; // ���� ��������� ��� �������	
		return checkBorders(currentBlock.getForm(), 0, 1); // ��������� ����������� � ������� �������� ��� ������ �� 1 �� ��� Y
	}

	/**
	 * ��������� ���������� �������� ������.
	 * 
	 * �������� ���������� ������ blockCanMoveDown(), ������ ������� ������ ���� ������� ������, 
	 * ������������ ���, ��������� ����� ������� � ����������� � ������� ��������.
	 * 
	 * @param direction ����������� ��������: ������ ��� �����
	 * 
	 * @return true ���� ���� ������������ ��� �������� ������
	 */
	private boolean blockCanRotate(String direction) {
		Block tmpBlock = new Block(currentBlock);
		tmpBlock.formFlip(direction); 
		if ((blockBottom - tmpBlock.getFormSizeY() < 0)||(blockLeft + tmpBlock.getFormSizeX() > fieldArray[0].length)) return false;	
		return checkBorders(tmpBlock.getForm(), 0, 0);
	}
	
	/**
	 * ��������� ���������� ������ ������ �� �����������.
	 * 
	 * �������� ���������� ������ blockCanMoveDown(), ������ ������� ��������� ����������� �����, 
	 * ����� ��������� ����� ������� � ����������� � ������� ��������.
	 * 
	 * @param direction ����������� ������: ������ ��� �����
	 * 
	 * @return true ���� ���� ������������ ��� ������ ������
	 */
	private boolean blockCanShift(String direction) {
		int shiftX = (direction == "right") ? 1 : -1;
		if ((blockLeft + shiftX < 0)||(blockLeft + currentBlock.getFormSizeX() +shiftX > fieldArray[0].length)) return false;	
		return checkBorders(currentBlock.getForm(), shiftX, 0);
	}

	/**
	 * ��� �� ����������� ��������, �� ��� ������. 
	 * 
	 * ��� ������� �������� ������ ��������� �����������, ������� (��� ���������� �������), ��������� ���������.
	 * ����� �������� ������� ������,
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
		if (Input.keys[GLFW_KEY_SPACE]) { //��������� ������� ������ � ������� ������������� ������ ������ iterate()
			this.iterate();
		}
		if (Input.keys[GLFW_KEY_ESCAPE]) {
			endOfGame = true; // ����� �� Escape
		}
	}


	public void draw() {
		Vector4f tmpColor; 
		int index = 0;
		for(Quad [] quadArr : quadFieldArray) {
			for(Quad quad : quadArr) {
				if (fieldFull) { // ������ "��������� ����", ���� ���� ��������� )
					tmpColor = (index % 2 == 0)? VEC4_BLACK : VEC4_WHITE;
					quad.setColor(tmpColor);
				}
				drawQuad(quad); // ������������ ������� �������
				index++;
			}
			index++;
		}
		if (fieldFull) endOfGame = true;
	}

	private void drawQuad(Quad quad) {
		shader.start();
		shader.setUniform4f("color", quad.getColor()); // � ������ ��������� ���� �������� Quada
		quad.draw();
		shader.stop();
	}
	
	/**
	 * ������ ���� ����������� ������� ������� � ��������� ���� ��� ��������� ������ � ���������� �����.
	 * 
	 * ��� ��� ��������? 
	 * "����������" � ������� ���������� � ������� ����� ���� � �������� [0][0]. 
	 * ���������� OpenGL � ���� �� -1.0f �� 1.0f �� X � �� Y, ����� [0,0] ��������� � ������.
	 * ������ ConvertCoord ��������� ����� �������� �������� ������� � ������� �������� ������� ������,
	 * ����� ������ �� �������� �������, ��������, [0][0] �������������� � ���� ������� � ���������� [-1.0f, 1.0f] � �.�.
	 * ��������� ROWS_OFFSET - �������������� ����� �� Y � ��������� ������� ����, ����� ������ ���������� � ���� ����������.    
	 * 	  
	 * @param 	��������� ������� ������� fieldArray j(��� X) � i(��� Y)
	 * @return	���������� ���������� OpenGL X � Y � ���� ��� Quad
	 */
	private float convertCoordY(int i) { 
		return -(float)((i + 1 - ROWS_OFFSET) * Q_HEIGHT) + 1.0f; 
	}

	private float convertCoordX(int j) {
		return (float)(j * Q_WIDTH) - 1.0f; 
	}
}
