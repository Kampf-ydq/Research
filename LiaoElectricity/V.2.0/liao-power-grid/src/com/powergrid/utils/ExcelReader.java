/**
 * Excel文件读取器
 */
package com.powergrid.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.powergrid.pojo.NumberFrequency;

/*** @author 作者 E-mail:* @version 创建时间：2022年3月1日 下午10:08:06* 类说明*/
/**
 * @author ydq
 * @version 2022年3月1日
 */
public class ExcelReader {
	private XSSFSheet sheet;

	/**
	 * 构造函数，初始化excel数据
	 * @param filePath  excel路径
	 * @param sheetName sheet表名
	 */
	public ExcelReader(String filePath,String sheetName){
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(filePath);
			XSSFWorkbook sheets = new XSSFWorkbook(fileInputStream);
			//获取sheet
			sheet = sheets.getSheet(sheetName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据行和列的索引获取单元格的数据
	 * @param row
	 * @param column
	 * @return
	 */
	public String getExcelDateByIndex(int row,int column){
		XSSFRow row1 = sheet.getRow(row);
		String cell = row1.getCell(column).toString();
		return cell;
	}

	/**
	 * 根据某一列值为“******”的这一行，来获取该行第x列的值
	 * @param caseName
	 * @param currentColumn 当前单元格列的索引
	 * @param targetColumn 目标单元格列的索引
	 * @return
	 */
	public String getCellByCaseName(String caseName,int currentColumn,int targetColumn){
		String operateSteps="";
			//获取行数
			int rows = sheet.getPhysicalNumberOfRows();
			for(int i=0;i<rows;i++){
				XSSFRow row = sheet.getRow(i);
				String cell = row.getCell(currentColumn).toString();
				if(cell.equals(caseName)){
					operateSteps = row.getCell(targetColumn).toString();
					break;
				}
			}
		return operateSteps;
	}

	//打印excel数据
	public void showExcelData(){
		//获取行数
		int rows = sheet.getPhysicalNumberOfRows();
		for(int i=0;i<rows;i++){
			//获取列数
			XSSFRow row = sheet.getRow(i);
			int columns = row.getPhysicalNumberOfCells();
			for(int j=0;j<columns;j++){
				String cell = row.getCell(j).toString();
				System.out.println(cell);
			}
		}
	}
	
	// 读取excel的一行，用NumberFrequency对象接收
	public List<NumberFrequency> readExcelData(){
		List<NumberFrequency> ls = new ArrayList<NumberFrequency>();
		NumberFrequency nf = null;
		float[] vec = null;
		//获取行数
		int rows = sheet.getPhysicalNumberOfRows();
		for(int i=1;i<rows;i++){
			//获取列数
			XSSFRow row = sheet.getRow(i);
			int columns = row.getPhysicalNumberOfCells();
			nf = new NumberFrequency();
			vec = new float[10];
			for(int j=0;j<10;j++){
				String cell = row.getCell(j).toString();
				vec[j] = (new Integer(cell)).floatValue();
			}
			nf.setVector(vec);
			nf.setUserId(row.getCell(10).toString());
			nf.setLabel(null);
			ls.add(nf);
		}
		return ls;
	}
	
	/**
     * 写入数据到excel文件
     * @param list 待写入的内容
     * @param filepath 文件全路径
     * @param sheetName Sheet页
     * @param titles 表头
     */
	public void writeDatatoExcel(List<NumberFrequency> list, String filepath, String sheetName, List<String> titles){
		String suffiex = getSuffiex(filepath);
		Workbook workbook;
        if ("xls".equals(suffiex.toLowerCase())) {
            workbook = new HSSFWorkbook();
        } else {
            workbook = new XSSFWorkbook();
        }
        // 生成一个表格
        org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet();
        // 写入表头: user_id, label, vector
        // 创建行
        Row header = sheet.createRow(0);
        for (int i = 0; i < titles.size(); i++) {
            // 创单元格
            Cell cell = header.createCell(i);
            // 向单元格中写入数据
            cell.setCellValue(titles.get(i));
		}
        // 从第一行开始写入数据
        for (int i = 0; i < list.size(); i++) {
			Row row = sheet.createRow(i + 1);
			Cell userIdCell = row.createCell(0);
            userIdCell.setCellValue(list.get(i).getUserId());
            Cell labelCell = row.createCell(1);
            labelCell.setCellValue(list.get(i).getLabel());
            Cell vecCell = row.createCell(2);
            vecCell.setCellValue(Arrays.toString(list.get(i).getVector()));
		}   
        try {
            //路径需要存在
             FileOutputStream fos = new FileOutputStream(filepath);
             workbook.write(fos);
             fos.close();
             workbook.close();
             System.out.println("写数据结束！");
           } catch (IOException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
           }
	}
	
	/**
     * 获取后缀
     * @param filepath filepath 文件全路径
     */
    private static String getSuffiex(String filepath) {
        /*if (StringUtils.isBlank(filepath)) {
            return "";
        }*/
        int index = filepath.lastIndexOf(".");
        if (index == -1) {
            return "";
        }
        return filepath.substring(index + 1, filepath.length());
    }
}

