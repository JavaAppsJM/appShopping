package be.hvwebsites.shopping.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import be.hvwebsites.libraryandroid4.helpers.CheckboxHelper;
import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.repositories.Cookie;
import be.hvwebsites.libraryandroid4.repositories.CookieRepository;
import be.hvwebsites.libraryandroid4.repositories.FlexiRepository;
import be.hvwebsites.libraryandroid4.returninfo.ReturnInfo;
import be.hvwebsites.libraryandroid4.statics.StaticData;
import be.hvwebsites.winkelen.constants.SpecificData;
import be.hvwebsites.winkelen.entities.Product;
import be.hvwebsites.winkelen.entities.ProductInShop;
import be.hvwebsites.winkelen.entities.Shop;
import be.hvwebsites.winkelen.entities.ShopEntity;

public class ShopEntitiesViewModel extends AndroidViewModel {
    private FlexiRepository repository;
    private String basedir;
    private String baseSwitch;
    // File declaraties
    File shopFile;
    File productFile;
    File productInShopFile;
    File shopExtFile;
    File productExtFile;
    File productInShopExtFile;
    // File names constants
    public static final String SHOP_FILE = "shop.txt";
    public static final String PRODUCT_FILE = "product.txt";
    public static final String PRODUCTSHOP_FILE = "productshop.txt";
    // Lijst om data in te zetten
    private List<Shop> shopList = new ArrayList<>();
    private List<Product> productList = new ArrayList<>();
    private List<ProductInShop> productInShopList = new ArrayList<>();
    // Lijst om bluetooth data in te zetten
    private List<Shop> shopListBt = new ArrayList<>();
    private List<Product> productListBt = new ArrayList<>();
    private List<ProductInShop> productInShopListBt = new ArrayList<>();
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
        return returnInfo;
    }

    public ReturnInfo saveInBaseDir(String basedir){
        ReturnInfo returnInfo = new ReturnInfo(0);
        // Filedefinities
        shopExtFile = new File(basedir, SHOP_FILE);
        productExtFile = new File(basedir, PRODUCT_FILE);
        productInShopExtFile = new File(basedir, PRODUCTSHOP_FILE);

        repository.storeData(shopExtFile, convertEntityListinDataList(shopList));
        // Zet hoogste ID in Cookie
        setHighestIDInCookie(Shop.SHOP_LATEST_ID, basedir, determineHighestShopID());

        repository.storeData(productExtFile, convertEntityListinDataList(productList));
        // Zet hoogste ID in Cookie
        setHighestIDInCookie(Product.PRODUCT_LATEST_ID, basedir, determineHighestProductID());

        repository.storeData(productInShopExtFile, convertProdShopListinDataList(productInShopList));

        return returnInfo;
    }

    public int determineHighestShopID(){
        int highestShopID = 0;
        for (int i = 0; i < shopList.size(); i++) {
            if (shopList.get(i).getEntityId().getId() > highestShopID ){
                highestShopID = shopList.get(i).getEntityId().getId();
            }
        }
        return highestShopID;
    }

    public int determineHighestProductID(){
        int highestProductID = 0;
        for (int i = 0; i < productList.size(); i++) {
            if (productList.get(i).getEntityId().getId() > highestProductID ){
                highestProductID = productList.get(i).getEntityId().getId();
            }
        }
        return highestProductID;
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
        saveInBaseDir(basedir);
        boolean debug = true;
    }

    private void setHighestIDInCookie(String entity, String basedir, int highestId){
        CookieRepository cookieRepository = new CookieRepository(basedir);
        if (cookieRepository.bestaatCookie(entity) != CookieRepository.COOKIE_NOT_FOUND){
            // Cookie vervangen met nieuwe hoogste id
            Cookie cookie = new Cookie(entity, String.valueOf(highestId));
            cookieRepository.deleteCookie(entity);
            cookieRepository.addCookie(cookie);
        }else {
            // Cookie not found (er is nog geen latest ID voor opgegeven entity
            // Cookie met hoogste id bewaren
            Cookie cookie = new Cookie(entity, String.valueOf(highestId));
            cookieRepository.addCookie(cookie);
        }

    }

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

    public Shop getShopByID(IDNumber shopID){
        // Bepaalt de shop voor een opgegeven IDNumber
        for (int i = 0; i < shopList.size(); i++) {
            if (shopList.get(i).getEntityId().getId() == shopID.getId()){
                return shopList.get(i);
            }
        }
        // indien niet gevonden
        return null;
    }

    public int getShopIndexById(IDNumber shopID){
        // Bepaalt de index vd shop voor een opgegeven IDNumber
        for (int i = 0; i < shopList.size(); i++) {
            if (shopList.get(i).getEntityId().getId() == shopID.getId()){
                return i;
            }
        }
        return StaticData.ITEM_NOT_FOUND;
    }

    public List<String> getShopNameList(){
        // bepaalt een lijst met shopnamen obv shoplist
        List<String> displayList = new ArrayList<>();
        for (int i = 0; i < shopList.size(); i++) {
            displayList.add(shopList.get(i).getDisplayLine());
        }
        return displayList;
    }

    public List<String> getShopNamesByProduct(Product inProduct){
        // Bepaalt de shops namen die een combinatie hebben met het opgegeven produkten
        // TODO: Wat indien geen shops gevonden ?
        List<String> shopsForProduct = new ArrayList<>();
        for (int i = 0; i < productInShopList.size(); i++) {
            if (productInShopList.get(i).getProductId().getId() == inProduct.getEntityId().getId()){
                shopsForProduct.add(getShopByID(productInShopList.get(i).getShopId()).getDisplayLine());
            }
        }
        return shopsForProduct;
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
        for (int i = 0; i < productInShopList.size(); i++) {
            if (productInShopList.get(i).getProductId().getId() == inProductID.getId() &&
                    productInShopList.get(i).getShopId().getId() == inShopID.getId()){
                return i;
            }
        }
        return StaticData.ITEM_NOT_FOUND;
    }

    public List<Product> getProductsByShop(Shop inShop){
        // Bepaalt de produkten die een combinatie hebben met de opgegeven winkel
        List<Product> productForShops = new ArrayList<>();
        for (int i = 0; i < productInShopList.size(); i++) {
            if (productInShopList.get(i).getShopId().getId() == inShop.getEntityId().getId()){
                if(getProductByID(productInShopList.get(i).getProductId()) != null){
                    productForShops.add(getProductByID(productInShopList.get(i).getProductId()));
                }
            }
        }
        return productForShops;
    }

    public Product getProductByID(IDNumber inProductID){
        // Bepaalt het produkt voor een opgegeven IDNumber
        for (int i = 0; i < productList.size(); i++) {
            if (productList.get(i).getEntityId().getId() == inProductID.getId()){
                return productList.get(i);
            }
        }
        return null; // geen product gevonden met ID
    }

    public List<Product> getProductsByPrefShop(Shop inPrefShop){
        // Bepaalt de produkten voor een opgegeven preferred shop
        List<Product> productForShops = new ArrayList<>();
        if (inPrefShop != null){
            for (int i = 0; i < productList.size(); i++) {
                if (productList.get(i).getPreferredShopId().getId() == inPrefShop.getEntityId().getId()){
                    productForShops.add(productList.get(i));
                }
            }
        }
        return productForShops;
    }

    public ReturnInfo storeProdInShop(){
        // Bewaart de produktInShopList
        ReturnInfo returnInfo = new ReturnInfo(0);
        repository.storeData(productInShopFile, convertProdShopListinDataList(productInShopList));
        return returnInfo;
    }

    public ReturnInfo storeShops(){
        // Bewaart de shoplist
        // Eerst de shoplist alfabetisch sorteren
        sortShopList(shopList);
        ReturnInfo returnInfo = new ReturnInfo(0);
        repository.storeData(shopFile, convertEntityListinDataList(shopList));
        return returnInfo;
    }

    public ReturnInfo storeProducts(){
        // Bewaart de productlist
        // Eerst de productlist alfabetisch sorteren
        sortProductList(productList);
        ReturnInfo returnInfo = new ReturnInfo(0);
        repository.storeData(productFile, convertEntityListinDataList(productList));
        return returnInfo;
    }

    public ReturnInfo deleteShop(int position){
        // Verwijdert een shop
        ReturnInfo returnInfo = new ReturnInfo(0);
        // Alle productinshops moeten voor die shop eerst verwijderd worden
        deleteProductsByShop(shopList.get(position));
        storeProdInShop();
        //
        shopList.remove(position);
        storeShops();
        return returnInfo;
    }

    public ReturnInfo deleteProduct(int position){
        // Verwijdert een produkt
        ReturnInfo returnInfo = new ReturnInfo(0);
        // Alle productinshops moeten voor die shop ook verwijderd worden
        deleteShopsByProduct(productList.get(position));
        storeProdInShop();
        // en tenslotte mag het produkt gedelete worden
        productList.remove(position);
        storeProducts();
        return returnInfo;
    }

    private void deleteProductsByShop(ShopEntity inShop){
        // Verwijdert de prodinshop combinaties voor een opgegeven shop
        int position = getFirstProductInshopByShop(inShop);
        while (position != StaticData.ITEM_NOT_FOUND){
            productInShopList.remove(position);
            position = getFirstProductInshopByShop(inShop);
        }
    }

    private int getFirstProductInshopByShop(ShopEntity inShop){
        // Bepaalt de eerste index vd produktinshop combinatie die gevonden wordt, voor een
        // opgegeven shop
        for (int i = 0; i < productInShopList.size(); i++) {
            if (productInShopList.get(i).getShopId().getId() == inShop.getEntityId().getId()){
                return i;
            }
        }
        return StaticData.ITEM_NOT_FOUND;
    }

    private void deleteShopsByProduct(Product inProduct){
        // Verwijdert de prodinshop combinaties voor een opgegeven produkt
        int position = getFirstProductInshopByProduct(inProduct);
        while (position != StaticData.ITEM_NOT_FOUND){
            productInShopList.remove(position);
            position = getFirstProductInshopByProduct(inProduct);
        }
    }

    private int getFirstProductInshopByProduct(Product inProduct){
        // Bepaalt de eerste index vd produktinshop combinatie die gevonden wordt, voor een
        // opgegeven produkt
        for (int i = 0; i < productInShopList.size(); i++) {
            if (productInShopList.get(i).getProductId().getId() == inProduct.getEntityId().getId()){
                return i;
            }
        }
        return StaticData.ITEM_NOT_FOUND;
    }

    private void sortShopList(List<Shop> shopList){
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

    private void sortProductList(List<Product> productList){
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

    private List<String> convertEntityListinDataList(List<? extends ShopEntity> shopEntityList){
        // Converteert een shopentitylist in een datalist voor bewaard te worden in een bestand
        List<String> lineList = new ArrayList<>();
        for (int i = 0; i < shopEntityList.size(); i++) {
            lineList.add(shopEntityList.get(i).convertToFileLine());
        }
        return lineList;
    }

    private List<String> convertProdShopListinDataList(List<ProductInShop> itemList){
        // Converteert een prodinshoplist in een datalist voor bewaard te worden in een bestand
        List<String> lineList = new ArrayList<>();
        for (int i = 0; i < itemList.size(); i++) {
            lineList.add(itemList.get(i).convertProdShopInFileLine());
        }
        return lineList;
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

    public List<CheckboxHelper> convertProductsToCheckboxs(List<Product> prodList, int inDisplayType){
        // Converteert een lijst met produkten in een checkboxList obv een displaytype
        // Het displaytype bepaalt of de preferred shop meegetoond wordt (LARGE) of niet (SMALL)
        List<CheckboxHelper> checkboxList = new ArrayList<>();
        String productDisplayLine;
        String cbTextStyle = "";
        for (int i = 0; i < prodList.size(); i++) {
            if (inDisplayType == SpecificData.PRODUCT_DISPLAY_LARGE){
                productDisplayLine = getProductLargeDisplay(prodList.get(i));
            }else {
                productDisplayLine = prodList.get(i).getEntityName();
            }
            if (inDisplayType == SpecificData.PRODUCT_DISPLAY_SMALL_BOLD){
                cbTextStyle = StaticData.BOLD_TEXT;
            }
            checkboxList.add(new CheckboxHelper(
                    productDisplayLine,
                    prodList.get(i).isToBuy(),cbTextStyle,
                    prodList.get(i).getEntityId()));
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
}
