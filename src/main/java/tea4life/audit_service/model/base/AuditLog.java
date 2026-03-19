package tea4life.audit_service.model.base;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import tea4life.audit_service.model.enums.AuditAction;
import tea4life.audit_service.model.enums.EntityType;

import java.time.Instant;

/**
 * @author : user664dntp
 * @mailto : phatdang19052004@gmail.com
 * @created : 19/03/2026, Thursday
 **/

@Document(collection = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuditLog {
    @Id
    String id;

    @Field("entity_type")
    EntityType entityType;

    @Field("entity_id")
    String entityId;

    @Field("action")
    AuditAction action;

    @Field("performed_by")
    Long performedBy;

    @Field("timestamp")
    Instant timestamp;

    @Field("message")
    String message;
}

