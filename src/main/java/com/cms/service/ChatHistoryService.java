package com.cms.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatHistoryService {

    public void addMessage(Long studentId, String message) {
        // Logic to add message to chat history
    }

    public List<Object> getMessages(Long studentId) {
        // Logic to retrieve chat history messages
        return null;
    }
}
