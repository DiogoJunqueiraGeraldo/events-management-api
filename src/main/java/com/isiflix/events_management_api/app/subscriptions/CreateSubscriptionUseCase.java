package com.isiflix.events_management_api.app.subscriptions;

import com.isiflix.events_management_api.app.events.FindEventUseCase;
import com.isiflix.events_management_api.app.subscriptions.dtos.CreateSubscriptionDTO;
import com.isiflix.events_management_api.app.subscriptions.dtos.SubscriptionDTO;
import com.isiflix.events_management_api.app.users.CreateUserUseCase;
import com.isiflix.events_management_api.app.users.FindUserUseCase;
import com.isiflix.events_management_api.app.users.dtos.CreateUserDTO;
import com.isiflix.events_management_api.app.users.dtos.UserDTO;
import com.isiflix.events_management_api.domain.events.*;
import com.isiflix.events_management_api.domain.users.UserFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CreateSubscriptionUseCase {

    private final FindEventUseCase findEventUseCase;
    private final FindUserUseCase findUserUseCase;
    private final CreateUserUseCase createUserUseCase;

    private final SubscriptionRepository subscriptionRepository;

    @Autowired
    public CreateSubscriptionUseCase(
            FindEventUseCase findEventUseCase,
            FindUserUseCase findUserUseCase,
            CreateUserUseCase createUserUseCase,
            SubscriptionRepository subscriptionRepository
    ) {
        this.findEventUseCase = findEventUseCase;
        this.findUserUseCase = findUserUseCase;
        this.createUserUseCase = createUserUseCase;
        this.subscriptionRepository = subscriptionRepository;
    }

    public SubscriptionDTO createNewSubscription(CreateSubscriptionDTO dto) {
        final var referrer = Optional.ofNullable(dto.referrerId())
                .flatMap((id) -> findUserUseCase.findUserById(id).map(UserFactory::of));

        final var eventDTO = findEventUseCase.findEventByPrettyNameOrThrow(dto.prettyName());
        final var userDTO = findUserOrCreate(dto.email(), dto.userName());

        final var event = EventFactory.of(eventDTO);
        final var user = UserFactory.of(userDTO);

        final var subscription = event.subscribe(user, referrer.orElse(null));
        return subscriptionRepository.saveAndCheckConstraints(subscription).toDTO();
    }

    private UserDTO findUserOrCreate(String email, String userName) {
        return findUserUseCase.findByEmail(email)
                .orElseGet(() -> {
                    final var createUserDTO = new CreateUserDTO(userName, email);
                    return createUserUseCase.createUser(createUserDTO);
                });
    }
}
