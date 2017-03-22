package intellisysla.com.vanheusenshop.ux.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import intellisysla.com.vanheusenshop.R;
import intellisysla.com.vanheusenshop.entities.client.Client;
import intellisysla.com.vanheusenshop.interfaces.ClientRecyclerInterface;

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
        return new ClientsRecyclerAdapter.ViewHolder(context, view, clientRecyclerInterface);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Client client = getItem(position);
        holder.bindContent(client);
        // - replace the contents of the view with that element
        holder.clientName.setText(holder.client.getName());
        holder.clientPhone.setText(holder.client.getPhone());
        holder.clientCardCode.setText(holder.client.getCardCode());
        //holder.clientPhone.setText(holder.client.getPhone());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView clientCardCode;
        public TextView clientName;
        public TextView clientPhone;
        private Client client;

        public ViewHolder(final Context context, View v, final ClientRecyclerInterface clientRecyclerInterface) {
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

            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    //Toast.makeText(context, "Menu alterno", Toast.LENGTH_LONG).show();
                    return false;
                }
            });
        }

        public void bindContent(Client client) {
            this.client = client;
        }
    }
}
