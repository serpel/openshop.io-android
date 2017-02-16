package intellisysla.com.vanheusenshop.interfaces;

import android.view.View;

import intellisysla.com.vanheusenshop.entities.wishlist.WishlistItem;

public interface WishlistInterface {

    void onWishlistItemSelected(View view, WishlistItem wishlistItem);

    void onRemoveItemFromWishList(View caller, WishlistItem wishlistItem, int adapterPosition);
}
