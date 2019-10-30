package com.example.batch.controller;

import com.example.batch.service.ThreadingService;
import com.example.batch.util.UUIDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * @ClassName SZAsyncQueryController
 * @Description TODO
 * @Author yankai
 * @Date 2019/10/2915:10
 * @Version 1.0.0
 */
@Component
@Controller
public class SZAsyncQueryController {
    /** The logger. */
    private static final Logger log = LoggerFactory
            .getLogger(SZAsyncQueryController.class);
    @Resource
    @Autowired
    private ThreadingService threadingService;
    @ResponseBody
    @RequestMapping("/run")
    public String run(String filePath){
        String reqTxnSeq = UUIDUtil.getTimebaseUUID().toString();
        long timeStart = System.currentTimeMillis();
        log.info(reqTxnSeq+"视甄跑批文件查询开始=====");
        log.info(reqTxnSeq+"跑批文件位置:" + filePath);
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(filePath), "UTF-8");
            BufferedReader br = new BufferedReader(reader);
            String lineTxt = null;
            // 逐行读取本地文件
            while ((lineTxt = br.readLine()) != null) {
                log.info("读到lineTxt="  + lineTxt);
                threadingService.executeAsync(lineTxt);
            }
            log.info("批量文件读取结束，: "+"耗时："+(System.currentTimeMillis()-timeStart));
        }catch (Exception e){
            log.info(e.getMessage());
        }
        
        
        return "success";
    }
}
