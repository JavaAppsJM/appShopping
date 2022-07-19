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

import be.hvwebsites.libraryandroid4.statics.StaticData;
import be.hvwebsites.winkelen.ManageItemActivity;
import be.hvwebsites.winkelen.R;

public class TextItemListAdapter extends RecyclerView.Adapter<TextItemListAdapter.ListViewHolder> {
    private final LayoutInflater inflater;
    private Context mContext;

    private List<String> reusableList;
    private String reference;
    private String baseSwitch;

    public TextItemListAdapter(Context context) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
    }

    class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView textItemView;

        private ListViewHolder(View itemView){
            super(itemView);
            textItemView = itemView.findViewById(R.id.a4ListTextItem);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // er is geclicked op een item, dit betekent dat er nr detail vd item vr evt update wordt gegaan
            // daarvoor gaan we nr de update activity
            int indexToUpdate = getAdapterPosition();
            //String currentLine = reusableList.get(indexToUpdate);

            Intent intent = new Intent(mContext, ManageItemActivity.class);
            intent.putExtra(StaticData.EXTRA_INTENT_KEY_TYPE, reference);
            intent.putExtra(StaticData.EXTRA_INTENT_KEY_ACTION, StaticData.ACTION_UPDATE);
            intent.putExtra(StaticData.EXTRA_INTENT_KEY_SELECTION, baseSwitch);
            intent.putExtra(StaticData.EXTRA_INTENT_KEY_INDEX, indexToUpdate);
            mContext.startActivity(intent);
        }
    }

    public List<String> getReusableList() {
        return reusableList;
    }

    public void setReusableList(List<String> reusableList) {
        this.reusableList = reusableList;
        notifyDataSetChanged();
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
            String currentLine = reusableList.get(position);
            holder.textItemView.setText(currentLine);
        }else {
            holder.textItemView.setText("No data !");
        }
    }

    public String getBaseSwitch() {
        return baseSwitch;
    }

    public void setBaseSwitch(String baseSwitch) {
        this.baseSwitch = baseSwitch;
    }

    @Override
    public int getItemCount() {
        if (reusableList != null) return reusableList.size();
        else return 0;
    }


}
