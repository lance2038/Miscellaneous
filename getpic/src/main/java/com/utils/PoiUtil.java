package com.utils;

import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.util.Iterator;


public class PoiUtil
{
    public class XSSFDateUtil extends DateUtil
    {

    }

    public static void copyCellStyle(XSSFCellStyle fromStyle, XSSFCellStyle toStyle)
    {

        toStyle.cloneStyleFrom(fromStyle);//此一行代码搞定
        //下面统统不用
        /*
        //对齐方式
        toStyle.setAlignment(fromStyle.getAlignment());
        //边框和边框颜色
        toStyle.setBorderBottom(fromStyle.getBorderBottom());
        toStyle.setBorderLeft(fromStyle.getBorderLeft());
        toStyle.setBorderRight(fromStyle.getBorderRight());
        toStyle.setBorderTop(fromStyle.getBorderTop());
        toStyle.setTopBorderColor(fromStyle.getTopBorderColor());
        toStyle.setBottomBorderColor(fromStyle.getBottomBorderColor());
        toStyle.setRightBorderColor(fromStyle.getRightBorderColor());
        toStyle.setLeftBorderColor(fromStyle.getLeftBorderColor());
        //背景和前景
        //toStyle.setFillPattern(fromStyle.getFillPattern());  //填充图案，不起作用，转为黑色
        toStyle.setFillBackgroundColor(fromStyle.getFillBackgroundColor());  //不起作用
        toStyle.setFillForegroundColor(fromStyle.getFillForegroundColor());
        toStyle.setDataFormat(fromStyle.getDataFormat());  //数据格式
        //toStyle.setFont(fromStyle.getFont());  //不起作用
        toStyle.setHidden(fromStyle.getHidden());
        toStyle.setIndention(fromStyle.getIndention());//首行缩进
        toStyle.setLocked(fromStyle.getLocked());
        toStyle.setRotation(fromStyle.getRotation());//旋转
        toStyle.setVerticalAlignment(fromStyle.getVerticalAlignment());  //垂直对齐
        toStyle.setWrapText(fromStyle.getWrapText()); //文本换行
        */
    }

    public static void mergeSheetAllRegion(XSSFSheet fromSheet, XSSFSheet toSheet)
    {//合并单元格
        int num = fromSheet.getNumMergedRegions();
        CellRangeAddress cellR = null;
        for (int i = 0; i < num; i++)
        {
            cellR = fromSheet.getMergedRegion(i);
            toSheet.addMergedRegion(cellR);
        }
    }

    public static void copyCell(XSSFWorkbook wb, XSSFCell fromCell, XSSFCell toCell)
    {
        XSSFCellStyle newstyle = wb.createCellStyle();
        copyCellStyle(fromCell.getCellStyle(), newstyle);
        //toCell.setEncoding(fromCell.getEncoding());
        //样式
        toCell.setCellStyle(newstyle);
        if (fromCell.getCellComment() != null)
        {
            toCell.setCellComment(fromCell.getCellComment());
        }
        // 不同数据类型处理
        int fromCellType = fromCell.getCellType();
        toCell.setCellType(fromCellType);
        if (fromCellType == XSSFCell.CELL_TYPE_NUMERIC)
        {
            if (XSSFDateUtil.isCellDateFormatted(fromCell))
            {
                toCell.setCellValue(fromCell.getDateCellValue());
            }
            else
            {
                toCell.setCellValue(fromCell.getNumericCellValue());
            }
        }
        else if (fromCellType == XSSFCell.CELL_TYPE_STRING)
        {
            toCell.setCellValue(fromCell.getRichStringCellValue());
        }
        else if (fromCellType == XSSFCell.CELL_TYPE_BLANK)
        {
            // nothing21
        }
        else if (fromCellType == XSSFCell.CELL_TYPE_BOOLEAN)
        {
            toCell.setCellValue(fromCell.getBooleanCellValue());
        }
        else if (fromCellType == XSSFCell.CELL_TYPE_ERROR)
        {
            toCell.setCellErrorValue(fromCell.getErrorCellValue());
        }
        else if (fromCellType == XSSFCell.CELL_TYPE_FORMULA)
        {
            toCell.setCellFormula(fromCell.getCellFormula());
        }
        else
        { // nothing29
        }

    }

    public static void copyRow(XSSFWorkbook wb, XSSFRow oldRow, XSSFRow toRow)
    {
        toRow.setHeight(oldRow.getHeight());
        for (Iterator cellIt = oldRow.cellIterator(); cellIt.hasNext(); )
        {
            XSSFCell tmpCell = (XSSFCell) cellIt.next();
            XSSFCell newCell = toRow.createCell(tmpCell.getColumnIndex());
            copyCell(wb, tmpCell, newCell);
        }
    }

    public static void copySheet(XSSFWorkbook wb, XSSFSheet fromSheet, XSSFSheet toSheet)
    {
        mergeSheetAllRegion(fromSheet, toSheet);
        //设置列宽
        for (int i = 0; i <= fromSheet.getRow(fromSheet.getFirstRowNum()).getLastCellNum(); i++)
        {
            toSheet.setColumnWidth(i, fromSheet.getColumnWidth(i));
        }
        for (Iterator rowIt = fromSheet.rowIterator(); rowIt.hasNext(); )
        {
            XSSFRow oldRow = (XSSFRow) rowIt.next();
            XSSFRow newRow = toSheet.createRow(oldRow.getRowNum());
            copyRow(wb, oldRow, newRow);
        }
    }


}