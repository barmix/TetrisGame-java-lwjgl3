package me.barshay.tetris.forms;

import static me.barshay.tetris.utils.Utilites.*;

import me.barshay.tetris.Blocks;


public class Block {
	
	private int formSizeX, formSizeY;
	private int [][] form;
	private Blocks nextBlock;
	public Block() {
		nextBlock = Blocks.getRandomBlock();
		setForm(nextBlock);
	}
	public Block(int id) {
		nextBlock = Blocks.getBlockByNumber(id);
		setForm(nextBlock);
	}
	
	public Block(Block clone) {
		this.form = clone.form;
		this.formSizeX = clone.formSizeX;
		this.formSizeY = clone.formSizeY;
	}
		
	private void setForm(Blocks block) {
		this.form = block.getBlockForm();
		setSize(this.form);
	}

	private void setSize(int[][] form) {
		this.formSizeX = form[0].length;
		this.formSizeY = form.length;
	}
	
	public int[][] getForm(){
		return this.form;
	}
	
	public int getFormSizeX() {
		return this.formSizeX;
	}
	
	public int getFormSizeY() {
		return this.formSizeY;
	}
	
	public void formFlip(String direction) {
		switch (direction) {
		case "right": this.form = arrayRightFlip(this.form);
			break;
		case "left": this.form = arrayLeftFlip(this.form);
		default: break;
		}
		setSize(this.form);
	}
}

