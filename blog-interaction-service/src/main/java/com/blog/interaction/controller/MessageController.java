package com.blog.interaction.controller;

import com.blog.interaction.entity.Message;
import com.blog.interaction.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/unread/count/{userId}")
    public long getUnreadCount(@PathVariable Long userId) {
        return messageService.getUnreadCount(userId);
    }

    @GetMapping("/user/{userId}")
    public List<Message> getMessages(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return messageService.getMessages(userId, page, size);
    }

    @PutMapping("/read/{id}")
    public boolean markAsRead(@PathVariable Long id) {
        return messageService.markAsRead(id);
    }

    @PutMapping("/read/all/{userId}")
    public boolean markAllAsRead(@PathVariable Long userId) {
        return messageService.markAllAsRead(userId);
    }

    @PostMapping("/send")
    public boolean sendMessage(@RequestBody Message message) {
        return messageService.sendMessage(message);
    }

    @DeleteMapping("/{id}")
    public boolean deleteMessage(@PathVariable Long id) {
        return messageService.removeById(id);
    }

    @GetMapping("/messages")
    public List<Message> getAllMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return messageService.getAllMessages(page, size);
    }

    @PostMapping("/create")
    public boolean createMessage(@RequestBody Message message) {
        return messageService.sendMessage(message);
    }
}
