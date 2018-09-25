package com.utils;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ExcelUtil
{

    public static void mergeExcel(String outputFileName, String... inputFileNameArray)
    {
        if (inputFileNameArray.length == 1)
        {
            System.out.println("至少需要两个文件才能合并，请验证！！！");
            return;
        }
        try
        {
            XSSFWorkbook newExcelCreat = new XSSFWorkbook();
            int a = 0;
            for (String f : inputFileNameArray)
            {
                InputStream in = new FileInputStream(f);
                XSSFWorkbook fromExcel = new XSSFWorkbook(in);
                for (int i = 0; i < fromExcel.getNumberOfSheets(); i++)
                {
                    //遍历每个sheet
                    a++;
                    XSSFSheet oldSheet = fromExcel.getSheetAt(i);
                    XSSFSheet newSheet = newExcelCreat.createSheet(a + "");
                    PoiUtil.copySheet(newExcelCreat, oldSheet, newSheet);
                }
            }
            FileOutputStream fileOut = new FileOutputStream(outputFileName);
            newExcelCreat.write(fileOut);
            fileOut.flush();
            fileOut.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void mergeExcelForOneSheet(String outputFileName, String... inputFileNameArray)
    {
        if (inputFileNameArray.length == 1)
        {
            System.out.println("至少需要两个文件才能合并，请验证！！！");
            return;
        }
        try
        {
            XSSFWorkbook newExcelCreat = new XSSFWorkbook();
            XSSFSheet newSheet = newExcelCreat.createSheet("文字识别结果,仅供参考");
            int rowNum = 0;
            for (String f : inputFileNameArray)
            {
                InputStream in = new FileInputStream(f);
                XSSFWorkbook fromExcel = new XSSFWorkbook(in);
                for (int i = 0; i < fromExcel.getNumberOfSheets(); i++)
                {
                    //遍历每个sheet
                    XSSFSheet oldSheet = fromExcel.getSheetAt(i);
                    for (int j = 0; j <= oldSheet.getLastRowNum(); j++)
                    {
                        rowNum++;
                        XSSFRow toRow = newSheet.createRow(rowNum);
                        PoiUtil.copyRow(newExcelCreat, oldSheet.getRow(j), toRow);
                    }
                }
            }
            FileOutputStream fileOut = new FileOutputStream(outputFileName);
            newExcelCreat.write(fileOut);
            fileOut.flush();
            fileOut.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
