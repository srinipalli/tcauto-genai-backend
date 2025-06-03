package com.example.jirastories.repository;

import com.example.jirastories.model.JiraStory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

@Repository
public class JiraStoryRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(JiraStory story) {
        jdbcTemplate.execute((Connection conn) -> {
            String sql = "INSERT INTO jira_stories (id, key, summary, description, status, vector, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, story.getId());
                ps.setString(2, story.getKey());
                ps.setString(3, story.getSummary());
                ps.setString(4, story.getDescription());
                ps.setString(5, story.getStatus());
                // Format vector as a JSON-like array string: [0.1,0.2,...]
                float[] vector = story.getVector();
                if (vector != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("[");
                    for (int i = 0; i < vector.length; i++) {
                        sb.append(vector[i]);
                        if (i < vector.length - 1) sb.append(",");
                    }
                    sb.append("]");
                    ps.setString(6, sb.toString());
                } else {
                    ps.setNull(6, java.sql.Types.VARCHAR);
                }
                ps.setTimestamp(7, story.getCreatedAt() != null ? story.getCreatedAt() : new Timestamp(System.currentTimeMillis()));
                ps.setTimestamp(8, story.getUpdatedAt() != null ? story.getUpdatedAt() : new Timestamp(System.currentTimeMillis()));
                ps.executeUpdate();
            }
            return null;
        });
    }
}
