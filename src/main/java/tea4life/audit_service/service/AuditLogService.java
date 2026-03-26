package tea4life.audit_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tea4life.audit_service.model.AuditLog;
import tea4life.audit_service.model.AuditAction;

/**
 * @author : user664dntp
 * @mailto : phatdang19052004@gmail.com
 * @created : 19/03/2026, Thursday
 **/
public interface AuditLogService {
    void saveLog(String entityType, String entityId, AuditAction action, String performedBy, long timestamp, String message);

    Page<AuditLog> getAllLogs(String entityType, String action, Pageable pageable);
}
