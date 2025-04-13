package com.isiflix.events_management_api.app.subscriptions.rest;

import com.isiflix.events_management_api.app.subscriptions.CreateSubscriptionUseCase;
import com.isiflix.events_management_api.app.subscriptions.dtos.CreateSubscriptionDTO;
import com.isiflix.events_management_api.domain.events.EventRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionsController {
    private final CreateSubscriptionUseCase createSubscriptionUseCase;

    @Autowired
    public SubscriptionsController(CreateSubscriptionUseCase createSubscriptionUseCase) {
        this.createSubscriptionUseCase = createSubscriptionUseCase;
    }

    @PostMapping("/{prettyName}")
    @ResponseStatus(HttpStatus.CREATED)
    public void subscribe(
            @PathVariable String prettyName,
            @Valid @RequestBody CreateSubscriptionRequest req
    ) {
        createSubscriptionUseCase.createNewSubscription(
                new CreateSubscriptionDTO(prettyName, req.userName(), req.email())
        );
    }
}
