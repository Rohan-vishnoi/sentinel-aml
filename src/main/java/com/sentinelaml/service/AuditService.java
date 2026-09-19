package com.sentinelaml.service;

import com.sentinelaml.domain.AuditEvent;
import com.sentinelaml.repository.AuditEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

    private final AuditEventRepository auditEventRepository;

    public AuditService(AuditEventRepository auditEventRepository) {
        this.auditEventRepository = auditEventRepository;
    }

    @Transactional
    public void record(String entityType, String entityId, String action, String actorIdentity, String details) {
        AuditEvent event = new AuditEvent();
        event.setEntityType(entityType);
        event.setEntityId(entityId);
        event.setAction(action);
        event.setActorIdentity(actorIdentity);
        event.setDetails(details);
        auditEventRepository.save(event);
    }
}
