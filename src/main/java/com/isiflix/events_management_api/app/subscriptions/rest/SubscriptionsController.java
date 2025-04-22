package com.isiflix.events_management_api.app.subscriptions.rest;

import com.isiflix.events_management_api.app.subscriptions.CreateSubscriptionUseCase;
import com.isiflix.events_management_api.app.subscriptions.rest.requests.CreateSubscriptionRequest;
import com.isiflix.events_management_api.app.subscriptions.rest.responses.CreateSubscriptionResponse;
import com.isiflix.events_management_api.app.subscriptions.rest.responses.ReferralPodiumItemResponse;
import com.isiflix.events_management_api.app.subscriptions.rest.responses.UserReferralRankResponse;
import com.isiflix.events_management_api.app.subscriptions.ReferralPodiumUseCase;
import com.isiflix.events_management_api.app.subscriptions.UserReferralRankUseCase;
import com.isiflix.events_management_api.app.subscriptions.dtos.CreateSubscriptionDTO;
import com.isiflix.events_management_api.app.subscriptions.dtos.SubscriptionDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionsController {
    private final CreateSubscriptionUseCase createSubscriptionUseCase;
    private final ReferralPodiumUseCase referralPodiumUseCase;
    private final UserReferralRankUseCase userReferralRankUseCase;

    private final String designationBaseUrl;

    @Autowired
    public SubscriptionsController(CreateSubscriptionUseCase createSubscriptionUseCase,
                                   ReferralPodiumUseCase referralPodiumUseCase,
                                   UserReferralRankUseCase userReferralRankUseCase,
                                   @Value("app.designation-base-url") String designationBaseUrl
    ) {
        this.createSubscriptionUseCase = createSubscriptionUseCase;
        this.referralPodiumUseCase = referralPodiumUseCase;
        this.userReferralRankUseCase = userReferralRankUseCase;
        this.designationBaseUrl = designationBaseUrl;
    }

    @PostMapping({"/{prettyName}", "/{prettyName}/{referrerId}"})
    @ResponseStatus(HttpStatus.CREATED)
    public CreateSubscriptionResponse subscribe(
            @PathVariable String prettyName,
            @PathVariable(required = false) Long referrerId,
            @Valid @RequestBody CreateSubscriptionRequest req
    ) {
        final var createSubscriptionDTO = new CreateSubscriptionDTO(prettyName, req.userName(), req.email());
        SubscriptionDTO subscriptionDTO = createSubscriptionUseCase.createNewSubscription(createSubscriptionDTO, referrerId);
        return CreateSubscriptionResponse.fromDTO(subscriptionDTO, designationBaseUrl);
    }

    @GetMapping("/{prettyName}/ranking")
    @ResponseStatus(HttpStatus.OK)
    public List<ReferralPodiumItemResponse> getPodiumRanking(@PathVariable String prettyName) {
        return referralPodiumUseCase.getPodiumRanking(prettyName)
                .stream()
                .map(rank -> new ReferralPodiumItemResponse(rank.userName(), rank.subscribers()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{prettyName}/ranking/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserReferralRankResponse getUserReferralRank(
            @PathVariable String prettyName,
            @PathVariable Long userId
    ) {
        final var rankDto = userReferralRankUseCase.getUserReferralRank(prettyName, userId);

        return new UserReferralRankResponse(
                rankDto.rankingPosition(),
                rankDto.userDTO().id(),
                rankDto.userDTO().name(),
                rankDto.referralsCounter()
        );
    }
}
