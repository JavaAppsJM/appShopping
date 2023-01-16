package be.hvwebsites.shopping.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Build;
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
import be.hvwebsites.shopping.ManageItemActivity;
import be.hvwebsites.shopping.R;
import be.hvwebsites.shopping.constants.SpecificData;

public class CheckboxListAdapter extends RecyclerView.Adapter<CheckboxListAdapter.CbListViewHolder> {

    private final LayoutInflater inflater;
    private final Context mContext;
    private List<CheckboxHelper> checkboxList;
    private String reference;
    private ClickListener clickListener;
    private String activityMaster;
    private String baseSwitch;

    public CheckboxListAdapter(Context context) {
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
        void onItemClicked(int position, View v, boolean checked);
    }

    class CbListViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox checkBoxView;

        private CbListViewHolder(View checkboxView){
            super(checkboxView);
            this.checkBoxView = checkboxView.findViewById(R.id.a4ListCheckboxItem);
            this.checkBoxView.isChecked();
            this.checkBoxView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // aan en af vinken zit ingebakken in checkbox, daar moet niets voor gebeuren
                    // de eigenschap toBuy moet wel bewaard worden want in de volgende activity
                    // wordt de waarde terug uit de file gehaald !!
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

    public String getActivityMaster() {
        return activityMaster;
    }

    public void setActivityMaster(String activityMaster) {
        this.activityMaster = activityMaster;
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

            if (activityMaster.equals(SpecificData.ACTIVITY_A4LIST)){
                holder.checkBoxView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        // er is geclicked op een item, dit betekent dat er nr detail vd item vr evt update wordt gegaan
                        int indexToUpdate = holder.getAdapterPosition();
                        //String currentLine = checkboxList.get(indexToUpdate).getName();

                        Intent intent = new Intent(mContext, ManageItemActivity.class);
                        intent.putExtra(StaticData.EXTRA_INTENT_KEY_TYPE, reference);
                        intent.putExtra(StaticData.EXTRA_INTENT_KEY_ACTION, StaticData.ACTION_UPDATE);
                        // baseswitch doorgeven is niet nodig omdat ManageItemActivity zelf baseswitch
                        // bepaald
                        //intent.putExtra(StaticData.EXTRA_INTENT_KEY_FILE_BASE, baseSwitch);
                        intent.putExtra(StaticData.EXTRA_INTENT_KEY_INDEX, indexToUpdate);
                        mContext.startActivity(intent);
                        return true;
                    }
                });
            } else if (activityMaster.equals(SpecificData.ACTIVITY_A4SHOPPINGLIST)){
                switch (checkboxList.get(position).getStyle()){
                    case StaticData.PURPLE_500:
                        holder.checkBoxView.setTextColor(ContextCompat.getColor(mContext,
                                R.color.purple_500));
                        break;
                    case SpecificData.STYLE_COOLED_BOLD:
                        holder.checkBoxView.setTextColor(ContextCompat.getColor(mContext,
                                R.color.cooling));
                        holder.checkBoxView.setTypeface(null, Typeface.BOLD);
                        // Om checkbox andere tint te geven, werkt niet bij An
//                        ColorStateList tint = holder.checkBoxView.getResources().getColorStateList(R.color.cbstatecolorlist);
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                            holder.checkBoxView.setButtonTintList(tint);
//                        }
                        break;
                    case SpecificData.STYLE_COOLED:
                        holder.checkBoxView.setTextColor(ContextCompat.getColor(mContext,
                                R.color.cooling));
                        holder.checkBoxView.setTypeface(null, Typeface.NORMAL);
                        break;
                    default:
                        holder.checkBoxView.setTextColor(ContextCompat.getColor(mContext,
                                R.color.black));
                        holder.checkBoxView.setTypeface(null, Typeface.NORMAL);
                }

            }
        }else {
            holder.checkBoxView.setText("No data !");
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
        if (checkboxList != null) return checkboxList.size();
        else return 0;
    }
}
