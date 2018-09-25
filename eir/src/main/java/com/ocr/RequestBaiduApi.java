package com.ocr;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.baidu.aip.ocr.AipOcr;
import org.testng.annotations.Test;

import java.util.HashMap;

import static com.ocr.Constant.*;


public class RequestBaiduApi
{
    /**
     * 推送excel图片文件
     *
     * @param path
     * @return
     * @throws JSONException
     */
    public static JSONObject tableRecognitionAsync(String path) throws JSONException
    {
        // 初始化一个AipOcr
        AipOcr client = new AipOcr(BAIDU_APP_ID, BAIDU_API_KEY, BAIDU_SECRET_KEY);
        client.setConnectionTimeoutInMillis(60000);
        client.setSocketTimeoutInMillis(60000);

        // 调用接口
        org.json.JSONObject res = client.tableRecognitionAsync(path, new HashMap<String, String>());
        JSONObject jsonObject = JSONObject.parseObject(res.toString());
        return jsonObject;
    }

    /**
     * 获取excel识别文件
     *
     * @param imgId
     * @return
     * @throws JSONException
     */
    public static JSONObject tableResultGet(String imgId, boolean isJson) throws JSONException
    {
        // 初始化一个AipOcr
        AipOcr client = new AipOcr(BAIDU_APP_ID, BAIDU_API_KEY, BAIDU_SECRET_KEY);
        client.setConnectionTimeoutInMillis(60000);
        client.setSocketTimeoutInMillis(60000);

        org.json.JSONObject res = null;
        // 调用接口
        if (isJson)
        {
            res = client.getTableRecognitionJsonResult(imgId);
        }
        else
        {
            res = client.getTableRecognitionExcelResult(imgId);
        }
        JSONObject jsonObject = JSONObject.parseObject(res.toString());
        return jsonObject;
    }

    /**
     * 识别文字
     *
     * @param path
     * @return
     * @throws JSONException
     */
    public static JSONObject general(String path) throws JSONException
    {
        // 初始化一个AipOcr
        AipOcr client = new AipOcr(BAIDU_APP_ID, BAIDU_API_KEY, BAIDU_SECRET_KEY);
        client.setConnectionTimeoutInMillis(60000);
        client.setSocketTimeoutInMillis(60000);

        // 调用接口
        org.json.JSONObject res = client.general(path, new HashMap<String, String>());
        JSONObject jsonObject = JSONObject.parseObject(res.toString());
        return jsonObject;
    }

    @Test
    public void xlsRequestTest()
    {
        String imgPath = "C:\\Users\\Administrator\\Desktop\\randomResult_pic\\img-2.png";
        JSONObject jsonResult = tableRecognitionAsync(imgPath);
        System.out.println("发送xls图片结果:\n" + jsonResult);
    }

    @Test
    public void xlsResultTest()
    {
        String imgId = "11554539_410513";
        JSONObject jsonResult = tableResultGet(imgId, false);
        System.out.println("接收xls图片结果:\n" + jsonResult);
    }

    @Test
    public void generalTest()
    {
        String imgPath = "C:/Users/lance/Desktop/randomResult/img-1.png";
        JSONObject jsonResult = general(imgPath);
        System.out.println("接受文字图片结果:\n" + jsonResult);
    }
}
