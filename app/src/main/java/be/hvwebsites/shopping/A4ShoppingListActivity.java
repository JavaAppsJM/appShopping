package be.hvwebsites.shopping;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

import be.hvwebsites.libraryandroid4.adapters.NothingSelectedSpinnerAdapter;
import be.hvwebsites.libraryandroid4.helpers.CheckboxHelper;
import be.hvwebsites.libraryandroid4.repositories.Cookie;
import be.hvwebsites.libraryandroid4.repositories.CookieRepository;
import be.hvwebsites.libraryandroid4.returninfo.ReturnInfo;
import be.hvwebsites.libraryandroid4.statics.StaticData;
import be.hvwebsites.shopping.adapters.ChckbxListAdapter;
import be.hvwebsites.shopping.constants.SpecificData;
import be.hvwebsites.shopping.entities.Product;
import be.hvwebsites.shopping.entities.Shop;
import be.hvwebsites.shopping.viewmodels.ShopEntitiesViewModel;

public class A4ShoppingListActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ShopEntitiesViewModel viewModel;
    private CookieRepository cookieRepository;
    private List<Product> productsMatchingShopfilter = new ArrayList<>();
    private List<Product> prodinShopMatchingShopFilter = new ArrayList<>();
    private List<CheckboxHelper> checkboxList = new ArrayList<>();
    private String shopFilterString = "";
    private Shop shopFilter;
    private ChckbxListAdapter cbListAdapter;
    private ArrayAdapter<String> shopFilterAdapter;
    private Switch switchV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a4_shopping_list);

        // Intent definieren
        Intent sListIntent = getIntent();

        /** Data ophalen */
        // Get a viewmodel from the viewmodelproviders
        viewModel = new ViewModelProvider(this).get(ShopEntitiesViewModel.class);
        // Basis directory definitie
        String baseDir = "";
        String baseSwitch = sListIntent.getStringExtra(StaticData.EXTRA_INTENT_KEY_SELECTION);
        if (baseSwitch == null){
            baseSwitch = SpecificData.BASE_DEFAULT;
        }
        if (baseSwitch.equals(SpecificData.BASE_INTERNAL)){
            baseDir = getBaseContext().getFilesDir().getAbsolutePath();
        }else {
            baseDir = getBaseContext().getExternalFilesDir(null).getAbsolutePath();
        }
        // Initialize viewmodel mt basis directory (data wordt opgehaald in viewmodel)
        ReturnInfo viewModelStatus = viewModel.initializeViewModel(baseDir);
        if (viewModelStatus.getReturnCode() == 0) {
            // Files gelezen
        } else if (viewModelStatus.getReturnCode() == 100) {
            Toast.makeText(A4ShoppingListActivity.this,
                    viewModelStatus.getReturnMessage(),
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(A4ShoppingListActivity.this,
                    "Ophalen data is mislukt",
                    Toast.LENGTH_LONG).show();
        }

        /** Scherm voorbereidingen */
        // ShopFilter Spinner
        Spinner shopFilterSpinner = (Spinner) findViewById(R.id.spinnerShopFilter);
        // Adapter voor de ShopFilter Spinner
        shopFilterAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item);
        shopFilterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Adapter vullen met shops
        shopFilterAdapter.addAll(viewModel.getShopNameList());
        shopFilterAdapter.add(SpecificData.NO_FILTER);

        // TODO: Enkel aangeklikte artikels ?
        switchV = findViewById(R.id.switchChecked);
        // Zet switch default af
        switchV.setChecked(false);
        // Detecteer verandering
        switchV.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    switchV.setChecked(true);
                }else {
                    switchV.setChecked(false);
                }
                composeCheckboxList();
                cbListAdapter.setCheckboxList(checkboxList);
                boolean debug = true;
            }
        });

        // Is er al een ShopFilter in de cookie repo ?
        cookieRepository = new CookieRepository(baseDir);
        shopFilterString = cookieRepository.getCookieValueFromLabel(SpecificData.SHOP_FILTER);
        if (shopFilterString.equals(String.valueOf(CookieRepository.COOKIE_NOT_FOUND))){
            // er is nog geen shopfilter
            // nothing selected spinner definieren
            shopFilterSpinner.setAdapter(new NothingSelectedSpinnerAdapter(
                    shopFilterAdapter, R.layout.contact_spinner_row_nothing_selected, this
            ));
            // Alle producten ophalen ongefilterd en de checkboxlist steken
            checkboxList.clear();
            checkboxList.addAll(viewModel.convertProductsToCheckboxs(
                    viewModel.getProductList(),
                    SpecificData.PRODUCT_DISPLAY_SMALL,
                    switchV.isChecked()));
        }else {
            // er is een shopfilter
            // bepaal shop
            if (!shopFilterString.equals(SpecificData.NO_FILTER)){
                shopFilter = viewModel.getShopByShopName(shopFilterString);
            }
            // Vul checkboxlist mt produkten gefilterd obv shopfilter
            checkboxList.clear();
            composeCheckboxList();
            // spinner met selectie gebruiken
            shopFilterSpinner.setAdapter(shopFilterAdapter);
            // animate parameter moet false staan om het onnodig afvuren vd spinner tegen te gaan
            if (shopFilterString.equals(SpecificData.NO_FILTER)){
                int positionAA = shopFilterAdapter.getCount()-1;
                shopFilterSpinner.setSelection(positionAA, false);
            }else {
                shopFilterSpinner.setSelection(viewModel.getShopIndexById(shopFilter.getEntityId()), false);
            }
        }
        // selection listener activeren, moet gebueren nadat de adapter gekoppeld is aan de spinner !!
        shopFilterSpinner.setOnItemSelectedListener(this);

        // Recyclerview definieren
        RecyclerView recyclerView = findViewById(R.id.recyclerviewShoppingListProducts);
        cbListAdapter = new ChckbxListAdapter(this);
        recyclerView.setAdapter(cbListAdapter);
        LinearLayoutManager cbLineairLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(cbLineairLayoutManager);

        // Invullen adapter vd recyclerview met checkboxlist
        cbListAdapter.setReference(SpecificData.LIST_TYPE_2);
        cbListAdapter.setCheckboxList(checkboxList);
        setTitle(SpecificData.TITLE_SHOPPING_LIST);

        // Als er geclicked is op een checkbox, wordt dat hier gecapteerd ?
        cbListAdapter.setOnItemClickListener(new ChckbxListAdapter.ClickListener() {
            @Override
            public void onItemClicked(int position, View v, boolean checked) {
                // via het itemID vd checkboxhelper het juiste produkt bepalen en de toBuy zetten
                viewModel.getProductByID(checkboxList.get(position).getItemID()).setToBuy(checked);
                viewModel.storeProducts();
                composeCheckboxList();
                cbListAdapter.setCheckboxList(checkboxList);
                boolean debug = true;
            }
        });
    }

    // Als er een shop geselecteerd is in de spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getItemAtPosition(position) != null){
            Toast.makeText(A4ShoppingListActivity.this,
                    "Hersamenstellen artikels ...",
                    Toast.LENGTH_LONG).show();
            shopFilterString = String.valueOf(parent.getItemAtPosition(position));
            // Shopfilter bewaren als Cookie
            cookieRepository.addCookie(new Cookie(SpecificData.SHOP_FILTER, shopFilterString));
            // bepaal shop voor herbepalen checkboxlist
            if (!shopFilterString.equals(SpecificData.NO_FILTER)){
                shopFilter = viewModel.getShopByShopName(shopFilterString);
            }
            // spinner refreshen hoeft niet
