package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseUpdater implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        String sql = "ALTER TABLE invoice ADD COLUMN IF NOT EXISTS photo_name VARCHAR(255)";
        try {
            jdbcTemplate.execute(sql);
            System.out.println("Column 'photo_name' added successfully!");
        } catch (Exception e) {
            System.err.println("Error adding column: " + e.getMessage());
        }
    }
}
