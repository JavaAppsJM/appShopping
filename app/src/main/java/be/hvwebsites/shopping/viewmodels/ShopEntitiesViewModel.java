package be.hvwebsites.shopping.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import be.hvwebsites.libraryandroid4.helpers.CheckboxHelper;
import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.helpers.ListItemHelper;
import be.hvwebsites.libraryandroid4.repositories.Cookie;
import be.hvwebsites.libraryandroid4.repositories.CookieRepository;
import be.hvwebsites.libraryandroid4.repositories.FlexiRepository;
import be.hvwebsites.libraryandroid4.returninfo.ReturnInfo;
import be.hvwebsites.libraryandroid4.statics.StaticData;
import be.hvwebsites.shopping.constants.SpecificData;
import be.hvwebsites.shopping.entities.Meal;
import be.hvwebsites.shopping.entities.MealInMeal;
import be.hvwebsites.shopping.entities.Product;
import be.hvwebsites.shopping.entities.ProductInMeal;
import be.hvwebsites.shopping.entities.ProductInShop;
import be.hvwebsites.shopping.entities.Shop;
import be.hvwebsites.shopping.entities.ShoppingEntity;
import be.hvwebsites.shopping.entities.SuperCombination;

public class ShopEntitiesViewModel extends AndroidViewModel {
    private FlexiRepository repository;
    private String basedir;
    private String baseSwitch;
    // File declaraties
    File shopFile;
    File productFile;
    File productInShopFile;
    File mealFile;
    File productInMealFile;
    File mealInMealFile;
    // File names constants
    public static final String SHOP_FILE = "shop.txt";
    public static final String PRODUCT_FILE = "product.txt";
    public static final String PRODUCTSHOP_FILE = "productshop.txt";
    public static final String MEAL_FILE = "meal.txt";
    public static final String PRODUCTMEAL_FILE = "productmeal.txt";
    public static final String MEALMEAL_FILE = "mealmeal.txt";
    // Lijst om data in te zetten
    private List<Shop> shopList = new ArrayList<>();
    private List<Product> productList = new ArrayList<>();
    private List<ProductInShop> productInShopList = new ArrayList<>();
    private List<Meal> mealList = new ArrayList<>();
    private List<ProductInMeal> productInMealList = new ArrayList<>();
    private List<MealInMeal> mealInMealList = new ArrayList<>();
    // Lijst om bluetooth data in te zetten, mag verwijderd worden als
    // BlueToothViewModel geactiveerd wordt
    private List<Shop> shopListBt = new ArrayList<>();
    private List<Product> productListBt = new ArrayList<>();
    private List<ProductInShop> productInShopListBt = new ArrayList<>();
    private List<Meal> mealListBt = new ArrayList<>();
    private List<ProductInMeal> productInMealListBt = new ArrayList<>();
    private List<MealInMeal> mealInMealListBt = new ArrayList<>();
    // SpinnerSelection wordt gebruikt in ProductFragment voor het bewaren vd preferredShop
    // vh nieuwe/gewijzigde product !!
    private String spinnerSelection = "";

    public ShopEntitiesViewModel(@NonNull Application application) {
        super(application);
    }

    public ReturnInfo initializeViewModel(String basedir){
        ReturnInfo returnInfo = new ReturnInfo(0);
        this.basedir = basedir;
        // Filedefinities
        shopFile = new File(basedir, SHOP_FILE);
        productFile = new File(basedir, PRODUCT_FILE);
        productInShopFile = new File(basedir, PRODUCTSHOP_FILE);
        mealFile = new File(basedir, MEAL_FILE);
        productInMealFile = new File(basedir, PRODUCTMEAL_FILE);
        mealInMealFile = new File(basedir, MEALMEAL_FILE);

        // Data ophalen
        // Ophalen Winkels
        repository = new FlexiRepository(shopFile);
        shopList.addAll(getShopsFromDataList(repository.getDataList()));

        // Ophalen artikels
        repository = new FlexiRepository(productFile);
        productList.addAll(getProductsFromDataList(repository.getDataList()));

        // Ophalen artikel in winkel combinaties
        repository = new FlexiRepository(productInShopFile);
        productInShopList.addAll(getProdInShopsFromDataList(repository.getDataList()));
        // eenmalig om prodinshop op te kiusen
        // cleanProdInshop();

        // Ophalen gerechten
        repository = new FlexiRepository(mealFile);
        mealList.addAll(getMealsFromDataList(repository.getDataList()));

        // Ophalen artikel in gerecht combinaties
        repository = new FlexiRepository(productInMealFile);
        productInMealList.addAll(getProdInMealFromDataList(repository.getDataList()));

        // Ophalen gerecht in gerecht combinaties
        repository = new FlexiRepository(mealInMealFile);
        mealInMealList.addAll(getMealInMealFromDataList(repository.getDataList()));
        return returnInfo;
    }

    // Mag verwijderd worden als in BluetoothCom, method acceptData geactiveerd wordt
    public void forceBtData(String basedir){
        // Bluetooth data accepteren
        if (shopListBt.size() > 0){
            // Er zijn shops te accepteren
            shopList.clear();
            shopList.addAll(shopListBt);
        }
        if (productListBt.size() > 0){
            // Er zijn producten te accepteren
            productList.clear();
            productList.addAll(productListBt);
        }
        if (productInShopListBt.size() > 0){
            // Er zijn prodinshops te accepteren
            productInShopList.clear();
            productInShopList.addAll(productInShopListBt);
        }
        if (mealListBt.size() > 0){
            // Er zijn gerechten te accepteren
            mealList.clear();
            mealList.addAll(mealListBt);
        }
        if (productInMealList.size() > 0){
            // Er zijn prodinmeals te accepteren
            productInMealList.clear();
            productInMealList.addAll(productInMealListBt);
        }
        if (mealInMealList.size() > 0){
            // Er zijn mealinmeals te accepteren
            mealInMealList.clear();
            mealInMealList.addAll(mealInMealListBt);
        }
        // Gegevens geheugen in bestanden steken
        storeShops();
        storeProducts();
        storeMeals();
        storeProdInShop();
        storeProdsInMeal();
        storeMealInMeal();
        //saveInBaseDir(basedir);

        // Hoogste ID's in cookies aanpassen
        correctHighestID(Shop.SHOP_LATEST_ID, shopList);
        correctHighestID(Product.PRODUCT_LATEST_ID, productList);
        correctHighestID(Meal.MEAL_LATEST_ID, mealList);
        boolean debug = true;
    }

