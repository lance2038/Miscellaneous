package com.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 下载web图片
 */
public class DownloadUtil
{

    /**
     * 获取img标签正则
     */
    private static final String IMG_URL_REG = "<img.*src=(.*?)[^>]*?>";
    /**
     * 获取src路径的正则
     */
    private static final String IMG_SRC_REG = "[a-zA-z]+://[^\\s]*";


    /**
     * 获取html内容
     */
    public static String getHTML(String srcUrl) throws Exception
    {
        URL url = new URL(srcUrl);
        URLConnection conn = url.openConnection();
        InputStream is = conn.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        String line = null;
        StringBuffer buffer = new StringBuffer();
        while ((line = br.readLine()) != null)
        {
            buffer.append(line);
            buffer.append("\n");
        }
        br.close();
        isr.close();
        is.close();
        return buffer.toString();
    }

    /**
     * 获取image url地址
     */
    public static List<String> getImageURL(String html)
    {
        Matcher matcher = Pattern.compile(IMG_URL_REG).matcher(html);
        List<String> list = new ArrayList<>();
        while (matcher.find())
        {
            list.add(matcher.group());
        }
        return list;
    }

    /**
     * 获取image src地址
     */
    public static List<String> getImageSrc(List<String> listUrl)
    {
        List<String> listSrc = new ArrayList<>();
        for (String img : listUrl)
        {
            Matcher matcher = Pattern.compile(IMG_SRC_REG).matcher(img);
            while (matcher.find())
            {
                listSrc.add(matcher.group().substring(0, matcher.group().length() - 1));
            }
        }
        listSrc.remove(listSrc.size() - 1);
        listSrc.remove(listSrc.size() - 1);
        listSrc.remove(1);
        listSrc.remove(0);
        return listSrc;
    }

    /**
     * 下载图片
     */
    public static void Download(List<String> listImgSrc, String floderPath, int pageSize)
    {
        try
        {
            checkPath(floderPath);
            //每 pageSize 张图片拼接一次
            int pageNo = 1;
            int totalSize = listImgSrc.size();
            int forSize = totalSize % pageSize == 0 ? totalSize : ((int) (totalSize / pageSize) + 1) * (pageSize);
            for (int i = 0; i <= forSize; i++)
            {
                if (i > 0 && i % pageSize == 0)
                {
                    List<InputStream> list = new ArrayList<InputStream>();
                    // 从第几张开始拼接
                    int bgnIndex = (pageNo - 1) * pageSize;
                    // 结束拼接到第几张
                    int endIndex = pageNo * pageSize > totalSize ? (totalSize) : pageNo * pageSize;

                    for (int p = bgnIndex; p < endIndex; p++)
                    {
                        if (p <= totalSize)
                        {
                            String url = listImgSrc.get(p);
                            URL uri = new URL(url);
                            if (url.indexOf("png") > 0 || url.indexOf("jpg") > 0 || url.indexOf("jpeg") > 0)
                            {
                                list.add(uri.openStream());
                            }
                        }
                    }

                    boolean result = false;
                    try
                    {
                        result = yPic(list, floderPath + "\\" + "img-" + i / pageSize + ".png");
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    if (result)
                    {
                        System.out.println("下载完成:" + i / pageSize);
                    }
                    pageNo++;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 纵向处理图片
     */
    public static boolean yPic(List<InputStream> piclist, String outPath)
    {
        if (piclist == null || piclist.size() <= 0)
        {
            System.out.println("图片数组为空!");
            return false;
        }
        try
        {
            // 总高度
            int sumHeight = 0;
            // 总宽度
            int width = 0;
            // 图片的数量
            int picNum = piclist.size();
            // 保存每个文件的高度
            int[] heightArray = new int[picNum];
            // 保存所有的图片的RGB
            BufferedImage[] array = new BufferedImage[picNum];

            for (int i = 0; i < picNum; i++)
            {
                BufferedImage buffer = ImageIO.read(piclist.get(i));
                array[i] = buffer;
                // 偏移高度
                int height = buffer.getHeight();
                if (i == 0)
                {
                    // 图片宽度
                    width = buffer.getWidth();
                }
                // 图片高度
                heightArray[i] = height;
                // 获取总高度
                sumHeight += height;
            }
            BufferedImage imageResult = new BufferedImage(width, sumHeight, BufferedImage.TYPE_INT_RGB);
            // 生成新图片
            File outFile = new File(outPath);
            Graphics g = imageResult.createGraphics();
            int height = 0;
            for (BufferedImage ib : array)
            {
                int thisHeight = ib.getHeight();
                g.drawImage(ib, 0, height, width, thisHeight, null);
                height += thisHeight;
            }
            g.dispose();
            ImageIO.write(imageResult, "png", outFile);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return true;
    }

    // 切图
    public static ArrayList<BufferedImage> cutImage(String fileUrl, int rows, int cols, int nums)
    {
        ArrayList<BufferedImage> list = new ArrayList<BufferedImage>();
        try
        {
            BufferedImage img = ImageIO.read(new File(fileUrl));
            int lw = img.getWidth() / cols;
            int lh = img.getHeight() / rows;
            for (int i = 0; i < nums; i++)
            {
                BufferedImage buffImg = img.getSubimage(i % cols * lw, i / cols * lh, lw, lh);
                list.add(buffImg);
            }
            return list;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 校验文件夹
     */
    public static void checkPath(String floderPath)
    {
        File file = new File(floderPath);
        if (!file.exists() && !file.isDirectory())
        {
            file.mkdir();
        }
    }

    /**
     * 删除文件夹下的所有文件
     */
    public static void deletefile(String delPath) throws Exception
    {
        try
        {
            File file = new File(delPath);
            if (!file.isDirectory())
            {
                file.delete();
            }
            else if (file.isDirectory())
            {
                String[] fileList = file.list();
                for (int i = 0; i < fileList.length; i++)
                {
                    File delFile = new File(delPath + "\\" + fileList[i]);
                    if (!delFile.isDirectory())
                    {
                        delFile.delete();
                    }
                    else if (delFile.isDirectory())
                    {
                        deletefile(delPath + "\\" + fileList[i]);
                    }
                }
                file.delete();
            }

        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException
    {
        ArrayList<BufferedImage> biLists = cutImage("C:\\Users\\Administrator\\Desktop\\img-9.png", 2, 1, 2);
        String fileNameString = "C:\\Users\\Administrator\\Desktop\\32";
        int number = 0;
        String format = "png";
        for (BufferedImage bi : biLists)
        {
            File file1 = new File(fileNameString + File.separator + number + "." + format);
            ImageIO.write(bi, format, file1);
            number++;
        }
    }
}
