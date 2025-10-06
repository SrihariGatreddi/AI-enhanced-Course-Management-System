package com.cms.listener;

import com.cms.model.ActiveSession;
import com.cms.repository.ActiveSessionRepository;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SessionCounterListener implements HttpSessionListener {

    private final ActiveSessionRepository activeSessionRepository;

    @Autowired
    public SessionCounterListener(ActiveSessionRepository activeSessionRepository) {
        this.activeSessionRepository = activeSessionRepository;
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        String sessionId = se.getSession().getId();
        activeSessionRepository.save(new ActiveSession(sessionId));
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        String sessionId = se.getSession().getId();
        activeSessionRepository.findById(sessionId).ifPresent(activeSessionRepository::delete);
    }
}

