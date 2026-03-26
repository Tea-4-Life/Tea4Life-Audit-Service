package tea4life.audit_service.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tea4life.audit_service.model.AuditAction;
import tea4life.audit_service.model.EntityType;
import tea4life.audit_service.service.AuditLogService;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MasterAuditConsumer {

    ObjectMapper objectMapper;
    AuditLogService auditLogService;

    private record AuditMetadata(EntityType entityType, String nameField) {
    }

    private static final Map<String, AuditMetadata> AUDIT_DICTIONARY = Map.of(
            "productId", new AuditMetadata(EntityType.PRODUCT, "productName"),
            "categoryId", new AuditMetadata(EntityType.CATEGORY, "categoryName"),
            "optionId", new AuditMetadata(EntityType.PRODUCT_OPTION, "optionName"),
            "optionValueId", new AuditMetadata(EntityType.PRODUCT_OPTION_VALUE, "valueName")
    );

    @KafkaListener(topics = "${spring.kafka.topic.audit-log}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeAuditLog(String payload) {
        try {
            if (payload.startsWith("\"") && payload.endsWith("\"")) {
                payload = payload.substring(1, payload.length() - 1).replace("\\\"", "\"");
            }

            JsonNode jsonNode = objectMapper.readTree(payload);

            for (Map.Entry<String, AuditMetadata> entry : AUDIT_DICTIONARY.entrySet()) {
                String idField = entry.getKey();

                if (jsonNode.has(idField)) {
                    AuditMetadata meta = entry.getValue();

                    String entityId = jsonNode.get(idField).asText();
                    String entityName = jsonNode.get(meta.nameField()).asText();

                    AuditAction action = AuditAction.valueOf(jsonNode.get("action").asText());
                    String performedBy = jsonNode.get("performedBy").asText();
                    long timestamp = jsonNode.get("timestamp").asLong();
                    String message = jsonNode.get("message").asText();

                    auditLogService.saveLog(
                            meta.entityType().name(),
                            entityId,
                            action,
                            performedBy,
                            timestamp,
                            message
                    );

                    log.info("Đã lưu log {} {}: {}", action, meta.entityType().name(), entityName);
                    return;
                }
            }

            log.warn("Nhận được Event không nằm trong Từ Điển: {}", payload);

        } catch (Exception ex) {
            log.error("Lỗi khi xử lý Audit Log. Payload: {} - Lỗi: {}", payload, ex.getMessage());
        }
    }
}