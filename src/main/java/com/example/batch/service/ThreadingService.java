package com.example.batch.service;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @ClassName ThreadingService
 * @Description TODO
 * @Author yankai
 * @Date 2019/10/2915:24
 * @Version 1.0.0
 */
@Component
@Service("threadingService")
public interface ThreadingService {


    String executeAsync(String lineText);


}
