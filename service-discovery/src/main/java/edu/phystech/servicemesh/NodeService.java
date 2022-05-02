package edu.phystech.servicemesh;

import java.util.List;

import edu.phystech.servicemesh.model.NodeLayout;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NodeService {

    private final ServiceDao serviceDao;
    private final NodeLayoutDao nodeLayoutDao;

    public NodeService(
            ServiceDao serviceDao,
            NodeLayoutDao nodeLayoutDao
    ) {
        this.serviceDao = serviceDao;
        this.nodeLayoutDao = nodeLayoutDao;
    }

    @Transactional
    public void discoverNode(String serviceId, String address, int port) {
//        ClientService service = serviceDao.getCurrentVersion(serviceId);
//
//        service.getInstances().remove(new ServiceInstance(address));
//        service.getInstances().add(new ServiceInstance(address, port));
//
//        serviceDao.saveNewVersion(service);
    }

    public void deregisterNode(String serviceId, String address, int port) {
//        ClientService service = serviceDao.getCurrentVersion(serviceId);
//
//        ServiceInstance serviceInstance = new ServiceInstance(address, port);
//        if (!service.getInstances().contains(serviceInstance)) {
//            return;
//        }
//
//        service.getInstances().remove(serviceInstance);
//
//        serviceDao.saveNewVersion(service);
    }

    public List<NodeLayout> getAllLayouts() {
        return nodeLayoutDao.getAllLayouts();
    }
}
