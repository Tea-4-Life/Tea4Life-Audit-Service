package tea4life.audit_service.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tea4life.audit_service.dto.event.CategoryAuditEvent;
import tea4life.audit_service.dto.event.OptionAuditEvent;
import tea4life.audit_service.dto.event.OptionValueAuditEvent;
import tea4life.audit_service.dto.event.ProductAuditEvent;
import tea4life.audit_service.model.enums.EntityType;
import tea4life.audit_service.service.AuditLogService;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MasterAuditConsumer {

    ObjectMapper objectMapper;
    AuditLogService auditLogService;

    @KafkaListener(topics = "${spring.kafka.topic.audit-log}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeAuditLog(String payload) {
        try {
            if (payload.startsWith("\"") && payload.endsWith("\"")) {
                payload = payload.substring(1, payload.length() - 1).replace("\\\"", "\"");
            }

            JsonNode jsonNode = objectMapper.readTree(payload);

            if (jsonNode.has("productId")) {
                ProductAuditEvent event = objectMapper.treeToValue(jsonNode, ProductAuditEvent.class);
                auditLogService.saveLog(
                        EntityType.PRODUCT.name(),
                        event.productId().toString(),
                        event.action(),
                        event.performedBy(),
                        event.timestamp(),
                        event.message()
                );
                log.info("Đã lưu log {} SẢN PHẨM: {}", event.action(), event.productName());

            } else if (jsonNode.has("categoryId")) {
                CategoryAuditEvent event = objectMapper.treeToValue(jsonNode, CategoryAuditEvent.class);
                auditLogService.saveLog(
                        EntityType.CATEGORY.name(),
                        event.categoryId().toString(),
                        event.action(),
                        event.performedBy(),
                        event.timestamp(),
                        event.message()
                );
                log.info("Đã lưu log {} DANH MỤC: {}", event.action(), event.categoryName());
            } else if (jsonNode.has("optionId")) {
                OptionAuditEvent event = objectMapper.treeToValue(jsonNode, OptionAuditEvent.class);
                auditLogService.saveLog(
                        EntityType.PRODUCT_OPTION.name(),
                        event.optionId().toString(),
                        event.action(),
                        event.performedBy(),
                        event.timestamp(),
                        event.message()
                );
                log.info("Đã lưu log {} TÙY CHỌN SẢN PHẨM: {}", event.action(), event.optionName());

            } else if (jsonNode.has("optionValueId")) {

                OptionValueAuditEvent event = objectMapper.treeToValue(jsonNode, OptionValueAuditEvent.class);
                auditLogService.saveLog(
                        EntityType.PRODUCT_OPTION_VALUE.name(),
                        event.optionValueId().toString(),
                        event.action(),
                        event.performedBy(),
                        event.timestamp(),
                        event.message()
                );
                log.info("Đã lưu log {} GIÁ TRỊ TÙY CHỌN: {}", event.action(), event.valueName());

            } else {
                log.warn("Nhận được Event không xác định định dạng: {}", payload);
            }

        } catch (Exception ex) {
            log.error("Lỗi khi xử lý Audit Log. Payload: {} - Lỗi: {}", payload, ex.getMessage());
        }
    }
}