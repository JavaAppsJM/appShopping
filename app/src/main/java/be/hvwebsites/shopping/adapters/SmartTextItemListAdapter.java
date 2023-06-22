package be.hvwebsites.shopping.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import be.hvwebsites.libraryandroid4.helpers.ListItemHelper;
import be.hvwebsites.libraryandroid4.statics.StaticData;
import be.hvwebsites.shopping.ManageItemActivity;
import be.hvwebsites.shopping.R;
import be.hvwebsites.shopping.constants.SpecificData;

public class SmartTextItemListAdapter extends RecyclerView.Adapter<SmartTextItemListAdapter.ListViewHolder> {
    private final LayoutInflater inflater;
    private Context mContext;

    private List<ListItemHelper> reusableList;
    private String reference;
    private String client;
    private ClickListener clickListener;
//    private String baseSwitch;

    public SmartTextItemListAdapter(Context context) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
    }

    public void setOnItemClickListener(ClickListener clickListener){
        // Methode om de clicklistener property vd adapter in te vullen met het
        // bewaren vd tobuy
        this.clickListener = clickListener;
    }

    public interface ClickListener{
        // Interface om een clicklistener door te geven nr de activity
        void onItemClicked(int position, View v);
    }

    class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView textItemView;

        private ListViewHolder(View itemView){
            super(itemView);
            // list_text_item.xml gebruiken
            textItemView = itemView.findViewById(R.id.a4ListTextItem);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // Er is geclicked op een item
            switch (client){
                case SpecificData.ACTIVITY_ADDMEALCOMBINS:
                    // Verwerking vn click vr addmealcombins
                    // clicklistener mt properties doorgeven nr activity
                    clickListener.onItemClicked(getAdapterPosition(), v);
                    break;
                case SpecificData.ACTIVITY_A4SHOPCOMPETITION:
                    // Verwerking vn click vr activity A4ShopCompetitionList
                    // clicklistener mt properties doorgeven nr activity
                    clickListener.onItemClicked(getAdapterPosition(), v);
                    break;
                default:
            }

/*
            if (client.equals(SpecificData.ACTIVITY_ADDMEALCOMBINS)){
                // Verwerking vn click vr addmealcombins
                // clicklistener mt properties doorgeven nr activity
                clickListener.onItemClicked(getAdapterPosition(), v);
            }
*/


/*
            Intent intent = new Intent(mContext, ManageItemActivity.class);
            intent.putExtra(StaticData.EXTRA_INTENT_KEY_TYPE, reference);
            intent.putExtra(StaticData.EXTRA_INTENT_KEY_ACTION, StaticData.ACTION_UPDATE);
//            intent.putExtra(StaticData.EXTRA_INTENT_KEY_FILE_BASE, baseSwitch);
            intent.putExtra(StaticData.EXTRA_INTENT_KEY_INDEX, indexToUpdate);
            mContext.startActivity(intent);
*/
        }
    }

    public List<ListItemHelper> getReusableList() {
        return reusableList;
    }

    public void setReusableList(List<ListItemHelper> reusableList) {
        this.reusableList = reusableList;
        notifyDataSetChanged();
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.list_text_item, parent, false);
        return new ListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        if (reusableList != null){
            String currentLine = reusableList.get(position).getItemtext();
            holder.textItemView.setText(currentLine);
        }else {
            holder.textItemView.setText("No data !");
        }
    }

    @Override
    public int getItemCount() {
        if (reusableList != null) return reusableList.size();
        else return 0;
    }
}
