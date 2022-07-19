package be.hvwebsites.shopping.fragments;

import android.content.Intent;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import be.hvwebsites.libraryandroid4.adapters.NothingSelectedSpinnerAdapter;
import be.hvwebsites.libraryandroid4.statics.StaticData;
import be.hvwebsites.shopping.A4ListActivity;
import be.hvwebsites.shopping.adapters.SmallItemListAdapter;
import be.hvwebsites.shopping.constants.SpecificData;
import be.hvwebsites.shopping.entities.Product;
import be.hvwebsites.shopping.entities.ProductInShop;
import be.hvwebsites.shopping.entities.Shop;
import be.hvwebsites.shopping.viewmodels.ShopEntitiesViewModel;
import be.hvwebsites.shopping.R;

public class ProductFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private ShopEntitiesViewModel viewModel;
    private String action;
    private int indexToUpdate = 0;
    // Product
    private Product productToSave = new Product();

    // Toegevoegd vanuit android tutorial
    public ProductFragment(){
        super(R.layout.fragment_product);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // button
        Button saveButton = view.findViewById(R.id.addButtonProduct);
        saveButton.setText(SpecificData.BUTTON_TOEVOEGEN);

        // Via het viewmodel uit de activity kan je over de data beschikken !
        viewModel = new ViewModelProvider(requireActivity()).get(ShopEntitiesViewModel.class);

        // Preferred Shop Spinner
        // findviewbyid werkt hier wel !
        // fragmentview moet eerst bestaan voor dat je zaken kan adreseren
        Spinner prefShopSpinner = (Spinner) view.findViewById(R.id.spinnerPrefShop);
        ArrayAdapter<String> prefShopAdapter = new ArrayAdapter(this.getContext(),
                android.R.layout.simple_spinner_item);
        prefShopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Spinner vullen met shops
        prefShopAdapter.addAll(viewModel.getShopNameList());
        // selection listener activeren
        prefShopSpinner.setOnItemSelectedListener(this);

        // Recyclerviews en adapters definieren
        RecyclerView recycViewSelShops = view.findViewById(R.id.recyclerviewSelShops);
        final SmallItemListAdapter recycSelShopsadapter = new SmallItemListAdapter(this.getContext());
        recycViewSelShops.setAdapter(recycSelShopsadapter);
        recycViewSelShops.setLayoutManager(new LinearLayoutManager(this.getContext()));
        RecyclerView recycViewUnSelShops = view.findViewById(R.id.recyclerviewUnSelShops);
        final SmallItemListAdapter recycUnSelShopsadapter = new SmallItemListAdapter(this.getContext());
        recycViewUnSelShops.setAdapter(recycUnSelShopsadapter);
        recycViewUnSelShops.setLayoutManager(new LinearLayoutManager(this.getContext()));

        // Wat zijn de argumenten die werden meegegeven
        action = requireArguments().getString(StaticData.EXTRA_INTENT_KEY_ACTION);
        if (action.equals(StaticData.ACTION_UPDATE)){
            indexToUpdate = requireArguments().getInt(StaticData.EXTRA_INTENT_KEY_INDEX);
            // Bepaal geselecteerd product bepalen obv meegegeven index
            Product productToUpdate = viewModel.getProductList().get(indexToUpdate);
            // TODO: Is dit nodig ?
            productToSave.setProduct(productToUpdate);
            // Vul Scherm in met gegevens
            EditText nameView = view.findViewById(R.id.editNameNewProduct);
            nameView.setText(productToUpdate.getEntityName());
            // Is er al een preferred shop voor het product in kwestie ?
            int prefShopIndex = viewModel.getShopIndexById(productToUpdate.getPreferredShopId());
//            String prefShopName = "nog geen voorkeur winkel geregistreerd !";
            if (prefShopIndex != StaticData.ITEM_NOT_FOUND) {
                // er is een preferred shop
//                Shop prefShop = new Shop();
//                prefShop = viewModel.getShopByID(productToUpdate.getPreferredShopId());
//                prefShopName = viewModel.getShopList()
//                        .get(viewModel.getShopIndexById(productToUpdate.getPreferredShopId()))
//                        .getEntityName();
                prefShopSpinner.setAdapter(prefShopAdapter);
                prefShopSpinner.setSelection(prefShopIndex);
//                prefShopSpinner.setPrompt(prefShopName);
            }else {
                prefShopSpinner.setAdapter(new NothingSelectedSpinnerAdapter(
                        prefShopAdapter, R.layout.contact_spinner_row_nothing_selected, getContext()
                ));
            }
            // Checkboxen invullen
            CheckBox toBuyView = view.findViewById(R.id.toBuyCheckBox);
            toBuyView.setChecked(productToUpdate.isToBuy());
            CheckBox wantedView = view.findViewById(R.id.wantedCheckbox);
            wantedView.setChecked(productToUpdate.isWanted());
            // gekoppelde en niet gekoppelde shops in recyclerviews steken
            // Recyclerviews invullen
            recycSelShopsadapter.setReference(SpecificData.LIST_TYPE_1);
            recycSelShopsadapter.setReusableList(viewModel.getShopNamesByProduct(productToUpdate));
            recycUnSelShopsadapter.setReference(SpecificData.LIST_TYPE_1);
            recycUnSelShopsadapter.setReusableList(viewModel.getUnselectedShopNamesByProduct(productToUpdate));
            // button tekst
            saveButton.setText(SpecificData.BUTTON_AANPASSEN);
        }else {
            // Bij new geen wanted en geen winkels koppelen
            CheckBox wantedView = view.findViewById(R.id.wantedCheckbox);
            wantedView.setVisibility(View.INVISIBLE);
            TextView labelWinkels = view.findViewById(R.id.labelWinkels);
            labelWinkels.setVisibility(View.INVISIBLE);
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
                Spinner prefShopView = (Spinner) view.findViewById(R.id.spinnerPrefShop);
                if (action.equals(StaticData.ACTION_NEW)){
                    Product newProduct = new Product(viewModel.getBasedir(), false);
                    newProduct.setEntityName(String.valueOf(nameView.getText()));
                    newProduct.setToBuy(toBuyView.isChecked());
                    // geselecteerde shop uit spinner halen
                    newProduct.setPreferredShopId(viewModel.determineShopBySpinnerSelection());
                    viewModel.getProductList().add(newProduct);
                    productToSave = newProduct;
                }else {
                    productToSave.setEntityName(String.valueOf(nameView.getText()));
                    productToSave.setToBuy(toBuyView.isChecked());
                    productToSave.setPreferredShopId(viewModel.determineShopBySpinnerSelection());
                    viewModel.getProductList().set(indexToUpdate, productToSave);
                }
                viewModel.storeProducts();
                Intent replyIntent = new Intent(getContext(), A4ListActivity.class);
                replyIntent.putExtra(StaticData.EXTRA_INTENT_KEY_TYPE, SpecificData.LIST_TYPE_2);
                replyIntent.putExtra(StaticData.EXTRA_INTENT_KEY_SELECTION, viewModel.getBaseSwitch());
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

    // als iets geselecteerd werd in de spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selection = String.valueOf(parent.getItemAtPosition(position));
        viewModel.setSpinnerSelection(selection);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}