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

import be.hvwebsites.libraryandroid4.helpers.ListItemHelper;
import be.hvwebsites.libraryandroid4.repositories.CookieRepository;
import be.hvwebsites.libraryandroid4.returninfo.ReturnInfo;
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
//    private List<Product> productsMatchingShopfilter = new ArrayList<>();
//    private List<Product> prodinShopMatchingShopFilter = new ArrayList<>();
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

    private void composeCompetitionList3(){
        List<SuperCombination> prodShopCombins = new ArrayList<>();

        // Alle combinaties prodshops inladen
        for (int i = 0; i < viewModel.getProductInShopList().size(); i++) {
            ProductInShop tempProdShop = viewModel.getProductInShopList().get(i);
            SuperCombination tempCombin = new SuperCombination(
                    tempProdShop.getFirstID()
                    ,tempProdShop.getSecondID()
                    ," "
                    ," "
            );
            prodShopCombins.add(tempCombin);
        }

        // Combinaties uit produktlist toevoegen
        for (int i = 0; i < viewModel.getProductList().size(); i++) {
            if (viewModel.getProductList().get(i).isToBuy()){
                SuperCombination tempCombin = new SuperCombination(
                        viewModel.getProductList().get(i).getPreferredShopId()
                        ,viewModel.getProductList().get(i).getEntityId()
                        ," "
                        ," "
                );
                prodShopCombins.add(tempCombin);
            }
        }
    }

/*
    private void composeCompetitionList2(){
        competitionList.clear();

        // Bepalen per winkel hoeveel artikels deze winkel als prefered hebben
        for (int i = 0; i < viewModel.getShopList().size(); i++) {
            Shop tempShop = viewModel.getShopList().get(i);
            // Bepalen alle checked products met voorkeurwinkel
            for (int i = 0; i < viewModel.getProductList().size(); i++) {
                Product tempProduct = viewModel.getProductList().get(i);
                if ((tempProduct.getPreferredShopId().getId() == tempShop.getEntityId().getId())
                        && tempProduct.isToBuy()){
                    StringIntCombin combin = new StringIntCombin();
                    combin.setTextID(tempShop.getEntityId().getId());
                    combin.setText(tempShop.getEntityName());
                    combin.setTeller1((combin.getTeller1()+1));
                    SuperCombination tempCombinPref = new SuperCombination(
                            tempProduct.getEntityId()
                            ,tempProduct.getPreferredShopId()
                            ," "
                            ," "
                    );

                }
            }


        }

    }
*/

    private void composeCompetitionList(){
        List<SuperCombination> checkedProducts = new ArrayList<>();
        List<SuperCombination> productShopCombins = new ArrayList<>();
        List<StringIntCombin> tempCompetition = new ArrayList<>();

        competitionList.clear();

        // Initialize tempCompetition
        for (int i = 0; i < viewModel.getShopList().size(); i++) {
            Shop tempShop = viewModel.getShopList().get(i);
            StringIntCombin tempCombin = new StringIntCombin();
            tempCombin.setTextID(tempShop.getEntityId().getId());
            tempCombin.setText(tempShop.getEntityName());
            tempCombin.setTeller1(0);
            tempCombin.setTeller2(0);
            tempCompetition.add(tempCombin);
        }

        // Bepalen alle checked products met voorkeurwinkel
        for (int i = 0; i < viewModel.getProductList().size(); i++) {
            Product tempProduct = viewModel.getProductList().get(i);
            if (tempProduct.isToBuy()){
                // Verhoog teller in tempCompetition
                for (int j = 0; j < tempCompetition.size(); j++) {
                    if (tempCompetition.get(i).getTextID() == tempProduct.getPreferredShopId().getId()){
                        tempCompetition.get(i).setTeller1(tempCompetition.get(i).getTeller1()+1);
                        // Bewaar combinatie
                        SuperCombination tempCombinPref = new SuperCombination(
                                tempProduct.getEntityId()
                                ,tempProduct.getPreferredShopId()
                                ," "
                                ," "
                        );
                        checkedProducts.add(tempCombinPref);
                    }
                }
            }
        }

        // Bepalen alle produktshopcombins
        for (int i = 0; i < viewModel.getProductInShopList().size(); i++) {
            ProductInShop tempProdShop = viewModel.getProductInShopList().get(i);
            if (viewModel.getProductByID(tempProdShop.getSecondID()).isToBuy()){
                SuperCombination tempCombin = new SuperCombination(
                        tempProdShop.getSecondID()
                        ,tempProdShop.getFirstID()
                        ," "
                        ," "
                );
                productShopCombins.add(tempCombin);
            }
        }


    }


}