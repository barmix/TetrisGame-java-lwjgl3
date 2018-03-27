package me.barshay.tetris;

import me.barshay.tetris.utils.Vector4f;

public class Const {
	
	public static final int 
	COLUMNS_NUM = 10, //����� �������� �� ����
	ROWS_NUM = 20, // ����� ����� (������� �� ����)
	ROWS_OFFSET = 3, // ����� ��� ���������� ����������� ������ ��� ������� �����
	NUM_OF_PIXELS = 32,
	FIELD_WIDTH = NUM_OF_PIXELS * COLUMNS_NUM,	// ������ ���� � ��������
	FIELD_HEIGHT = NUM_OF_PIXELS * ROWS_NUM
	;
	
	public static final float 
	Q_WIDTH = 2.0f/COLUMNS_NUM, // ������ ����� ������ � ����������� GL 
	Q_HEIGHT = Q_WIDTH * (float)(FIELD_WIDTH / (float)FIELD_HEIGHT) // ������ ����� ������
	;
	
	public static final Vector4f // ����� �����
	VEC4_BLACK = new Vector4f(),
	VEC4_RED = new Vector4f(0.9f,0.1f,0.1f,1.0f),
	VEC4_GREEN = new Vector4f(0.1f,0.9f,0.1f,1.0f),
	VEC4_BLUE = new Vector4f(0.1f,0.1f,0.9f,1.0f),
	VEC4_AQUA = new Vector4f(0.1f,0.9f,0.9f,1.0f), 
	VEC4_YELLOW = new Vector4f(0.9f,0.9f,0.1f,1.0f), 
	VEC4_ORANGE = new Vector4f(0.9f,0.5f,0.1f,1.0f), 
	VEC4_PURPLE = new Vector4f(0.5f,0.1f,0.5f,1.0f),
	VEC4_WHITE = new Vector4f(1.0f,1.0f,1.0f,1.0f)
	;
		
	public static final int[][] // �������, ����������� ������. ����� - ����
	FORM_I = {	{3,},{3,},{3,},{3,},
	},
	FORM_J = {	{0,5,},
				{0,5,},
				{5,5,},
	},
	FORM_L = {	{6,0,},
				{6,0,},
				{6,6,},
	},
	FORM_S = {	{0,4,4,},
				{4,4,0,},
	},
	FORM_Z = {	{7,7,0,},
				{0,7,7,},
	},
	FORM_T = {	{0,1,0,},
				{1,1,1,},
	},
	FORM_O = {	{2,2,},
				{2,2,},
	};
	
	public static final String 
	TITLE = "Tetris", // ��������� ����
	RIGHT = "right", 
	LEFT = "left"
	;

}