    /** Flexi methodes */

    private void correctHighestID(String entity,
                                  List<? extends ShoppingEntity> inList){
        CookieRepository cookieRepository = new CookieRepository(basedir);
        // Highest Id cookie corrigeren indien nodig
        // Wat is de cookie
        int cookieId = 0;
        if (cookieRepository.bestaatCookie(entity) != CookieRepository.COOKIE_NOT_FOUND){
            cookieId = Integer.parseInt(cookieRepository.getCookieValueFromLabel(entity));
        }
        // Wat is de echte hoogste Id
        int highestId = determineHighestID(inList);

        // Als de echte > cookie, cookie aanpassen
        if (highestId > cookieId){
            Cookie highestIdCookie = new Cookie(entity, String.valueOf(highestId));
            cookieRepository.deleteCookie(entity);
            cookieRepository.addCookie(highestIdCookie);
        }
    }

    public int determineHighestID(List<? extends ShoppingEntity> inList){
        int highestID = 0;
        for (int i = 0; i < inList.size(); i++) {
            if (inList.get(i).getEntityId().getId() > highestID ){
                highestID = inList.get(i).getEntityId().getId();
            }
        }
        return highestID;
    }

    public int getIndexById(List<? extends ShoppingEntity> inList, IDNumber inID){
        // Bepaalt de index vh element met een opgegeven IDNumber
        for (int i = 0; i < inList.size(); i++) {
            if (inList.get(i).getEntityId().getId() == inID.getId()){
                return i;
            }
        }
        return StaticData.ITEM_NOT_FOUND;
    }

    public int getIndexByIdsFromCList(List<? extends SuperCombination> inList,
                                      IDNumber firstID,
                                      IDNumber secondID){
        // Bepaalt de index vh element voor opgegeven IDNumbers in SuperCombination list
        for (int i = 0; i < inList.size(); i++) {
            if ((inList.get(i).getFirstID().getId() == firstID.getId()) &&
                    (inList.get(i).getSecondID().getId() == secondID.getId())){
                return i;
            }
        }
        return StaticData.ITEM_NOT_FOUND;
    }

    public List<String> convertCombinListinDataList(List<? extends SuperCombination> itemList){
        // Converteert een SuperCombinationList in een datalist voor bewaard te worden in een bestand
        List<String> lineList = new ArrayList<>();
        for (int i = 0; i < itemList.size(); i++) {
            lineList.add(itemList.get(i).convertCombinInFileLine());
        }
        return lineList;
    }

    private List<String> convertEntityListinDataList(List<? extends ShoppingEntity> shopEntityList){
        // Converteert een shopentitylist in een datalist voor bewaard te worden in een bestand
        List<String> lineList = new ArrayList<>();
        for (int i = 0; i < shopEntityList.size(); i++) {
            lineList.add(shopEntityList.get(i).convertToFileLine());
        }
        return lineList;
    }

    public List<String> getNameListFromList(List<? extends ShoppingEntity> inList, int indisplay){
        // bepaalt een lijst met entitynamen obv inlist
        List<String> nameList = new ArrayList<>();
        for (int i = 0; i < inList.size(); i++) {
            switch (indisplay){
                case SpecificData.DISPLAY_SMALL:
                    nameList.add(inList.get(i).getEntityName());
                    break;
                case SpecificData.DISPLAY_LARGE:
                    nameList.add(inList.get(i).getDisplayLine());
                    break;
                default:
                    break;
            }
        }
        return nameList;
    }

    public List<ListItemHelper> getItemsFromList(List<? extends ShoppingEntity> inList){
        // bepaalt een lijst met ListItemHelpers obv inlist
        List<ListItemHelper> nameList = new ArrayList<>();
        for (int i = 0; i < inList.size(); i++) {
            nameList.add(new ListItemHelper(inList.get(i).getEntityName(),
                    "",
                    inList.get(i).getEntityId()));
        }
        return nameList;
    }

    public String getNameByIdFromList(List<? extends ShoppingEntity> inList, int inId){
        // bepaalt de entitynaam obv inlist en inId
        String name = "";
        for (int i = 0; i < inList.size(); i++) {
            if (inList.get(i).getEntityId().getId() == inId){
                return inList.get(i).getEntityName();
            }
        }
        return null;
    }

    public List<Integer> getFirstIdsBySecondId(List<? extends SuperCombination> inList, int secondId){
        // Bepaalt een lijst met first integer Id's obv een second integer Id
        List<Integer> resultIds = new ArrayList<>();
        for (int i = 0; i < inList.size(); i++) {
            if (inList.get(i).getSecondID().getId() == secondId){
                resultIds.add(inList.get(i).getFirstID().getId());
            }
        }
        return resultIds;
    }

    public List<Integer> getSecondIdsByFirstId(List<? extends SuperCombination> inList, int firstId){
        // Bepaalt een lijst met second integer Id's obv een first integer Id
        List<Integer> resultIds = new ArrayList<>();
        for (int i = 0; i < inList.size(); i++) {
            if (inList.get(i).getFirstID().getId() == firstId){
                resultIds.add(inList.get(i).getSecondID().getId());
            }
        }
        return resultIds;
    }

