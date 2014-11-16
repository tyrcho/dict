package com.tyrcho.dictionary.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

public final class ExcelExporter {
	private ExcelExporter() {
	}

  public static void export(String lang1, String lang2, String[][] data, File file)
			throws IOException {

    String[] titles = { lang1, lang2, "comments" };

		int[] colWidths = { 20, 20, 80 };

		Workbook wb = new HSSFWorkbook();

		Map<String, CellStyle> styles = createStyles(wb);

    Sheet sheet = wb.createSheet(lang1 + " to " + lang2);
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
		for (int i = 0; i < data.length; i++) {
			Row row = sheet.createRow(rownum++);
			for (int j = 0; j < titles.length; j++) {
				Cell cell = row.createCell(j);
				cell.setCellStyle(styles.get(j == 1 ? "large" : "cell"));
			}
		}

		// set sample data
		for (int i = 0; i < data.length; i++) {
			Row row = sheet.getRow(2 + i);
			for (int j = 0; j < data[i].length; j++) {
				if (data[i][j] != null) {
					row.getCell(j).setCellValue(data[i][j]);
				}
			}
		}

		// finally set column widths, the width is measured in units of 1/256th
		// of a character width
		for (int i = 0; i < colWidths.length; i++) {
			sheet.setColumnWidth(i, colWidths[i] * 256);
		}

		// Write the output to a file
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
		style.setWrapText(true);
		styles.put("cell", style);

		style = wb.createCellStyle();
		style.setWrapText(true);
		Font largeFont = wb.createFont();
		largeFont.setFontHeightInPoints((short) 24);
		style.setFont(largeFont);
		styles.put("large", style);

		return styles;
	}
}
