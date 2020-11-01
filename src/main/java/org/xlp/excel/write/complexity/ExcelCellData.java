package org.xlp.excel.write.complexity;

import java.io.Serializable;
import java.util.List;

import org.xlp.excel.write.complexity.enumeration.Color;
import org.xlp.excel.write.complexity.enumeration.DataType;
import org.xlp.excel.write.complexity.enumeration.HorizontalType;
import org.xlp.excel.write.complexity.enumeration.VerticalType;
import org.xlp.excel.write.complexity.font.CellFont;
import org.xlp.utils.XLPStringUtil;

/**
 * @author xlp
 * @version 1.0 创建时间：2020年6月27日 下午11:11:18
 * @Description 填充excel cell数据对象，方便写excel
 */
public class ExcelCellData implements Serializable{
	private static final long serialVersionUID = 1525155538380664810L;

	/**
	 * excel cell 内容垂直位置，默认垂直居中
	 */
	private VerticalType verticalType = VerticalType.MIDDLE;

	/**
	 * excel cell 内容水平位置，默认水平居左
	 */
	private HorizontalType horizontalType = HorizontalType.LEFT;

	/**
	 * cell宽度
	 */
	private int cellWidth = 0;

	/**
	 * cell 内容字体
	 */
	private CellFont cellFont = CellFont.NORMAL_FONT;

	/**
	 * 起始行号，从0开始
	 */
	private int fromRow;

	/**
	 * 结束行号，从0开始
	 */
	private int toRow;

	/**
	 * 起始列号，从0开始
	 */
	private int fromColumn;

	/**
	 * 结束列号，从0开始
	 */
	private int toColumn;

	/**
	 * excel cell中的数据是枚举类型时，可选数据
	 */
	private List<String> selectOptions;

	/**
	 * excel cell填充值
	 */
	private Object cellValue;

	/**
	 * excel cell value 默认格式
	 */
	public static final String DEFAULT_FORMTTER = "@";

	/**
	 * excel cell value formatter
	 */
	private String formatter = DEFAULT_FORMTTER;

	/**
	 * excel cell value类型
	 */
	private DataType dataType = DataType.STRING;
	
	/**
	 * 设置某列需要验证数据行数，如cell值为下拉选择框时生效
	 */
	private int maxFormatRow = 0;
	
	/**
	 * 单元格验证错误提示信息
	 */
	private String errorTip = "";
	
	/**
	 * 单元格值的上限
	 * <p>当为日期格式时，该值设置样例："2020-01-01"或时间时间毫秒值或java中的（Date，Calendar，LocalDateTime，LocalDate对象
	 * 	</br>或表达式Date(2020, 1, 12), 但设置成表达式时minValue的值也必须设置成表达式Date(2020, 1, 12)
	 * </p>
	 * <p>当为时间格式时，该值设置样例："23:44:00"或java中的（Date，Calendar，LocalDateTime，LocalDate, LocalTime对象
	 * 	</br>或表达式Time(23, 1, 12), 但设置成表达式时minValue的值也必须设置成表达式Time(23, 1, 12)</p>
	 * <p>当为数字格式时，该值设置样例：22.78或22或字符串22</p>
	 * <p>当为字符格式时，该值表示是字符串的最大长度</p>
	 */
	private Object maxValue;
	
	/**
	 * 单元格值的下限
	 * <p>当为日期格式时，该值设置样例："2020-01-01"或时间时间毫秒值或java中的（Date，Calendar，LocalDateTime，LocalDate对象
	 * 	</br>或表达式Date(2020, 1, 12), 但设置成表达式时minValue的值也必须设置成表达式Date(2020, 1, 12)
	 * </p>
	 * <p>当为时间格式时，该值设置样例："23:44:00"或java中的（Date，Calendar，LocalDateTime，LocalDate, LocalTime对象
	 * 	</br>或表达式Time(23, 1, 12), 但设置成表达式时minValue的值也必须设置成表达式Time(23, 1, 12)</p>
	 * <p>当为数字格式时，该值设置样例：22.78或22或字符串22</p>
	 * <p>当为字符格式时，该值表示是字符串的最小长度</p>
	 */
	private Object minValue;

