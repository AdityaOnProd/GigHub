package com.app.gighub.services;

import com.app.gighub.models.Message;
import com.app.gighub.models.User;
import com.app.gighub.models.Job;
import com.app.gighub.repositories.MessageRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;
    
    @Value("${freelancer.message_room.page_size}")
    private int messageRoomPageSize;
    
    public Message save(Message message) {
        return messageRepository.save(message);
    }

    public Message get(Long id) {
        // ✅ Updated to use `findById(id).orElse(null)`
        return messageRepository.findById(id).orElse(null);
    }
    
    public List<Message> findByJobAndContractor(Job job, User contractor) {
        
        int pageNumber = 1;

        // ✅ Updated to use PageRequest.of()
        PageRequest request = PageRequest.of(pageNumber - 1, messageRoomPageSize, Sort.by(Sort.Direction.DESC, "id"));

        Page<Message> messages = messageRepository.findByJobAndSenderOrReceiver(job, contractor, request);
        return messages.getContent();
    }

    public List<Message> getRoomsByUser(User me) {
        List<Message> allMessages = messageRepository.findBySenderOrReceiver(me);
        List<Message> result = new ArrayList<>();

        Map<String, Message> uniqueRooms = new HashMap<>();

        allMessages.forEach(m -> {
            // Unique hash map key "job-contributor"
            String key = (m.getJob() != null) ? String.valueOf(m.getJob().getId()) : "X";
            key += '-';
            key += (m.getReceiver().getId() == me.getId() ? m.getSender().getId() : m.getReceiver().getId());

            // If room does not exist, add it to unique list
            uniqueRooms.putIfAbsent(key, m);
        });

        result.addAll(uniqueRooms.values());

        return result;
    }

    public List<Message> findByMyConversers(User me, User converser) {
        return messageRepository.findByMyConversers(me, converser);
    }
}
