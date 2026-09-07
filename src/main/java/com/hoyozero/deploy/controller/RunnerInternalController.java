package com.hoyozero.deploy.controller;
import com.hoyozero.deploy.service.RunnerQueueService; import org.springframework.beans.factory.annotation.Value; import org.springframework.http.HttpStatus; import org.springframework.web.bind.annotation.*; import java.util.Map;
@RestController @RequestMapping("/internal/runner") public class RunnerInternalController {
 private final RunnerQueueService q; @Value("${hoyozero.runner-token:}") private String token; public RunnerInternalController(RunnerQueueService q){this.q=q;}
 private void auth(String supplied){if(token!=null&&!token.isBlank()&&!token.equals(supplied))throw new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED,"runner token invalid");}
 @PostMapping("/claim") public Object claim(@RequestHeader(value="X-Runner-Token",required=false)String t,@RequestBody Map<String,String> body)throws Exception{auth(t);return q.claim(body.getOrDefault("runner","runner"));}
 @PostMapping("/result") public Map<String,Object> result(@RequestHeader(value="X-Runner-Token",required=false)String t,@RequestBody Map<String,Object> p){auth(t);q.result(Long.valueOf(p.get("buildId").toString()),p.get("status").toString(),(String)p.getOrDefault("log",""),(String)p.get("commit"),(String)p.get("image"),(String)p.get("tag"),(String)p.get("digest"),(String)p.get("reason"));return Map.of("ok",true);}
}
