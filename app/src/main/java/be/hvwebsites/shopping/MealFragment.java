package be.hvwebsites.shopping;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import be.hvwebsites.libraryandroid4.adapters.NothingSelectedSpinnerAdapter;
import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.helpers.ListItemHelper;
import be.hvwebsites.libraryandroid4.statics.StaticData;
import be.hvwebsites.shopping.adapters.SmallItemListAdapter;
import be.hvwebsites.shopping.adapters.TextItemListAdapter;
import be.hvwebsites.shopping.constants.SpecificData;
import be.hvwebsites.shopping.entities.Meal;
import be.hvwebsites.shopping.entities.Product;
import be.hvwebsites.shopping.entities.ProductInShop;
import be.hvwebsites.shopping.entities.Shop;
import be.hvwebsites.shopping.viewmodels.ShopEntitiesViewModel;

public class MealFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private ShopEntitiesViewModel viewModel;
    private List<ListItemHelper> itemList = new ArrayList<>();
    private String listEntityType;
    private String action;
    private int indexToUpdate = 0;
    // Meal
    private Meal mealToSave = new Meal();
    // Entities voor recyclerview list
    private static final String ENTITY_PRODUCT = "product";
    private static final String ENTITY_CHILD_MEAL = "childmeal";
    private static final String ENTITY_PARENT_MEAL = "parentmeal";

    // Toegevoegd vanuit android tutorial
    public MealFragment(){
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
        action = requireArguments().getString(StaticData.EXTRA_INTENT_KEY_ACTION);
        if (action.equals(StaticData.ACTION_UPDATE)){
            indexToUpdate = requireArguments().getInt(StaticData.EXTRA_INTENT_KEY_INDEX);
            // Bepaal geselecteerd gerecht obv meegegeven index
            Meal mealToUpdate = viewModel.getMealList().get(indexToUpdate);
            // TODO: Is dit nodig ?
            productToSave.setProduct(productToUpdate);
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
                                viewModel.de(idNumberToBeDeleted);
                            } else if (listEntityType.equals(ENTITY_PARENT_MEAL)) {
                                // TODO: Delete parentmeal in mealmeal
                                viewModel.deleteLogByID(idNumberToBeDeleted);
                            }
                            // Refresh recyclerview
                            adapter.setEntityType(listEntityType);
                            adapter.setCallingActivity(SpecificData.ENTITY_TYPE_RUBRIEK);
                            adapter.setItemList(itemList);
                        }
                    }
            );
            helper.attachToRecyclerView(recyclerView);

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
            recycViewSelShops.setVisibility(View.INVISIBLE);
            recycViewUnSelShops.setVisibility(View.INVISIBLE);
            Button selButton = view.findViewById(R.id.buttonRemShop);
            selButton.setVisibility(View.INVISIBLE);
            Button unselButton = view.findViewById(R.id.buttonAddShop);
            unselButton.setVisibility(View.INVISIBLE);
            prefShopSpinner.setAdapter(new NothingSelectedSpinnerAdapter(
                    prefShopAdapter, R.layout.contact_spinner_row_nothing_selected, getContext()
            ));
        }

        // Als toevoegen/aanpassen ingedrukt wordt...
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Definitie inputvelden
                EditText nameView = view.findViewById(R.id.editNameNewProduct);
                CheckBox toBuyView = view.findViewById(R.id.toBuyCheckBox);
                CheckBox cooledView = view.findViewById(R.id.cooledCheckBox);
                Spinner prefShopView = (Spinner) view.findViewById(R.id.spinnerPrefShop);
                if (action.equals(StaticData.ACTION_NEW)){
                    Product newProduct = new Product(viewModel.getBasedir(), false);
                    newProduct.setEntityName(String.valueOf(nameView.getText()));
                    newProduct.setToBuy(toBuyView.isChecked());
                    newProduct.setCooled(cooledView.isChecked());
                    // geselecteerde shop uit spinner halen
                    newProduct.setPreferredShopId(viewModel.determineShopBySpinnerSelection());
                    viewModel.getProductList().add(newProduct);
                    productToSave = newProduct;
                }else {
                    productToSave.setEntityName(String.valueOf(nameView.getText()));
                    productToSave.setToBuy(toBuyView.isChecked());
                    productToSave.setCooled(cooledView.isChecked());
                    productToSave.setPreferredShopId(viewModel.determineShopBySpinnerSelection());
                    viewModel.getProductList().set(indexToUpdate, productToSave);
                }
                viewModel.storeProducts();
                Intent replyIntent = new Intent(getContext(), A4ListActivity.class);
                replyIntent.putExtra(StaticData.EXTRA_INTENT_KEY_TYPE, SpecificData.LIST_TYPE_2);
                replyIntent.putExtra(StaticData.EXTRA_INTENT_KEY_FILE_BASE, viewModel.getBaseSwitch());
                startActivity(replyIntent);
            }
        });

        // Als je een shop wilt koppelen met product
        Button selShopbutton = view.findViewById(R.id.buttonAddShop);
        selShopbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Welke shop is geselecteerd in de unselected groep ?
                String toCoupleItem = recycUnSelShopsadapter.getSelectedItem();
                if (toCoupleItem != null){
                    Shop shopToCouple = viewModel.getShopByShopName(toCoupleItem);
                    // Shop koppelen met product
                    // combinatie maken en in entiteit steken
                    if (viewModel.getProductShopCombinIndex(productToSave.getEntityId(), shopToCouple.getEntityId()) == StaticData.ITEM_NOT_FOUND){
                        ProductInShop newProdinShop = new ProductInShop(productToSave.getEntityId(), shopToCouple.getEntityId());
                        viewModel.getProductInShopList().add(newProdinShop);
                        viewModel.storeProdInShop();
                    }
                    // Recyclerviews refreshen
                    recycSelShopsadapter.setReference(SpecificData.LIST_TYPE_2);
                    recycSelShopsadapter.setReusableList(viewModel.getShopNamesByProduct(productToSave));
                    recycUnSelShopsadapter.setReference(SpecificData.LIST_TYPE_2);
                    recycUnSelShopsadapter.setReusableList(viewModel.getUnselectedShopNamesByProduct(productToSave));
                    recycUnSelShopsadapter.clearPrevViewBackground();
                }else {
                    // TODO: er is geen shop geselecteerd, een boodschap laten zien
                }
            }
        });

        // Als je een shop wilt ontkoppelen van product
        Button unSelShopbutton = view.findViewById(R.id.buttonRemShop);
        unSelShopbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Welke shop is geselecteerd in de selected groep ?
                String toUnCoupleItem = recycSelShopsadapter.getSelectedItem();
                if (toUnCoupleItem != null){
                    Shop shopToUnCouple = viewModel.getShopByShopName(toUnCoupleItem);
                    // Shop ontkoppelen vn product
                    // Combinatie zoeken
                    int combination = viewModel.getProductShopCombinIndex(productToSave.getEntityId(), shopToUnCouple.getEntityId());
                    if (combination != StaticData.ITEM_NOT_FOUND){
                        // combinatie bestaat, ze wordt gedelete
                        viewModel.getProductInShopList().remove(combination);
                        viewModel.storeProdInShop();
                    }
                    // Recyclerviews refreshen
                    recycSelShopsadapter.setReference(SpecificData.LIST_TYPE_2);
                    recycSelShopsadapter.setReusableList(viewModel.getShopNamesByProduct(productToSave));
                    recycSelShopsadapter.clearPrevViewBackground();
                    recycUnSelShopsadapter.setReference(SpecificData.LIST_TYPE_2);
                    recycUnSelShopsadapter.setReusableList(viewModel.getUnselectedShopNamesByProduct(productToSave));
                }else {
                    // TODO: er is geen shop geselecteerd, een boodschap laten zien
                }
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