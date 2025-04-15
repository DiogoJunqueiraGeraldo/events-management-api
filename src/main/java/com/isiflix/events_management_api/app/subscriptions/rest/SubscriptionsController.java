package com.isiflix.events_management_api.app.subscriptions.rest;

import com.isiflix.events_management_api.app.subscriptions.CreateSubscriptionUseCase;
import com.isiflix.events_management_api.app.subscriptions.dtos.CreateSubscriptionDTO;
import com.isiflix.events_management_api.app.subscriptions.dtos.SubscriptionDTO;
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
    public CreateSubscriptionResponse subscribe(
            @PathVariable String prettyName,
            @Valid @RequestBody CreateSubscriptionRequest req
    ) {
        final var createSubscriptionDTO = new CreateSubscriptionDTO(prettyName, req.userName(), req.email());
        SubscriptionDTO subscriptionDTO = createSubscriptionUseCase.createNewSubscription(createSubscriptionDTO);
        return new CreateSubscriptionResponse(subscriptionDTO.id(), subscriptionDTO.designation());
    }
}