	/**
	 * excel cell 的批注内容
	 */
	private String mark;
	
	/**
	 * excel cell 的批注内容所占的高度 
	 */
	private int markRow = 5;
	
	/**
	 * excel cell 的批注内容所占的宽度 
	 */
	private int markCol = 3;
	
	/**
	 * excel cell 的背景色
	 */
	private Color backgroundColor;
	
	/**
	 * excel cell是否可修改，值为true时，可修改，否则不允许修改
	 */
	private boolean canEdit = true;
	
	/**
	 * 构造函数
	 */
	public ExcelCellData() {
	}

	/**
	 * 构造函数
	 * 
	 * @param cellValue
	 * @param fromRow
	 * @param toRow
	 * @param fromColumn
	 * @param toColumn
	 */
	public ExcelCellData(Object cellValue, int fromRow, int toRow, int fromColumn, int toColumn) {
		this.fromRow = fromRow;
		this.toRow = toRow;
		this.fromColumn = fromColumn;
		this.toColumn = toColumn;
		this.cellValue = cellValue;
	}

	/**
	 * 构造函数
	 * 
	 * @param fromRow
	 * @param toRow
	 * @param fromColumn
	 * @param toColumn
	 * @param cellValue
	 * @param formatter
	 * @param dataType
	 */
	public ExcelCellData(int fromRow, int toRow, int fromColumn, int toColumn, Object cellValue, 
			String formatter, DataType dataType) {
		this.fromRow = fromRow;
		this.toRow = toRow;
		this.fromColumn = fromColumn;
		this.toColumn = toColumn;
		this.cellValue = cellValue;
		this.formatter = formatter;
		setDataType(dataType);
	}

	/**
	 * 构造函数
	 * 
	 * @param fromRow
	 * @param toRow
	 * @param fromColumn
	 * @param toColumn
	 * @param selectOptions
	 */
	public ExcelCellData(Object cellValue, int fromRow, int toRow, int fromColumn, int toColumn, 
			List<String> selectOptions) {
		this.fromRow = fromRow;
		this.toRow = toRow;
		this.fromColumn = fromColumn;
		this.toColumn = toColumn;
		this.selectOptions = selectOptions;
		this.cellValue = cellValue;
	}

	public VerticalType getVerticalType() {
		return verticalType;
	}

	/**
	 * 设置excel cell 内容垂直位置
	 * 
	 * @param verticalType
	 */
	public void setVerticalType(VerticalType verticalType) {
		if (verticalType != null) {
			this.verticalType = verticalType;
		}
	}

	public HorizontalType getHorizontalType() {
		return horizontalType;
	}

	/**
	 * excel cell 内容水平位置
	 * 
	 * @param horizontalType
	 */
	public void setHorizontalType(HorizontalType horizontalType) {
		if (horizontalType != null) {
			this.horizontalType = horizontalType;
		}
	}

	public int getCellWidth() {
		return cellWidth;
	}

	/**
	 * excel cell宽度
	 * 
	 * @param cellWidth
	 */
	public void setCellWidth(int cellWidth) {
		this.cellWidth = cellWidth;
	}

	public CellFont getCellFont() {
		return cellFont;
	}

	/**
	 * 设置excel cell 内容的字体
	 * 
	 * @param cellFont
	 */
	public void setCellFont(CellFont cellFont) {
		if (cellFont != null) {
			this.cellFont = cellFont;
		}
	}

	public int getFromRow() {
		return fromRow;
	}

	/**
	 * 设置起始行(从0开始)
	 * 
	 * @param fromRow
	 */
	public void setFromRow(int fromRow) {
		this.fromRow = fromRow;
	}

	public int getToRow() {
		return toRow;
	}

	/**
	 * 设置结束行
	 * 
	 * @param fromRow
	 */
	public void setToRow(int toRow) {
		this.toRow = toRow;
	}

	public int getFromColumn() {
		return fromColumn;
	}