    public List<String> getNamesByCombinEntityId(List<? extends SuperCombination> inCList,
                                                 IDNumber inID,
                                                 List<? extends ShoppingEntity> inSList){
        // Bepaalt de namen die een combinatie hebben met het opgegeven CombinEntity
        List<String> shopsForProduct = new ArrayList<>();
        List<Integer> shopIds = getFirstIdsBySecondId(inCList, inID.getId());

        for (int i = 0; i < shopIds.size(); i++) {
            String foundName = getNameByIdFromList(inSList, shopIds.get(i));
            // opvangen indien id niet bestaat
            if (foundName != null){
                shopsForProduct.add(foundName);
            }
        }
        return shopsForProduct;
    }

    public int getFirstPartnerIndexfromId(List<? extends SuperCombination> inList,
                                          IDNumber inID,
                                          boolean first){
        for (int i = 0; i < inList.size(); i++) {
            if ((inList.get(i).getFirstID().getId() == inID.getId()) && (first)){
                return i;
            }else if ((inList.get(i).getSecondID().getId() == inID.getId()) && !(first)){
                return i;
            }
        }
        return StaticData.ITEM_NOT_FOUND;
    }

    public void deleteCombinById(List<? extends SuperCombination> inList,
                                 IDNumber inID,
                                 boolean first){
        // Verwijdert de combinaties voor een first of second opgegeven ID
        int position = getFirstPartnerIndexfromId(inList, inID, first);
        while (position != StaticData.ITEM_NOT_FOUND){
            inList.remove(position);
            position = getFirstPartnerIndexfromId(inList, inID, first);
        }
    }

    /** Shoplist methodes */

    public Shop getShopByShopName(String inShopName){
        // Wordt gbruikt in A4ShoppingListActivity om obv de shopfilter de shop te bepalen
        for (int i = 0; i < shopList.size(); i++) {
            if (shopList.get(i).getEntityName().equals(inShopName) ){
                return shopList.get(i);
            }
        }
        // indien geen gevonden
        return null;
    }

    public Shop getShopByID(IDNumber inID){
        // Bepaalt de shop voor een opgegeven IDNumber
        int indexInShopList = getIndexById(shopList, inID);
        if (indexInShopList != StaticData.ITEM_NOT_FOUND){
            return shopList.get(indexInShopList);
        }else {
            return null; // Shop niet gevonden
        }
    }

    private List<Shop> getShopsFromDataList(List<String> dataList){
        // Converteert een datalist met shops in een shoplist
        List<Shop> shops = new ArrayList<>();
        for (int j = 0; j < dataList.size(); j++) {
            shops.add(new Shop(dataList.get(j)));
        }
        return shops;
    }

    /** Productlist methodes */

    public Product getProductByID(IDNumber inProductID){
        // Bepaalt het produkt voor een opgegeven IDNumber
        int indexInProductList = getIndexById(productList, inProductID);
        if (indexInProductList != StaticData.ITEM_NOT_FOUND){
            return productList.get(indexInProductList);
        }else {
            return null; // Product niet gevonden
        }
    }

    public List<Product> getProductsByPrefShop(Shop inPrefShop){
        // Bepaalt de produkten voor een opgegeven preferred shop
        List<Product> productsForShop = new ArrayList<>();
        if (inPrefShop != null){
            for (int i = 0; i < productList.size(); i++) {
                if (productList.get(i).getPreferredShopId().getId() == inPrefShop.getEntityId().getId()){
                    productsForShop.add(productList.get(i));
                }
            }
        }
        return productsForShop;
    }

    private List<Product> getProductsFromDataList(List<String> dataList){
        // Converteert een datalist met produkten in een produktlist
        List<Product> products = new ArrayList<>();
        for (int j = 0; j < dataList.size(); j++) {
            products.add(new Product(dataList.get(j)));
        }
        return products;
    }

    /** Meallist methodes */

    public Meal getMealByID(IDNumber inMealID){
        int indexInMeallist = getIndexById(mealList, inMealID);
        if (indexInMeallist != StaticData.ITEM_NOT_FOUND){
            return mealList.get(indexInMeallist);
        }else {
            return null;
        }
    }

    private List<Meal> getMealsFromDataList(List<String> dataList){
        // Converteert een datalist met gerechten in een meallist
        List<Meal> meals = new ArrayList<>();
        for (int j = 0; j < dataList.size(); j++) {
            meals.add(new Meal(dataList.get(j)));
        }
        return meals;
    }

    /** Produkt-Shoplist methodes */

    private List<ProductInShop> getProdInShopsFromDataList(List<String> dataList){
        // Converteert een datalist met prodinshops in een prodinshoplist
        List<ProductInShop> productInShopList = new ArrayList<>();
        for (int j = 0; j < dataList.size(); j++) {
            productInShopList.add(new ProductInShop(dataList.get(j)));
        }
        return productInShopList;
    }

    public List<Integer> getShopIdsForProductId(Integer inProductId){
        return getFirstIdsBySecondId(productInShopList, inProductId);
    }

    public List<String> getShopNamesByProduct(Product inProduct){
        // Bepaalt de shops namen die een combinatie hebben met het opgegeven produkten
        return getNamesByCombinEntityId(productInShopList, inProduct.getEntityId(), shopList);
    }

    public List<String> getUnselectedShopNamesByProduct(Product inProduct){
        // Bepaalt de shops namen die geen combinatie hebben met produkt (inputparm)
        List<String> shopsNotForProduct = new ArrayList<>();
        for (int i = 0; i < shopList.size(); i++) {
            if (getProductShopCombinIndex(inProduct.getEntityId(), shopList.get(i).getEntityId()) == StaticData.ITEM_NOT_FOUND){
                shopsNotForProduct.add(shopList.get(i).getDisplayLine());
            }
        }
        return shopsNotForProduct;
    }

    public int getProductShopCombinIndex(IDNumber inProductID, IDNumber inShopID){
        // Bepaalt de index vd produkt-shop combinatie als die bestaat
        return getIndexByIdsFromCList(productInShopList, inShopID, inProductID);
    }

