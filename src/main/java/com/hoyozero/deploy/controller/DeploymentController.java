package com.hoyozero.deploy.controller;
import com.hoyozero.deploy.common.Result; import com.hoyozero.deploy.service.ComposeDeploymentService; import com.hoyozero.deploy.service.DeploymentService; import com.hoyozero.deploy.entity.Deployment; import cn.dev33.satoken.stp.StpUtil; import org.springframework.web.bind.annotation.*; import java.util.Map;
@RestController @RequestMapping("/api/deployment")
public class DeploymentController {
 private final ComposeDeploymentService service; private final DeploymentService records; public DeploymentController(ComposeDeploymentService s,DeploymentService r){service=s;records=r;}
 @GetMapping("/list") public Result<Object> list(@RequestParam Long projectId){return Result.success(records.lambdaQuery().eq(Deployment::getProjectId,projectId).orderByDesc(Deployment::getDeploymentTime).list());}
 @PostMapping("/deploy") public Result<Map<String,String>> deploy(@RequestBody Map<String,Object> p) throws Exception { return execute(p,"DEPLOYING"); }
 @PostMapping("/rollback") public Result<Map<String,String>> rollback(@RequestBody Map<String,Object> p) throws Exception { return execute(p,"ROLLING_BACK"); }
 private Result<Map<String,String>> execute(Map<String,Object> p,String state) throws Exception { Long pid=Long.valueOf(p.get("projectId").toString()),sid=Long.valueOf(p.get("serverId").toString()); String image=(String)p.get("image"); StringBuilder l=new StringBuilder(); Deployment d=new Deployment();d.setProjectId(pid);d.setServerId(sid);d.setNewImage(image);d.setStatus(state);d.setOperateBy(StpUtil.getLoginIdAsString());records.save(d);try{service.deploy(pid,sid,image,l::append);d.setStatus(state.equals("DEPLOYING")?"SUCCESS":"ROLLBACK_SUCCESS");}catch(Exception e){d.setStatus(state.equals("DEPLOYING")?"FAILED":"ROLLBACK_FAILED");l.append("[错误] ").append(e.getMessage());throw e;}finally{d.setDeploymentLog(l.toString());records.updateById(d);}return Result.success(Map.of("message","操作成功","log",l.toString())); }
}
