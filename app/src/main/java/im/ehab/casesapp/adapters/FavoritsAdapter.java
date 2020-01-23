package im.ehab.casesapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import im.ehab.casesapp.R;
import im.ehab.casesapp.listeners.OnItemClickListener1;
import im.ehab.casesapp.models.Message;

import java.util.List;

/**
 * Created by baher on 16/11/2017.
 */

public class FavoritsAdapter extends RecyclerView.Adapter<FavoritsAdapter.ViewHolder> {
    List<Message> carBrandsList;
    Context context;
    private OnItemClickListener1 listener1;
    private OnItemClickListener1 listener22;
    private OnItemClickListener1 listener33;
    private OnItemClickListener1 listener44;
    private OnItemClickListener1 listener55;
    private OnItemClickListener1 listener66;

    public FavoritsAdapter(List<Message> carBrandsList, Context context) {
        this.carBrandsList = carBrandsList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_item_style, viewGroup, false);
        ViewHolder holderR = new ViewHolder(view);
        return holderR;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(FavoritsAdapter.ViewHolder holder, int position) {
        final Message message = carBrandsList.get(position);

        holder.title.setText(message.getCategory().getName());
        holder.message.setText(message.getContent());
        holder.date.setText(message.getCreatedAt());
        holder.favorite.setChecked(true);
        holder.favorite.setEnabled(false);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener1.onItemClick(message);
            }
        };View.OnClickListener listener2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener22.onItemClick(message);
            }
        };View.OnClickListener listener3 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener33.onItemClick(message);
            }
        };View.OnClickListener listener4 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener44.onItemClick(message);
            }
        };View.OnClickListener listener5 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener55.onItemClick(message);
            }
        };View.OnClickListener listener6 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener66.onItemClick(message);
            }
        };

        holder.cardView.setOnClickListener(listener);
        holder.favorite.setOnClickListener(listener2);
        holder.copy.setOnClickListener(listener3);
        holder.share.setOnClickListener(listener4);
        holder.whatsapp.setOnClickListener(listener5);
        holder.messenger.setOnClickListener(listener6);

    }

    @Override
    public int getItemCount() {
        return carBrandsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, message, date;
        CardView cardView;
        ImageView copy, share, whatsapp, messenger;
        CheckBox favorite;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            date = itemView.findViewById(R.id.date);
            message = itemView.findViewById(R.id.message);
            cardView = itemView.findViewById(R.id.cardview);
            favorite = itemView.findViewById(R.id.favorite);
            copy = itemView.findViewById(R.id.copy);
            share = itemView.findViewById(R.id.share);
            whatsapp = itemView.findViewById(R.id.whatsapp);
            messenger = itemView.findViewById(R.id.messenger);
        }
    }

    public void setOnClickListener(OnItemClickListener1 listener) {
        this.listener1 = listener;
    }public void setOnClickListener2(OnItemClickListener1 listener) {
        this.listener22 = listener;
    }public void setOnClickListener3(OnItemClickListener1 listener) {
        this.listener33 = listener;
    }public void setOnClickListener4(OnItemClickListener1 listener) {
        this.listener44 = listener;
    }public void setOnClickListener5(OnItemClickListener1 listener) {
        this.listener55 = listener;
    }public void setOnClickListener6(OnItemClickListener1 listener) {
        this.listener66 = listener;
    }

}
