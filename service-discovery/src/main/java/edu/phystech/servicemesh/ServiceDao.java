package edu.phystech.servicemesh;

import edu.phystech.servicemesh.exception.ServiceAlreadyExistsException;
import edu.phystech.servicemesh.model.ClientService;
import edu.phystech.servicemesh.model.ClientServiceVersioned;
import edu.phystech.servicemesh.model.ClientServiceVersionedId;
import edu.phystech.servicemesh.repositories.service.simple.ClientServiceRepository;
import edu.phystech.servicemesh.repositories.service.versioned.ClientServiceWithVersionRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class ServiceDao {
    private final ClientServiceRepository simpleRepository;
    private final ClientServiceWithVersionRepository versionRepository;

    public ServiceDao(ClientServiceRepository simpleRepository, ClientServiceWithVersionRepository versionRepository) {
        this.simpleRepository = simpleRepository;
        this.versionRepository = versionRepository;
    }

    @Transactional
    public ClientService saveNewVersion(ClientService clientService) {
        clientService.setVersion(clientService.getVersion() + 1);
        simpleRepository.save(clientService);
        versionRepository.save(new ClientServiceVersioned(clientService));
        return clientService;
    }

    public ClientService getByServiceIdAndVersion(String serviceId, long version) {
        ClientServiceVersionedId id = new ClientServiceVersionedId(serviceId, version);
        return versionRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, "service")).getClientService();
    }

    public ClientService getCurrentVersion(String serviceId) {
        return simpleRepository.getById(serviceId);
    }

    public List<ClientService> getByIds(Collection<String> ids) {
        List<ClientService> result = new ArrayList<>();
        simpleRepository.findAllById(ids).forEach(result::add);
        return result;
    }

    public boolean exists(String serviceId) {
        return simpleRepository.existsById(serviceId);
    }

    public List<ClientService> getAllServices() {
        return simpleRepository.findAll();
    }
}
