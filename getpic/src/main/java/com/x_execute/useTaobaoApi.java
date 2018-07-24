package com.x_execute;

import com.ocr.RequestTaobaoApi;
import com.utils.DownloadUtil;
import com.utils.ExcelUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


public class useTaobaoApi
{
    public static void main(String[] args) throws Exception
    {
        String URL = "https://mp.weixin.qq.com/s/ovRpuQKrkwXLk8MLWl6s4w";
        String basePath = "C:\\Users\\Administrator\\Desktop\\";
        String picFloderName = "randomResult_pic";
        String excelName = "shanhuwan.xlsx";
        // 下载图片
        String html = DownloadUtil.getHTML(URL);
        List<String> listUrl = DownloadUtil.getImageURL(html);
        List<String> listSrc = DownloadUtil.getImageSrc(listUrl);
        DownloadUtil.Download(listSrc, basePath + picFloderName, 6);
        // 转excel
        invokeAliyunApi(basePath, picFloderName, basePath + excelName);
    }

    public static void invokeAliyunApi(String basePath, String picFloderName, String newExcel) throws Exception
    {
        String xlsxFloderName = "randomResult_xlsx";
        DownloadUtil.checkPath(basePath + xlsxFloderName);

        File file = new File(basePath + picFloderName);
        File[] fs = file.listFiles();
        Arrays.sort(fs, new CompratorByLastModified());
        List<String> excelList = new ArrayList();
        for (File f : fs)
        {
            if (!f.isDirectory())
            {
                String picPath = f.getPath();
                String xlsxPath = picPath.substring(picPath.lastIndexOf("/") + 1, picPath.indexOf(".")) + ".xlsx";
                xlsxPath = xlsxPath.replace(picFloderName, xlsxFloderName);
                excelList.add(xlsxPath);
                RequestTaobaoApi.requestXls(picPath, xlsxPath);
            }
        }
        ExcelUtil.mergeExcel(newExcel, excelList.toArray(new String[excelList.size()]));
    }

    static class CompratorByLastModified implements Comparator<File>
    {
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
