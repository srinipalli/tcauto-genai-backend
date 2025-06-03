package com.example.jirastories.service;

import com.example.jirastories.model.JiraStory;
import com.example.jirastories.repository.JiraStoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class JiraStoryService {
    @Value("${jira.api.url}")
    private String jiraApiUrl;

    @Value("${jira.api.user}")
    private String jiraUser;

    @Value("${jira.api.token}")
    private String jiraToken;

    @Autowired
    private JiraStoryRepository repository;

    @Autowired
    private OpenAiEmbeddingService openAiEmbeddingService;

    public List<JiraStory> fetchAndStoreInProgressStories() {
        RestTemplate restTemplate = new RestTemplate();
       // String jql = "status=\"In Progress\" AND issuetype=Story";
        String jql = "issuetype=Story";
        String url = jiraApiUrl + "?jql=" + jql;

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(jiraUser, jiraToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        List<JiraStory> stories = new ArrayList<>();
        if (response.getStatusCode() == HttpStatus.OK) {
            List<Map<String, Object>> issues = (List<Map<String, Object>>) response.getBody().get("issues");
            for (Map<String, Object> issue : issues) {
                JiraStory story = new JiraStory();
                story.setId((String) issue.get("id"));
                story.setKey((String) issue.get("key"));
                Map<String, Object> fields = (Map<String, Object>) issue.get("fields");
                story.setSummary((String) fields.get("summary"));
                story.setDescription((String) fields.get("description"));
                story.setStatus("In Progress");
                // Combine summary and description for embedding
                String textForEmbedding = (story.getSummary() != null ? story.getSummary() : "") + " " +
                                          (story.getDescription() != null ? story.getDescription() : "");
                float[] embedding = openAiEmbeddingService.getEmbedding(textForEmbedding);
                System.out.println("embedding"+textForEmbedding);

                System.out.println("embedding"+embedding);
                story.setVector(embedding);
                repository.save(story);
                stories.add(story);
            }
        }
        return stories;
    }
}
