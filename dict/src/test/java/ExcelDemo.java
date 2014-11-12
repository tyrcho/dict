import java.io.IOException;

import com.tyrcho.dictionary.util.ExcelExporter;

/**
 * A weekly timesheet created using Apache POI. Usage: TimesheetDemo -xls|xlsx
 * 
 * @author Yegor Kozlov
 */
public class ExcelDemo {
	private static final String[] titles = { "Francais", "Arabe",
			"Explications" };

	private static Object[][] sample_data = {

	{ "Bojjour", "Hello", "bla bla \n bla bla" },
			{ "Bojjour", "Hello", "bla bla \n bla bla" },
			{ "Bojjour", "Hello", "bla bla \n bla bla" },
			{ "Bojjour", "Hello", "bla bla \n bla bla" },
			{ "Bojjour", "Hello", "bla bla \n bla bla" },
			{ "Bojjour", "Hello", "bla bla \n bla bla" },
			{ "Bojjour", "Hello", "bla bla \n bla bla" },
			{ "Bojjour", "Hello", "bla bla \n bla bla" },
			{ "Bojjour", "Hello", "bla bla \n bla bla" },
			{ "Bojjour", "Hello", "bla bla \n bla bla" },
			{ "Bojjour", "Hello", "bla bla \n bla bla" },
			{ "Bojjour", "Hello", "bla bla \n bla bla" },
			{ "Bojjour", "Hello", "bla bla \n bla bla" },
			{ "Bojjour", "Hello", "bla bla \n bla bla" },
			{ "Bojjour", "Hello", "bla bla \n bla bla" },
			{ "Bojjour", "Hello", "bla bla \n bla bla" },
			{ "Bojjour", "Hello", "bla bla \n bla bla" },
			{ "Bojjour", "Hello", "bla bla \n bla bla" },
			{ "Bojjour", "Hello", "bla bla \n bla bla" },
			{ "Bojjour", "Hello", "bla bla \n bla bla" },
			{ "Bojjour", "Hello", "bla bla \n bla bla" },
			{ "Bojjour", "Hello", "bla bla \n bla bla" },
			{ "Bojjour", "Hello", "bla bla \n bla bla" },
			{ "Bojjour", "Hello", "bla bla \n bla bla" },
			{ "Bojjour", "Hello", "bla bla \n bla bla" },
			{ "Bojjour", "Hello", "bla bla \n bla bla" },
			{ "Bojjour", "Hello", "bla bla \n bla bla" },
			{ "Bojjour", "Hello", "bla bla \n bla bla" },
			{ "Bojjour", "Hello", "bla bla \n bla bla" },
			{ "Bojjour", "Hello", "bla bla \n bla bla" },
			{ "Bojjour", "Hello", "bla bla \n bla bla" },
			{ "Bojjour", "Hello", "bla bla \n bla bla" },
			{ "Bojjour", "Hello", "bla bla \n bla bla" },
			{ "Bojjour", "Hello", "bla bla \n bla bla" },
			{ "Bojjour", "Hello", "bla bla \n bla bla" },
			{ "Bojjour", "Hello", "bla bla \n bla bla" },
			{ "Bojjour", "Hello", "bla bla \n bla bla" }, };

	public static void main(String[] args) throws IOException {
		int[] widths = { 20, 20, 80 };
		ExcelExporter.export(titles, sample_data, widths);
	}
}