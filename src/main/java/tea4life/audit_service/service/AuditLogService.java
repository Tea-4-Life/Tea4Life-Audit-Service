package tea4life.audit_service.service;

/**
 * @author : user664dntp
 * @mailto : phatdang19052004@gmail.com
 * @created : 19/03/2026, Thursday
 **/
public interface AuditLogService {
    void saveLog(String entityType, String entityId, String action, Long performedBy, long timestamp, String message);
}
