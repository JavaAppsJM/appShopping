package be.hvwebsites.shopping;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.helpers.ListItemHelper;
import be.hvwebsites.libraryandroid4.statics.StaticData;
import be.hvwebsites.shopping.adapters.TextItemListAdapter;
import be.hvwebsites.shopping.constants.SpecificData;
import be.hvwebsites.shopping.entities.Meal;
import be.hvwebsites.shopping.viewmodels.ShopEntitiesViewModel;

public class MealFragmentOld extends Fragment implements AdapterView.OnItemSelectedListener {
    private ShopEntitiesViewModel viewModel;
    private List<ListItemHelper> itemList = new ArrayList<>();
    private String listEntityType;
    private String action;
    private int indexToUpdate = 0;
    private String fileBaseDir;
    // Meal
    private Meal mealToSave = new Meal();
    // Views voor headers recyclerview
    private TextView labelProductsView;
    private TextView labelChildMealsView;
    private TextView labelParentMealView;
    // Entities voor recyclerview list
    private String recyclerListEntity;
    private static final String ENTITY_PRODUCT = "product";
    private static final String ENTITY_CHILD_MEAL = "childmeal";
    private static final String ENTITY_PARENT_MEAL = "parentmeal";

    // Toegevoegd vanuit android tutorial
    public MealFragmentOld(){
        super(R.layout.fragment_meal);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_meal, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // button
        Button saveButton = view.findViewById(R.id.saveButtonMeal);
        saveButton.setText(SpecificData.BUTTON_TOEVOEGEN);

        // Via het viewmodel uit de activity kan je over de data beschikken !
        viewModel = new ViewModelProvider(requireActivity()).get(ShopEntitiesViewModel.class);

        // Wat zijn de argumenten die werden meegegeven
        fileBaseDir = requireArguments().getString(StaticData.EXTRA_INTENT_KEY_FILE_BASE_DIR);
        action = requireArguments().getString(StaticData.EXTRA_INTENT_KEY_ACTION);
        if (action.equals(StaticData.ACTION_UPDATE)){
            indexToUpdate = requireArguments().getInt(StaticData.EXTRA_INTENT_KEY_INDEX);
            // Bepaal geselecteerd gerecht obv meegegeven index
            Meal mealToUpdate = viewModel.getMealList().get(indexToUpdate);
            // Vul Scherm in met gegevens
            EditText nameView = view.findViewById(R.id.editNameNewMeal);
            nameView.setText(mealToUpdate.getEntityName());
            // Checkboxen invullen
            CheckBox toBuyView = view.findViewById(R.id.toBuyCheckBoxMeal);
            toBuyView.setChecked(mealToUpdate.isToBuy());
            CheckBox wantedView = view.findViewById(R.id.wantedCheckboxMeal);
            wantedView.setChecked(mealToUpdate.isWanted());
            // button tekst
            saveButton.setText(SpecificData.BUTTON_AANPASSEN);
            // Recyclerview voor Artikels/deelgerechten/parentgerechten definieren
            RecyclerView recyclerView = view.findViewById(R.id.recyclerMealDetail);
            final TextItemListAdapter adapter = new TextItemListAdapter(this.getContext());
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
            itemList.clear();
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
/*
                        Toast.makeText(MealFragment.this,
                                "Deleting item ... ",
                                Toast.LENGTH_LONG).show();
*/
                            // Bepalen entity IDNumber to be deleted from the list from the meal
                            int position = viewHolder.getAdapterPosition();
                            IDNumber idNumberToBeDeleted = itemList.get(position).getItemID();
                            // Leegmaken itemlist
                            itemList.clear();
                            if (listEntityType.equals(ENTITY_PRODUCT)) {
                                // Delete product in meal
                                viewModel.deleteMealByProduct(viewModel.getProductByID(idNumberToBeDeleted));
                                itemList.addAll(viewModel.getProductNamesByMeal(mealToUpdate));
                            } else if (listEntityType.equals(ENTITY_CHILD_MEAL)) {
                                // TODO: Delete childmeal in mealmeal
                                viewModel.deleteChildMealinMeal(viewModel.getMealByID(idNumberToBeDeleted));
                            } else if (listEntityType.equals(ENTITY_PARENT_MEAL)) {
                                // TODO: Delete parentmeal in mealmeal
                                viewModel.deleteParentMealinMeal(viewModel.getMealByID(idNumberToBeDeleted));
                            }
                            // Refresh recyclerview
                            // TODO: ListItemAdapter nog aanpassen om te werken met listitemhelper !!
                            //adapter.setEntityType(listEntityType);
                            //adapter.setCallingActivity(SpecificData.ENTITY_TYPE_RUBRIEK);
                            //adapter.setReusableList(itemList);
                        }
                    }
            );
            helper.attachToRecyclerView(recyclerView);
            // TODO:Sturing Headers en invullen recyclerlist vn Deel 2
            labelProductsView = view.findViewById(R.id.labelProductsMeal);
            labelChildMealsView = view.findViewById(R.id.labelSubMeal);
            // Artikels voorzien mr enkel activeren indien er bestaan
            try {
                itemList.addAll(viewModel.getProductNamesByMeal(mealToUpdate));
                // Er zijn artikels, artikels activeren en laten zien
                labelProductsView.setTypeface(null, Typeface.BOLD);
                labelProductsView.setTextColor(ContextCompat.getColor(this.getContext(),
                        R.color.black));
                labelProductsView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // format vn ParentMeals
                        labelParentMealView.setTypeface(null, Typeface.NORMAL);
                        labelParentMealView.setTextColor(ContextCompat.getColor(v.getContext(),
                                R.color.grey));
                        // format vn childMeal
                        labelChildMealsView.setTypeface(null, Typeface.NORMAL);
                        labelChildMealsView.setTextColor(ContextCompat.getColor(v.getContext(),
                                R.color.grey));
                        // format vn artikels
                        labelProductsView.setTypeface(null, Typeface.BOLD);
                        labelProductsView.setTextColor(ContextCompat.getColor(v.getContext(),
                                R.color.black));
                        // Opvullen recycler list met artikels vr gerecht in kwestie
                        itemList.clear();
                        itemList.addAll(viewModel.getProductNamesByMeal(mealToUpdate));
                        recyclerListEntity = ENTITY_PRODUCT;
                        //listEntityType = SpecificData.LIST_TYPE_3;
                        // TODO: Listitemadapter moet nog aangepast worden !
                        //adapter.setEntityType(SpecificData.ENTITY_TYPE_RUBRIEK);
                        //adapter.setCallingActivity(SpecificData.ENTITY_TYPE_RUBRIEK);
                        //adapter.setItemList(itemList);
                    }
                });
                // Vullen recyclerlist mt artikels
                recyclerListEntity = ENTITY_PRODUCT;
                //listEntityType = SpecificData.LIST_TYPE_3;
                // TODO: Listitemadapter moet nog aangepast worden !
                //adapter.setEntityType(listEntityType);
                //adapter.setItemList(itemList);
            }catch (NullPointerException ex){
                // Er zijn geen artikels
                labelProductsView.setTypeface(null, Typeface.NORMAL);
                labelProductsView.setTextColor(ContextCompat.getColor(this.getContext(),
                        R.color.grey));
                labelChildMealsView.setTypeface(null, Typeface.BOLD);
                labelChildMealsView.setTextColor(ContextCompat.getColor(this.getContext(),
                        R.color.black));
                // Opvullen recycler list met deelgerechten ChildMeals vr meals in kwestie
                itemList.addAll(viewModel.getChildMealNamesByMeal(mealToUpdate));
                recyclerListEntity = ENTITY_CHILD_MEAL;
                //listEntityType = SpecificData.LIST_TYPE_3;
                // TODO: TextItemList adapter moet nog aangepast worden
                //adapter.setEntityType(SpecificData.ENTITY_TYPE_OPVOLGINGSITEM);
                //adapter.setCallingActivity(SpecificData.ENTITY_TYPE_RUBRIEK);
                //adapter.setItemList(itemList);
            }

            labelChildMealsView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // format vn parentmeals
                    labelParentMealView.setTypeface(null, Typeface.NORMAL);
                    labelParentMealView.setTextColor(ContextCompat.getColor(v.getContext(),
                            R.color.grey));
                    // format vn artikels
                    labelProductsView.setTypeface(null, Typeface.NORMAL);
                    labelProductsView.setTextColor(ContextCompat.getColor(v.getContext(),
                            R.color.grey));
                    // format vn childmeals
                    labelChildMealsView.setTypeface(null, Typeface.BOLD);
                    labelChildMealsView.setTextColor(ContextCompat.getColor(v.getContext(),
                            R.color.black));
                    // Opvullen recycler list met deelgerechten vr gerecht in kwestie
                    itemList.clear();
                    itemList.addAll(viewModel.getChildMealNamesByMeal(mealToUpdate));
                    recyclerListEntity = ENTITY_CHILD_MEAL;
                    //listEntityType = SpecificData.LIST_TYPE_3;
                    // TODO: TextItemList adapter moet nog aangepast worden
                    //adapter.setEntityType(SpecificData.ENTITY_TYPE_OPVOLGINGSITEM);
                    //adapter.setCallingActivity(SpecificData.ENTITY_TYPE_RUBRIEK);
                    //adapter.setItemList(itemList);
                }
            });

            labelParentMealView = view.findViewById(R.id.labelParentMeal);
            labelParentMealView.setTypeface(null, Typeface.NORMAL);
            labelParentMealView.setTextColor(ContextCompat.getColor(this.getContext(),
                    R.color.grey));
            labelParentMealView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // format vn chilsmeals
                    labelChildMealsView.setTypeface(null, Typeface.NORMAL);
                    labelChildMealsView.setTextColor(ContextCompat.getColor(v.getContext(),
                            R.color.grey));
                    // format vn artikels
                    labelProductsView.setTypeface(null, Typeface.NORMAL);
                    labelProductsView.setTextColor(ContextCompat.getColor(v.getContext(),
                            R.color.grey));
                    // format vn parentmeals
                    labelParentMealView.setTypeface(null, Typeface.BOLD);
                    labelParentMealView.setTextColor(ContextCompat.getColor(v.getContext(),
                            R.color.black));
                    // Opvullen recycler list met parentmeals vr meal in kwestie
                    itemList.clear();
                    itemList.addAll(viewModel.getParentMealNamesByMeal(mealToUpdate));
                    recyclerListEntity = ENTITY_PARENT_MEAL;
                    //listEntityType = SpecificData.LIST_TYPE_3;
                    // TODO: TextItemList adapter moet nog aangepast worden
                    //adapter.setEntityType(SpecificData.ENTITY_TYPE_LOG);
                    //adapter.setCallingActivity(SpecificData.ENTITY_TYPE_RUBRIEK);
                    //adapter.setItemList(itemList);
                }
            });
            // FloatingActionButton
            FloatingActionButton fab = view.findViewById(R.id.fab_edit_meal_detail);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO: ofwel wil je een artikel, een deelgerecht toevoegen ofwel een parentmeal
                    Intent intent = new Intent(getContext(), ManageItemActivity.class);
                    if (recyclerListEntity.equals(ENTITY_PRODUCT)) {
                    } else if (recyclerListEntity.equals(ENTITY_PARENT_MEAL)) {
                        // intent voor editparentmeal
                    } else if (recyclerListEntity.equals(ENTITY_CHILD_MEAL)) {
                        // Intent voor editchildmeal
                    }
                    intent.putExtra(StaticData.EXTRA_INTENT_KEY_ACTION, StaticData.ACTION_NEW);
                    //intent.putExtra(SpecificData.ID_RUBRIEK, rubriekToUpdate.getEntityId().getId());
                    //intent.putExtra(StaticData.EXTRA_INTENT_KEY_RETURN, SpecificData.ENTITY_TYPE_RUBRIEK);
                    startActivity(intent);
                }
            });

        }else {
            // Bij new geen wanted en geen details laten zien
            CheckBox wantedView = view.findViewById(R.id.wantedCheckboxMeal);
            wantedView.setVisibility(View.INVISIBLE);
            TextView labelProducts = view.findViewById(R.id.labelProductsMeal);
            labelProducts.setVisibility(View.INVISIBLE);
            TextView labelSubMeal = view.findViewById(R.id.labelSubMeal);
            labelSubMeal.setVisibility(View.INVISIBLE);
            TextView labelParentMeal = view.findViewById(R.id.labelParentMeal);
            labelParentMeal.setVisibility(View.INVISIBLE);
        }

        // Als toevoegen/aanpassen ingedrukt wordt...
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Definitie inputvelden aanpassen
                EditText nameView = view.findViewById(R.id.editNameNewProduct);
                CheckBox toBuyView = view.findViewById(R.id.toBuyCheckBox);
                CheckBox cooledView = view.findViewById(R.id.cooledCheckBox);
                Spinner prefShopView = (Spinner) view.findViewById(R.id.spinnerPrefShop);
                // Reply intent definieren
                Intent replyIntent = new Intent(getContext(), A4ListActivity.class);
                if (action.equals(StaticData.ACTION_NEW)){
                    Meal newMeal = new Meal(fileBaseDir, false);
                    newMeal.setEntityName(String.valueOf(nameView.getText()));
                    newMeal.setToBuy(toBuyView.isChecked());
                    viewModel.getMealList().add(newMeal);
                    mealToSave = newMeal;
                }else { // Update
                    mealToSave.setEntityName(String.valueOf(nameView.getText()));
                    mealToSave.setToBuy(toBuyView.isChecked());
                    viewModel.getMealList().set(indexToUpdate, mealToSave);
                    // Zet index to update in replyIntent want de oproeper heeft die nodig
                    replyIntent.putExtra(StaticData.EXTRA_INTENT_KEY_INDEX,
                            indexToUpdate);
                }
                viewModel.storeMeals();
                replyIntent.putExtra(StaticData.EXTRA_INTENT_KEY_TYPE, SpecificData.LIST_TYPE_3);
                replyIntent.putExtra(StaticData.EXTRA_INTENT_KEY_ACTION, action);
                //replyIntent.putExtra(StaticData.EXTRA_INTENT_KEY_FILE_BASE, viewModel.getBaseSwitch());
                startActivity(replyIntent);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        String selection = String.valueOf(parent.getItemAtPosition(position));
        viewModel.setSpinnerSelection(selection);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}