package edu.phystech.servicemash;

import edu.phystech.servicemash.exception.WrongParameterException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ClientServiceProcessor {
    private final ServiceDao serviceDao;

    public ClientServiceProcessor(
            ServiceDao serviceDao
    ) {
        this.serviceDao = serviceDao;
    }

    @Transactional
    public ClientService createService(String serviceId, String name, String fqdn) {
        return serviceDao.createNewService(serviceId, name, fqdn);
    }

    public ClientService getServiceByServiceIdAndVersion(String serviceId, long version) {
        return serviceDao.getByServiceIdAndVersion(serviceId, version);
    }

    public ClientService getCurrentServiceVersion(String serviceId) {
        return serviceDao.getCurrentVersion(serviceId);
    }

    @Transactional
    public ClientService editMeta(String serviceId, String newName, String fqdn) {
        ClientService currentVersion = serviceDao.getCurrentVersion(serviceId);
        currentVersion.setName(newName);
        currentVersion.setFqdn(fqdn);
        return serviceDao.saveNewVersion(currentVersion);
    }

    @Transactional
    public ClientService addUsedService(String serviceId, Set<String> usedServiceIds) {
        ClientService currentVersion = serviceDao.getCurrentVersion(serviceId);

        Set<String> existingServiceIds = serviceDao.getByIds(usedServiceIds).stream().map(ClientService::getServiceId).collect(Collectors.toSet());

        usedServiceIds.forEach(service -> {
            if (!existingServiceIds.contains(service)) {
                throw new WrongParameterException(service  + "does not exist");
            }
        });

        currentVersion.setUsedServices(usedServiceIds);

        return serviceDao.saveNewVersion(currentVersion);
    }
}
