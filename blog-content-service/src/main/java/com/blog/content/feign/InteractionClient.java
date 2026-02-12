package com.blog.content.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "interaction-service")
public interface InteractionClient {

    @PostMapping("/notification/send")
    Boolean sendNotification(@RequestBody Map<String, Object> notification);
}
