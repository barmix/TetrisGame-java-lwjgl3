package me.barshay.tetris;
import static me.barshay.tetris.Const.*;
import java.util.Random;
import me.barshay.tetris.utils.Vector4f;

public enum Colors {
	BLACK(VEC4_BLACK), 
	RED(VEC4_RED), 
	GREEN(VEC4_GREEN), 
	BLUE(VEC4_BLUE), 
	AQUA(VEC4_AQUA), 
	YELLOW(VEC4_YELLOW), 
	ORANGE(VEC4_ORANGE), 
	PURPLE(VEC4_PURPLE),
	WHITE(VEC4_WHITE);
    private static Colors[] colorByNumber = { BLACK, RED, GREEN, BLUE, AQUA, YELLOW, ORANGE, PURPLE, WHITE};
    private Vector4f colorVec4;
    private Colors(Vector4f vec4) {
    	colorVec4 = vec4;
    }
    public Vector4f getColorVec4() {
    	return colorVec4;
    }
    public static Colors getRandomColor() {
        int colorNumber = new Random().nextInt(colorByNumber.length);
        return colorByNumber[colorNumber];
    }
    public static Colors getColorByNumber(int colorNumber) {
        return colorByNumber[colorNumber];
    }
    public static int getColorByNumberLength() {
        return colorByNumber.length;
    }

}
