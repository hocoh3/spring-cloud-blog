package com.blog.interaction.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "data-service")
public interface DataClient {

    @PostMapping("/aggregation/user/{userId}/activity")
    Boolean updateUserActivity(@PathVariable("userId") Long userId);
}