	/**
	 * 设置起始列
	 * 
	 * @param fromRow
	 */
	public void setFromColumn(int fromColumn) {
		this.fromColumn = fromColumn;
	}

	public int getToColumn() {
		return toColumn;
	}

	/**
	 * 设置结束列
	 * 
	 * @param fromRow
	 */
	public void setToColumn(int toColumn) {
		this.toColumn = toColumn;
	}

	public List<String> getSelectOptions() {
		return selectOptions;
	}

	/**
	 * 设置excel cell中的数据是枚举类型时，可选数据，当isEnum值为true时生效
	 * 
	 * @param fromRow
	 */
	public void setSelectOptions(List<String> selectOptions) {
		this.selectOptions = selectOptions;
	}

	public Object getCellValue() {
		return cellValue;
	}

	/**
	 * 设置excel cell值
	 * 
	 * @param cellValue
	 */
	public void setCellValue(Object cellValue) {
		this.cellValue = cellValue;
	}

	public String getFormatter() {
		return formatter;
	}

	/**
	 * 设置excel cell value formatter
	 * 
	 * @param formatter
	 */
	public void setFormatter(String formatter) {
		if (!XLPStringUtil.isEmpty(formatter)) {
			this.formatter = formatter.trim();
		}
	}

	public DataType getDataType() {
		return dataType;
	}

	/**
	 * 设置excel cell value类型
	 * 
	 * @param dataType
	 */
	public void setDataType(DataType dataType) {
		if (dataType != null) {
			this.dataType = dataType;
		}
	}
	
	public int getMaxFormatRow() {
		return maxFormatRow;
	}

	/**
	 * 当导出模板时，设置某列的格式行数,当isExporTemplate值为true生效
	 * 
	 * @param maxFormatRow
	 */
	public void setMaxFormatRow(int maxFormatRow) {
		this.maxFormatRow = maxFormatRow;
	}

	public String getErrorTip() {
		return errorTip;
	}

	/**
	 * 当为模板导出时，单元格验证错误提示信息，当isExporTemplate值为true生效
	 * 
	 * @param errorTip
	 */
	public void setErrorTip(String errorTip) {
		this.errorTip = errorTip;
	}

	/**
	 * 获取单元格值的上限值
	 * <p>当为日期格式时，该值设置样例："2020-01-01"或时间时间毫秒值或java中的（Date，Calendar，LocalDateTime，LocalDate对象
	 * 	</br>或表达式Date(2020, 1, 12), 但设置成表达式时minValue的值也必须设置成表达式Date(2020, 1, 12)
	 * </p>
	 * <p>当为时间格式时，该值设置样例："23:44:00"或java中的（Date，Calendar，LocalDateTime，LocalDate, LocalTime对象
	 * 	</br>或表达式Time(23, 1, 12), 但设置成表达式时minValue的值也必须设置成表达式Time(23, 1, 12)</p>
	 * <p>当为数字格式时，该值设置样例：22.78或22或字符串22</p>
	 * <p>当为字符格式时，该值表示是字符串的最大长度</p>
	 * 
	 * @return
	 */
	public Object getMaxValue() {
		return maxValue;
	}

	/**
	 * 设置单元格值的上限
	 * <p>当为日期格式时，该值设置样例："2020-01-01"或时间时间毫秒值或java中的（Date，Calendar，LocalDateTime，LocalDate对象
	 * 	</br>或表达式Date(2020, 1, 12), 但设置成表达式时minValue的值也必须设置成表达式Date(2020, 1, 12)
	 * </p>
	 * <p>当为时间格式时，该值设置样例："23:44:00"或java中的（Date，Calendar，LocalDateTime，LocalDate, LocalTime对象
	 * 	</br>或表达式Time(23, 1, 12), 但设置成表达式时minValue的值也必须设置成表达式Time(23, 1, 12)</p>
	 * <p>当为数字格式时，该值设置样例：22.78或22或字符串22</p>
	 * <p>当为字符格式时，该值表示是字符串的最大长度</p>
	 */
	public void setMaxValue(Object maxValue) {
		this.maxValue = maxValue;
	}

