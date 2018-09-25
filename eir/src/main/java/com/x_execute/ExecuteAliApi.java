package com.x_execute;

import com.ocr.RequestAliApi;
import com.utils.DownloadUtil;
import com.utils.ExcelUtil;
import org.testng.annotations.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static com.utils.DownloadUtil.cutImage;


public class ExecuteAliApi
{
    private static final String URL = "https://mp.weixin.qq.com/s/CE1_6tzkufgE-hWsA07DTw";
    private static final String BASE_PATH = "C:\\Users\\Administrator\\Desktop\\";
    private static final String PIC_FOLDER_NAME = "randomResult_pic";
    private static final String EXCEL_NAME = "result.xlsx";

    public static void main(String[] args) throws Exception
    {
        // 下载图片
        String html = DownloadUtil.getHTML(URL);
        List<String> listUrl = DownloadUtil.getImageURL(html);
        List<String> listSrc = DownloadUtil.getImageSrc(listUrl);
        DownloadUtil.Download(listSrc, BASE_PATH + PIC_FOLDER_NAME, 3);
        // 转excel
        invokeAliyunApi(BASE_PATH, PIC_FOLDER_NAME, BASE_PATH + EXCEL_NAME);
    }

    @Test
    public static void invokeAliyunApiTest() throws Exception
    {
        invokeAliyunApi(BASE_PATH, PIC_FOLDER_NAME, BASE_PATH + EXCEL_NAME);
    }

    @Test
    public static void cutImageTest() throws IOException
    {
        int number = 0;
        File file = new File("C:\\Users\\Administrator\\Desktop\\randomResult_pic");
        File[] fs = file.listFiles();
        Arrays.sort(fs, new CompratorByLastModified());
        for (File f : fs)
        {
            if (!f.isDirectory())
            {
                String picPath = f.getPath();
                ArrayList<BufferedImage> biLists = cutImage(picPath, 2, 1, 2);
                String fileNameString = "C:\\Users\\Administrator\\Desktop\\32";
                String format = "png";
                for (BufferedImage bi : biLists)
                {
                    File file1 = new File(fileNameString + File.separator + number + "." + format);
                    ImageIO.write(bi, format, file1);
                    number++;
                }
            }
        }
    }

    @Test
    public static void mergeExcelForOneSheetTest() throws IOException
    {
        String BASE_PATH = "C:\\Users\\Administrator\\Desktop";
        String xlsxFloderName = "randomResult_xlsx";

        String newExcel = BASE_PATH + File.separator + "rrr.xlsx";
        File excels = new File(BASE_PATH + File.separator + xlsxFloderName);
        File[] fs = excels.listFiles();
        Arrays.sort(fs, new CompratorByLastModified());
        List<String> excelList = new ArrayList<>();
        for (File f : fs)
        {
            if (!f.isDirectory())
            {
                excelList.add(f.getPath());
            }
        }
        ExcelUtil.mergeExcelForOneSheet(newExcel, excelList.toArray(new String[excelList.size()]));
    }


    private static void invokeAliyunApi(String BASE_PATH, String PIC_FOLDER_NAME, String newExcel) throws Exception
    {
        String xlsxFloderName = "randomResult_xlsx";
        DownloadUtil.checkPath(BASE_PATH + xlsxFloderName);

        File file = new File(BASE_PATH + PIC_FOLDER_NAME);
        File[] fs = file.listFiles();
        Arrays.sort(fs, new CompratorByLastModified());
        List<String> excelList = new ArrayList<>();
        for (File f : fs)
        {
            if (!f.isDirectory())
            {
                String picPath = f.getPath();
                String xlsxPath = picPath.substring(picPath.lastIndexOf("/") + 1, picPath.indexOf(".")) + ".xlsx";
                xlsxPath = xlsxPath.replace(PIC_FOLDER_NAME, xlsxFloderName);
                excelList.add(xlsxPath);
                RequestAliApi.requestXls(picPath, xlsxPath);
            }
        }
        ExcelUtil.mergeExcelForOneSheet(newExcel, excelList.toArray(new String[excelList.size()]));
    }

    /**
     * 文件排序
     */
    static class CompratorByLastModified implements Comparator<File>
    {
        @Override
        public int compare(File f1, File f2)
        {
            long diff = f1.lastModified() - f2.lastModified();
            if (diff > 0)
            {
                return 1;
            }
            else if (diff == 0)
            {
                return 0;
            }
            else
            {
                return -1;
            }
        }
    }
}
