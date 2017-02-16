package intellisysla.com.vanheusenshop.interfaces;

import android.view.View;

import intellisysla.com.vanheusenshop.entities.drawerMenu.DrawerItemCategory;
import intellisysla.com.vanheusenshop.entities.drawerMenu.DrawerItemPage;

public interface DrawerRecyclerInterface {

    void onCategorySelected(View v, DrawerItemCategory drawerItemCategory);

    void onPageSelected(View v, DrawerItemPage drawerItemPage);

    void onHeaderSelected();
}
