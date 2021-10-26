package com.example.starter;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.util.Arrays;

@Slf4j
public class JdbcBatchTests extends JdbcTest {

    @Test
    @SneakyThrows
    public void txTest() {
        try {
            connection.setAutoCommit(false);
            insertTest(true);
            log.info("commit");
            connection.commit();
        } catch (Exception e) {
            connection.rollback();
            log.info("rollback");
        } finally {
            connection.setAutoCommit(false);
            log.info("setAutoCommit false");
        }
    }

    @SneakyThrows
    private void insertTest(boolean mockException) {
        try (final PreparedStatement statement = connection.prepareStatement("INSERT INTO city (`name`, `CountryCode`, `District`, `Population`) values ( ?,?,?,? )");) {
            for (int i = 0; i < 1000; i++) {
                statement.setString(1, "city" + i);
                statement.setString(2, "AFG");
                if (mockException && i == 500) {
                    statement.setString(3, "AAABBBBBBBBBBBCCCCCCCCCCCC");
                } else {
                    statement.setString(3, "AAA");
                }
                statement.setString(4, String.valueOf(i * i));

                statement.addBatch();
            }
            final int[] result = statement.executeBatch();
            log.info("batchTest: {}", Arrays.toString(result));
        }
    }


    @Test
    @SneakyThrows
    public void batchTest() {
        insertTest(false);
    }
}
