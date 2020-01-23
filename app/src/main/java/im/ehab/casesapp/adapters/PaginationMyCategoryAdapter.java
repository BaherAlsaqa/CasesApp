package im.ehab.casesapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import im.ehab.casesapp.R;
import im.ehab.casesapp.listeners.OnItemClickListener;
import im.ehab.casesapp.models.Category;

import java.util.ArrayList;

public class PaginationMyCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private ArrayList<Category> CategoryList;
    private Context context;

    private boolean isLoadingAdded = false;
    private OnItemClickListener listener1;

    public PaginationMyCategoryAdapter(Context context) {
        this.context = context;
        CategoryList = new ArrayList<>();
    }

    public ArrayList<Category> getCategoryList() {
        return CategoryList;
    }

    public void setCategoryList(ArrayList<Category> CategoryList) {
        this.CategoryList = CategoryList;
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
        View v1 = inflater.inflate(R.layout.category_item_style, parent, false);
        viewHolder = new Holder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final Category category = CategoryList.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                final Holder holder1 = (Holder) holder;

                holder1.title.setText(category.getName());
                holder1.description.setText(category.getDescription());

                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener1.onItemClick(category);
                    }
                };

                holder1.cardView.setOnClickListener(listener);

                break;

            case LOADING:
//                Do nothing
                break;
        }

    }

    @Override
    public int getItemCount() {
        return CategoryList == null ? 0 : CategoryList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == CategoryList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }


    /*
   Helpers
   _________________________________________________________________________________________________
    */

    public void add(Category r) {
        CategoryList.add(r);
        notifyItemInserted(CategoryList.size() - 1);
    }

    public void addAll(ArrayList<Category> CategoryList) {
        for (Category result : CategoryList) {
            add(result);
        }
    }

    public void remove(Category r) {
        int position = CategoryList.indexOf(r);
        if (position > -1) {
            CategoryList.remove(position);
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
        add(new Category());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = CategoryList.size() - 1;
        Category result = getItem(position);

        if (result != null) {
            CategoryList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Category getItem(int position) {
        return CategoryList.get(position);
    }


   /*
   View Holders
   _________________________________________________________________________________________________
    */

    /**
     * Main list's content ViewHolder
     */
    protected class Holder extends RecyclerView.ViewHolder {

        TextView title, description;
        CardView cardView;

        public Holder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.categorytitle);
            description = itemView.findViewById(R.id.description);
            cardView = itemView.findViewById(R.id.cardview);
        }
    }

    public void setOnClickListener(OnItemClickListener listener) {
        this.listener1 = listener;
    }

    protected class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }

    public void setFilter(ArrayList<Category> orders) {
        CategoryList = new ArrayList<>();
        CategoryList.addAll(orders);
        notifyDataSetChanged();
    }


}