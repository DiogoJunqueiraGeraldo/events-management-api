package com.isiflix.events_management_api.app.errors;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tests/simulate")
@Profile("dev")
public class FailureSimulationController {
    @GetMapping("/unexpected-failure")
    @ResponseStatus(HttpStatus.OK)
    public void unexpectedFailure() {
        throw new RuntimeException();
    }
}
