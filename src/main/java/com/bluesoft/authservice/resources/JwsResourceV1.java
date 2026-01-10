package com.bluesoft.authservice.resources;


import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

public interface JwsResourceV1 {

    @GetMapping("/.well-known/jwks.json")
    Map<String, Object> keys() throws Exception ;
}
