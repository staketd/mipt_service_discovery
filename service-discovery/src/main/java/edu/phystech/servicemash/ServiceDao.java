package edu.phystech.servicemash;

import edu.phystech.servicemash.exception.ServiceAlreadyExistsException;
import edu.phystech.servicemash.repositories.simple.ClientServiceRepository;
import edu.phystech.servicemash.repositories.versioned.ClientServiceWithVersionRepository;
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

    public ClientService createNewService(String serviceId, String name, String fqdn) {
        if (simpleRepository.existsById(serviceId)) {
            throw new ServiceAlreadyExistsException(serviceId);
        }
        ClientService service = new ClientService(serviceId, name, fqdn);
        versionRepository.save(new ClientServiceVersioned(service));
        return simpleRepository.save(service);
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
}