    public List<Product> getProductsByShop(Shop inShop){
        // Bepaalt de produkten die een combinatie hebben met de opgegeven winkel
        List<Product> productForShops = new ArrayList<>();
        List<Integer> productforShopsnotfound = new ArrayList<>();
        for (int i = 0; i < productInShopList.size(); i++) {
            if (productInShopList.get(i).getFirstID().getId() == inShop.getEntityId().getId()){
                // Shop gevonden in productinshoplist
                if(getProductByID(productInShopList.get(i).getSecondID()) != null){
                    // Product gevonden
                    productForShops.add(getProductByID(productInShopList.get(i).getSecondID()));
                }else {
                    //productInShopList.remove(i);
                    productforShopsnotfound.add(i);
                }
            }
        }
        return productForShops;
    }

    /** Produkt-Meal list methodes */

    private List<ProductInMeal> getProdInMealFromDataList(List<String> dataList){
        // Converteert een datalist met prodinmeal in een prodinmeallist
        List<ProductInMeal> productsInMeal = new ArrayList<>();
        for (int j = 0; j < dataList.size(); j++) {
            productsInMeal.add(new ProductInMeal(dataList.get(j)));
        }
        return productsInMeal;
    }

    public List<ListItemHelper> getProductNamesByMeal(Meal inMeal){
        List<ListItemHelper> productNames = new ArrayList<>();
        List<Product> productsInMeal = getProductsByMeal(inMeal);
        for (int i = 0; i < productsInMeal.size(); i++) {
            ListItemHelper itemHelper = new ListItemHelper(
                    productsInMeal.get(i).getEntityName(),
                    "",
                    productsInMeal.get(i).getEntityId());
            productNames.add(itemHelper);
        }
        return productNames;
    }

    public List<Product> getProductsByMeal(Meal inMeal){
        // Bepaalt de produkten die een combinatie hebben met het opgegeven gerecht
        List<Product> productsForMeal = new ArrayList<>();
        for (int i = 0; i < productInMealList.size(); i++) {
            if (productInMealList.get(i).getFirstID().getId() == inMeal.getEntityId().getId()){
                if(getProductByID(productInMealList.get(i).getSecondID()) != null){
                    productsForMeal.add(getProductByID(productInMealList.get(i).getSecondID()));
                }
            }
        }
        return productsForMeal;
    }

    /** Meal-Meal list methodes */

    private List<MealInMeal> getMealInMealFromDataList(List<String> dataList){
        // Converteert een datalist met mealinmeal in een mealinmeallist
        List<MealInMeal> mealInMeals = new ArrayList<>();
        for (int j = 0; j < dataList.size(); j++) {
            mealInMeals.add(new MealInMeal(dataList.get(j)));
        }
        return mealInMeals;
    }

    public List<ListItemHelper> getParentMealNamesByMeal(Meal inMeal){
        List<ListItemHelper> parentMealNames = new ArrayList<>();
        // Bepaalt de parentgerechten die een combinatie hebben met het opgegeven gerecht
        List<Meal> childMeals = new ArrayList<>();
        for (int i = 0; i < mealInMealList.size(); i++) {
            if (mealInMealList.get(i).getSecondID().getId() == inMeal.getEntityId().getId()){
                if(getMealByID(mealInMealList.get(i).getFirstID()) != null){
                    ListItemHelper itemHelper = new ListItemHelper(
                            getMealByID(mealInMealList.get(i).getFirstID()).getEntityName(),
                            "",
                            mealInMealList.get(i).getFirstID());
                    parentMealNames.add(itemHelper);
                }
            }
        }
        return parentMealNames;
    }

    public List<ListItemHelper> getChildMealNamesByMeal(Meal inMeal){
        List<ListItemHelper> childMealNames = new ArrayList<>();
        // Bepaalt de deelgerechten die een combinatie hebben met het opgegeven gerecht
        List<Meal> childMeals = new ArrayList<>();
        for (int i = 0; i < mealInMealList.size(); i++) {
            if (mealInMealList.get(i).getFirstID().getId() == inMeal.getEntityId().getId()){
                if(getMealByID(mealInMealList.get(i).getSecondID()) != null){
                    ListItemHelper itemHelper = new ListItemHelper(
                            getMealByID(mealInMealList.get(i).getSecondID()).getEntityName(),
                            "",
                            mealInMealList.get(i).getSecondID());
                    childMealNames.add(itemHelper);
                }
            }
        }
        return childMealNames;
    }

    /** Store methodes */

    public ReturnInfo storeProdInShop(){
        // Bewaart de produktInShopList
        ReturnInfo returnInfo = new ReturnInfo(0);
        repository.storeData(productInShopFile, convertCombinListinDataList(productInShopList));
        return returnInfo;
    }

    public ReturnInfo storeProdsInMeal(){
        // Bewaart de produktInMealList
        ReturnInfo returnInfo = new ReturnInfo(0);
        repository.storeData(productInMealFile, convertCombinListinDataList(productInMealList));
        return returnInfo;
    }

    public ReturnInfo storeMealInMeal(){
        // Bewaart de mealInMealList
        ReturnInfo returnInfo = new ReturnInfo(0);
        repository.storeData(mealInMealFile, convertCombinListinDataList(mealInMealList));
        return returnInfo;
    }

    public ReturnInfo storeShops(){
        // Bewaart de shoplist
        // Sortering is reeds gebeurd in ShopFragment
        ReturnInfo returnInfo = new ReturnInfo(0);
        repository.storeData(shopFile, convertEntityListinDataList(shopList));
        return returnInfo;
    }

    public ReturnInfo storeProducts(){
        // Bewaart de productlist
        // Sortering is reeds gebeurd in ProductFragment
        ReturnInfo returnInfo = new ReturnInfo(0);
        repository.storeData(productFile, convertEntityListinDataList(productList));
        return returnInfo;
    }

    public ReturnInfo storeMeals(){
        // Bewaart de meallist
        // Sortering moet reeds gebeurd zijn
        ReturnInfo returnInfo = new ReturnInfo(0);
        repository.storeData(mealFile, convertEntityListinDataList(mealList));
        return returnInfo;
    }

    /** Delete methodes */

