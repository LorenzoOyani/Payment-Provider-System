package org.example.paymentgateway.repositories;

import org.example.paymentgateway.entities.WebhookEvent;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WebhookEventRepository extends ListCrudRepository<WebhookEvent, Long> {

    Optional<WebhookEvent> findByEventId(String eventId);

    List<WebhookEvent> findWebhookEventById(long id);

    @Query("SELECT t FROM WebhookEvent t where t.status= 'FAILED' AND t.retryCount < :maxRetries ")
    List<WebhookEvent> findFailedWebhooksForRetry(@Param("maxRetries") Integer maxRetries);


}
