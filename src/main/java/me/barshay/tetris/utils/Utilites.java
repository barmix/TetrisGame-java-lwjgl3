package me.barshay.tetris.utils;
import java.io.*;
import java.nio.FloatBuffer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;

public class Utilites {

	public static int loadShader(String file, int type) {
		String result = "";
		InputStream stream = Utilites.class.getClassLoader().getResourceAsStream(file);
		try {
			if (stream == null) {
				throw new Exception("Cannot find file " + file);
			}
			result = IOUtils.toString(stream);
		} catch (Exception e) {
			e.printStackTrace();
		}

		int shaderID = glCreateShader(type);
		glShaderSource(shaderID, result);
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
