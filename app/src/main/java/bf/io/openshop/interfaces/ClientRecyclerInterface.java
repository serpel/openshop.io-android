package bf.io.openshop.interfaces;

import android.view.View;

import bf.io.openshop.entities.client.Client;

/**
 * Created by alienware on 2/1/2017.
 */

public interface ClientRecyclerInterface {
    void onClientSelected(View view, Client client);
}
