package edu.phystech.servicemash;

import edu.phystech.servicemash.exception.ServiceAlreadyExistsException;
import edu.phystech.servicemash.repositories.simple.ClientServiceRepository;
import edu.phystech.servicemash.repositories.versioned.ClientServiceWithVersionRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientServiceProcessor {
    private final ClientServiceRepository simpleRepository;
    private final ClientServiceWithVersionRepository versionRepository;

    public ClientServiceProcessor(
            ClientServiceRepository simpleRepository,
            ClientServiceWithVersionRepository versionRepository
    ) {
        this.simpleRepository = simpleRepository;
        this.versionRepository = versionRepository;
    }

    @Transactional
    public ClientService createService(long serviceId, String name, String fqdn) {
        if (simpleRepository.existsById(serviceId)) {
            throw new ServiceAlreadyExistsException(serviceId);
        }
        ClientService service = new ClientService(serviceId, name, fqdn);
        versionRepository.save(new ClientServiceVersioned(service));
        return simpleRepository.save(service);
    }

    public ClientService findByServiceIdAndVersion(long serviceId, long version) {
        ClientServiceVersionedId id = new ClientServiceVersionedId(serviceId, version);
        return versionRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, "service")).getClientService();
    }

    @Transactional
    public ClientService editName(long serviceId, String newName) {
        ClientService currentVersion = simpleRepository.findById(serviceId).orElseThrow(() -> new ObjectNotFoundException(serviceId, "service"));
        currentVersion.setName(newName);
        long version = currentVersion.getVersion() + 1;
        currentVersion.setVersion(version);
        ClientService result = simpleRepository.save(currentVersion);
        versionRepository.save(new ClientServiceVersioned(currentVersion));
        return result;
    }


}
