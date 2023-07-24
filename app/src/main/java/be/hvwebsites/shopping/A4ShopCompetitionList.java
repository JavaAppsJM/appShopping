package be.hvwebsites.shopping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.helpers.ListItemHelper;
import be.hvwebsites.libraryandroid4.repositories.CookieRepository;
import be.hvwebsites.libraryandroid4.returninfo.ReturnInfo;
import be.hvwebsites.libraryandroid4.statics.StaticData;
import be.hvwebsites.shopping.adapters.SmartTextItemListAdapter;
import be.hvwebsites.shopping.constants.SpecificData;
import be.hvwebsites.shopping.entities.Product;
import be.hvwebsites.shopping.entities.ProductInShop;
import be.hvwebsites.shopping.entities.Shop;
import be.hvwebsites.shopping.entities.SuperCombination;
import be.hvwebsites.shopping.helpers.StringIntCombin;
import be.hvwebsites.shopping.services.FileBaseService;
import be.hvwebsites.shopping.viewmodels.ShopEntitiesViewModel;

public class A4ShopCompetitionList extends AppCompatActivity {
    // Device
    private final String deviceModel = Build.MODEL;
    private ShopEntitiesViewModel viewModel;
    private CookieRepository cookieRepository;
    private List<StringIntCombin> preCompetitionList = new ArrayList<>();
    private int totaalArtikelenToBuy = 0;
    private List<ListItemHelper> competitionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a4_shop_competition_list);

        // Creer een filebase service (bevat file base en file base directory) obv device en package name
        FileBaseService fileBaseService = new FileBaseService(deviceModel, getPackageName());

        /** Data ophalen */
        // Get a viewmodel from the viewmodelproviders
        viewModel = new ViewModelProvider(this).get(ShopEntitiesViewModel.class);
        // Initialize viewmodel mt basis directory (data wordt opgehaald in viewmodel)
        ReturnInfo viewModelStatus = viewModel.initializeViewModel(fileBaseService.getFileBaseDir());
        if (viewModelStatus.getReturnCode() != 0) {
            Toast.makeText(A4ShopCompetitionList.this,
                    "Ophalen data is mislukt",
                    Toast.LENGTH_LONG).show();
        }

        // Cookies initialiseren
        cookieRepository = new CookieRepository(fileBaseService.getFileBaseDir());

        // Recyclerview en adapter definieren
        RecyclerView recyclerView = findViewById(R.id.recyclShopCompetitionList);
        SmartTextItemListAdapter listAdapter = new SmartTextItemListAdapter(this);
        recyclerView.setAdapter(listAdapter);
        LinearLayoutManager cbLineairLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(cbLineairLayoutManager);
        // Adapter invullen
        // Activity doorgeven aan adapter
        listAdapter.setClient(SpecificData.ACTIVITY_A4SHOPCOMPETITION);
        listAdapter.setReference(SpecificData.LIST_TYPE_1);
        listAdapter.setDevice(deviceModel);
        // Invullen adapter vd recyclerview met competitionlist
        composeCompetitionList();
        listAdapter.setReusableList(competitionList);

        // Titel Activity invullen
        setTitle(SpecificData.TITLE_SHOPPING_LIST);

        // Als er geclicked is op een shop, wordt dat hier verder afgehandeld
        listAdapter.setOnItemClickListener(new SmartTextItemListAdapter.ClickListener() {
            @Override
            public void onItemClicked(int position, View v) {
                // Shop reconstrueren waarop geclicked is
                Shop clickedShop = viewModel.getShopByID(competitionList.get(position).getItemID());
                if (clickedShop == null){
                    // Shop niet gevonden als shopfilter in cookie steken vr A4Shoppinglist activity
                    cookieRepository.registerCookie(SpecificData.SHOP_FILTER, String.valueOf(SpecificData.NO_FILTER_INT));
                }else {
                    // Shop als shopfilter in cookie steken vr A4Shoppinglist activity
                    cookieRepository.registerCookie(SpecificData.SHOP_FILTER, String.valueOf(clickedShop.getEntityId().getId()));
                }

                // Ga naar A4ShoppingList
                Intent compIntent = new Intent(A4ShopCompetitionList.this, A4ShoppingListActivity.class);
                startActivity(compIntent);
            }
        });

    }

    private void composeCompetitionList(){
        competitionList.clear();
        preCompetitionList.clear();

        // Bepalen en tellen alle toBuy Artikelen
        for (int i = 0; i < viewModel.getProductList().size(); i++) {
            Product tempProduct = viewModel.getProductList().get(i);

            // Enkel benodigde artikelen komen in aanmerking
            if (tempProduct.isToBuy()){
                // Teller totaal aantal artikelen tobuy verhogen
                totaalArtikelenToBuy++;

                // preCompetitionlist opvullen, de shop kan al in de precompetitionlist zitten
                // vanuit een ander produkt !!!
                if (tempProduct.getPreferredShopId().getId() != StaticData.ITEM_NOT_FOUND){
                    // Enkel als het product een voorkeurwinkel heeft, wordt de voorkeurwinkel geteld
                    processShopInCompetition(tempProduct.getPreferredShopId().getId(), 1);
                }

                // Bepalen en tellen in welke andere winkels het product ook nog kan gekocht worden
                for (int j = 0; j < viewModel.getShopIdsForProductId(tempProduct.getEntityId().getId()).size(); j++) {
                    int shopId = viewModel.getShopIdsForProductId(tempProduct.getEntityId().getId()).get(j);
                    if (shopId != tempProduct.getPreferredShopId().getId()){
                        // Nog een winkel gevonden waar het produkt kan gekocht worden
                        processShopInCompetition(shopId, 2);
                    }
                }
            }
        }
        // precompetionlist sorteren vlgns aantal artikelen descending
        StringIntCombin sortHelper = new StringIntCombin();
        for (int i = preCompetitionList.size() ; i > 0 ; i--) {
            for (int j = 1; j < i ; j++) {
                if (preCompetitionList.get(j).getTeller2() > preCompetitionList.get(j-1).getTeller2()){
                    sortHelper.setCombin(preCompetitionList.get(j));
                    preCompetitionList.get(j).setCombin(preCompetitionList.get(j-1));
                    preCompetitionList.get(j-1).setCombin(sortHelper);
                }
            }
        }

        // precompetionlist omzetten naar competitionlist
        // Bepalen shopNameMaxLength afhankelijk vn device
        int shopNameMaxL = 21; // A3
        if (deviceModel.equals("GT-I9100")){
            shopNameMaxL = 18;
        }
        String shopOpenStyle = SpecificData.STYLE_DEFAULT;
        for (int i = 0; i < preCompetitionList.size(); i++) {
            // Bepaal shopCompetitor
            StringIntCombin shopCompetitor = preCompetitionList.get(i);
            // Bepalen hoeveel procent artikels vn totaal aantal artikels in deze winkel kunnen gekocht worden
            shopCompetitor.setProcent((shopCompetitor.getTeller2() * 100)/ totaalArtikelenToBuy);
            // Bepaal of shop open is
            if(viewModel.getShopByID(new IDNumber(shopCompetitor.getTextID())).isOpen()){
                shopOpenStyle = SpecificData.STYLE_SHOP_OPEN;
            }else {
                shopOpenStyle = SpecificData.STYLE_SHOP_CLOSED;
            }
            // Bepaal listItemHelper
            ListItemHelper shopItem = new ListItemHelper(
                    shopCompetitor.getFormattedString(shopNameMaxL)
                    , shopOpenStyle
                    , new IDNumber(shopCompetitor.getTextID()));
            // voeg shop in competitionlist
            competitionList.add(shopItem);
        }
    }

    private void processShopInCompetition(Integer inShopId, int whichTeller){
        boolean inCompetition = false;

        // Controleert of de winkel reeds in de precompetition zit en zo ja verhoog teller1
        for (int j = 0; j < preCompetitionList.size(); j++) {
            if (inShopId == preCompetitionList.get(j).getTextID()){
                inCompetition = true;
                // Verhoog totaal aantal artikels voor de shop in kwestie
                preCompetitionList.get(j).setTeller2(preCompetitionList.get(j).getTeller2()+1);
                if (whichTeller == 1){
                    // Verhoog aantal artikels preferred shop voor de shop in kwestie
                    preCompetitionList.get(j).setTeller1(preCompetitionList.get(j).getTeller1()+1);
                }
            }
        }
        if (!inCompetition){
            // Indien winkel nog niet in precompetition, toevoegen
            // Eerst kijken of winkel bestaat, enkel indien winkel bestaat toevoegen !
            Shop addShop = viewModel.getShopByID(new IDNumber(inShopId));
            if (addShop != null){
                StringIntCombin tempCompEntry = new StringIntCombin();
                tempCompEntry.setTextID(inShopId);
                tempCompEntry.setText(addShop.getEntityName());
                // Tellers invullen
                tempCompEntry.setTeller2(tempCompEntry.getTeller2()+1);
                if (whichTeller == 1){
                    tempCompEntry.setTeller1(tempCompEntry.getTeller1()+1);
                }
                preCompetitionList.add(tempCompEntry);
            }else {
                // Shop niet gevonden
                boolean debug = true;
            }
        }
    }
}