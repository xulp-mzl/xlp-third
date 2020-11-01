package org.xlp.excel.write.complexity.font;

import org.xlp.excel.write.complexity.enumeration.Color;
import org.xlp.utils.XLPStringUtil;

/**
 * @author xlp
 * @version 1.0 创建时间：2020年6月27日 下午11:24:23
 * @Description excel cell字体
*/
public class CellFont {
	/*
	 * 字体名称
	 */
	private String fontName = "宋体";
	
	/**
	 * 是否粗体
	 */
	private boolean blod = false;
	
	/**
	 * 字体大小
	 */
	private short fontSize = 12;
	
	/**
	 * 字体颜色
	 */
	private int fontColor = Color.BLACK.getIndex();
	
	/**
	 * 字体颜色
	 */
	private Color color;
	
	/**
	 * 12号粗体
	 */
	public static CellFont BLOD_FONT = new CellFont(null, true, (short) 12, Color.BLACK);
	
	/**
	 * 14号粗体
	 */
	public static CellFont BLOD_FONT_14 = new CellFont(null, true, (short) 14, Color.BLACK);
	
	/**
	 * 16号粗体
	 */
	public static CellFont BLOD_FONT_16 = new CellFont(null, true, (short) 16, Color.BLACK);
	
	/**
	 * 12号正常字体
	 */
	public static CellFont NORMAL_FONT = new CellFont(null, false, (short) 12, Color.BLACK);
	
	/**
	 * 14号正常字体
	 */
	public static CellFont NORMAL_FONT_14 = new CellFont(null, false, (short) 14, Color.BLACK);
	
	/**
	 * 16号正常字体
	 */
	public static CellFont NORMAL_FONT_16 = new CellFont(null, false, (short) 16, Color.BLACK);
	
	public CellFont(){}

	/**
	 * 
	 * @param fontName
	 * @param blod
	 * @param fontSize
	 * @param fontColor
	 */
	public CellFont(String fontName, boolean blod, short fontSize, int fontColor) {
		setFontName(fontName);
		this.blod = blod;
		this.fontSize = fontSize;
		this.fontColor = fontColor;
	}

	public CellFont(String fontName, boolean blod, short fontSize, Color color) {
		setFontName(fontName);
		this.blod = blod;
		this.fontSize = fontSize;
		setColor(color); 
	}
	
	public String getFontName() {
		return fontName;
	}

	public void setFontName(String fontName) {
		if (!XLPStringUtil.isEmpty(fontName)) {
			this.fontName = fontName.trim();
		}
	}

	public boolean isBlod() {
		return blod;
	}

	public void setBlod(boolean blod) {
		this.blod = blod;
	}

	public short getFontSize() {
		return fontSize;
	}

	public void setFontSize(short fontSize) {
		if (fontSize > 0) {
			this.fontSize = fontSize;
		}
	}

	public int getFontColor() {
		return fontColor;
	}

	public void setFontColor(int fontColor) {
		if (fontColor > 0) {
			this.fontColor = fontColor;
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CellFont [fontName=").append(fontName).append(", blod=").append(blod).append(", fontSize=")
				.append(fontSize).append(", fontColor=").append(fontColor).append("]");
		return builder.toString();
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
		if (color != null) {
			this.fontColor = color.getIndex();
		}
	}
}
