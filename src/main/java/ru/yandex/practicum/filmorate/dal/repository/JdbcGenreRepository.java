package ru.yandex.practicum.filmorate.dal.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcGenreRepository implements GenreRepository {

    private final NamedParameterJdbcOperations jdbc;
    private final GenreRowMapper genreMapper;

    @Override
    public List<Genre> getAll() {
        String sql = "SELECT * FROM genres ORDER BY id";
        return jdbc.query(sql, genreMapper);
    }

    @Override
    public Optional<Genre> getById(Long id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        String sql = "SELECT * FROM genres WHERE id = :id";
        return jdbc.query(sql, params, genreMapper).stream()
                .findFirst();
    }
}
