package tc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiPredicate;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class TCSearcher {

	static final Path BASE_DIR = Paths.get("");

	public static void main(String[] args) throws IOException {

		BiPredicate<Path, BasicFileAttributes> matcher = (path, attr) -> {
			String fileName = path.getFileName().toString();
			if (attr.isRegularFile() && fileName.endsWith("_Testcases.xlsx") && (fileName.startsWith("RateStream_")
					|| fileName.startsWith("RFS_") || fileName.startsWith("LeaveOrder_"))) {
				return true;
			}
			return false;
		};
		
		Files.find(BASE_DIR, 20, matcher).forEach(file -> {
			try {
				boolean flag = false;
				Workbook workbook = WorkbookFactory.create(Files.newInputStream(file, null));
				String id = workbook.getSheet("Testcases").getRow(1).getCell(2).getStringCellValue().trim();
				String api = workbook.getSheet("Testcases").getRow(2).getCell(2).getStringCellValue().trim();
				String msg = workbook.getSheet("Testcases").getRow(3).getCell(2).getStringCellValue().trim();
				for(int i=1; i<1000; i++) {
					Row row = workbook.getSheet("Testcases").getRow(i);
					String tc = row.getCell(0).getStringCellValue();
					if ("TC#".equals(tc)) {
						flag = true;
						continue;
					} else if (flag && tc!=null && !"".equals(tc.trim())) {
						String env = row.getCell(2).getStringCellValue().trim();
						String res = row.getCell(9).getStringCellValue();
						System.out.println(id+"\t"+file.getFileName().toString()
								+"\t"+msg+"\t■\t"+tc.trim()+"\t"+env+"\t-\t"+res);
					} else if (flag){
						break;
					}
				}				
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});

	}

}
