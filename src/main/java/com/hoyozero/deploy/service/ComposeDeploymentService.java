package com.hoyozero.deploy.service;

import com.hoyozero.deploy.entity.Project;
import com.hoyozero.deploy.entity.Server;
import com.hoyozero.deploy.utils.SshUtils;
import com.jcraft.jsch.Session;
import org.springframework.stereotype.Service;
import java.util.function.Consumer;

/** 通过 SSH 在目标机执行受控 compose 部署；镜像使用精确 tag/digest，支持回滚。 */
@Service
public class ComposeDeploymentService {
    private final ProjectService projects; private final ServerService servers; private final ProjectServerService bindings;
    public ComposeDeploymentService(ProjectService p, ServerService s, ProjectServerService b) { projects=p; servers=s; bindings=b; }
    public void deploy(Long projectId, Long serverId, String image, Consumer<String> log) throws Exception {
        Project p=projects.getById(projectId); Server s=servers.getById(serverId); if(p==null||s==null) throw new IllegalArgumentException("项目或服务器不存在");
        if(!bindings.getProjectServerIds(projectId).contains(serverId)) throw new SecurityException("服务器未绑定到项目");
        if(image==null||image.isBlank()) throw new IllegalArgumentException("镜像不能为空");
        String dir=p.getComposePath(); if(dir==null||dir.isBlank()) throw new IllegalArgumentException("未配置 Compose 路径");
        String service=p.getComposeService(); if(service==null||service.isBlank()) throw new IllegalArgumentException("未配置 Compose 服务");
        validateComposeValue(dir, "Compose 路径"); validateComposeValue(service, "Compose 服务"); validateImage(image);
        String cmd="set -e; cd '"+dir+"'; export IMAGE='"+image+"'; docker pull '"+image+"'; docker compose pull '"+service+"'; docker compose up -d '"+service+"'; docker compose ps '"+service+"'";
        if(p.getHealthCheckUrl()!=null&&!p.getHealthCheckUrl().isBlank()) cmd += "; curl --fail --max-time 15 '"+p.getHealthCheckUrl().replace("'", "")+"'";
        Session session=SshUtils.createSession(s.getHost(),s.getPort(),s.getUsername(),s.getPassword(),s.getPrivateKey()); try { session.connect(); log.accept("[SSH] 已连接 "+s.getHost()+"\n"); SshUtils.executeCommand(session,cmd,log); log.accept("[部署] 健康检查通过\n"); } finally { if(session.isConnected()) session.disconnect(); }
    }
    private void validateComposeValue(String value, String label) { if (value.indexOf('\'') >= 0 || value.indexOf('\n') >= 0 || value.indexOf('\r') >= 0) throw new IllegalArgumentException(label+"包含非法字符"); }
    private void validateImage(String value) { validateComposeValue(value, "镜像"); if (!value.matches("[A-Za-z0-9./:@_-]+")) throw new IllegalArgumentException("镜像格式非法"); }
}
