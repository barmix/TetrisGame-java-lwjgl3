package me.barshay.tetris;
import static me.barshay.tetris.Const.*;
import java.util.Random;

public enum Blocks {
	BLOCK_T(FORM_T), 
	BLOCK_O(FORM_O), 
	BLOCK_I(FORM_I), 
	BLOCK_S(FORM_S), 
	BLOCK_J(FORM_J), 
	BLOCK_L(FORM_L), 
	BLOCK_Z(FORM_Z);
	private static Blocks[] blockByNumber = { BLOCK_T, BLOCK_O, BLOCK_I, BLOCK_S, BLOCK_J, BLOCK_L, BLOCK_Z, };
	private int[][] blockForm;
	private Blocks(int[][] f) {
		blockForm = f;
	}
	public int[][] getBlockForm(){
		return blockForm;
	}
    public static Blocks getRandomBlock() {
        int blockNumber = new Random().nextInt(blockByNumber.length);
        return blockByNumber[blockNumber];
    }
    public static Blocks getBlockByNumber(int blockNumber) {
        return blockByNumber[blockNumber];
    }
    public static int getBlockByNumberLength() {
        return blockByNumber.length;
    }

}
