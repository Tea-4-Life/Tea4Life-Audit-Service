package tea4life.audit_service.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import tea4life.audit_service.model.base.AuditLog;
import tea4life.audit_service.model.enums.AuditAction;
import tea4life.audit_service.model.enums.EntityType;
import tea4life.audit_service.repository.AuditLogRepository; // Đã có sau khi tạo ở bước 2
import tea4life.audit_service.service.AuditLogService;

import java.time.Instant;

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
    @Override
    public void saveLog(String entityType, String entityId, AuditAction action, String performedBy, long timestamp, String message) {
        AuditLog log = AuditLog.builder()
                .entityType(EntityType.valueOf(entityType))
                .entityId(entityId)
                .action(action)
                .performedBy(performedBy) // Lưu cái Email xuống DB
                .timestamp(Instant.ofEpochMilli(timestamp))
                .message(message)
                .build();

        auditLogRepository.save(log);
    }
}