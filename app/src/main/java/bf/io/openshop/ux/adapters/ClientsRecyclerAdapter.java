package bf.io.openshop.ux.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import bf.io.openshop.R;
import bf.io.openshop.entities.client.Client;
import bf.io.openshop.entities.product.Product;
import bf.io.openshop.interfaces.CategoryRecyclerInterface;
import bf.io.openshop.interfaces.ClientRecyclerInterface;
import bf.io.openshop.views.ResizableImageView;

/**
 * Created by alienware on 2/1/2017.
 */

public class ClientsRecyclerAdapter extends RecyclerView.Adapter<ClientsRecyclerAdapter.ViewHolder> {

    private final Context context;
    private final ClientRecyclerInterface clientRecyclerInterface;
    private List<Client> clients = new ArrayList<>();
    private LayoutInflater layoutInflater;

    public ClientsRecyclerAdapter(Context context, ClientRecyclerInterface clientRecyclerInterface) {
        this.context = context;
        this.clientRecyclerInterface = clientRecyclerInterface;
    }

    public Client getItem(int position) {
        return this.clients.get(position);
    }

    @Override
    public int getItemCount() {
        return this.clients.size();
    }

    public void addClients(List<Client> clients){
        this.clients = clients;
        notifyDataSetChanged();
    }

    public void clear() {
        clients.clear();
    }

    @Override
    public ClientsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.list_item_clients, parent, false);
        return new ClientsRecyclerAdapter.ViewHolder(view, clientRecyclerInterface);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Product product = getItem(position);
        holder.bindContent(product);
        // - replace the contents of the view with that element
        holder.productNameTV.setText(holder.product.getName());
        holder.productSKU.setText(holder.product.getCode());

        if (loadHighRes && product.getMainImageHighRes() != null) {
            Picasso.with(context).load(product.getMainImageHighRes())
                    .fit().centerInside()
                    .placeholder(R.drawable.placeholder_loading)
                    .error(R.drawable.placeholder_error)
                    .into(holder.productImage);
        } else {
            Picasso.with(context).load(holder.product.getMainImage())
                    .fit().centerInside()
                    .placeholder(R.drawable.placeholder_loading)
                    .error(R.drawable.placeholder_error)
                    .into(holder.productImage);
        }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView clientCardCode;
        public TextView clientName;
        public TextView clientPhone;
        private Client client;

        public ViewHolder(View v, final ClientRecyclerInterface clientRecyclerInterface) {
            super(v);
            clientCardCode = (TextView) v.findViewById(R.id.client_item_card_code);
            clientName = (TextView) v.findViewById(R.id.client_item_name);
            clientPhone = (TextView) v.findViewById(R.id.client_item_phone);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clientRecyclerInterface.onClientSelected(v, client);
                }
            });
        }

        public void bindContent(Client client) {
            this.client = client;
        }
    }
}