    public ReturnInfo deleteShop(int position){
        // Verwijdert een shop
        ReturnInfo returnInfo = new ReturnInfo(0);
        // Alle productinshops moeten voor die shop eerst verwijderd worden
        deleteCombinById(productInShopList,
                shopList.get(position).getEntityId(),
                true);
        //deleteProductsByShop(shopList.get(position));
        storeProdInShop();
        //
        shopList.remove(position);
        storeShops();
        return returnInfo;
    }

    public ReturnInfo deleteProduct(int position){
        // Verwijdert een produkt
        ReturnInfo returnInfo = new ReturnInfo(0);
        // Alle productinshops moeten voor dat produkt ook verwijderd worden
        deleteCombinById(productInShopList,
                productList.get(position).getEntityId(),
                false);
        //deleteShopsByProduct(productList.get(position));
        storeProdInShop();
        // Alle productsinmeal moeten voor dat produkt ook verwijderd worden
        deleteCombinById(productInMealList,
                productList.get(position).getEntityId(),
                false);
        //deleteMealByProduct(productList.get(position));
        storeProdsInMeal();
        // en tenslotte mag het produkt gedelete worden
        productList.remove(position);
        storeProducts();
        return returnInfo;
    }

    public ReturnInfo deleteMeal(int position){
        // Verwijdert een gerecht
        ReturnInfo returnInfo = new ReturnInfo(0);
        // Alle productinmeal moeten voor dat gerecht ook verwijderd worden
        deleteCombinById(productInMealList,
                mealList.get(position).getEntityId(),
                true);
        //deleteProductsByMeal(mealList.get(position));
        storeProdsInMeal();
        // Alle mealinmeal moeten voor dat gerecht ook verwijderd worden
        deleteCombinById(mealInMealList,
                mealList.get(position).getEntityId(),
                true);
        deleteCombinById(mealInMealList,
                mealList.get(position).getEntityId(),
                false);
        //deleteMealMeal(mealList.get(position));
        storeMealInMeal();
        // en tenslotte mag het gerecht gedelete worden
        mealList.remove(position);
        storeMeals();
        return returnInfo;
    }

    /** Sort methodes */

    public void sortShopList(){
        // Sorteert een shoplist op entityname alfabetisch
        Shop tempShop = new Shop();
        for (int i = shopList.size() ; i > 0; i--) {
            for (int j = 1; j < i ; j++) {
                if (shopList.get(j).getEntityName().compareToIgnoreCase(shopList.get(j-1).getEntityName()) < 0){
                    tempShop.setShop(shopList.get(j));
                    shopList.get(j).setShop(shopList.get(j-1));
                    shopList.get(j-1).setShop(tempShop);
                }
            }
        }
    }

    public void sortProductList(){
        // Sorteert een productlist op entityname alfabetisch
        Product tempProduct = new Product();
        for (int i = productList.size() ; i > 0; i--) {
            for (int j = 1; j < i ; j++) {
                if (productList.get(j).getEntityName().compareToIgnoreCase(
                        productList.get(j-1).getEntityName()) < 0){
                    tempProduct.setProduct(productList.get(j));
                    productList.get(j).setProduct(productList.get(j-1));
                    productList.get(j-1).setProduct(tempProduct);
                }
            }
        }
    }

    public void sortMealList(){
        // Sorteert een meallist op entityname alfabetisch
        Meal tempEntity = new Meal();
        for (int i = mealList.size() ; i > 0; i--) {
            for (int j = 1; j < i ; j++) {
                if (mealList.get(j).getEntityName().compareToIgnoreCase(
                        mealList.get(j-1).getEntityName()) < 0){
                    tempEntity.setMeal(mealList.get(j));
                    mealList.get(j).setMeal(mealList.get(j-1));
                    mealList.get(j-1).setMeal(tempEntity);
                }
            }
        }
    }

    /** Convert to checkboxes */

    public List<CheckboxHelper> convertProductsToCheckboxs(List<Product> prodList,
                                                           int inDisplayType,
                                                           boolean onlyChecked){
        // Converteert een lijst met produkten in een checkboxList obv een displaytype
        // Het displaytype bepaalt of de preferred shop meegetoond wordt (LARGE) of niet (SMALL)
        List<CheckboxHelper> checkboxList = new ArrayList<>();
        String productDisplayLine;
        String cbTextStyle = SpecificData.STYLE_DEFAULT;
        for (int i = 0; i < prodList.size(); i++) {
            cbTextStyle = SpecificData.STYLE_DEFAULT;
            // Controle only checked
            if (prodList.get(i).isToBuy() || !onlyChecked){
                if (inDisplayType == SpecificData.DISPLAY_LARGE){
                    productDisplayLine = getProductLargeDisplay(prodList.get(i));
                }else {
                    productDisplayLine = prodList.get(i).getEntityName();
                }
                if ((inDisplayType == SpecificData.DISPLAY_SMALL_BOLD) &&
                        (prodList.get(i).isCooled())){
                    cbTextStyle = SpecificData.STYLE_COOLED_BOLD;
                }
                // Check eigenschap cooled, style = cooled
                if ((inDisplayType == SpecificData.DISPLAY_SMALL) &&
                        (prodList.get(i).isCooled())){
                    cbTextStyle = SpecificData.STYLE_COOLED;
                }
                checkboxList.add(new CheckboxHelper(
                        productDisplayLine,
                        prodList.get(i).isToBuy(),cbTextStyle,
                        prodList.get(i).getEntityId()));
            }
        }
        return checkboxList;
    }

    private String getProductLargeDisplay(Product inProduct){
        // Bepaalt de LARGE display voor een produkt (inclusief naam preferred shop)
        String productDisplayLine = inProduct.getEntityName();
        for (int i = 0; i < shopList.size(); i++) {
            if (shopList.get(i).getEntityId().getId() == inProduct.getPreferredShopId().getId()){
                productDisplayLine = productDisplayLine.concat(" in " + shopList.get(i).getEntityName());
                return productDisplayLine;
            }
        }
        productDisplayLine = productDisplayLine.concat(" in geen voorkeurwinkel");
        return productDisplayLine;
    }

