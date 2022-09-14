package com.github.klefstad_teaching.cs122b.idm.repo;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.RefreshToken;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.User;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.TokenStatus;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.util.NestedServletException;

import java.nio.charset.StandardCharsets;
import java.sql.Ref;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;

@Component
public class IDMRepo
{
    private NamedParameterJdbcTemplate template;

    @Autowired
    public IDMRepo(NamedParameterJdbcTemplate template)
    {
        this.template = template;
    }

    //1. Create the ID (AUTO-ASSIGNED), email = email, user_status_id = DEFAULT(1), salt = salt, hashed_password = hashPassword
    public int updateUser(String email, int user_status, String salt, String hashed_password)
    {
//        String saltStr = new String(salt, StandardCharsets.UTF_8);
//        String hash_passwordStr = new String(hash_password, StandardCharsets.UTF_8);
        try
        {
//        System.out.println("Hash_password: " + Arrays.toString(hash_password));
            int rowsUpdate = this.template.update(
                    "INSERT INTO idm.user (email, user_status_id, salt, hashed_password)" +
                            "VALUES (:email, :user_status_id, :salt, :hashed_password);",
                    new MapSqlParameterSource()
                            .addValue("email", email, Types.VARCHAR)
                            .addValue("user_status_id", user_status, Types.INTEGER)
                            .addValue("salt", salt, Types.CHAR)
                            .addValue("hashed_password", hashed_password, Types.CHAR)
            );

        }
        catch (DuplicateKeyException e)
        {
            return -1;
        }
        return 1;
    }

    public User getUserByEmail(String email)
    {
        try
        {
            return this.template.queryForObject(
                    "SELECT id, email, user_status_id, salt, hashed_password " +
                            "FROM idm.user " +
                            "WHERE email = :email;",
                    new MapSqlParameterSource()
                            .addValue("email", email, Types.VARCHAR),

                    (rs, rowNum) ->
                            new User()
                                    .setId(rs.getInt("id"))
                                    .setEmail(rs.getString("email"))
                                    .setUserStatus(UserStatus.fromId(rs.getInt("user_status_id")))
                                    .setSalt(rs.getString("salt"))
                                    .setHashedPassword(rs.getString("hashed_password"))
            );
        }
        catch (DataAccessException e)
        {
            throw new ResultError(IDMResults.USER_NOT_FOUND);
        }
    }

    public User getUserById(int id)
    {
        try
        {
            return this.template.queryForObject(
                    "SELECT id, email, user_status_id, salt, hashed_password " +
                            "FROM idm.user " +
                            "WHERE id = :id;",
                    new MapSqlParameterSource()
                            .addValue("id", id, Types.INTEGER),

                    (rs, rowNum) ->
                            new User()
                                    .setId(rs.getInt("id"))
                                    .setEmail(rs.getString("email"))
                                    .setUserStatus(UserStatus.fromId(rs.getInt("user_status_id")))
                                    .setSalt(rs.getString("salt"))
                                    .setHashedPassword(rs.getString("hashed_password"))
            );
        }
        catch (DataAccessException e)
        {
            throw new ResultError(IDMResults.USER_NOT_FOUND);
        }
    }

    public void addToken(RefreshToken refreshToken)
    {

        this.template.update(
                "INSERT INTO idm.refresh_token (token, user_id, token_status_id, expire_time, max_life_time)" +
                        "VALUES (:token, :user_id, :token_status_id, :expire_time, :max_life_time);",
                new MapSqlParameterSource()
                        .addValue("token", refreshToken.getToken(), Types.CHAR)
                        .addValue("user_id", refreshToken.getUserId(), Types.INTEGER)
                        .addValue("token_status_id", refreshToken.getTokenStatus().id(), Types.INTEGER)
                        .addValue("expire_time", Timestamp.from(refreshToken.getExpireTime()), Types.TIMESTAMP)
                        .addValue("max_life_time", Timestamp.from(refreshToken.getMaxLifeTime()), Types.TIMESTAMP)
        );

    }

    public RefreshToken getRefreshToken(String token)
    {
        try
        {
            return this.template.queryForObject(
              "SELECT id, token, user_id, token_status_id, expire_time, max_life_time " +
                      "FROM idm.refresh_token " +
                      "WHERE token = :token;",
                    new MapSqlParameterSource()
                            .addValue("token", token, Types.CHAR),

                    (rs, rowNum) ->
                            new RefreshToken()
                                    .setId(rs.getInt("id"))
                                    .setToken(rs.getString("token"))
                                    .setUserId(rs.getInt("user_id"))
                                    .setTokenStatus(TokenStatus.fromId(rs.getInt("token_status_id")))
                                    .setExpireTime(rs.getTimestamp("expire_time").toInstant())
                                    .setMaxLifeTime(rs.getTimestamp("max_life_time").toInstant())
            );
        }
        catch (DataAccessException e)
        {
            throw new ResultError(IDMResults.REFRESH_TOKEN_NOT_FOUND);
        }
    }

    public void updateRefreshToken(RefreshToken refreshToken)
    {
        this.template.update(
               "UPDATE idm.refresh_token "+
               "SET token_status_id = :token_status " +
               "WHERE token = :token;",
               new MapSqlParameterSource()
                       .addValue("token_status", refreshToken.getTokenStatus().id(), Types.INTEGER)
                       .addValue("token", refreshToken.getToken(), Types.CHAR)

        );
    }

    public void updateExpireRefreshToken(RefreshToken refreshToken)
    {
        this.template.update(
                "UPDATE idm.refresh_token "+
                        "SET expire_time = :expire_time " +
                        "WHERE token = :token;",
                new MapSqlParameterSource()
                        .addValue("expire_time", Timestamp.from(refreshToken.getExpireTime()), Types.TIMESTAMP)
                        .addValue("token", refreshToken.getToken(), Types.CHAR)

        );
    }

}
