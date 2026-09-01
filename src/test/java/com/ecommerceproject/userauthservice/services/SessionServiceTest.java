package com.ecommerceproject.userauthservice.services;

import com.ecommerceproject.userauthservice.exceptions.SessionAlreadyExpiredException;
import com.ecommerceproject.userauthservice.exceptions.SessionDoesNotExistException;
import com.ecommerceproject.userauthservice.exceptions.UnAuthorizedException;
import com.ecommerceproject.userauthservice.models.Session;
import com.ecommerceproject.userauthservice.models.enums.State;
import com.ecommerceproject.userauthservice.repositories.SessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @InjectMocks
    private SessionService sessionService;


    /*
     * =========================================================
     * IS SESSION VALID
     * =========================================================
     */

    @Test
    void isSessionValid_shouldReturnTrue_whenSessionExistsAndIsActive() {

        String token = "valid-token";

        Session session = new Session();
        session.setToken(token);
        session.setState(State.ACTIVE);

        when(sessionRepository.findByToken(token))
                .thenReturn(Optional.of(session));

        boolean result =
                sessionService.isSessionValid(token);

        assertTrue(result);

        verify(sessionRepository)
                .findByToken(token);
    }


    @Test
    void isSessionValid_shouldReturnFalse_whenSessionIsInactive() {

        String token = "inactive-token";

        Session session = new Session();
        session.setToken(token);
        session.setState(State.INACTIVE);

        when(sessionRepository.findByToken(token))
                .thenReturn(Optional.of(session));

        boolean result =
                sessionService.isSessionValid(token);

        assertFalse(result);

        verify(sessionRepository)
                .findByToken(token);
    }


    @Test
    void isSessionValid_shouldReturnFalse_whenSessionDoesNotExist() {

        String token = "unknown-token";

        when(sessionRepository.findByToken(token))
                .thenReturn(Optional.empty());

        boolean result =
                sessionService.isSessionValid(token);

        assertFalse(result);

        verify(sessionRepository)
                .findByToken(token);
    }


    /*
     * =========================================================
     * FIND BY TOKEN
     * =========================================================
     */

    @Test
    void findByToken_shouldReturnSession_whenSessionExists() {

        String token = "valid-token";

        Session session = new Session();
        session.setToken(token);
        session.setState(State.ACTIVE);

        when(sessionRepository.findByToken(token))
                .thenReturn(Optional.of(session));

        Optional<Session> result =
                sessionService.findByToken(token);

        assertTrue(result.isPresent());
        assertSame(session, result.get());

        verify(sessionRepository)
                .findByToken(token);
    }


    @Test
    void findByToken_shouldReturnEmpty_whenSessionDoesNotExist() {

        String token = "unknown-token";

        when(sessionRepository.findByToken(token))
                .thenReturn(Optional.empty());

        Optional<Session> result =
                sessionService.findByToken(token);

        assertTrue(result.isEmpty());

        verify(sessionRepository)
                .findByToken(token);
    }


    /*
     * =========================================================
     * SAVE SESSION
     * =========================================================
     */

    @Test
    void saveSession_shouldSaveSessionUsingRepository() {

        Session session = new Session();
        session.setToken("token");
        session.setState(State.ACTIVE);

        sessionService.saveSession(session);

        verify(sessionRepository)
                .save(session);
    }


    /*
     * =========================================================
     * LOGOUT
     * =========================================================
     */

    @Test
    void logout_shouldMarkSessionInactive_whenSessionIsActive() {

        String token = "valid-token";
        String authHeader = "Bearer " + token;

        Session session = new Session();
        session.setToken(token);
        session.setState(State.ACTIVE);

        when(sessionRepository.findByToken(token))
                .thenReturn(Optional.of(session));

        sessionService.logout(authHeader);

        assertEquals(
                State.INACTIVE,
                session.getState()
        );

        verify(sessionRepository)
                .findByToken(token);

        verify(sessionRepository)
                .save(session);
    }


    @Test
    void logout_shouldThrowUnauthorizedException_whenAuthorizationHeaderIsNull() {

        assertThrows(
                UnAuthorizedException.class,
                () -> sessionService.logout(null)
        );

        verifyNoInteractions(sessionRepository);
    }


    @Test
    void logout_shouldThrowUnauthorizedException_whenHeaderDoesNotContainBearerPrefix() {

        String authHeader = "invalid-token";

        assertThrows(
                UnAuthorizedException.class,
                () -> sessionService.logout(authHeader)
        );

        verifyNoInteractions(sessionRepository);
    }


    @Test
    void logout_shouldThrowSessionDoesNotExistException_whenSessionIsMissing() {

        String token = "unknown-token";
        String authHeader = "Bearer " + token;

        when(sessionRepository.findByToken(token))
                .thenReturn(Optional.empty());

        SessionDoesNotExistException exception =
                assertThrows(
                        SessionDoesNotExistException.class,
                        () -> sessionService.logout(authHeader)
                );

        assertEquals(
                "Session does not exist",
                exception.getMessage()
        );

        verify(sessionRepository)
                .findByToken(token);

        verify(sessionRepository, never())
                .save(any(Session.class));
    }


    @Test
    void logout_shouldThrowSessionAlreadyExpiredException_whenSessionIsInactive() {

        String token = "expired-token";
        String authHeader = "Bearer " + token;

        Session session = new Session();
        session.setToken(token);
        session.setState(State.INACTIVE);

        when(sessionRepository.findByToken(token))
                .thenReturn(Optional.of(session));

        SessionAlreadyExpiredException exception =
                assertThrows(
                        SessionAlreadyExpiredException.class,
                        () -> sessionService.logout(authHeader)
                );

        assertEquals(
                "Session already Expired",
                exception.getMessage()
        );

        verify(sessionRepository)
                .findByToken(token);

        verify(sessionRepository, never())
                .save(any(Session.class));
    }
}