    public List<CheckboxHelper> convertMealsToCheckboxs(List<Meal> mealList,
                                                        boolean onlyChecked){
        // Converteert een lijst met gerechten in een checkboxList
        List<CheckboxHelper> checkboxList = new ArrayList<>();
        for (int i = 0; i < mealList.size(); i++) {
            // Controle only checked
            if (mealList.get(i).isToBuy() || !onlyChecked){
                checkboxList.add(new CheckboxHelper(
                        mealList.get(i).getEntityName(),
                        mealList.get(i).isToBuy(),SpecificData.STYLE_DEFAULT,
                        mealList.get(i).getEntityId()));
            }
        }
        return checkboxList;
    }

    /** Allerlei */

    public IDNumber determineShopBySpinnerSelection(){
        // Bepaalt de IDNumber vd shop obv de spinnerselection
        boolean shopFound = false;
        int i = 0;
        while (!shopFound && i < shopList.size()) {
            if (shopList.get(i).getEntityName().equals(this.spinnerSelection)){
                shopFound = true;
            }
            i++;
        }
        if (shopFound){
            return shopList.get(i - 1).getEntityId();
        }else {
            return StaticData.IDNUMBER_NOT_FOUND;
        }
    }

    public void setToBuyForSubMeals(Meal inMeal, boolean inToBuy){
        List<Integer> deelgerechtIds;
        deelgerechtIds = getSecondIdsByFirstId(mealInMealList, inMeal.getEntityId().getId());
        if (deelgerechtIds.size() > 0){
            setToBuyforMealByList(deelgerechtIds, inToBuy);
            // Deelgerechten vn deelgerechten
            for (int i = 0; i < deelgerechtIds.size(); i++) {
                setToBuyForSubMeals(getMealByID(new IDNumber(deelgerechtIds.get(i))), inToBuy);
            }
        }
    }

    public void setToBuyForProducts(Meal inMeal, boolean inToBuy){
        List<Integer> productIds;
        productIds = getSecondIdsByFirstId(productInMealList, inMeal.getEntityId().getId());
        if (productIds.size() > 0){
            setToBuyforProductByList(productIds, inToBuy);
        }
    }

    private void setToBuyforProductByList(List<Integer> inListIds, boolean inToBuy){
        // Zet voor een lijst vn ids, de artikelen op ToBuy of niet
        boolean gerechtToBuy = false;
        for (int i = 0; i < inListIds.size(); i++) {
            // Indien toBuy moet afgezet worden, moet er eerst gecontrollerd worden of
            // artikel niet behoort tot een gerecht waarvan toBuy opstaat !
            if (!inToBuy){
                // Bepaal gerechten waartoe artikel behoort
                List<Integer> gerechtIds;
                gerechtIds = getFirstIdsBySecondId(
                        productInMealList, inListIds.get(i));
                // Is er een gerecht waarvan toBuy opstaat, dan gerechtToBuy = true
                // Er wordt maar 1 parent laag gecontroleerd !
                for (int j = 0; (j < gerechtIds.size() && !gerechtToBuy); j++) {
                    if (getMealByID(new IDNumber(gerechtIds.get(j))).isToBuy()){
                        gerechtToBuy = true;
                    }
                }
            }
            // Indien toBuy moet afgezet worden dan kan dit alleen als er geen gerechten zijn,
            // waartoe artikel behoort, waarvan toBuy opstaat
            if (inToBuy || !gerechtToBuy){
                // Bepaal artikel
                Product tewijzigenProduct = productList.get(getIndexById(productList, new IDNumber(inListIds.get(i))));

                if (inToBuy){
                    // Als toBuy voor een artikel moet opgezet worden vanuit een meal
                    // dan moet toBuy van het artikel eerst bewaard worden
                    tewijzigenProduct.setPreviousToBuy(tewijzigenProduct.isToBuy());
                    tewijzigenProduct.setToBuy(inToBuy);
                    boolean debug = true;
                }else {
                    // Als toBuy voor een artikel moet afgezet worden (er zijn dan geen meals meer
                    // waarvoor het op moet staan) vanuit een meal
                    // dan moet previoustoBuy van het artikel teruggezet worden
                    tewijzigenProduct.setToBuy(tewijzigenProduct.isPreviousToBuy());
                }
            }
        }
    }

    private void setToBuyforMealByList(List<Integer> inListIds, boolean inToBuy){
        // Zet voor een lijst vn ids, de gerechten op ToBuy of niet
        boolean parentMealToBuy = false;
        for (int i = 0; i < inListIds.size(); i++) {
            // Indien toBuy moet afgezet worden, moet er eerst gecontrollerd worden of
            // gerecht geen deelgerecht is van een gerecht waarvan toBuy opstaat !
            if (!inToBuy){
                // Bepaal gerechten waarvan het een deelgerecht is
                List<Integer> parentMealIds;
                parentMealIds = getFirstIdsBySecondId(
                        mealInMealList, inListIds.get(i));
                // Is er een gerecht waarvan toBuy opstaat, dan parentMealToBuy = true
                // Er wordt maar 1 parent laag gecontroleerd omdat deze methode in een
                // recursive processing zit
                for (int j = 0; (j < parentMealIds.size() && !parentMealToBuy); j++) {
                    if (getMealByID(new IDNumber(parentMealIds.get(i))).isToBuy()){
                        parentMealToBuy = true;
                    }
                }
            }
            // Indien toBuy moet afgezet worden dan kan dit alleen als er geen gerechten zijn,
            // waarvan gerecht in kwestie deelgerecht is en waarvan toBuy opstaat
            if (inToBuy || !parentMealToBuy){
                mealList.get(getIndexById(
                        mealList,
                        new IDNumber(inListIds.get(i)))).setToBuy(inToBuy);
            }
        }
    }

