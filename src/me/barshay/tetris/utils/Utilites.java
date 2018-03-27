package me.barshay.tetris.utils;
import java.nio.FloatBuffer;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import org.lwjgl.BufferUtils;

public class Utilites {

	public static int loadShader(String filepath, int type) {
		StringBuilder result = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filepath));
			String buffer = "";
			while((buffer = reader.readLine()) != null) {
				result.append(buffer);
				result.append("\n");
			}
			reader.close();
		}catch (IOException e) {
			System.err.println(e);
		}
		int shaderID = glCreateShader(type);
		glShaderSource(shaderID, result.toString());
		glCompileShader(shaderID);
		if(glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
			System.err.println("Could not compile shader");
		}
		return shaderID;
	}

	
	public static FloatBuffer createFloatBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	public static ByteBuffer createByteBuffer(byte[] data) {
		ByteBuffer buffer = BufferUtils.createByteBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	public static int[][] arrayLeftFlip(int[][] sArr) {
		int [][] rArr = new int[sArr[0].length][sArr.length];
	    int rArrI = sArr[0].length - 1;
	    int rArrJ = 0;
	    for (int [] sArrI : sArr){
	        for (int sArrJ : sArrI)
	            rArr[rArrI--][rArrJ] = sArrJ;
	        rArrI = sArr[0].length - 1;
	        rArrJ++;
	    }
	    return rArr;
	}
	
	public static int[][] arrayRightFlip(int[][] sArr) {
		int [][] rArr = new int[sArr[0].length][sArr.length];
	    int rArrI = 0;
	    int rArrJ = sArr.length - 1;
	    for (int [] sArrI : sArr){
	        for (int sArrJ : sArrI)
	            rArr[rArrI++][rArrJ] = sArrJ;
	        rArrI = 0;
	        rArrJ--;
	    }
	    return rArr;
	}
}
