package tea4life.audit_service.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tea4life.audit_service.model.enums.EntityType;
import tea4life.audit_service.service.AuditLogService;
import org.springframework.kafka.annotation.KafkaListener;
import tea4life.audit_service.dto.event.ProductDeletedAuditEvent;
/**
 * @author : user664dntp
 * @mailto : phatdang19052004@gmail.com
 * @created : 19/03/2026, Thursday
 **/

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductAuditConsumer {
    ObjectMapper objectMapper;
    AuditLogService auditLogService;

    @KafkaListener(topics = "${spring.kafka.topic.audit-log}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeProductAudit(String payload) {
        try {
            ProductDeletedAuditEvent event = objectMapper.readValue(payload, ProductDeletedAuditEvent.class);

            log.info("Bắt được event XÓA SẢN PHẨM: id={}, name={}", event.productId(), event.productName());

            auditLogService.saveLog(
                    EntityType.PRODUCT.name(),
                    event.productId().toString(),
                    event.action(),
                    event.performedBy(),
                    event.timestamp(),
                    event.message()
            );

        } catch (JsonProcessingException ex) {
            log.error("Lỗi parse chuỗi JSON từ Kafka (ProductAudit): {}", ex.getMessage());
        } catch (Exception ex) {
            log.error("Lỗi không xác định khi lưu Audit Log: {}", ex.getMessage());
        }
    }
}
