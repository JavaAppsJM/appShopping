package be.hvwebsites.shopping.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import be.hvwebsites.libraryandroid4.helpers.CheckboxHelper;
import be.hvwebsites.shopping.R;
import be.hvwebsites.shopping.adapters.CheckboxListAdapter;
import be.hvwebsites.shopping.constants.SpecificData;
import be.hvwebsites.shopping.viewmodels.ShopEntitiesViewModel;

public class MealListFragment extends Fragment{
    private ShopEntitiesViewModel viewModel;
    private List<CheckboxHelper> checkboxList = new ArrayList<>();

    // Toegevoegd vanuit android tutorial
    public MealListFragment(){
        super(R.layout.fragment_checkbox_item_recycler);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_checkbox_item_recycler, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Via het viewmodel uit de activity kan je over de data beschikken !
        viewModel = new ViewModelProvider(requireActivity()).get(ShopEntitiesViewModel.class);

        // Recyclerview definieren
        RecyclerView recyclerView = view.findViewById(R.id.recyclerviewCheckBoxes);
        final CheckboxListAdapter cbListAdapter = new CheckboxListAdapter(this.getContext());
        recyclerView.setAdapter(cbListAdapter);
        LinearLayoutManager cbLineairLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(cbLineairLayoutManager);

        TextView labelColHead1 = view.findViewById(R.id.listColHeadCheckBoxes);
        labelColHead1.setText(SpecificData.HEAD_LIST_ACTIVITY_T3);
        checkboxList.clear();
        checkboxList.addAll(viewModel.convertMealsToCheckboxs(
                viewModel.getMealList(),
                false));

        // om te kunnen swipen in de recyclerview ; swippen == deleten
        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        Toast.makeText(MealListFragment.super.getContext(),
                                "Deleting item ... ",
                                Toast.LENGTH_LONG).show();
                        viewModel.deleteMeal(position);
                        // Refresh recyclerview
                        checkboxList.clear();
                        checkboxList.addAll(viewModel.convertMealsToCheckboxs(
                                viewModel.getMealList(),
                                false));
                        cbListAdapter.setReference(SpecificData.LIST_TYPE_3);
                        cbListAdapter.setBaseSwitch(viewModel.getBaseSwitch());
                        cbListAdapter.setCheckboxList(checkboxList);
                    }
                });
        helper.attachToRecyclerView(recyclerView);

        // Invullen adapter
        cbListAdapter.setReference(SpecificData.LIST_TYPE_3);
        cbListAdapter.setBaseSwitch(viewModel.getBaseSwitch());
        cbListAdapter.setCheckboxList(checkboxList);

        // Als er geclicked is op een checkbox, kan ik dat hier capteren ?
        cbListAdapter.setOnItemClickListener(new CheckboxListAdapter.ClickListener() {
            @Override
            public void onItemClicked(int position, View v, boolean checked) {
                viewModel.getMealList().get(position).setToBuy(checked);
                viewModel.storeMeals();
                //TODO: als een meal to buy gezet wordt moeten alle onderliggende artikels ook to buy gezet worden !
                cbListAdapter.setBaseSwitch(viewModel.getBaseSwitch());
                cbListAdapter.setCheckboxList(checkboxList);
                boolean debug = true;
            }
        });
    }

}