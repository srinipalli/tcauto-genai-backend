package com.example.jirastories.controller;

import com.example.jirastories.model.JiraStory;
import com.example.jirastories.service.JiraStoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jira")
public class JiraStoryController {
    @Autowired
    private JiraStoryService service;

    @GetMapping("/inprogress-stories")
    public List<JiraStory> getInProgressStories() {
        return service.fetchAndStoreInProgressStories();
    }
}
