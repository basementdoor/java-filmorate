package ru.yandex.practicum.filmorate.dal.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mapper.RatingRowMapper;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class JdbcRatingRepository implements RatingRepository {

    private final NamedParameterJdbcOperations jdbc;
    private final RatingRowMapper ratingMapper;

    @Override
    public List<Rating> getAll() {
        String sql = "SELECT * FROM ratings ORDER BY mpa_id";
        return jdbc.query(sql, ratingMapper);
    }

    @Override
    public Optional<Rating> getById(Long id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        String sql = "SELECT * FROM ratings WHERE mpa_id = :id";
        return jdbc.query(sql, params, ratingMapper).stream()
                .findFirst();
    }
}