//            parent.setAdapter(shopFilterAdapter);
//            parent.setSelection(viewModel.getShopIndexById(shopFilter.getEntityId()));
            // Refresh recyclerview met aangepaste checkboxlist
            composeCheckboxList();
            cbListAdapter.setCheckboxList(checkboxList);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void composeCheckboxList(){
        // stel de checkboxlist samen obv de shopfilter
        // selectief producten ophalen
        // eerst de producten met de shopfilter als prefShop
        if (shopFilterString.equals(SpecificData.NO_FILTER)){
            prodinShopMatchingShopFilter = viewModel.getProductList();
        }else {
            productsMatchingShopfilter = viewModel.getProductsByPrefShop(shopFilter);
            // dan de producten met de shopfilter als prodinshop combinatie
            prodinShopMatchingShopFilter = viewModel.getProductsByShop(shopFilter);
        }
        checkboxList.clear();

        // eerst producten met pref shop omzetten nr checkboxen
        if (productsMatchingShopfilter.size() > 0){
            checkboxList.addAll(viewModel.convertProductsToCheckboxs(
                    productsMatchingShopfilter,
                    SpecificData.PRODUCT_DISPLAY_SMALL_BOLD,
                    switchV.isChecked()));
        }
        if (productsMatchingShopfilter.size() > 0 && prodinShopMatchingShopFilter.size() > 0){
            // dubbele producten uitfilteren in prodinShopMatchingShopFilter
            removeDoubleProducts();
        }
        // dan andere producten
        if (prodinShopMatchingShopFilter.size() > 0){
            checkboxList.addAll(viewModel.convertProductsToCheckboxs(
                    prodinShopMatchingShopFilter,
                    SpecificData.PRODUCT_DISPLAY_SMALL,
                    switchV.isChecked()));
        }
        if (productsMatchingShopfilter.size() == 0 && prodinShopMatchingShopFilter.size() == 0){
            // TODO: Als er geen produkten voldoen aan de filter, worden geen produkten getoond ?
            Toast.makeText(A4ShoppingListActivity.this,
                    "Geen artikels gevonden !",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void removeDoubleProducts(){
        int i = 0;
        do {
            if (prodExistsByPrefShop(prodinShopMatchingShopFilter.get(i))){
                prodinShopMatchingShopFilter.remove(i);
            }else {
                i++;
            }
        } while (i < prodinShopMatchingShopFilter.size());
    }

    private boolean prodExistsByPrefShop(Product inProduct){
        for (int i = 0; i < productsMatchingShopfilter.size(); i++) {
            if (productsMatchingShopfilter.get(i).getEntityId().getId() == inProduct.getEntityId().getId()){
                return true;
            }
        }
        return false;
    }
}