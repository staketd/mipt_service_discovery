package edu.phystech.servicemash;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NodeService {

    private final ServiceDao serviceDao;

    public NodeService(ServiceDao serviceDao) {
        this.serviceDao = serviceDao;
    }

    @Transactional
    public void discoverNode(String serviceId, String address, int port) {
        ClientService service = serviceDao.getCurrentVersion(serviceId);

        service.getInstances().remove(new Node(address));
        service.getInstances().add(new Node(address, port));

        serviceDao.saveNewVersion(service);
    }

    public void deregisterNode(String serviceId, String address, int port) {
        ClientService service = serviceDao.getCurrentVersion(serviceId);

        Node node = new Node(address, port);
        if (!service.getInstances().contains(node)) {
            return;
        }

        service.getInstances().remove(node);

        serviceDao.saveNewVersion(service);
    }
}