    public void correctToBuyMeals(){
        // Voor alle Meals, met ToBuy op
        boolean correctToBuy = true;
        for (int i = 0; i < mealList.size(); i++) {
            if (mealList.get(i).isToBuy()){
                for (int j = 0; ((j < productInMealList.size()) && (correctToBuy)); j++) {
                    if (productInMealList.get(j).getFirstID().getId() ==
                            mealList.get(i).getEntityId().getId()){
                        // Haal product op
                        if(getProductByID(productInMealList.get(j).getSecondID()).isToBuy()){
                            // Indien er een product toBuy staat, geen correctie nodig
                            correctToBuy = false;
                        }
                    }
                }
                if (correctToBuy){
                    mealList.get(i).setToBuy(false);
                }
            }
        }
    }

    public void clearAllProductInShop(){
        // Verwijdert alle prodinshop combinaties
        productInShopList.clear();
        storeProdInShop();
    }

    public void clearAllProductInMeal(){
        // Verwijdert alle prodinmeal combinaties
        productInMealList.clear();
        storeProdsInMeal();
    }

    /** Fill lists (worden gebruikt door BluetoothCom voor het accepteren van de data
     * die met bluetooth ontvangen werd. Let op de huidige data wordt verwijderd */

    public void fillShopListWithOtherShopList(List<Shop> inList){
        shopList.clear();
        shopList.addAll(inList);
        storeShops();
        correctHighestID(Shop.SHOP_LATEST_ID, shopList);
    }

    public void fillProdListWithOtherProdList(List<Product> inList){
        productList.clear();
        productList.addAll(inList);
        storeProducts();
        correctHighestID(Product.PRODUCT_LATEST_ID, productList);
    }

    public void fillProdInShopListWithOtherList(List<ProductInShop> inList){
        productInShopList.clear();
        productInShopList.addAll(inList);
        storeProdInShop();
    }

    public void fillMealListWithOtherMealList(List<Meal> inList){
        mealList.clear();
        mealList.addAll(inList);
        storeMeals();
        correctHighestID(Meal.MEAL_LATEST_ID, mealList);
    }

    public void fillProdInMealListWithOtherList(List<ProductInMeal> inList){
        productInMealList.clear();
        productInMealList.addAll(inList);
        storeProdsInMeal();
    }

    public void fillMealInMealListWithOtherList(List<MealInMeal> inList){
        mealInMealList.clear();
        mealInMealList.addAll(inList);
        storeMealInMeal();
    }

    public void setSpinnerSelection(String spinnerSelection) {
        this.spinnerSelection = spinnerSelection;
    }

    public String getBasedir() {
        return basedir;
    }

    public String getBaseSwitch() {
        return baseSwitch;
    }

    public void setBaseSwitch(String baseSwitch) {
        this.baseSwitch = baseSwitch;
    }

