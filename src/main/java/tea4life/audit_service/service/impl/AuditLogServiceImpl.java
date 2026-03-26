package tea4life.audit_service.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import tea4life.audit_service.model.base.AuditLog;
import tea4life.audit_service.model.enums.AuditAction;
import tea4life.audit_service.model.enums.EntityType;
import tea4life.audit_service.repository.AuditLogRepository; // Đã có sau khi tạo ở bước 2
import tea4life.audit_service.service.AuditLogService;

import java.time.Instant;
import java.util.List;

/**
 * @author : user664dntp
 * @mailto : phatdang19052004@gmail.com
 * @created : 19/03/2026, Thursday
 **/

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuditLogServiceImpl implements AuditLogService {

    AuditLogRepository auditLogRepository;
    MongoTemplate mongoTemplate;
    @Override
    public void saveLog(String entityType, String entityId, AuditAction action, String performedBy, long timestamp, String message) {
        AuditLog log = AuditLog.builder()
                .entityType(EntityType.valueOf(entityType))
                .entityId(entityId)
                .action(action)
                .performedBy(performedBy)
                .timestamp(Instant.ofEpochMilli(timestamp))
                .message(message)
                .build();

        auditLogRepository.save(log);
    }

    @Override
    public Page<AuditLog> getAllLogs(String entityType, String action, Pageable pageable) {
        Query query = new Query().with(pageable);

        if (entityType != null && !entityType.trim().isEmpty()) {
            query.addCriteria(Criteria.where("entityType").is(entityType));
        }

        if (action != null && !action.trim().isEmpty()) {
            query.addCriteria(Criteria.where("action").is(action));
        }

        List<AuditLog> logs = mongoTemplate.find(query, AuditLog.class);

        Query countQuery = Query.of(query).limit(-1).skip(-1);
        long total = mongoTemplate.count(countQuery, AuditLog.class);

        return new PageImpl<>(logs, pageable, total);
    }
}