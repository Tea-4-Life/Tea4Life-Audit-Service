package tea4life.audit_service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tea4life.audit_service.model.base.AuditLog;

/**
 * @author : user664dntp
 * @mailto : phatdang19052004@gmail.com
 * @created : 19/03/2026, Thursday
 **/
@Repository
public interface AuditLogRepository extends MongoRepository<AuditLog, String> {

}
