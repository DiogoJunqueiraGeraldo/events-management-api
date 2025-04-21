package com.isiflix.events_management_api.app.subscriptions.rest;

import com.isiflix.events_management_api.app.events.FindEventUseCase;
import com.isiflix.events_management_api.app.subscriptions.CreateSubscriptionUseCase;
import com.isiflix.events_management_api.app.subscriptions.rest.requests.CreateSubscriptionRequest;
import com.isiflix.events_management_api.app.subscriptions.rest.responses.CreateSubscriptionResponse;
import com.isiflix.events_management_api.app.subscriptions.rest.responses.ReferralPodiumItemResponse;
import com.isiflix.events_management_api.app.subscriptions.rest.responses.UserReferralRankResponse;
import com.isiflix.events_management_api.app.users.FindUserUseCase;
import com.isiflix.events_management_api.app.subscriptions.ReferralPodiumUseCase;
import com.isiflix.events_management_api.app.subscriptions.UserReferralRankUseCase;
import com.isiflix.events_management_api.app.subscriptions.dtos.CreateSubscriptionDTO;
import com.isiflix.events_management_api.app.subscriptions.dtos.SubscriptionDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionsController {
    private final CreateSubscriptionUseCase createSubscriptionUseCase;
    private final ReferralPodiumUseCase referralPodiumUseCase;
    private final FindEventUseCase findEventUseCase;
    private final FindUserUseCase findUserUseCase;
    private final UserReferralRankUseCase userReferralRankUseCase;

    private final String designationBaseUrl;

    @Autowired
    public SubscriptionsController(CreateSubscriptionUseCase createSubscriptionUseCase,
                                   ReferralPodiumUseCase referralPodiumUseCase,
                                   FindEventUseCase findEventUseCase,
                                   FindUserUseCase findUserUseCase,
                                   UserReferralRankUseCase userReferralRankUseCase,
                                   @Value("app.designation-base-url") String designationBaseUrl
    ) {
        this.createSubscriptionUseCase = createSubscriptionUseCase;
        this.referralPodiumUseCase = referralPodiumUseCase;
        this.findEventUseCase = findEventUseCase;
        this.findUserUseCase = findUserUseCase;
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
    public List<ReferralPodiumItemResponse> getPodiumRanking(@PathVariable String prettyName) throws NoResourceFoundException {
        final var eventDto = findEventUseCase.findByPrettyName(prettyName);
        if(eventDto.isEmpty()) {
            final var resourcePath = "/subscriptions/".concat(prettyName).concat("/ranking");
            throw new NoResourceFoundException(HttpMethod.GET, resourcePath);
        }

        return referralPodiumUseCase.getPodiumRanking(eventDto.get())
                .stream()
                .map(rank -> new ReferralPodiumItemResponse(rank.userName(), rank.subscribers()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{prettyName}/ranking/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserReferralRankResponse getUserReferralRank(
            @PathVariable String prettyName,
            @PathVariable Long userId
    ) throws NoResourceFoundException {
        final var eventDtoFound = findEventUseCase.findByPrettyName(prettyName);
        final var userDtoFound = findUserUseCase.findUserById(userId);

        if(eventDtoFound.isEmpty() || userDtoFound.isEmpty()) {
            final var resourcePath = "/subscriptions/".concat(prettyName).concat("/ranking/").concat(userId.toString());
            throw new NoResourceFoundException(HttpMethod.GET, resourcePath);
        }

        final var eventDto = eventDtoFound.get();
        final var userDto = userDtoFound.get();

        final var rankDto = userReferralRankUseCase.getUserReferralRank(eventDto, userDto);

        return new UserReferralRankResponse(
                rankDto.rankingPosition(),
                userDto.id(),
                userDto.name(),
                rankDto.referralsCounter()
        );
    }
}
