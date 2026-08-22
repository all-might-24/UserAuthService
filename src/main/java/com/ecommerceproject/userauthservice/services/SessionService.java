package com.ecommerceproject.userauthservice.services;

import com.ecommerceproject.userauthservice.exceptions.SessionAlreadyExpiredException;
import com.ecommerceproject.userauthservice.exceptions.SessionDoesNotExistException;
import com.ecommerceproject.userauthservice.exceptions.UnAuthorizedException;
import com.ecommerceproject.userauthservice.models.Session;
import com.ecommerceproject.userauthservice.models.enums.State;
import com.ecommerceproject.userauthservice.repositories.SessionRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SessionService implements ISessionService{

    private SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public boolean isSessionValid(String token) {
        Optional<Session> optionalSession = findByToken(token);
        return optionalSession.isPresent() && optionalSession.get().getState() == State.ACTIVE;
    }

    @Override
    public Optional<Session> findByToken(String token) {
        return sessionRepository.findByToken(token);
    }

    @Override
    public void saveSession(Session session) {
        sessionRepository.save(session);
    }

    @Override
    public void logout(String authHeader) {
        String bearer = "Bearer ";
        if(authHeader == null || !authHeader.startsWith(bearer)) {
            throw new UnAuthorizedException("Unauthorized Access");
        }
        String token = authHeader.substring(bearer.length());

        Optional<Session> optionalSession = findByToken(token);
        if(optionalSession.isEmpty()) {
            throw new SessionDoesNotExistException("Session does not exist");
        }

        Session session = optionalSession.get();
        if(session.getState().equals(State.INACTIVE)){
            throw new SessionAlreadyExpiredException("Session already Expired");
        }

        session.setState(State.INACTIVE);
        saveSession(session);
    }
}
