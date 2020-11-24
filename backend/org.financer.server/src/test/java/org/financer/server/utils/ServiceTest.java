package org.financer.server.utils;

import org.financer.server.application.service.AuthenticationService;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.when;

public abstract class ServiceTest extends SpringTest {

    @MockBean
    protected AuthenticationService authenticationService;

    protected void mockAnotherUserAuthenticated() {
        when(authenticationService.getUserId()).thenReturn(-1L);
    }

}
