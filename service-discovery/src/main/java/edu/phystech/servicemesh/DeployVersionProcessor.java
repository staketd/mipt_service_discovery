package edu.phystech.servicemesh;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeployVersionProcessor {
    private final ServiceDao serviceDao;

    public DeployVersionProcessor(ServiceDao serviceDao) {
        this.serviceDao = serviceDao;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateDeployedVersion(String serviceId, long version) {
        long currentVersion = serviceDao.getCurrentVersion(serviceId).getMaxDeployedVersion();

        if (currentVersion < version) {
            serviceDao.setCurrentDeployedVersion(serviceId, version);
        }
    }
}
