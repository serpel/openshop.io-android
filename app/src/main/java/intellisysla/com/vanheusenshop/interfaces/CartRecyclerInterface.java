package intellisysla.com.vanheusenshop.interfaces;

import intellisysla.com.vanheusenshop.entities.cart.CartDiscountItem;
import intellisysla.com.vanheusenshop.entities.cart.CartProductItem;

public interface CartRecyclerInterface {

    void onProductUpdate(CartProductItem cartProductItem);

    void onProductDelete(CartProductItem cartProductItem);

    void onDiscountDelete(CartDiscountItem cartDiscountItem);

    void onProductSelect(long productId);

}
