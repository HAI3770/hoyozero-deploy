package com.hoyozero.deploy.service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl; import com.hoyozero.deploy.entity.Deployment; import com.hoyozero.deploy.mapper.DeploymentMapper; import org.springframework.stereotype.Service;
@Service public class DeploymentService extends ServiceImpl<DeploymentMapper,Deployment> {}
