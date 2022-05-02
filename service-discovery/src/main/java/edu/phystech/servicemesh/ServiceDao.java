package edu.phystech.servicemesh;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import edu.phystech.servicemesh.model.ClientService;
import edu.phystech.servicemesh.model.ClientServiceVersioned;
import edu.phystech.servicemesh.model.ClientServiceVersionedId;
import edu.phystech.servicemesh.repositories.service.simple.ClientServiceRepository;
import edu.phystech.servicemesh.repositories.service.versioned.ClientServiceWithVersionRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicUpdate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServiceDao {
    private final ClientServiceRepository simpleRepository;
    private final ClientServiceWithVersionRepository versionRepository;
    private final MongoTemplate mongoTemplate;

    public ServiceDao(ClientServiceRepository simpleRepository,
                      ClientServiceWithVersionRepository versionRepository,
                      MongoTemplate mongoTemplate
    ) {
        this.simpleRepository = simpleRepository;
        this.versionRepository = versionRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Transactional(rollbackFor = Exception.class)
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
        return simpleRepository.findById(serviceId).orElseThrow(() -> new ObjectNotFoundException(serviceId, "service"));
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

    public long getCurrentDeployedVersion(String serviceId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(serviceId));
        query.fields().include("deployedVersion");
        return Objects.requireNonNullElse(mongoTemplate.findOne(query, Long.class, "services"), 0L);
    }

    public void setCurrentDeployedVersion(String serviceId, long version) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(serviceId));
        mongoTemplate.updateFirst(query, BasicUpdate.update("deployedVersion", version), "services");
    }

    public void deleteService(String serviceId) {
        simpleRepository.deleteById(serviceId);
    }
}