	/**
	 * 获取单元格值的下限值
	 * <p>当为日期格式时，该值设置样例："2020-01-01"或时间时间毫秒值或java中的（Date，Calendar，LocalDateTime，LocalDate对象
	 * 	</br>或表达式Date(2020, 1, 12), 但设置成表达式时minValue的值也必须设置成表达式Date(2020, 1, 12)
	 * </p>
	 * <p>当为时间格式时，该值设置样例："23:44:00"或java中的（Date，Calendar，LocalDateTime，LocalDate, LocalTime对象
	 * 	</br>或表达式Time(23, 1, 12), 但设置成表达式时minValue的值也必须设置成表达式Time(23, 1, 12)</p>
	 * <p>当为数字格式时，该值设置样例：22.78或22或字符串22</p>
	 * <p>当为字符格式时，该值表示是字符串的最小长度</p>
	 * 
	 * @return
	 */
	public Object getMinValue() {
		return minValue;
	}

	/**
	 * 设置单元格值的下限
	 * <p>当为日期格式时，该值设置样例："2020-01-01"或时间时间毫秒值或java中的（Date，Calendar，LocalDateTime，LocalDate对象
	 * 	</br>或表达式Date(2020, 1, 12), 但设置成表达式时minValue的值也必须设置成表达式Date(2020, 1, 12)
	 * </p>
	 * <p>当为时间格式时，该值设置样例："23:44:00"或java中的（Date，Calendar，LocalDateTime，LocalDate, LocalTime对象
	 * 	</br>或表达式Time(23, 1, 12), 但设置成表达式时minValue的值也必须设置成表达式Time(23, 1, 12)</p>
	 * <p>当为数字格式时，该值设置样例：22.78或22或字符串22</p>
	 * <p>当为字符格式时，该值表示是字符串的最小长度</p>
	 */
	public void setMinValue(Object minValue) {
		this.minValue = minValue;
	}

	public String getMark() {
		return mark;
	}

	/**
	 * 设置excel cell 的标注内容
	 * 
	 * @param mark 
	 * 		excel cell 的批注内容
	 */
	public void setMark(String mark) {
		this.mark = mark;
	}

	public int getMarkRow() {
		return markRow;
	}

	/**
	 * 设置excel cell 的批注内容所占的高度 
	 * 
	 * @param markRow
	 */
	public void setMarkRow(int markRow) {
		this.markRow = markRow;
	}

	public int getMarkCol() {
		return markCol;
	}

	/**
	 * 设置excel cell 的批注内容所占的宽度 
	 * 
	 * @param markCol
	 */
	public void setMarkCol(int markCol) {
		this.markCol = markCol;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * 设置excel cell 的背景色
	 * 
	 * @param backgroundColor
	 */
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public boolean isCanEdit() {
		return canEdit;
	}

	/**
	 * 设置该excel cell是否可修改，值为true时，可修改，否则不允许修改
	 * 
	 * @param canEdit
	 */
	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ExcelCellData [verticalType=").append(verticalType).append(", horizontalType=")
				.append(horizontalType).append(", cellWidth=").append(cellWidth).append(", cellFont=").append(cellFont)
				.append(", fromRow=").append(fromRow).append(", toRow=").append(toRow).append(", fromColumn=")
				.append(fromColumn).append(", toColumn=").append(toColumn).append(", selectOptions=")
				.append(selectOptions).append(", cellValue=").append(cellValue).append(", formatter=").append(formatter)
				.append(", dataType=").append(dataType).append(", maxFormatRow=").append(maxFormatRow)
				.append(", errorTip=").append(errorTip).append(", maxValue=").append(maxValue).append(", minValue=")
				.append(minValue).append(", mark=").append(mark).append(", markRow=").append(markRow)
				.append(", markCol=").append(markCol).append(", backgroundColor=").append(backgroundColor)
				.append(", canEdit=").append(canEdit).append("]");
		return builder.toString();
	}
}
