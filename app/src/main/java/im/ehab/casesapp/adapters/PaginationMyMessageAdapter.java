package im.ehab.casesapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import im.ehab.casesapp.Contains;
import im.ehab.casesapp.R;
import im.ehab.casesapp.listeners.OnItemClickListener1;
import im.ehab.casesapp.models.Message;
import im.ehab.casesapp.utils.AppSharedPreferences;

import java.util.ArrayList;

public class PaginationMyMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private ArrayList<Message> messageList;
    private ArrayList<Message> messageListFavorite;
    private Context context;
    private AppSharedPreferences appSharedPreferences;

    private boolean isLoadingAdded = false;
    private OnItemClickListener1 listener1;
    private OnItemClickListener1 listener22;
    private OnItemClickListener1 listener33;
    private OnItemClickListener1 listener44;
    private OnItemClickListener1 listener55;
    private OnItemClickListener1 listener66;

    public PaginationMyMessageAdapter(Context context) {
        this.context = context;
        messageList = new ArrayList<>();
        messageListFavorite = new ArrayList<>();
        appSharedPreferences = new AppSharedPreferences(context);
    }

    public ArrayList<Message> getmessageList() {
        return messageList;
    }

    public void setMessageList(ArrayList<Message> messageList) {
        this.messageList = messageList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.message_item_style, parent, false);
        viewHolder = new Holder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final Message message = messageList.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                final Holder holder1 = (Holder) holder;
                //favorite, copy, share, whatsapp, messenger
                holder1.title.setText(message.getCategory().getName());
                holder1.message.setText(message.getContent());
                holder1.date.setText(message.getCreatedAt());
                holder1.favorite.setOnCheckedChangeListener(null);
                holder1.favorite.setOnClickListener(null);
                holder1.favorite.setChecked(false);
                messageListFavorite = appSharedPreferences.readArray(Contains.arraySave);
                Log.d(Contains.LOG+"favSize", messageListFavorite.size()+"");
                for (int i=0; i<messageListFavorite.size();i++ ){
                    int m_id = messageListFavorite.get(i).getId();
                    int new_m_id = message.getId();
                    if (m_id == new_m_id){
                        holder1.favorite.setChecked(true);
                    }
                }

                holder1.favorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        messageListFavorite = appSharedPreferences.readArray(Contains.arraySave);
                        if (isChecked == true){
                            Log.d(Contains.LOG+"checked", isChecked+"");
                            messageListFavorite.add(new Message(message.getId(), message.getContent(), message.getCategoryId(), message.getCreatedAt(), message.getUpdatedAt(), message.getDeletedAt(), message.getCategory()));
                            appSharedPreferences.writeArray(Contains.arraySave, messageListFavorite);
                        }else{
                            Log.d(Contains.LOG+"checked", isChecked+"");
                            for (int i=0; i<messageListFavorite.size(); i++){
                                int m_id = message.getId();
                                int new_m_id = messageListFavorite.get(i).getId();
                                if (m_id == new_m_id){
                                    Log.d(Contains.LOG+"==", "m_id = "+m_id+" new_m_id = "+new_m_id);
                                    messageListFavorite.remove(i);
                                }
                            }
                            appSharedPreferences.writeArray(Contains.arraySave, messageListFavorite);
                        }
                    }
                });

                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener1.onItemClick(message);
                    }
                };
                View.OnClickListener listener3 = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener33.onItemClick(message);
                    }
                };
                View.OnClickListener listener4 = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener44.onItemClick(message);
                    }
                };
                View.OnClickListener listener5 = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener55.onItemClick(message);
                    }
                };
                View.OnClickListener listener6 = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener66.onItemClick(message);
                    }
                };

                holder1.cardView.setOnClickListener(listener);
                holder1.copy.setOnClickListener(listener3);
                holder1.share.setOnClickListener(listener4);
                holder1.whatsapp.setOnClickListener(listener5);
                holder1.messenger.setOnClickListener(listener6);

                break;

            case LOADING:
//                Do nothing
                break;
        }

    }

    @Override
    public int getItemCount() {
        return messageList == null ? 0 : messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == messageList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }


    /*
   Helpers
   _________________________________________________________________________________________________
    */

    public void add(Message r) {
        messageList.add(r);
        notifyItemInserted(messageList.size() - 1);
    }

    public void addAll(ArrayList<Message> messageList) {
        for (Message result : messageList) {
            add(result);
        }
    }

    public void remove(Message r) {
        int position = messageList.indexOf(r);
        if (position > -1) {
            messageList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Message());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = messageList.size() - 1;
        Message result = getItem(position);

        if (result != null) {
            messageList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Message getItem(int position) {
        return messageList.get(position);
    }


   /*
   View Holders
   _________________________________________________________________________________________________
    */

    /**
     * Main list's content ViewHolder
     */
    protected class Holder extends RecyclerView.ViewHolder {

        TextView title, message, date;
        CardView cardView;
        ImageView copy, share, whatsapp, messenger;
        CheckBox favorite;

        public Holder(View itemView) {
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

    protected class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }

    public void setFilter(ArrayList<Message> orders) {
        messageList = new ArrayList<>();
        messageList.addAll(orders);
        notifyDataSetChanged();
    }


}