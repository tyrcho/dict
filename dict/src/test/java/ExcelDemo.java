import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * A weekly timesheet created using Apache POI. Usage: TimesheetDemo -xls|xlsx
 * 
 * @author Yegor Kozlov
 */
public class ExcelDemo {
	private static final String[] titles = { "Person", "ID", "Mon", "Tue",
			"Wed", "Thu", "Fri", "Sat", "Sun", "Total\nHrs", "Overtime\nHrs",
			"Regular\nHrs" };

	private static Object[][] sample_data = {

	{ "Yegor Kozlov", "YK", 5.0, 8.0, 10.0, 5.0, 5.0, 7.0, 6.0 },
			{ "Yegor Kozlov", "YK", 5.0, 8.0, 10.0, 5.0, 5.0, 7.0, 6.0 },
			{ "Yegor Kozlov", "YK", 5.0, 8.0, 10.0, 5.0, 5.0, 7.0, 6.0 },
			{ "Yegor Kozlov", "YK", 5.0, 8.0, 10.0, 5.0, 5.0, 7.0, 6.0 },
			{ "Yegor Kozlov", "YK", 5.0, 8.0, 10.0, 5.0, 5.0, 7.0, 6.0 },
			{ "Yegor Kozlov", "YK", 5.0, 8.0, 10.0, 5.0, 5.0, 7.0, 6.0 },
			{ "Yegor Kozlov", "YK", 5.0, 8.0, 10.0, 5.0, 5.0, 7.0, 6.0 },
			{ "Yegor Kozlov", "YK", 5.0, 8.0, 10.0, 5.0, 5.0, 7.0, 6.0 },
			{ "Yegor Kozlov", "YK", 5.0, 8.0, 10.0, 5.0, 5.0, 7.0, 6.0 },
			{ "Yegor Kozlov", "YK", 5.0, 8.0, 10.0, 5.0, 5.0, 7.0, 6.0 },
			{ "Yegor Kozlov", "YK", 5.0, 8.0, 10.0, 5.0, 5.0, 7.0, 6.0 },
			{ "Yegor Kozlov", "YK", 5.0, 8.0, 10.0, 5.0, 5.0, 7.0, 6.0 },
			{ "Yegor Kozlov", "YK", 5.0, 8.0, 10.0, 5.0, 5.0, 7.0, 6.0 },
			{ "Yegor Kozlov", "YK", 5.0, 8.0, 10.0, 5.0, 5.0, 7.0, 6.0 },
			{ "Yegor Kozlov", "YK", 5.0, 8.0, 10.0, 5.0, 5.0, 7.0, 6.0 },
			{ "Gisella Bronzetti", "GB", 4.0, 3.0, 1.0, 3.5, null, null, 4.0 } };

	public static void main(String[] args) throws Exception {
		Workbook wb = new HSSFWorkbook();

		Map<String, CellStyle> styles = createStyles(wb);

		Sheet sheet = wb.createSheet("Timesheet");
		PrintSetup printSetup = sheet.getPrintSetup();
		printSetup.setLandscape(true);
		sheet.setFitToPage(true);
		sheet.setHorizontallyCenter(true);

		// header row
		Row headerRow = sheet.createRow(1);
		headerRow.setHeightInPoints(40);
		Cell headerCell;
		for (int i = 0; i < titles.length; i++) {
			headerCell = headerRow.createCell(i);
			headerCell.setCellValue(titles[i]);
			headerCell.setCellStyle(styles.get("header"));
		}

		int rownum = 2;
		for (int i = 0; i < sample_data.length; i++) {
			Row row = sheet.createRow(rownum++);
			for (int j = 0; j < titles.length; j++) {
				Cell cell = row.createCell(j);
				cell.setCellStyle(styles.get("cell"));
			}
		}

		// set sample data
		for (int i = 0; i < sample_data.length; i++) {
			Row row = sheet.getRow(2 + i);
			for (int j = 0; j < sample_data[i].length; j++) {
				if (sample_data[i][j] == null)
					continue;

				if (sample_data[i][j] instanceof String) {
					row.getCell(j).setCellValue((String) sample_data[i][j]);
				} else {
					row.getCell(j).setCellValue((Double) sample_data[i][j]);
				}
			}
		}

		// finally set column widths, the width is measured in units of 1/256th
		// of a character width
		sheet.setColumnWidth(0, 30 * 256); // 30 characters wide
		for (int i = 2; i < 9; i++) {
			sheet.setColumnWidth(i, 6 * 256); // 6 characters wide
		}
		sheet.setColumnWidth(10, 10 * 256); // 10 characters wide

		// Write the output to a file
		String file = "timesheet.xls";
		FileOutputStream out = new FileOutputStream(file);
		wb.write(out);
		out.close();
	}

	/**
	 * Create a library of cell styles
	 */
	private static Map<String, CellStyle> createStyles(Workbook wb) {
		Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
		CellStyle style;

		Font monthFont = wb.createFont();
		monthFont.setFontHeightInPoints((short) 24);
		monthFont.setColor(IndexedColors.WHITE.getIndex());
		style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setFont(monthFont);
		style.setWrapText(true);
		styles.put("header", style);

		style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setWrapText(true);
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(CellStyle.BORDER_THIN);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		styles.put("cell", style);

		return styles;
	}
}
