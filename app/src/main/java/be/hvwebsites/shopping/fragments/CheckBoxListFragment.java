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
import be.hvwebsites.libraryandroid4.statics.StaticData;
import be.hvwebsites.shopping.R;
import be.hvwebsites.shopping.adapters.CheckboxListAdapter;
import be.hvwebsites.shopping.constants.SpecificData;
import be.hvwebsites.shopping.viewmodels.ShopEntitiesViewModel;

public class CheckBoxListFragment extends Fragment{
    // Super fragment dat voor alle entities (product en meal, niet voor shop !)
    // een checkboxlist kan aanmaken
    private ShopEntitiesViewModel viewModel;
    private List<CheckboxHelper> checkboxList = new ArrayList<>();
    private String entityName;
    private String activityMaster;

    // Toegevoegd vanuit android tutorial
    public CheckBoxListFragment(){
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
        // Ook baseswitch zit hierin net als de files !! Dus je hoeft filebasedir of baseswitch niet
        // apart door te geven
        viewModel = new ViewModelProvider(requireActivity()).get(ShopEntitiesViewModel.class);

        // Wat zijn de argumenten die werden meegegeven entityName ? callingactivity ?
        entityName = requireArguments().getString(SpecificData.LIST_TYPE);
        activityMaster = requireArguments().getString(SpecificData.CALLING_ACTIVITY);

        // Recyclerview definieren
        RecyclerView recyclerView = view.findViewById(R.id.recyclerviewCheckBoxes);
        final CheckboxListAdapter cbListAdapter = new CheckboxListAdapter(this.getContext());
        recyclerView.setAdapter(cbListAdapter);
        LinearLayoutManager cbLineairLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(cbLineairLayoutManager);
        cbListAdapter.setActivityMaster(activityMaster);

        // Kolom header definieren
        TextView labelColHead1 = view.findViewById(R.id.listColHeadCheckBoxes);

        // Checkboxlist clearen
        checkboxList.clear();

        switch (entityName){
            case SpecificData.LIST_TYPE_1:
                labelColHead1.setText(SpecificData.HEAD_LIST_ACTIVITY_T1);
                // Als je voor shops ook checkboxlist wenst dan moet er nog een convert
                //  aangemaakt worden
                break;
            case SpecificData.LIST_TYPE_2:
                labelColHead1.setText(SpecificData.HEAD_LIST_ACTIVITY_T2);
                checkboxList.addAll(viewModel.convertProductsToCheckboxs(
                        viewModel.getProductList(),
                        SpecificData.PRODUCT_DISPLAY_LARGE,
                        false));
                break;
            case SpecificData.LIST_TYPE_3:
                labelColHead1.setText(SpecificData.HEAD_LIST_ACTIVITY_T3);
                checkboxList.addAll(viewModel.convertMealsToCheckboxs(
                        viewModel.getMealList(),
                        false));
                break;
            default:
        }

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
                        Toast.makeText(CheckBoxListFragment.super.getContext(),
                                "Deleting item ... ",
                                Toast.LENGTH_LONG).show();
                        // Refresh recyclerview
                        checkboxList.clear();
                        switch (entityName){
                            case SpecificData.LIST_TYPE_2:
                                viewModel.deleteProduct(position);
                                checkboxList.addAll(viewModel.convertProductsToCheckboxs(
                                        viewModel.getProductList(),
                                        SpecificData.PRODUCT_DISPLAY_LARGE,
                                        false));
                                cbListAdapter.setReference(SpecificData.LIST_TYPE_2);
                                break;
                            case SpecificData.LIST_TYPE_3:
                                viewModel.deleteMeal(position);
                                checkboxList.addAll(viewModel.convertMealsToCheckboxs(
                                        viewModel.getMealList(),
                                        false));
                                cbListAdapter.setReference(SpecificData.LIST_TYPE_3);
                                break;
                            default:
                                break;
                        }
                        // baseswitch doorgeven nr adapter is niet nodig !
                        //cbListAdapter.setBaseSwitch(viewModel.getBaseSwitch());
                        cbListAdapter.setCheckboxList(checkboxList);
                    }
                });
        helper.attachToRecyclerView(recyclerView);

        // Invullen adapter
        cbListAdapter.setReference(entityName);
        // baseswitch doorgeven nr adapter is niet nodig !
        //cbListAdapter.setBaseSwitch(viewModel.getBaseSwitch());
        cbListAdapter.setCheckboxList(checkboxList);

        // Als er geclicked is op een checkbox, kan ik dat hier capteren ?
        cbListAdapter.setOnItemClickListener(new CheckboxListAdapter.ClickListener() {
            @Override
            public void onItemClicked(int position, View v, boolean checked) {
                // Refresh recyclerview
                checkboxList.clear();
                switch (entityName){
                    case SpecificData.LIST_TYPE_2:
                        viewModel.getProductList().get(position).setToBuy(checked);
                        viewModel.storeProducts();
                        checkboxList.addAll(viewModel.convertProductsToCheckboxs(
                                viewModel.getProductList(),
                                SpecificData.PRODUCT_DISPLAY_LARGE,
                                false));
                        cbListAdapter.setReference(SpecificData.LIST_TYPE_2);
                        break;
                    case SpecificData.LIST_TYPE_3:
                        // meal in kwestie toBuy wordt gewijzigd
                        viewModel.getMealList().get(position).setToBuy(checked);
                        // TODO: eigen artikels toBuy moeten gewijzigd worden
                        // TODO: deelgerechten toBuy moeten gewijzigd worden en deelgerechten van
                        //  deelgerechten enz..
                        viewModel.storeMeals();
                        checkboxList.addAll(viewModel.convertMealsToCheckboxs(
                                viewModel.getMealList(),
                                false));
                        cbListAdapter.setReference(SpecificData.LIST_TYPE_3);
                        break;
                    default:
                        break;
                }
                // baseswitch doorgeven nr adapter is niet nodig !
                //cbListAdapter.setBaseSwitch(viewModel.getBaseSwitch());
                cbListAdapter.setCheckboxList(checkboxList);
                boolean debug = true;
            }
        });
    }
}