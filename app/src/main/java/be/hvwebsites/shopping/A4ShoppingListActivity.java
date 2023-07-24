package be.hvwebsites.shopping;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import be.hvwebsites.libraryandroid4.adapters.NothingSelectedSpinnerAdapter;
import be.hvwebsites.libraryandroid4.helpers.CheckboxHelper;
import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.helpers.ListItemHelper;
import be.hvwebsites.libraryandroid4.repositories.Cookie;
import be.hvwebsites.libraryandroid4.repositories.CookieRepository;
import be.hvwebsites.libraryandroid4.returninfo.ReturnInfo;
import be.hvwebsites.libraryandroid4.statics.StaticData;
import be.hvwebsites.shopping.adapters.CheckboxListAdapter;
import be.hvwebsites.shopping.constants.SpecificData;
import be.hvwebsites.shopping.entities.Product;
import be.hvwebsites.shopping.entities.Shop;
import be.hvwebsites.shopping.fragments.TextItemListFragment;
import be.hvwebsites.shopping.services.FileBaseService;
import be.hvwebsites.shopping.viewmodels.ShopEntitiesViewModel;

public class A4ShoppingListActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    // Device
    private final String deviceModel = Build.MODEL;
    private ShopEntitiesViewModel viewModel;
    private CookieRepository cookieRepository;
    private boolean smsOn = false;
    private List<Product> productsMatchingShopfilter = new ArrayList<>();
    private List<Product> prodinShopMatchingShopFilter = new ArrayList<>();
    private List<CheckboxHelper> checkboxList = new ArrayList<>();
    private Shop shopFilter;
    private CheckboxListAdapter cbListAdapter;
    private Switch switchV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a4_shopping_list);

        // Creer een filebase service (bevat file base en file base directory) obv device en package name
        FileBaseService fileBaseService = new FileBaseService(deviceModel, getPackageName());

        /** Data ophalen */
        // Get a viewmodel from the viewmodelproviders
        viewModel = new ViewModelProvider(this).get(ShopEntitiesViewModel.class);
        // Initialize viewmodel mt basis directory (data wordt opgehaald in viewmodel)
        ReturnInfo viewModelStatus = viewModel.initializeViewModel(fileBaseService.getFileBaseDir());
        if (viewModelStatus.getReturnCode() != 0) {
            Toast.makeText(A4ShoppingListActivity.this,
                    "Ophalen data is mislukt",
                    Toast.LENGTH_LONG).show();
        }

        /** Scherm voorbereidingen */
        // ShopFilter Spinner
        Spinner shopFilterSpinner = findViewById(R.id.spinnerShopFilter);

        // ShopfilterAdapter obv ListItemHelper
        ArrayAdapter<ListItemHelper> shopItemAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item);
        shopItemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Adapter vullen met shops als listitemhelpers
        shopItemAdapter.addAll(viewModel.getItemsFromList(viewModel.getShopList()));
        shopItemAdapter.add(new ListItemHelper(SpecificData.NO_FILTER,
                "",
                new IDNumber(SpecificData.NO_FILTER_INT)));

        // Enkel aangeklikte artikels ?
        switchV = findViewById(R.id.switchChecked);

        // Zet switch default aan
        switchV.setChecked(true);

        // Detecteer verandering
        switchV.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                switchV.setChecked(isChecked);
                composeCheckboxList();
                cbListAdapter.setCheckboxList(checkboxList);
                boolean debug = true;
            }
        });

        // Is er al een ShopFilter in de cookie repo ?
        cookieRepository = new CookieRepository(fileBaseService.getFileBaseDir());
        String shopFilterString = cookieRepository.getCookieValueFromLabel(SpecificData.SHOP_FILTER);

        if ((shopFilterString.equals(SpecificData.NO_FILTER))
                || (viewModel.getShopByShopName(shopFilterString) != null)
                || (shopFilterString.equals(String.valueOf(SpecificData.NO_FILTER_INT)))
                || (shopFilterString.equals(String.valueOf(CookieRepository.COOKIE_NOT_FOUND)))
                || (viewModel.getShopByID(new IDNumber(Integer.parseInt(shopFilterString))) == null)) {
            // Geen geldige shopfilter ! ==> No_FILTER
            shopFilter = null;
            // wegschrijven als cookie
            cookieRepository.registerCookie(SpecificData.SHOP_FILTER, String.valueOf(SpecificData.NO_FILTER_INT));

            // nothing selected adapter kiezen
            shopFilterSpinner.setAdapter(new NothingSelectedSpinnerAdapter(
                    shopItemAdapter, R.layout.contact_spinner_row_nothing_selected, this
            ));

            // Alle producten ophalen ongefilterd en de checkboxlist steken
            composeCheckboxList();
        } else {
            // Er is een geldige shopfilterID in cookie
            // bepaal shopfilter
            shopFilter = viewModel.getShopByID(new IDNumber(Integer.parseInt(shopFilterString)));
            // Zet op scherm of winkel open is
            TextView shopOpenV = findViewById(R.id.labelShListOpenHours);
            shopOpenV.setText(shopFilter.isOpenString());
            shopFilterString = shopFilter.getEntityName();

            // Vul checkboxlist mt produkten gefilterd obv shopfilter
            composeCheckboxList();
            // spinner met selectie gebruiken
            shopFilterSpinner.setAdapter(shopItemAdapter);
            // animate parameter moet false staan om het onnodig afvuren vd spinner tegen te gaan
            if (shopFilterString.equals(SpecificData.NO_FILTER)){
                // TODO: mag weg want Shopfilterstring zou niet NO_FILTER mogen bevatten op dit moment
                int positionAA = shopItemAdapter.getCount()-1;
                shopFilterSpinner.setSelection(positionAA, false);
            }else {
                shopFilterSpinner.setSelection(viewModel.getIndexById(viewModel.getShopList(),
                        shopFilter.getEntityId()), false);
            }
        }

        // selection listener activeren, moet gebueren nadat de adapter gekoppeld is aan de spinner !!
        shopFilterSpinner.setOnItemSelectedListener(this);

        // Recyclerview definieren
        RecyclerView recyclerView = findViewById(R.id.recyclerviewShoppingListProducts);
        // ChckbxListAdapter kan vervangen worden door CheckBoxListAdapter !!
        cbListAdapter = new CheckboxListAdapter(this);
        recyclerView.setAdapter(cbListAdapter);
        LinearLayoutManager cbLineairLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(cbLineairLayoutManager);
        // Indien CheckBoxListAdapter gekozen wordt, dan vlgnd statement geactiveerd worden !!
        cbListAdapter.setActivityMaster(SpecificData.ACTIVITY_A4SHOPPINGLIST);

        // Invullen adapter vd recyclerview met checkboxlist
        cbListAdapter.setReference(SpecificData.LIST_TYPE_2);
        cbListAdapter.setCheckboxList(checkboxList);
        setTitle(SpecificData.TITLE_SHOPPING_LIST);

        // check SMS permission indien SMS geactiveerd
        if (cookieRepository.getCookieValueFromLabel(StaticData.SMS_LABEL).equals(StaticData.SMS_VALUE_ON)){
            smsOn = true;
            ActivityCompat.requestPermissions(this ,new String[] { Manifest.permission.SEND_SMS}, 1);
        }else {
            smsOn = false;
        }

        // Als er geclicked is op een checkbox, wordt dat hier gecapteerd ?
        cbListAdapter.setOnItemClickListener(new CheckboxListAdapter.ClickListener() {
            @Override
            public void onItemClicked(int position, View v, boolean checked) {
                // via het itemID vd checkboxhelper het juiste produkt bepalen en de toBuy zetten
                Product clickedProduct = viewModel.getProductByID(checkboxList.get(position).getItemID());
                clickedProduct.setToBuy(checked);
                viewModel.storeProducts();

                // Checkboxlist terug bepalen
                if (!checked && switchV.isChecked()){
                    // Indien enkel benodigde artikelen getoond worden en artikel wordt niet meer
                    // nodig dan wordt het artikel uit de checkboxlist verwijderd
                    checkboxList.remove(position);
                }else if (!switchV.isChecked()){
                    // Indien alle artikels mogen getoond worden, wordt de checkboxlist terug samengesteld
                    composeCheckboxList();
                }
                cbListAdapter.setReference(SpecificData.LIST_TYPE_2);
                cbListAdapter.setCheckboxList(checkboxList);

                // Voorbereiden SMS msg indien sms on
                if (smsOn){
                    String smsMsg = clickedProduct.getSMSLine();
                    String smsReceiver = StaticData.SMS_RECEIVER_DEFAULT;
                    //Getting intent and PendingIntent instance
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent,0);
                    //Get the SmsManager instance and call the sendTextMessage method to send message
                    SmsManager sms = SmsManager.getDefault();
                    sms.sendTextMessage(smsReceiver, null, smsMsg, pi,null);

                    Toast.makeText(getApplicationContext(), "Message Sent!!",
                            Toast.LENGTH_LONG).show();
                }
                boolean debug = true;
            }
        });
        // Swippen toelaten om item tijdelijk uit lijst te halen
        // om te kunnen swipen in de recyclerview ; swippen == tijdelijk deleten
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
                        Toast.makeText(A4ShoppingListActivity.this,
                                "Item wordt tijdelijk verwijderd... ",
                                Toast.LENGTH_LONG).show();
                        // Refresh recyclerview
                        checkboxList.remove(position);
                        cbListAdapter.setReference(SpecificData.LIST_TYPE_2);
                        cbListAdapter.setCheckboxList(checkboxList);
                    }
                });
        helper.attachToRecyclerView(recyclerView);
    }

    // Als er een shop geselecteerd is in de spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getItemAtPosition(position) != null){
            Toast.makeText(A4ShoppingListActivity.this,
                    "Hersamenstellen artikels ...",
                    Toast.LENGTH_LONG).show();
            // Bepalen wat geselecteerd is
            ListItemHelper selecShop = (ListItemHelper) parent.getItemAtPosition(position);
            if (selecShop.getItemID().getId() == SpecificData.NO_FILTER_INT){
                // Alle artikels
                shopFilter = null;
                // Bewaren dat er geen shopfilter is als cookie
                cookieRepository.addCookie(new Cookie(SpecificData.SHOP_FILTER, String.valueOf(SpecificData.NO_FILTER_INT)));
            }else {
                // Bepaal Shop om te filteren ==> nieuwe shopfilter
                shopFilter = viewModel.getShopByID(selecShop.getItemID());
                // Shopfilter bewaren als Cookie ; de shopfilterId moet bewaard worden
                cookieRepository.addCookie(new Cookie(SpecificData.SHOP_FILTER, String.valueOf(shopFilter.getEntityId().getId())));
            }

            // Checkboxlist terug herbepalen
            composeCheckboxList();
            cbListAdapter.setCheckboxList(checkboxList);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void composeCheckboxList(){
        // stel de checkboxlist samen obv de shopfilter
        // Clearen vn Lists
        prodinShopMatchingShopFilter.clear();
        productsMatchingShopfilter.clear();
        checkboxList.clear();
        // selectief producten ophalen
        // eerst de producten met de shopfilter als prefShop
        if (shopFilter == null){
            // Als er niet moet gefilterd worden
            prodinShopMatchingShopFilter.addAll(viewModel.getProductList());
        }else {
            productsMatchingShopfilter.addAll(viewModel.getProductsByPrefShop(shopFilter));
            // dan de producten met de shopfilter als prodinshop combinatie
            prodinShopMatchingShopFilter.addAll(viewModel.getProductsByShop(shopFilter));
        }

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
            // Als er geen produkten voldoen aan de filter, wordt enkel een toast getoond
            Toast.makeText(A4ShoppingListActivity.this,
                    "Geen artikels gevonden !",
                    Toast.LENGTH_LONG).show();
        }

        // Sorteren op cooling van achter zetten
        CheckboxHelper temphelper = new CheckboxHelper();
        for (int i = checkboxList.size() ; i > 0; i--) {
            for (int j = 1; j < i ; j++) {
                if (((checkboxList.get(j - 1).getStyle().equals(SpecificData.STYLE_COOLED)) ||
                        (checkboxList.get(j - 1).getStyle().equals(SpecificData.STYLE_COOLED_BOLD))) &&
                                ((!checkboxList.get(j).getStyle().equals(SpecificData.STYLE_COOLED)) &&
                                        (!checkboxList.get(j).getStyle().equals(SpecificData.STYLE_COOLED_BOLD)))){
                    temphelper.setCBHelper(checkboxList.get(j));
                    checkboxList.get(j).setCBHelper(checkboxList.get(j-1));
                    checkboxList.get(j-1).setCBHelper(temphelper);
                }
            }
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