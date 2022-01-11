package pl.teksusik.auther.storage;

import com.zaxxer.hikari.HikariDataSource;

public interface Storage {
    HikariDataSource getHikariDataSource();
}
