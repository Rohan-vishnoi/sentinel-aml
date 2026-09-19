package com.sentinelaml.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity(name = "audit_events")
public class AuditEvent extends AuditedEntity {

    @Column(name = "entity_type", nullable = false)
    private String entityType;
    @Column(name = "entity_id", nullable = false)
    private String entityId;
    @Column(nullable = false)
    private String action;
    @Column(name = "actor_identity", nullable = false)
    private String actorIdentity;
    @Column(length = 4000)
    private String details;

    public AuditEvent() {
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getActorIdentity() {
        return actorIdentity;
    }

    public void setActorIdentity(String actorIdentity) {
        this.actorIdentity = actorIdentity;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
