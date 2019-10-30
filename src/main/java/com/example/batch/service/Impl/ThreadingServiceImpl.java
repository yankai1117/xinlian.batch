package com.example.batch.service.Impl;

import com.alibaba.fastjson.JSON;
import com.example.batch.dao.VehicleInfoDAO;
import com.example.batch.entity.VehicleInfoBean;
import com.example.batch.service.ThreadingService;
import com.example.batch.util.AES;
import com.example.batch.util.HttpClient;
import com.example.batch.util.VehicleTypeEnum;
import net.sf.json.JSONObject;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @ClassName ThreadingServiceImpl
 * @Description TODO
 * @Author yankai
 * @Date 2019/10/2916:36
 * @Version 1.0.0
 */
@Component
public class ThreadingServiceImpl implements ThreadingService {
    private static final Logger logger = LoggerFactory
            .getLogger(ThreadingServiceImpl.class);
    @Resource
    private VehicleInfoDAO vehicleInfoDAO;
    @Override
    @Async
    public String executeAsync(String lineText){
        String key = getPra("SZ_key");
//        logger.info("key=" + key);
        String secret = getPra("SZ_secret");
        String url = getPra("SZ_url");
        String token_url = getPra("SZ_token_url");
        String host = getPra("SZ_host");
        VehicleInfoBean bean = new VehicleInfoBean();
        String[] array = lineText.split("\\|");
        String carplatenumber = array[0];
        String owner = array[1];
//        logger.info("carplatenumber=" + carplatenumber + ",owner=" + owner);
        bean.setCarNumber(carplatenumber);
        bean.setName(owner);
        //先查询数据库中是否有该车数据
        if(null != vehicleInfoDAO.findByPlateNumAndName(bean)){//如果已经有该车数据
            logger.info("数据库中已经有" + "carplatenumber=" + carplatenumber + ",owner=" + owner + "的数据");

        }else{
            logger.info("准备发往视甄查询......" +  lineText);

            JSONObject obj = new JSONObject();
            obj.put("key", key);
            obj.put("secret", secret);
            CloseableHttpClient httpclient = HttpClients.createDefault();
            /** setConnectTimeout：设置连接超时时间，单位毫秒。
             *setConnectionRequestTimeout：设置从connect Manager(连接池)获取Connection 超时时间，单位毫秒。
             *这个属性是新加的属性，因为目前版本是可以共享连接池的。
             *setSocketTimeout：请求获取数据的超时时间(即响应时间)，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
             **/
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(2000).build();//设置请求和传输超时时间
            HttpPost httpPost = new HttpPost(token_url);
            httpPost.setConfig(requestConfig);
            httpPost.addHeader("Content-Type", "application/json;charset=UTF-8");

            // 解决中文乱码问题
            StringEntity stringEntity = new StringEntity(obj.toString(), "UTF-8");
            httpPost.setEntity(stringEntity);
            logger.info("Executing request " + httpPost.getRequestLine());
            CloseableHttpResponse response;
            BufferedReader in = null;
            String result = "";
            try {
                response = httpclient.execute(httpPost);
                in = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"UTF-8"));
                String line;
                while ((line = in.readLine()) != null) {
                    result += line;
                }
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                logger.info("(1) exception because of " + e1.getMessage());
            }


            logger.info("result=" + result);
            JSONObject rep = JSONObject.fromObject(result);
            String code1 = rep.get("code").toString();
            logger.info("code=" + code1);
            String message1 = rep.get("message").toString();
            logger.info("message=" + message1);
            String token = "";

            if("200" == code1 || "200".equals(code1)){
                String data = rep.getJSONObject("data").toString(); // 获取data的值
                logger.info("data="+data);
                Map maps = (Map)JSON.parse(data);
                token = maps.get("token").toString();
            }
            /**=============================================获取token结束=============================================== **/
            HttpClient http = new HttpClient();
            HashMap<String, String> map2 = new HashMap();

            //二、定义接口入参
            //######################################################
            //接口参数
            map2.put("name", owner);
            map2.put("carNumber", carplatenumber);
            map2.put("carType", "02");
            map2.put("carCode", "");
            String jsonStr = JSON.toJSONString(map2);
            logger.info("响应内容:"+jsonStr);
            //AES加密
            String encryptSecret = secret.substring(0, 16); //截取用户secret前16位 (java不支持256位加密)
            String encryptStr = null;
            try {
                encryptStr = AES.encrypt(jsonStr, encryptSecret);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                logger.info("(2) exception because of " + e1.getMessage());
            }
            encryptStr = encryptStr.replaceAll("\\r\\n", "");
            String param = "{\"params\":\"" + encryptStr + "\"}";
//                logger.info("请求结果(如果遇到401或403错误,请检查url、host、key、secret及token是否正确)：\n");
            String code = "";

            String repStr = null;
            try {
                repStr = http.httpRequest(url, param, "POST", host, "Bearer " +token, "application/json; charset=gbk");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                logger.info(e.getMessage());
            }
            logger.info("repStr="+repStr);
            repStr.replaceAll("UTF-8", "");
            JSONObject resp = JSONObject.fromObject(repStr);
            code = resp.get("code").toString();
            String message = resp.get("message").toString();
            String serialNo = resp.get("serialNo").toString();
            String ACCOUNT_FLAG = "1";
            VehicleInfoBean vo = null;
            String data = null;
            String vehicleType = "";
            if("200" == code || "200".equals(code)){
                if(resp.get("data").toString().startsWith("{")){
                    data = resp.getJSONObject("data").toString(); // 获取data的值
                    logger.info("data="+data);
                    ACCOUNT_FLAG = "0";
                    Map maps = (Map)JSON.parse(data);
                    vo = JSON.parseObject(data, VehicleInfoBean.class);
                    vehicleType = (String) maps.get("vehicleType");
                }else{
                    ACCOUNT_FLAG = "1";
                    message = resp.get("data").toString();
                }
                logger.info("所有人是否正确:" + vo.getCarOwner());
                if("true" == vo.getCarOwner() || "true".equals(vo.getCarOwner())){
                    vo.setVERIFY_STATUS("0");
                }else{
                    vo.setVERIFY_STATUS("1");
                }
                logger.info(vehicleType);
//            			logger.info(vo.getVehicleType() + "长度=" + vo.getVehicleType().length());
                String value = VehicleTypeEnum.getValue(vehicleType);
                logger.info(value);
                vo.setVehicleType(value);
            }else{
                ACCOUNT_FLAG = "1";
            }
            //数据入库
            vo.setPARTNER_ID("000000000000000");
            vo.setCHANNEL_FLAG("03");
            vo.setACCOUNT_FLAG(ACCOUNT_FLAG);
            vo.setCode(code);
            vo.setMessage(message);
            vo.setSerialNo(serialNo);
            vo.setName(owner);
            try {
                int num = vehicleInfoDAO.insert(vo);
                if(num != 1){
                    num = 0;
                }
                logger.info("受影响的行数:" + num);
            } catch (Exception e) {
                // TODO: handle exception
                logger.info(e.getMessage());
            }
            map2.clear();
        }
        return "success";
    }
    public String getPra(String praName){
        Locale locale = new Locale("zh", "CN");
        ResourceBundle bundle = ResourceBundle.getBundle("applicationConstat", locale);
        String url=bundle.getString(praName);
        return url;
    }
}
