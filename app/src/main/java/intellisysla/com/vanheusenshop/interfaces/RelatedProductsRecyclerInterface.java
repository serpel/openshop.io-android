package intellisysla.com.vanheusenshop.interfaces;

import android.view.View;

import intellisysla.com.vanheusenshop.entities.product.Product;

public interface RelatedProductsRecyclerInterface {

    void onRelatedProductSelected(View v, int position, Product product);
}
