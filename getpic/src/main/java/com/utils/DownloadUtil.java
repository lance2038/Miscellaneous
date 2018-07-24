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


public class DownloadUtil
{

    // 获取img标签正则
    private static final String IMGURL_REG = "<img.*src=(.*?)[^>]*?>";
    // 获取src路径的正则
    private static final String IMGSRC_REG = "[a-zA-z]+://[^\\s]*";


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
        Matcher matcher = Pattern.compile(IMGURL_REG).matcher(html);
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
            Matcher matcher = Pattern.compile(IMGSRC_REG).matcher(img);
            while (matcher.find())
            {
                listSrc.add(matcher.group().substring(0, matcher.group().length() - 1));
            }
        }
        return listSrc;
    }

    /**
     * 下载图片
     */
    public static void Download(List<String> listImgSrc, String floderPath, int pagesize)
    {
        try
        {
            checkPath(floderPath);
            //每3张图片拼接一次
            int pageno = 1;
            int totalSize = listImgSrc.size();
            int forSize = totalSize % pagesize == 0 ? totalSize : ((int) (totalSize / pagesize) + 1) * (pagesize);
            for (int i = 0; i <= forSize; i++)
            {
                if (i > 0 && i % pagesize == 0)
                {
                    List<InputStream> list = new ArrayList<InputStream>();
                    int bgnIndex = (pageno - 1) * pagesize;//从第几张开始拼接
                    int endIndex = pageno * pagesize > totalSize ? (totalSize) : pageno * pagesize;//结束拼接到第几张
                    for (int p = bgnIndex; p < endIndex; p++)
                    {
                        if (i <= totalSize)
                        {
                            String url = listImgSrc.get(p);
                            String imageName = url.substring(url.lastIndexOf("/") + 1, url.length()).replace("?wx_fmt=", ".");
                            URL uri = new URL(url);
                            if (imageName.indexOf("png") > 0 || imageName.indexOf("jpg") > 0 || imageName.indexOf("jpeg") > 0)
                            {
                                list.add(uri.openStream());
                            }
                        }
                    }
                    boolean result = false;
                    try
                    {
                        result = yPic(list, floderPath + "\\" + "img-" + i / pagesize + ".png");
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    if (result)
                    {
                        System.out.println("下载完成:" + i / pagesize);
                    }
                    pageno++;
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
    {// 纵向处理图片
        if (piclist == null || piclist.size() <= 0)
        {
            System.out.println("图片数组为空!");
            return false;
        }
        try
        {
            int sumheight = 0;// 总高度
            int width = 0; // 总宽度
            int picNum = piclist.size();// 图片的数量
            int[] heightArray = new int[picNum]; // 保存每个文件的高度
            BufferedImage[] array = new BufferedImage[picNum]; // 保存所有的图片的RGB

            for (int i = 0; i < picNum; i++)
            {
                BufferedImage buffer = ImageIO.read(piclist.get(i));
                array[i] = buffer;
                int height = buffer.getHeight(); // 临时的高度 , 或保存偏移高度
                if (i == 0)
                {
                    width = buffer.getWidth();// 图片宽度
                }
                heightArray[i] = height;// 图片高度
                sumheight += height; // 获取总高度
            }
            BufferedImage imageResult = new BufferedImage(width, sumheight, BufferedImage.TYPE_INT_RGB);
            // 生成新图片
            File outFile = new File(outPath);
            Graphics g = imageResult.createGraphics();
            int height = 0;
            for (BufferedImage ib : array)
            {
                int thisHight = ib.getHeight();
                g.drawImage(ib, 0, height, width, thisHight, null);
                height += thisHight;
            }
            g.dispose();
            ImageIO.write(imageResult, "png", outFile);// 写图片
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return true;
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


    public static void deletefile(String delpath) throws Exception
    {
        try
        {
            File file = new File(delpath);
            if (!file.isDirectory())
            {
                file.delete();
            }
            else if (file.isDirectory())
            {
                String[] filelist = file.list();
                for (int i = 0; i < filelist.length; i++)
                {
                    File delfile = new File(delpath + "\\" + filelist[i]);
                    if (!delfile.isDirectory())
                    {
                        delfile.delete();
                    }
                    else if (delfile.isDirectory())
                    {
                        deletefile(delpath + "\\" + filelist[i]);
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
}
