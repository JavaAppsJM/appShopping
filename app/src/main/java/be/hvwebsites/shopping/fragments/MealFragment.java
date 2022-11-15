package be.hvwebsites.shopping.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import be.hvwebsites.libraryandroid4.statics.StaticData;
import be.hvwebsites.shopping.A4ListActivity;
import be.hvwebsites.shopping.AddMealCombins;
import be.hvwebsites.shopping.R;
import be.hvwebsites.shopping.adapters.SmartTextItemListAdapter;
import be.hvwebsites.shopping.constants.SpecificData;
import be.hvwebsites.shopping.entities.Meal;
import be.hvwebsites.shopping.viewmodels.ShopEntitiesViewModel;

public class MealFragment extends Fragment {
    private ShopEntitiesViewModel viewModel;
    private String action;
    private int indexToUpdate = 0;
    // Meal
    private Meal mealToSave = new Meal();
    // SuperCombination Type
    private String combinationType = SpecificData.SC_PRODUCTSMEAL;
    // Textviews voor de labels vd recyclerview
    private TextView labelProductsMeal;
    private TextView labelSubMeal;
    private TextView labelParentMeal;

    // Toegevoegd vanuit android tutorial
    public MealFragment(){
        super(R.layout.fragment_meal);
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
        return inflater.inflate(R.layout.fragment_meal, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Via het viewmodel uit de activity kan je over de data beschikken !
        viewModel = new ViewModelProvider(requireActivity()).get(ShopEntitiesViewModel.class);

        // button definieren en invullen voorlpig al met toevoegen wordt evt verder overschreven
        // met update
        Button saveButton = view.findViewById(R.id.saveButtonMeal);
        saveButton.setText(SpecificData.BUTTON_TOEVOEGEN);

        // Textviews definieren
        EditText mealNameView = view.findViewById(R.id.editNameNewMeal);

        // Checkboxen definierne
        CheckBox toBuyView = view.findViewById(R.id.toBuyCheckBoxMeal);
        CheckBox wantedView = view.findViewById(R.id.wantedCheckboxMeal);

        // Labels voor recyclerlist definieren
        labelProductsMeal = view.findViewById(R.id.labelProductsMeal);
        labelSubMeal = view.findViewById(R.id.labelSubMeal);
        labelParentMeal = view.findViewById(R.id.labelParentMeal);

        // Recyclerviews en adapters definieren
        RecyclerView recycVwMealDetails = view.findViewById(R.id.recyclerMealDetail);
        final SmartTextItemListAdapter recycMealDetailAdapter =
                new SmartTextItemListAdapter(this.getContext());
        recycVwMealDetails.setAdapter(recycMealDetailAdapter);
        recycVwMealDetails.setLayoutManager(new LinearLayoutManager(this.getContext()));

        FloatingActionButton fab = view.findViewById(R.id.fab_edit_meal_detail);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Toevoegen van een productmeal of een childmeal of een parentmeal
                Intent intent = new Intent(getContext(),
                        AddMealCombins.class);
                // Meal Id en combinationType meegeven
                intent.putExtra(StaticData.EXTRA_INTENT_KEY_ID, mealToSave.getEntityId().getId());
                intent.putExtra(StaticData.EXTRA_INTENT_KEY_TYPE, combinationType);
                startActivity(intent);
            }
        });

        // Wat zijn de argumenten die werden meegegeven
        action = requireArguments().getString(StaticData.EXTRA_INTENT_KEY_ACTION);
        if (action.equals(StaticData.ACTION_UPDATE)){
            // Visible zetten vd invisible zaken
            labelProductsMeal.setVisibility(View.VISIBLE);
            labelSubMeal.setVisibility(View.VISIBLE);
            labelParentMeal.setVisibility(View.VISIBLE);
            recycVwMealDetails.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);

            // Bepaal het gerecht dat moet aangepast worden
            indexToUpdate = requireArguments().getInt(StaticData.EXTRA_INTENT_KEY_INDEX);
            Meal mealToUpdate = viewModel.getMealList().get(indexToUpdate);
            mealToSave.setMeal(mealToUpdate);

            // Vul Scherm in met gegevens, eerst Textviews in dit geval naam vh gerecht
            mealNameView.setText(mealToUpdate.getEntityName());

            // Checkboxen invullen
            toBuyView.setChecked(mealToUpdate.isToBuy());
            wantedView.setChecked(mealToUpdate.isWanted());

            // button tekst overschrijven
            saveButton.setText(SpecificData.BUTTON_AANPASSEN);

            // Labels voor recyclerview invullen, default artikels selecteren
            labelProductsMeal.setTypeface(null, Typeface.BOLD);
            labelProductsMeal.setTextColor(ContextCompat.getColor(getContext(),
                    R.color.black));
            labelProductsMeal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Andere labels grey zetten
                    labelSubMeal.setTypeface(null, Typeface.NORMAL);
                    labelSubMeal.setTextColor(ContextCompat.getColor(getContext(), R.color.grey));
                    labelParentMeal.setTypeface(null, Typeface.NORMAL);
                    labelParentMeal.setTextColor(ContextCompat.getColor(getContext(), R.color.grey));
                    // Geselecteerd label in focus zetten
                    labelProductsMeal.setTypeface(null, Typeface.BOLD);
                    labelProductsMeal.setTextColor(ContextCompat.getColor(getContext(),
                            R.color.black));
                    // CombinationType zetten
                    combinationType = SpecificData.SC_PRODUCTSMEAL;
                    // Recyclerview invullen met artikels
                    recycMealDetailAdapter.setReference(SpecificData.LIST_TYPE_2);
                    recycMealDetailAdapter.setReusableList(viewModel.getProductNamesByMeal(mealToUpdate));
                }
            });
            // andere labels grey zetten
            labelSubMeal.setTypeface(null, Typeface.NORMAL);
            labelSubMeal.setTextColor(ContextCompat.getColor(getContext(), R.color.grey));
            labelSubMeal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Andere labels grey zetten
                    labelProductsMeal.setTypeface(null, Typeface.NORMAL);
                    labelProductsMeal.setTextColor(ContextCompat.getColor(getContext(), R.color.grey));
                    labelParentMeal.setTypeface(null, Typeface.NORMAL);
                    labelParentMeal.setTextColor(ContextCompat.getColor(getContext(), R.color.grey));
                    // Geselecteerd label in focus zetten
                    labelSubMeal.setTypeface(null, Typeface.BOLD);
                    labelSubMeal.setTextColor(ContextCompat.getColor(getContext(),
                            R.color.black));
                    // CombinationType zetten
                    combinationType = SpecificData.SC_SUBMEAL;
                    // Recyclerview invullen met deelgerechten
                    recycMealDetailAdapter.setReference(SpecificData.LIST_TYPE_3);
                    recycMealDetailAdapter.setReusableList(viewModel.getChildMealNamesByMeal(mealToUpdate));
                }
            });
            labelParentMeal.setTypeface(null, Typeface.NORMAL);
            labelParentMeal.setTextColor(ContextCompat.getColor(getContext(), R.color.grey));
            labelParentMeal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Andere labels grey zetten
                    labelProductsMeal.setTypeface(null, Typeface.NORMAL);
                    labelProductsMeal.setTextColor(ContextCompat.getColor(getContext(), R.color.grey));
                    labelSubMeal.setTypeface(null, Typeface.NORMAL);
                    labelSubMeal.setTextColor(ContextCompat.getColor(getContext(), R.color.grey));
                    // Geselecteerd label in focus zetten
                    labelParentMeal.setTypeface(null, Typeface.BOLD);
                    labelParentMeal.setTextColor(ContextCompat.getColor(getContext(),
                            R.color.black));
                    // CombinationType zetten
                    combinationType = SpecificData.SC_PARENTMEAL;
                    // Recyclerview invullen met deelgerechten
                    recycMealDetailAdapter.setReference(SpecificData.LIST_TYPE_3);
                    recycMealDetailAdapter.setReusableList(viewModel.getParentMealNamesByMeal(mealToUpdate));
                }
            });

            // Recyclerview invullen, default met bijhorende artikels
            recycMealDetailAdapter.setReference(SpecificData.LIST_TYPE_2);
            recycMealDetailAdapter.setReusableList(viewModel.getProductNamesByMeal(mealToUpdate));
        }else {
            // Bij new geen wanted en geen labels en geen recyclerlist
            wantedView.setVisibility(View.INVISIBLE);
            labelProductsMeal.setVisibility(View.INVISIBLE);
            labelSubMeal.setVisibility(View.INVISIBLE);
            labelParentMeal.setVisibility(View.INVISIBLE);
            recycVwMealDetails.setVisibility(View.INVISIBLE);
            fab.setVisibility(View.INVISIBLE);
        }

        // Als toevoegen/aanpassen ingedrukt wordt...
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (action.equals(StaticData.ACTION_NEW)){ // Toevoegen
                    Meal newMeal = new Meal(viewModel.getBasedir(), false);
                    newMeal.setEntityName(String.valueOf(mealNameView.getText()));
                    newMeal.setToBuy(toBuyView.isChecked());
                    viewModel.getMealList().add(newMeal);
                    mealToSave = newMeal;
                }else { // Aanpassen
                    mealToSave.setEntityName(String.valueOf(mealNameView.getText()));
                    mealToSave.setToBuy(toBuyView.isChecked());
                    viewModel.getMealList().set(indexToUpdate, mealToSave);
                }
                viewModel.sortMealList();
                viewModel.storeMeals();
                Intent replyIntent = new Intent(getContext(), A4ListActivity.class);
                replyIntent.putExtra(StaticData.EXTRA_INTENT_KEY_TYPE, SpecificData.LIST_TYPE_3);
                //replyIntent.putExtra(StaticData.EXTRA_INTENT_KEY_FILE_BASE, viewModel.getBaseSwitch());
                startActivity(replyIntent);
            }
        });
    }
}