package model;

import com.github.klefstad_teaching.cs122b.billing.config.Item;
import com.github.klefstad_teaching.cs122b.core.base.ResponseModel;

import java.math.BigDecimal;
import java.util.List;

public class CartRetrieveResponse extends ResponseModel<CartRetrieveResponse> {
    private BigDecimal total;
    private List<Item> items;

    public BigDecimal getTotal() {
        return total;
    }

    public CartRetrieveResponse setTotal(BigDecimal total) {
        this.total = total;
        return this;
    }

    public List<Item> getItems() {
        return items;
    }

    public CartRetrieveResponse setItems(List<Item> items) {
        this.items = items;
        return this;
    }
}
