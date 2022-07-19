package be.hvwebsites.shopping.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import be.hvwebsites.libraryandroid4.helpers.CheckboxHelper;
import be.hvwebsites.libraryandroid4.statics.StaticData;
import be.hvwebsites.winkelen.R;

public class ChckbxListAdapter extends RecyclerView.Adapter<ChckbxListAdapter.CbListViewHolder> {
    private final LayoutInflater inflater;
    private Context mContext;

    private List<CheckboxHelper> checkboxList;
    private String reference;
    private ClickListener clickListener;

    public ChckbxListAdapter(Context context) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
    }

    public void setOnItemClickListener(ClickListener clickListener){
        // Methode om de clicklistener property vd adapter in te vullen met het
        // bewaren ve clicklistener
        this.clickListener = clickListener;
    }

    public interface ClickListener{
        // Interface om een clicklistener door te geven nr de activity
        void onItemClicked(int position, View v, boolean checked);
    }

    class CbListViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox checkBoxView;

        private CbListViewHolder(View checkboxView){
            super(checkboxView);
            this.checkBoxView = checkboxView.findViewById(R.id.a4ListCheckboxItem);
            //this.checkBoxView.isChecked();
            this.checkBoxView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // aan en af vinken zit ingebakken in checkbox, daar moet niets voor gebeuren
                    // de eigenschap toBuy moet wel bewaard worden want in de volgende activity
                    // wordt de waarde terug uit de file gehaald !!
                    // TODO: bewaren van toBuy
                    // viewmodel hier recuperen, lukt niet !
                    // Dus hoe moeten we toBuy bewaren ??
                    // Door in bovenliggende activity/fragment een setOnItemClickListener te creeren
                    // op de adapter !!
                    int indexToUpdate = getAdapterPosition();
                    boolean checked = checkBoxView.isChecked();
                    // clicklistener mt properties doorgeven nr activity
                    clickListener.onItemClicked(indexToUpdate, v, checked);

                    int johny =0;
                }
            });
        }
    }

    public List<CheckboxHelper> getCheckboxList() {
        return checkboxList;
    }

    public void setCheckboxList(List<CheckboxHelper> checkboxList) {
        this.checkboxList = checkboxList;
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
    public CbListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View checkBoxView = inflater.inflate(R.layout.list_checkbox_item, parent, false);
        return new CbListViewHolder(checkBoxView);
    }

    @Override
    public void onBindViewHolder(@NonNull CbListViewHolder holder, int position) {
        if (checkboxList != null){
            String currentLine = checkboxList.get(position).getName();
            holder.checkBoxView.setText(currentLine);
            holder.checkBoxView.setChecked(checkboxList.get(position).isChecked());
            if (checkboxList.get(position).getStyle() == StaticData.PURPLE_500){
                holder.checkBoxView.setTextColor(ContextCompat.getColor(mContext,
                        R.color.purple_500));
            } else {
                holder.checkBoxView.setTextColor(ContextCompat.getColor(mContext,
                        R.color.black));
            }
            if (checkboxList.get(position).getStyle() == StaticData.BOLD_TEXT){
                holder.checkBoxView.setTypeface(null, Typeface.BOLD);
            } else {
                holder.checkBoxView.setTypeface(null, Typeface.NORMAL);
            }

        }else {
            holder.checkBoxView.setText("No data !");
        }
    }

    @Override
    public int getItemCount() {
        if (checkboxList != null) return checkboxList.size();
        else return 0;
    }
}
