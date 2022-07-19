package be.hvwebsites.shopping.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import be.hvwebsites.winkelen.R;

public class SmallItemListAdapter extends RecyclerView.Adapter<SmallItemListAdapter.ListViewHolder> {
    private final LayoutInflater inflater;
    private Context mContext;

    private List<String> reusableList;
    private String reference;
    private String selectedItem;
    private View previousView;

    public SmallItemListAdapter(Context context) {
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
            // er is geclicked op een item, het geselecteerd item moet teruggeven worden
            // TODO: vorig item background op wit zetten
            if (previousView != null){
                previousView.setBackgroundColor(ContextCompat.getColor(mContext,
                        R.color.background_white));
            }
            int indexToUpdate = getAdapterPosition();
            selectedItem = reusableList.get(indexToUpdate);
            previousView = v;
            v.setBackgroundColor(ContextCompat.getColor(mContext, R.color.purple_200));

            int sdfgds=0;
        }
    }

    public void clearPrevViewBackground(){
        previousView.setBackgroundColor(ContextCompat.getColor(mContext,
                R.color.background_white));
    }

    public List<String> getReusableList() {
        return reusableList;
    }

    public void setReusableList(List<String> reusableList) {
        this.reusableList = reusableList;
        notifyDataSetChanged();
    }

    public String getSelectedItem() {
        return selectedItem;
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

    @Override
    public int getItemCount() {
        if (reusableList != null) return reusableList.size();
        else return 0;
    }


}
