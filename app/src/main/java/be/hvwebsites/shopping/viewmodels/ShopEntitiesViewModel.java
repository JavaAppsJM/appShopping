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
    // File declaraties voor copy int nr ext
    File shopExtFile;
    File productExtFile;
    File productInShopExtFile;
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
    // Lijst om bluetooth data in te zetten
    private List<Shop> shopListBt = new ArrayList<>();
    private List<Product> productListBt = new ArrayList<>();
    private List<ProductInShop> productInShopListBt = new ArrayList<>();
    private List<Meal> mealListBt = new ArrayList<>();
    private List<ProductInMeal> productInMealListBt = new ArrayList<>();
    private List<MealInMeal> mealInMealListBt = new ArrayList<>();
    // SpinnerSelection
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
        if (shopList.size() == 0){
            returnInfo.setReturnCode(100);
            returnInfo.setReturnMessage("Er zijn nog geen winkels !");
        }
        // Ophalen produkten
        repository = new FlexiRepository(productFile);
        productList.addAll(getProductsFromDataList(repository.getDataList()));
        if (productList.size() == 0){
            returnInfo.setReturnCode(100);
            returnInfo.setReturnMessage("Er zijn nog geen artikels !");
        }
        // Ophalen produkt in winkel combinaties
        repository = new FlexiRepository(productInShopFile);
        productInShopList.addAll(getProdInShopsFromDataList(repository.getDataList()));
        // eenmalig om prodinshop op te kiusen
        // cleanProdInshop();

        // Ophalen gerechten
        repository = new FlexiRepository(mealFile);
        mealList.addAll(getMealsFromDataList(repository.getDataList()));
        if (mealList.size() == 0){
            returnInfo.setReturnCode(100);
            returnInfo.setReturnMessage("Er zijn nog geen gerechten !");
        }
        // Ophalen produkt in gerecht combinaties
        repository = new FlexiRepository(productInMealFile);
        productInMealList.addAll(getProdInMealFromDataList(repository.getDataList()));

        // Ophalen gerecht in gerecht combinaties
        repository = new FlexiRepository(mealInMealFile);
        mealInMealList.addAll(getMealInMealFromDataList(repository.getDataList()));
        return returnInfo;
    }

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

        repository.storeData(productInMealFile, convertProdMealListinDataList(productInMealList));
        repository.storeData(mealInMealFile, convertMealMealListinDataList(mealInMealList));

        return returnInfo;
    }

    public void forceBtData(String basedir){
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
        saveInBaseDir(basedir);
        boolean debug = true;
    }

    /** Flexi methodes */

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

    public List<ShoppingEntity> sortShopEntityList(List<? extends ShoppingEntity> inList){
        // Sorteert een list op entityname alfabetisch
        ShoppingEntity tempEntity;
        List<ShoppingEntity> outList = new ArrayList<>();
        // Werkt niet met ShoppingEntity !!
        //outList.addAll(inList);
/*
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
*/
        return outList;
    }

    /** Shoplist methodes */

    // TODO: wordt gbruikt door shopfilter dat gebruik maakt ve naam ipv id
    //  deze moet veranded worden in een Id !!
    public Shop getShopByShopName(String inShopName){
        // Shop bepalen obv een shopnaam
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

    /** Produkt-Shoplist methodes */

    public List<String> getShopNamesByProduct(Product inProduct){
        // Bepaalt de shops namen die een combinatie hebben met het opgegeven produkten
        return getNamesByCombinEntityId(productInShopList, inProduct.getEntityId(), shopList);
/*
        List<String> shopsForProduct = new ArrayList<>();
        List<Integer> shopIds = getFirstIdsBySecondId(productInShopList, inProduct.getEntityId().getId());

        for (int i = 0; i < shopIds.size(); i++) {
            // opvangen indien shop id niet bestaat
            Shop foundShop = new Shop();
            foundShop.setShopEntity(getShopByID(new IDNumber(shopIds.get(i))));
            if (foundShop != null){
                shopsForProduct.add(foundShop.getDisplayLine());
            }
        }
        return shopsForProduct;
*/
    }

    public List<String> getUnselectedShopNamesByProduct(Product inProduct){
        // Bepaalt de shops namen die geen combinatie hebben met produkt (inputparm)
        List<String> shopsNotForProduct = new ArrayList<>();
        for (int i = 0; i < shopList.size(); i++) {
            // TODO: moet nog getest worden dat getProductShopCombin evengoed werkt als !existsProductShopCombin
//            if (!existsProductShopCombin(inProduct.getEntityId(), shopList.get(i).getEntityId())){
//                shopsNotForProduct.add(shopList.get(i).getDisplayLine());
//            }
            if (getProductShopCombinIndex(inProduct.getEntityId(), shopList.get(i).getEntityId()) == StaticData.ITEM_NOT_FOUND){
                shopsNotForProduct.add(shopList.get(i).getDisplayLine());
            }
        }
        return shopsNotForProduct;
    }

    public int getProductShopCombinIndex(IDNumber inProductID, IDNumber inShopID){
        // Bepaalt de index vd produkt-shop combinatie als die bestaat
        return getIndexByIdsFromCList(productInShopList, inShopID, inProductID);
/*
        for (int i = 0; i < productInShopList.size(); i++) {
            if (productInShopList.get(i).getSecondID().getId() == inProductID.getId() &&
                    productInShopList.get(i).getFirstID().getId() == inShopID.getId()){
                return i;
            }
        }
        return StaticData.ITEM_NOT_FOUND;
*/
    }

    public List<Product> getProductsByShop(Shop inShop){
        // Bepaalt de produkten die een combinatie hebben met de opgegeven winkel
        List<Product> productForShops = new ArrayList<>();
        List<Integer> productforShopsnotfound = new ArrayList<>();
        for (int i = 0; i < productInShopList.size(); i++) {
            if (productInShopList.get(i).getFirstID().getId() == inShop.getEntityId().getId()){
                // Shop gevonden in productinshoplist
                if(getProductByID(productInShopList.get(i).getSecondID()) != null){
                    productForShops.add(getProductByID(productInShopList.get(i).getSecondID()));
                }else {
                    //productInShopList.remove(i);
                    productforShopsnotfound.add(i);
                }
            }
        }
        return productForShops;
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
/*
        for (int i = 0; i < productList.size(); i++) {
            if (productList.get(i).getEntityId().getId() == inProductID.getId()){
                return productList.get(i);
            }
        }
        return null; // geen product gevonden met ID
*/
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

    /** Store methodes */

    public ReturnInfo storeProdInShop(){
        // Bewaart de produktInShopList
        ReturnInfo returnInfo = new ReturnInfo(0);
        repository.storeData(productInShopFile, convertProdShopListinDataList(productInShopList));
        return returnInfo;
    }

    public ReturnInfo storeProdsInMeal(){
        // Bewaart de produktInMealList
        ReturnInfo returnInfo = new ReturnInfo(0);
        repository.storeData(productInMealFile, convertProdMealListinDataList(productInMealList));
        return returnInfo;
    }

    public ReturnInfo storeMealInMeal(){
        // Bewaart de mealInMealList
        ReturnInfo returnInfo = new ReturnInfo(0);
        repository.storeData(mealInMealFile, convertMealMealListinDataList(mealInMealList));
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
        deleteMealByProduct(productList.get(position));
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
        deleteMealMeal(mealList.get(position));
        storeMealInMeal();
        // en tenslotte mag het gerecht gedelete worden
        mealList.remove(position);
        storeMeals();
        return returnInfo;
    }

    // TODO: Kan vervangen worden door FlexiListHandler
    private void deleteProductsByShop(ShoppingEntity inShop){
        // Verwijdert de prodinshop combinaties voor een opgegeven shop
        deleteCombinById(productInShopList, inShop.getEntityId(), true);
/*
        int position = getFirstProductInshopByShop(inShop);
        while (position != StaticData.ITEM_NOT_FOUND){
            productInShopList.remove(position);
            position = getFirstProductInshopByShop(inShop);
        }
*/
    }

    // TODO: Kan vervangen worden door FlexiListHandler
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

    // TODO: Kan vervangen worden door FlexiListHandler
    private void deleteShopsByProduct(Product inProduct){
        // Verwijdert de prodinshop combinaties voor een opgegeven produkt
        int position = getFirstProductInshopByProduct(inProduct);
        while (position != StaticData.ITEM_NOT_FOUND){
            productInShopList.remove(position);
            position = getFirstProductInshopByProduct(inProduct);
        }
    }

    // TODO: Kan vervangen worden door FlexiListHandler
    private int getFirstProductInshopByProduct(Product inProduct){
        // Bepaalt de eerste index vd produktinshop combinatie die gevonden wordt, voor een
        // opgegeven produkt
        return getFirstPartnerIndexfromId(productInShopList, inProduct.getEntityId(), false);
/*
        for (int i = 0; i < productInShopList.size(); i++) {
            if (productInShopList.get(i).getSecondID().getId() == inProduct.getEntityId().getId()){
                return i;
            }
        }
        return StaticData.ITEM_NOT_FOUND;
*/
    }

    // TODO: Kan vervangen worden door FlexiListHandler
    public void deleteMealByProduct(Product inProduct){
        // Verwijdert de prodinmeal combinaties voor een opgegeven produkt
        int position = getFirstProductInMealByProduct(inProduct);
        while (position != StaticData.ITEM_NOT_FOUND){
            productInMealList.remove(position);
            position = getFirstProductInMealByProduct(inProduct);
        }
    }

    // TODO: Kan vervangen worden door FlexiListHandler
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

    // TODO: Kan vervangen worden door FlexiListHandler
    public void deleteProductsByMeal(ShoppingEntity inMeal){
        // Verwijdert de prodinmeal combinaties voor een opgegeven gerecht
        int position = getFirstProductInMealByMeal(inMeal);
        while (position != StaticData.ITEM_NOT_FOUND){
            productInMealList.remove(position);
            position = getFirstProductInMealByMeal(inMeal);
        }
    }

    // TODO: Kan vervangen worden door FlexiListHandler
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

    // TODO: Kan vervangen worden door FlexiListHandler
    public void deleteMealMeal(Meal inMeal){
        // Verwijdert de mealinmeal combinaties voor een opgegeven meal
        int position = getFirstMealInMealByMeal(inMeal);
        while (position != StaticData.ITEM_NOT_FOUND){
            productInMealList.remove(position);
            position = getFirstMealInMealByMeal(inMeal);
        }
    }

    // TODO: Kan vervangen worden door FlexiListHandler
    public void deleteChildMealinMeal(Meal inChildMeal){

    }

    // TODO: Kan vervangen worden door FlexiListHandler
    public void deleteParentMealinMeal(Meal inChildMeal){

    }

    // TODO: Kan vervangen worden door FlexiListHandler
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

    // TODO: Kan vervangen worden door FlexiListHandler
    private List<String> convertProdShopListinDataList(List<ProductInShop> itemList){
        // Converteert een prodinshoplist in een datalist voor bewaard te worden in een bestand
        return convertCombinListinDataList(itemList);
/*
        List<String> lineList = new ArrayList<>();
        for (int i = 0; i < itemList.size(); i++) {
            lineList.add(itemList.get(i).convertCombinInFileLine());
        }
        return lineList;
*/
    }

    // TODO: Kan vervangen worden door FlexiListHandler
    private List<String> convertProdMealListinDataList(List<ProductInMeal> itemList){
        // Converteert een prodinmeallist in een datalist voor bewaard te worden in een bestand
        return convertCombinListinDataList(itemList);
/*
        List<String> lineList = new ArrayList<>();
        for (int i = 0; i < itemList.size(); i++) {
            lineList.add(itemList.get(i).convertCombinInFileLine());
        }
        return lineList;
*/
    }

    // TODO: Kan vervangen worden door FlexiListHandler
    private List<String> convertMealMealListinDataList(List<MealInMeal> itemList){
        // Converteert een mealinmeallist in een datalist voor bewaard te worden in een bestand
        return convertCombinListinDataList(itemList);
/*
        List<String> lineList = new ArrayList<>();
        for (int i = 0; i < itemList.size(); i++) {
            lineList.add(itemList.get(i).convertCombinInFileLine());
        }
        return lineList;
*/
    }

    private List<Shop> getShopsFromDataList(List<String> dataList){
        // Converteert een datalist met shops in een shoplist
        List<Shop> shops = new ArrayList<>();
        for (int j = 0; j < dataList.size(); j++) {
            shops.add(new Shop(dataList.get(j)));
        }
        return shops;
    }

    private List<Product> getProductsFromDataList(List<String> dataList){
        // Converteert een datalist met produkten in een produktlist
        List<Product> products = new ArrayList<>();
        for (int j = 0; j < dataList.size(); j++) {
            products.add(new Product(dataList.get(j)));
        }
        return products;
    }

    private List<ProductInShop> getProdInShopsFromDataList(List<String> dataList){
        // Converteert een datalist met prodinshops in een prodinshoplist
        List<ProductInShop> productInShopList = new ArrayList<>();
        for (int j = 0; j < dataList.size(); j++) {
            productInShopList.add(new ProductInShop(dataList.get(j)));
        }
        return productInShopList;
    }

    private List<Meal> getMealsFromDataList(List<String> dataList){
        // Converteert een datalist met gerechten in een meallist
        List<Meal> meals = new ArrayList<>();
        for (int j = 0; j < dataList.size(); j++) {
            meals.add(new Meal(dataList.get(j)));
        }
        return meals;
    }

    private List<ProductInMeal> getProdInMealFromDataList(List<String> dataList){
        // Converteert een datalist met prodinmeal in een prodinmeallist
        List<ProductInMeal> productsInMeal = new ArrayList<>();
        for (int j = 0; j < dataList.size(); j++) {
            productsInMeal.add(new ProductInMeal(dataList.get(j)));
        }
        return productsInMeal;
    }

    private List<MealInMeal> getMealInMealFromDataList(List<String> dataList){
        // Converteert een datalist met mealinmeal in een mealinmeallist
        List<MealInMeal> mealInMeals = new ArrayList<>();
        for (int j = 0; j < dataList.size(); j++) {
            mealInMeals.add(new MealInMeal(dataList.get(j)));
        }
        return mealInMeals;
    }

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
            if ((prodList.get(i).isToBuy() && onlyChecked) || (!onlyChecked)){
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

    public IDNumber determineShopBySpinnerSelection(){
        // Bepaalt de IDNumber vd shop obv de spinnerselection
        boolean shopFound = false;
        int i = 0;
        do {
            if (shopList.get(i).getEntityName().equals(this.spinnerSelection)){
                shopFound = true;
            }
            i++;
        } while (!shopFound && i < shopList.size());
        if (shopFound){
            return shopList.get(i - 1).getEntityId();
        }else {
            return StaticData.IDNUMBER_NOT_FOUND;
        }
    }

    public void clearAllProductInShop(){
        // Verwijdert alle prodinshop combinaties
        productInShopList.clear();
        storeProdInShop();
    }

    public List<CheckboxHelper> convertMealsToCheckboxs(List<Meal> mealList,
                                                           boolean onlyChecked){
        // Converteert een lijst met gerechten in een checkboxList
        List<CheckboxHelper> checkboxList = new ArrayList<>();
        for (int i = 0; i < mealList.size(); i++) {
            // Controle only checked
            if ((mealList.get(i).isToBuy() && onlyChecked) || (!onlyChecked)){
                checkboxList.add(new CheckboxHelper(
                        mealList.get(i).getDisplayLine(),
                        mealList.get(i).isToBuy(),SpecificData.STYLE_DEFAULT,
                        mealList.get(i).getEntityId()));
            }
        }
        return checkboxList;
    }

    public void clearAllProductInMeal(){
        // Verwijdert alle prodinmeal combinaties
        productInMealList.clear();
        storeProdInShop();
    }

    public int getProductMealCombinIndex(IDNumber inProductID, IDNumber inMealID){
        // Bepaalt de index vd produkt-shop combinatie als die bestaat
        return getIndexByIdsFromCList(productInMealList, inMealID, inProductID);
/*
        for (int i = 0; i < productInMealList.size(); i++) {
            if (productInMealList.get(i).getSecondID().getId() == inProductID.getId() &&
                    productInMealList.get(i).getFirstID().getId() == inMealID.getId()){
                return i;
            }
        }
        return StaticData.ITEM_NOT_FOUND;
*/
    }

    public List<ListItemHelper> getParentMealNamesByMeal(Meal inMeal){
        List<ListItemHelper> parentMealNames = new ArrayList<>();
        // Bepaalt de parentgerechten die een combinatie hebben met het opgegeven gerecht
        List<Meal> childMeals = new ArrayList<>();
        for (int i = 0; i < mealInMealList.size(); i++) {
            if (mealInMealList.get(i).getSecondID().getId() == inMeal.getEntityId().getId()){
                if(getMealByID(mealInMealList.get(i).getSecondID()) != null){
                    ListItemHelper itemHelper = new ListItemHelper(
                            getMealByID(mealInMealList.get(i).getSecondID()).getEntityName(),
                            "",
                            mealInMealList.get(i).getSecondID());
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

    public Meal getMealByID(IDNumber inMealID){
        return new Meal();
    }

    public void setSpinnerSelection(String spinnerSelection) {
        this.spinnerSelection = spinnerSelection;
    }

    public String getSpinnerSelection() {
        return spinnerSelection;
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
}
