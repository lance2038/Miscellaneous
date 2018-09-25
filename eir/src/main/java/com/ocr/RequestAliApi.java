package com.ocr;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.api.gateway.demo.util.HttpUtils;
import com.utils.Base64Util;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.ocr.Constant.ALIYUN_APPCODE;


/**
 * 使用APPCODE进行云市场ocr服务接口调用
 */

public class RequestAliApi
{
    /**
     * print err msg
     */
    private static void printFailedMsg(int stat, HttpResponse response) throws IOException
    {
        System.out.println("Http code: " + stat);
        System.out.println("http header error msg: " + response.getFirstHeader("X-Ca-Error-Message"));
        System.out.println("Http body error msg:" + EntityUtils.toString(response.getEntity()));
    }

    /**
     * pic to txt
     */
    public static void requestGeneral() throws Exception
    {
        //请求路径
        String host = "http://tysbgpu.market.alicloudapi.com";
        String path = "/api/predict/ocr_general";
        String imgFile = "C:/Users/lance/Desktop/1.png";

        //请求参数
        JSONObject configObj = new JSONObject();
        String configStr = configObj.toString();

        //请求方式
        String method = "POST";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "APPCODE " + ALIYUN_APPCODE);

        // 拼装请求body的json字符串
        JSONObject requestObj = new JSONObject();
        requestObj.put("image", Base64Util.fileToBase64(imgFile));
        if (configStr.length() > 0)
        {
            requestObj.put("configure", configStr);
        }
        String bodys = requestObj.toString();

        //获取返回结果
        HttpResponse response = HttpUtils.doPost(host, path, method, headers, new HashMap<String, String>(), bodys);
        int stat = response.getStatusLine().getStatusCode();
        if (stat != HttpStatus.SC_OK)
        {
            printFailedMsg(stat, response);
            return;
        }
        String res = EntityUtils.toString(response.getEntity());
        JSONObject resObj = JSON.parseObject(res);
        System.out.println(resObj.toJSONString());

    }

    /**
     * xls.pic to xls.xlsx
     */
    public static void requestXls(String imgFile, String xlsxPath) throws Exception
    {
        //请求路径
        String host = "https://form.market.alicloudapi.com";
        String path = "/api/predict/ocr_table_parse";

        //请求参数
        JSONObject configObj = new JSONObject();
        configObj.put("format", "xlsx");
        configObj.put("finance", false);
        configObj.put("dir_assure", false);
        String configStr = configObj.toString();

        //请求方式
        String method = "POST";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "APPCODE " + ALIYUN_APPCODE);

        // 拼装请求body的json字符串
        JSONObject requestObj = new JSONObject();
        requestObj.put("image", Base64Util.fileToBase64(imgFile));
        if (configStr.length() > 0)
        {
            requestObj.put("configure", configStr);
        }
        String bodys = requestObj.toString();

        //获取返回结果
        HttpResponse response = HttpUtils.doPost(host, path, method, headers, new HashMap<String, String>(), bodys);
        int stat = response.getStatusLine().getStatusCode();
        if (stat != HttpStatus.SC_OK)
        {
            printFailedMsg(stat, response);
            return;
        }
        String res = EntityUtils.toString(response.getEntity());
        JSONObject resObj = JSON.parseObject(res);
        System.out.println(resObj.toJSONString());
        Base64Util.base64ToFile(resObj.getString("tables"), xlsxPath);
    }
}

