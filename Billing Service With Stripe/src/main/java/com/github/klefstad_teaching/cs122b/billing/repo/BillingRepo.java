package com.github.klefstad_teaching.cs122b.billing.repo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.klefstad_teaching.cs122b.billing.config.Item;
import com.github.klefstad_teaching.cs122b.billing.config.Sale;
import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.BillingResults;
import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import model.CartResponse;
import model.CartRetrieveResponse;
import model.OrderListResponse;
import model.PaymentIntentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class BillingRepo
{
    private NamedParameterJdbcTemplate template;
    private ObjectMapper objectMapper;

    @Autowired
    public BillingRepo(ObjectMapper objectMapper, NamedParameterJdbcTemplate template)
    {
        this.template = template;
        this.objectMapper = objectMapper;
    }

    //language=sql
    private static final String MOVIE_RETRIEVE =
            "SELECT mp.premium_discount, c.quantity, mp.unit_price, mp.movie_id, m.title, m.backdrop_path, m.poster_path\n" +
                    "FROM billing.movie_price as mp\n" +
                    "JOIN movies.movie as m on mp.movie_id = m.id\n" +
                    "JOIN billing.cart as c on c.movie_id = mp.movie_id\n" +
                    "WHERE c.user_id = :userId ";

    //language=sql
    private static final String MOVIE_REMOVAL =
                "DELETE FROM billing.cart\n" +
                        "WHERE user_id = :userId";

    //language=sql
    private static final String ORDER_DETAILS =
            "SELECT mp.premium_discount, si.quantity, mp.unit_price, mp.movie_id, m.title, m.backdrop_path, m.poster_path\n" +
                    "FROM billing.sale_item si\n" +
                    "JOIN movies.movie m on si.movie_id = m.id\n" +
                    "JOIN billing.movie_price mp on m.id = mp.movie_id\n" +
                    "JOIN billing.sale s on s.id = si.sale_id\n" +
                    "WHERE si.sale_id = :saleId and s.user_id = :userId";

    public void insertMovies(Long userId, Long movieId, Integer quantity)
    {
        try
        {
            if (this.template.update(
                    "INSERT INTO billing.cart (user_id, movie_id, quantity)\n" +
                            "VALUES (:userId, :movieId, :quantity);",
                    new MapSqlParameterSource()
                            .addValue("userId", userId, Types.INTEGER)
                            .addValue("movieId", movieId, Types.INTEGER)
                            .addValue("quantity", quantity, Types.INTEGER)
            ) < 1)
            {
                throw new ResultError(BillingResults.CART_ITEM_DOES_NOT_EXIST);
            }
        }
        catch (DuplicateKeyException e)
        {
            throw new ResultError(BillingResults.CART_ITEM_EXISTS);
        }
    }

    public void updateMovies(Long userId, Long movieId, Integer quantity)
    {
        try
        {
            if (this.template.update(
                        "UPDATE billing.cart\n" +
                            "SET quantity = :quantity\n" +
                            "WHERE user_id = :userId and movie_id = :movieId;",
                    new MapSqlParameterSource()
                            .addValue("userId", userId, Types.INTEGER)
                            .addValue("movieId", movieId, Types.INTEGER)
                            .addValue("quantity", quantity, Types.INTEGER)
            ) < 1)
            {
                throw new ResultError(BillingResults.CART_ITEM_DOES_NOT_EXIST);
            }
        }
        catch (DuplicateKeyException e)
        {
            throw new ResultError(BillingResults.CART_ITEM_DOES_NOT_EXIST);
        }
    }

    public void deleteMovieFromCart(Long userId, Long movieId)
    {
        StringBuilder sql = new StringBuilder(MOVIE_REMOVAL);
        sql.append(" and movie_id = :movieId");
        try
        {
            if (this.template.update(
                            sql.toString(),
                    new MapSqlParameterSource()
                            .addValue("userId", userId, Types.INTEGER)
                            .addValue("movieId", movieId, Types.INTEGER)
            ) < 1)
            {
                throw new ResultError(BillingResults.CART_ITEM_DOES_NOT_EXIST);
            }
        }
        catch (DataAccessException e)
        {
            throw new ResultError(BillingResults.CART_ITEM_DOES_NOT_EXIST);
        }
    }


    public CartRetrieveResponse retrieveMovies(Long userId, boolean discount, boolean hidden)
    {
        List<Item> items = null;
        AtomicReference<BigDecimal> total = new AtomicReference<>(BigDecimal.ZERO);
        StringBuilder sql = new StringBuilder(MOVIE_RETRIEVE);
        //Handle hidden members
        if (hidden)
        {
            sql.append(" and m.hidden = false\n;");
        }
        else{
            sql.append(";");
        }

        try
        {
            //HANDLE HIDDEN MEMBERS
            items = this.template.query(
                    sql.toString(),
                    new MapSqlParameterSource()
                            .addValue("userId", userId, Types.INTEGER),
                    (rs, rowNum) ->
                            {
                                Item item = new Item()
                                        .setQuantity(rs.getInt("quantity"))
                                        .setMovieId(rs.getLong("movie_id"))
                                        .setMovieTitle(rs.getString("title"))
                                        .setBackdropPath(rs.getString("backdrop_path"))
                                        .setPosterPath(rs.getString("poster_path"));
                                Double temp = null;
                                if (discount)
                                {
                                    temp = rs.getDouble("unit_price") * (1.0 - (rs.getDouble("premium_discount")/ 100.0));
                                    item.setUnitPrice(BigDecimal.valueOf(temp).setScale(2, RoundingMode.DOWN));
                                }
                                else{
                                    temp = rs.getDouble("unit_price");
                                    item.setUnitPrice(rs.getBigDecimal("unit_price"));
                                }
                                temp = BigDecimal.valueOf(temp).setScale(2, RoundingMode.DOWN)
                                                .doubleValue() * rs.getDouble("quantity");
                                total.set(total.get().add(BigDecimal.valueOf(temp)));
                                return item;
                            }


            );
        }
        catch (DataAccessException e)
        {
            throw new ResultError(BillingResults.CART_EMPTY);
        }
//        total.add(rs.getBigDecimal("unit_price"));
//        System.out.println("The total: " + total);
//        for (Item e : items)
//        {
//            System.out.println("MovieID: " + e.getMovieTitle());
//            System.out.println("Unit Price: " + e.getUnitPrice());
//        }

        return (total.get().doubleValue() == 0.0 ?
                new CartRetrieveResponse()
                    .setResult(BillingResults.CART_EMPTY) :
                new CartRetrieveResponse()
                    .setItems(items)
                    .setTotal(total.get().setScale(2, RoundingMode.UP))
                    .setResult(BillingResults.CART_RETRIEVED));
    }

    public CartResponse clearCart(Long userId)
    {
        try
        {
            if (this.template.update(
                    MOVIE_REMOVAL,
                    new MapSqlParameterSource()
                            .addValue("userId", userId, Types.INTEGER)
            ) < 1)
            {
                return new CartResponse()
                        .setResult(BillingResults.CART_EMPTY);
            }
        }
        catch (DataAccessException e)
        {
            throw new ResultError(BillingResults.CART_EMPTY);
        }

        return new CartResponse()
                .setResult(BillingResults.CART_CLEARED);
    }

    public PaymentIntentResponse getPaymentIntent(Long userId, boolean discount, boolean hidden){
        //Contains Items(MovieTitle), TotalAmount
        CartRetrieveResponse response = retrieveMovies(userId, discount, hidden);

        PaymentIntentResponse retResponse = new PaymentIntentResponse();
        //Check if the cart is Empty
        if (response.getResult().equals(BillingResults.CART_EMPTY))
        {
            retResponse.setResult(BillingResults.CART_EMPTY);
            return retResponse;
        }

        StringBuilder description = new StringBuilder();
        for (Item e : response.getItems())
        {
            description.append(e.getMovieTitle()).append(", ");
        }
        description.delete(description.length()-2, description.length());
//        System.out.println(description);

        PaymentIntentCreateParams paymentIntentCreateParams =
                PaymentIntentCreateParams
                        .builder()
                        .setCurrency("USD")
                        .setDescription(description.toString())
                        .setAmount(response.getTotal().longValue())
                        .putMetadata("userId", Long.toString(userId))
                        .setAutomaticPaymentMethods(
                                PaymentIntentCreateParams.AutomaticPaymentMethods
                                        .builder()
                                        .setEnabled(true)
                                        .build()
                        )
                        .build();

        try{
            PaymentIntent paymentIntent = PaymentIntent.create(paymentIntentCreateParams);
            String paymentIntentId = paymentIntent.getId();
            String clientSecret = paymentIntent.getClientSecret();
            retResponse.setResult(BillingResults.ORDER_PAYMENT_INTENT_CREATED)
                    .setPaymentIntentId(paymentIntentId)
                    .setClientSecret(clientSecret);
        }
        catch(StripeException e)
        {
            throw new ResultError(BillingResults.STRIPE_ERROR);
        }

        return retResponse;
    }

    public void populateSaleOrders(Long userId, boolean discount, boolean hidden)
    {
        //Contains Items(MovieTitle), TotalAmount
        CartRetrieveResponse response = retrieveMovies(userId, discount, hidden);
        if (response.getTotal() == null)
        {
            throw new ResultError(BillingResults.ORDER_CANNOT_COMPLETE_NOT_SUCCEEDED);
        }
        try {
            Timestamp orderDate = Timestamp.from(Instant.now());
            //Insert the sale information
            this.template.update(
                    "INSERT INTO billing.sale (user_id, total, order_date)\n" +
                            "VALUES (:userId, :total, :orderDate);",
                    new MapSqlParameterSource()
                            .addValue("userId", userId, Types.INTEGER)
                            .addValue("total", response.getTotal(), Types.DECIMAL)
                            .addValue("orderDate", orderDate, Types.TIMESTAMP)
            );
            //Get the salesId
            Integer saleId = this.template.queryForObject(
                    "SELECT id\n" +
                    "FROM billing.sale\n" +
                    "WHERE user_id = :userId\n" +
                    "ORDER BY order_date DESC " +
                    "LIMIT 1;",
                    new MapSqlParameterSource()
                            .addValue("userId", userId, Types.INTEGER)
                            .addValue("orderDate", orderDate, Types.TIMESTAMP),
                    (rs, rowNum) ->
                            rs.getInt("id")
            );

            //Insert to the saleItem
            for (Item e : response.getItems())
            {
                System.out.println("movieId: " + e.getMovieId());
                System.out.println("quantity: " +  e.getQuantity());
                this.template.update(
                        "INSERT INTO billing.sale_item\n" +
                                "VALUES (:saleId, :movieId, :quantity)",
                        new MapSqlParameterSource()
                                .addValue("saleId", saleId, Types.INTEGER)
                                .addValue("movieId", e.getMovieId(), Types.INTEGER)
                                .addValue("quantity", e.getQuantity(), Types.INTEGER)
                );
            }
        }
        catch (DuplicateKeyException e)
        {
            throw new ResultError(BillingResults.ORDER_CANNOT_COMPLETE_NOT_SUCCEEDED);
        }

        //After clear the cart
        clearCart(userId);
    }

    public OrderListResponse getOrderList(Long userId)
    {
        List<Sale> sales = null;
        OrderListResponse response = new OrderListResponse();
        String sql = "SELECT id, total, order_date\n" +
                "FROM billing.sale\n" +
                "WHERE user_id = :userId\n" +
                "ORDER BY id DESC\n" +
                "LIMIT 5;";
        System.out.println("Sql: " + sql);
        try{
            sales = this.template.query(
                    "SELECT id, total, order_date\n" +
                        "FROM billing.sale\n" +
                        "WHERE user_id = :userId\n" +
                        "ORDER BY id DESC\n" +
                        "LIMIT 5;",
                    new MapSqlParameterSource()
                            .addValue("userId", userId, Types.INTEGER),
                    (rs, rowNum) ->
                            new Sale()
                                    .setSaleId(rs.getLong("id"))
                                    .setTotal(rs.getBigDecimal("total").setScale(2, RoundingMode.UP))
                                    .setOrderDate(rs.getTimestamp("order_date").toInstant())
            );
        }
        catch (DataAccessException e)
        {
            response.setResult(BillingResults.ORDER_LIST_NO_SALES_FOUND);
        }

        return (sales == null || sales.isEmpty() ? response.setResult(BillingResults.ORDER_LIST_NO_SALES_FOUND) :
                response.setResult(BillingResults.ORDER_LIST_FOUND_SALES)
                .setSales(sales));
    }

    public CartRetrieveResponse getSaleDetails(Long userId, Long saleId, boolean discount, boolean hidden)
    {
        CartRetrieveResponse response = null;
        List<Item> items = null;
        AtomicReference<BigDecimal> total = new AtomicReference<>(BigDecimal.ZERO);
        StringBuilder sql = new StringBuilder(ORDER_DETAILS);
        System.out.println(sql);
        if (hidden)
        {
            sql.append(" and m.hidden = false\n;");
        }
        else{
            sql.append(";");
        }

        try {
            items =
                    this.template.query(
                            sql.toString(),
                            new MapSqlParameterSource().addValue("saleId", saleId, Types.INTEGER)
                                    .addValue("userId", userId, Types.INTEGER),
                            (rs, rowNum) ->
                            {
                                Item item = new Item()
                                        .setQuantity(rs.getInt("quantity"))
                                        .setMovieId(rs.getLong("movie_id"))
                                        .setMovieTitle(rs.getString("title"))
                                        .setBackdropPath(rs.getString("backdrop_path"))
                                        .setPosterPath(rs.getString("poster_path"));
                                Double temp = null;
                                if (discount)
                                {
                                    temp = rs.getDouble("unit_price") * (1.0 - (rs.getDouble("premium_discount")/ 100.0));
                                    item.setUnitPrice(BigDecimal.valueOf(temp).setScale(2, RoundingMode.DOWN));
                                }
                                else{
                                    temp = rs.getDouble("unit_price");
                                    item.setUnitPrice(rs.getBigDecimal("unit_price"));
                                }
                                temp = BigDecimal.valueOf(temp).setScale(2, RoundingMode.DOWN)
                                        .doubleValue() * rs.getDouble("quantity");
                                total.set(total.get().add(BigDecimal.valueOf(temp)));
                                return item;
                            }
                    );
        }
        catch (DataAccessException e)
        {
            throw new ResultError(BillingResults.ORDER_DETAIL_NOT_FOUND);
        }

        System.out.println("The total: " + total);
        for (Item e : items)
        {
            System.out.println("MovieID: " + e.getMovieTitle());
            System.out.println("Unit Price: " + e.getUnitPrice());
        }
        return (total.get().doubleValue() == 0.0 ?
                new CartRetrieveResponse()
                        .setResult(BillingResults.ORDER_DETAIL_NOT_FOUND) :
                new CartRetrieveResponse()
                        .setItems(items)
                        .setTotal(total.get().setScale(2, RoundingMode.UP))
                        .setResult(BillingResults.ORDER_DETAIL_FOUND));
    }
}