    public List<Shop> getShopList() {
        return shopList;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public List<ProductInShop> getProductInShopList() {
        return productInShopList;
    }

    public List<Shop> getShopListBt() {
        return shopListBt;
    }

    public List<Product> getProductListBt() {
        return productListBt;
    }

    public List<ProductInShop> getProductInShopListBt() {
        return productInShopListBt;
    }

    public List<Meal> getMealList() {
        return mealList;
    }

    public List<ProductInMeal> getProductInMealList() {
        return productInMealList;
    }

    public List<MealInMeal> getMealInMealList() {
        return mealInMealList;
    }


    /** Clear lists  (worden momenteel niet gebruikt) */

/*
    public void clearShops(){
        // Verwijdert shops uit shoplist
        shopList.clear();
    }

    public void clearProducts(){
        // Verwijdert shops uit shoplist
        productList.clear();
    }

    public void clearMeals(){
        // Verwijdert shops uit shoplist
        mealList.clear();
    }

    public void clearProdInShops(){
        // Verwijdert shops uit shoplist
        productInShopList.clear();
    }

    public void clearProdInMeals(){
        // Verwijdert shops uit shoplist
        productInMealList.clear();
    }

    public void clearMealInMeal(){
        // Verwijdert shops uit shoplist
        mealInMealList.clear();
    }
*/

    // TODO: mag waarschijnlijk weg

/*
    // File declaraties voor copy int nr ext
    File shopExtFile;
    File productExtFile;
    File productInShopExtFile;
*/
/*
    public ReturnInfo saveInBaseDir(String basedir){
        ReturnInfo returnInfo = new ReturnInfo(0);
        // Filedefinities vr copy int nr ext
        shopExtFile = new File(basedir, SHOP_FILE);
        productExtFile = new File(basedir, PRODUCT_FILE);
        productInShopExtFile = new File(basedir, PRODUCTSHOP_FILE);
        CookieRepository cookieRepository = new CookieRepository(basedir);

        repository.storeData(shopExtFile, convertEntityListinDataList(shopList));
        // Zet hoogste ID in Cookie
        cookieRepository.registerCookie(Shop.SHOP_LATEST_ID,
                String.valueOf(determineHighestID(shopList)));

        repository.storeData(productExtFile, convertEntityListinDataList(productList));
        // Zet hoogste ID in Cookie
        cookieRepository.registerCookie(Product.PRODUCT_LATEST_ID,
                String.valueOf(determineHighestID(productList)));

        repository.storeData(productInShopExtFile, convertCombinListinDataList(productInShopList));

        repository.storeData(mealFile, convertEntityListinDataList(mealList));
        // Zet hoogste ID in Cookie
        cookieRepository.registerCookie(Meal.MEAL_LATEST_ID,
                String.valueOf(determineHighestID(mealList)));

        repository.storeData(productInMealFile, convertCombinListinDataList(productInMealList));
        repository.storeData(mealInMealFile, convertCombinListinDataList(mealInMealList));

        return returnInfo;
    }
*/

    // TODO: mag weg

/*
    public List<ShoppingEntity> sortShopEntityList(List<? extends ShoppingEntity> inList){
        // Sorteert een list op entityname alfabetisch
        ShoppingEntity tempEntity;
        List<ShoppingEntity> outList = new ArrayList<>();
        // Werkt niet met ShoppingEntity !!
        //outList.addAll(inList);
        for (int i = inList.size() ; i > 0; i--) {
            for (int j = 1; j < i ; j++) {
                if (inList.get(j).getEntityName().compareToIgnoreCase(inList.get(j-1).getEntityName()) < 0){
                    tempEntity = inList.get(j);
                    inList.get(j) = inList.get(j-1);
                    inList.set(j, inList.get(j-1));
                    inList.set(j-1, tempEntity);
                }
            }
        }
        return outList;
    }
*/

/*
    public int getProductMealCombinIndex(IDNumber inProductID, IDNumber inMealID){
        // Bepaalt de index vd produkt-shop combinatie als die bestaat
        return getIndexByIdsFromCList(productInMealList, inMealID, inProductID);
    }
*/

/*
    // Is vervangen worden door FlexiListHandler
    private void deleteProductsByShop(ShoppingEntity inShop){
        // Verwijdert de prodinshop combinaties voor een opgegeven shop
        deleteCombinById(productInShopList, inShop.getEntityId(), true);
    }
*/

/*
    // Is vervangen worden door FlexiListHandler
    private int getFirstProductInshopByShop(ShoppingEntity inShop){
        // Bepaalt de eerste index vd produktinshop combinatie die gevonden wordt, voor een
        // opgegeven shop
        for (int i = 0; i < productInShopList.size(); i++) {
            if (productInShopList.get(i).getFirstID().getId() == inShop.getEntityId().getId()){
                return i;
            }
        }
        return StaticData.ITEM_NOT_FOUND;
    }
*/

/*
    // Is vervangen worden door FlexiListHandler
    private void deleteShopsByProduct(Product inProduct){
        // Verwijdert de prodinshop combinaties voor een opgegeven produkt
        int position = getFirstProductInshopByProduct(inProduct);
        while (position != StaticData.ITEM_NOT_FOUND){
            productInShopList.remove(position);
            position = getFirstProductInshopByProduct(inProduct);
        }
    }
*/

/*
    // Is vervangen worden door FlexiListHandler
    private int getFirstProductInshopByProduct(Product inProduct){
        // Bepaalt de eerste index vd produktinshop combinatie die gevonden wordt, voor een
        // opgegeven produkt
        return getFirstPartnerIndexfromId(productInShopList, inProduct.getEntityId(), false);
    }
*/

/*
    // Is vervangen worden door FlexiListHandler
    public void deleteMealByProduct(Product inProduct){
        // Verwijdert de prodinmeal combinaties voor een opgegeven produkt
        deleteCombinById(productInMealList, inProduct.getEntityId(), false);
    }
*/

/*
    // Is vervangen worden door FlexiListHandler
    private int getFirstProductInMealByProduct(Product inProduct){
        // Bepaalt de eerste index vd produktinmeal combinatie die gevonden wordt, voor een
        // opgegeven produkt
        for (int i = 0; i < productInMealList.size(); i++) {
            if (productInMealList.get(i).getSecondID().getId() == inProduct.getEntityId().getId()){
                return i;
            }
        }
        return StaticData.ITEM_NOT_FOUND;
    }
*/

/*
    // Is vervangen worden door FlexiListHandler
    public void deleteProductsByMeal(ShoppingEntity inMeal){
        // Verwijdert de prodinmeal combinaties voor een opgegeven gerecht
        int position = getFirstProductInMealByMeal(inMeal);
        while (position != StaticData.ITEM_NOT_FOUND){
            productInMealList.remove(position);
            position = getFirstProductInMealByMeal(inMeal);
        }
    }
*/

/*
    // Is vervangen worden door FlexiListHandler
    private int getFirstProductInMealByMeal(ShoppingEntity inMeal){
        // Bepaalt de eerste index vd produktinmeal combinatie die gevonden wordt, voor een
        // opgegeven gerecht
        for (int i = 0; i < productInMealList.size(); i++) {
            if (productInMealList.get(i).getFirstID().getId() == inMeal.getEntityId().getId()){
                return i;
            }
        }
        return StaticData.ITEM_NOT_FOUND;
    }
*/

/*
    // Is vervangen worden door FlexiListHandler
    public void deleteMealMeal(Meal inMeal){
        // Verwijdert de mealinmeal combinaties voor een opgegeven meal
        int position = getFirstMealInMealByMeal(inMeal);
        while (position != StaticData.ITEM_NOT_FOUND){
            productInMealList.remove(position);
            position = getFirstMealInMealByMeal(inMeal);
        }
    }
*/

/*
    // Is vervangen worden door FlexiListHandler
    private int getFirstMealInMealByMeal(Meal inMeal){
        // Bepaalt de eerste index vd mealinmeal combinatie parent or child die gevonden wordt, voor een
        // opgegeven meal
        for (int i = 0; i < mealInMealList.size(); i++) {
            if (mealInMealList.get(i).getFirstID().getId() == inMeal.getEntityId().getId()){
                return i;
            }else if (mealInMealList.get(i).getSecondID().getId() == inMeal.getEntityId().getId()){
                return i;
            }
        }
        return StaticData.ITEM_NOT_FOUND;
    }
*/

/*
    // Is vervangen worden door FlexiListHandler
    private List<String> convertProdShopListinDataList(List<ProductInShop> itemList){
        // Converteert een prodinshoplist in een datalist voor bewaard te worden in een bestand
        return convertCombinListinDataList(itemList);
    }
*/

/*
    // Is vervangen worden door FlexiListHandler
    private List<String> convertProdMealListinDataList(List<ProductInMeal> itemList){
        // Converteert een prodinmeallist in een datalist voor bewaard te worden in een bestand
        return convertCombinListinDataList(itemList);
    }
*/

/*
    // Is vervangen worden door FlexiListHandler
    private List<String> convertMealMealListinDataList(List<MealInMeal> itemList){
        // Converteert een mealinmeallist in een datalist voor bewaard te worden in een bestand
        return convertCombinListinDataList(itemList);
    }
*/

}
