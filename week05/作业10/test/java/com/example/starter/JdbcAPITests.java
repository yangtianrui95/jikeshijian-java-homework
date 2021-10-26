package com.example.starter;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Slf4j
@SpringBootTest
class JdbcAPITests extends JdbcTest {

    @Test
    void contextLoads() {
    }

    @Test
    @SneakyThrows
    public void select() {
        try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM city where name = ?")) {
            statement.setString(1, "ytr");
            final ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                log.info("id: {}, name:{}", id, name);
            }
        }
    }


    @Test
    @SneakyThrows
    public void insert() {
        try (final PreparedStatement preparedStatement
                     = connection.prepareStatement("INSERT INTO city (`Name`, `CountryCode`, `District`, `Population`)  VALUES ( ?,?,?,? )")) {
            preparedStatement.setString(1, "ytr");
            preparedStatement.setString(2, "AFG");
            preparedStatement.setString(3, "district");
            preparedStatement.setString(4, "111");
            final boolean result = preparedStatement.execute();
            log.info("result: {}", result);
        }
    }
}
