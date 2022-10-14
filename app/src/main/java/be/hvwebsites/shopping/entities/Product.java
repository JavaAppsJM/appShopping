package be.hvwebsites.shopping.entities;

import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.statics.StaticData;

public class Product extends ShopEntity{
    private IDNumber preferredShopId;
    public static final int NO_PREFERRED_SHOP = StaticData.ITEM_NOT_FOUND;
    private boolean toBuy;
    private boolean previousToBuy;
    private boolean wanted; // wel of niet meer gewenst, kan tijdelijk zijn !
    private boolean cooled;
    public static final String PRODUCT_LATEST_ID = "product";

    public Product() {
    }

    public Product(String basedir, boolean b) {
        super(basedir, PRODUCT_LATEST_ID);
        preferredShopId = new IDNumber(NO_PREFERRED_SHOP);
        toBuy = false;
        wanted = true;
        cooled = false;
    }

    public Product(String fileLine){
        // Maakt een product obv een fileline - format:
        // <key><521><product><azerty><prefshop><123><tobuy><0><wanted><1>
        // fileLine splitsen in argumenten
        super();
        // Cooled default op false zetten, wordt wel overschreven als het in de file zit
        setCooled(false);
        // Gegevens uit de fileline halen en in het product steken
        String[] fileLineContent = fileLine.split("<");
        for (int i = 0; i < fileLineContent.length; i++) {
            if (fileLineContent[i].matches("key.*")){
                setEntityId(new IDNumber(fileLineContent[i+1].replace(">", "")));
            }
            if (fileLineContent[i].matches("product.*")){
                setEntityName(fileLineContent[i+1].replace(">",""));
            }
            if (fileLineContent[i].matches("prefshop.*")){
                this.preferredShopId = new IDNumber(fileLineContent[i+1].replace(">", ""));
            }
            if (fileLineContent[i].matches("tobuy.*")){
                this.toBuy = convertFileContentToBoolean(fileLineContent[i+1].replace(">",""));
            }
            if (fileLineContent[i].matches("wanted.*")){
                this.wanted = convertFileContentToBoolean(fileLineContent[i+1].replace(">",""));
            }
            if (fileLineContent[i].matches("cooled.*")){
                this.cooled = convertFileContentToBoolean(fileLineContent[i+1].replace(">",""));
            }
        }
    }

    public void setProduct(Product inProduct){
        setShopEntity(inProduct);
        setPreferredShopId(inProduct.getPreferredShopId());
        setToBuy(inProduct.isToBuy());
        setWanted(inProduct.isWanted());
        setCooled(inProduct.isCooled());
    }

    public String convertToFileLine(){
        String fileLine = "<key><" + getEntityId().getIdString()
                + "><product><" + getEntityName()
        + "><prefshop><" + this.preferredShopId.getIdString()
        + "><tobuy><" + convertBooleanToString(toBuy)
        + "><wanted><" + convertBooleanToString(wanted)
        + "><cooled><" + convertBooleanToString(cooled) + ">";
        return fileLine;
    }

    private String convertToBuyForSms(boolean b){
        if (b){
            return "toch meebrengen !";
        }else {
            return "moet toch niet meegebracht worden !";
        }
    }

    private String convertBooleanToBuy(boolean b){
        if (b){
            return "Buy";
        }else {
            return "Don't buy";
        }
    }

    public String convertBooleanToString(boolean b){
        if (b){
            return "1";
        }else {
            return "0";
        }
    }

    public boolean convertFileContentToBoolean(String fileContent){
        if (fileContent.equals("1")){
            return true;
        }else {
            return false;
        }
    }

    public IDNumber getPreferredShopId() {
        return preferredShopId;
    }

    public void setPreferredShopId(IDNumber preferredShopId) {
        this.preferredShopId = preferredShopId;
    }

    public String getDisplayLine(){
        String listDisplay = getEntityName()
                + " " + convertBooleanToBuy(this.toBuy);
        return listDisplay;
    }

    public String getSMSLine(){
        String smsLine = "Shopping app sms..., artikel : " + getEntityName()
                + " " + convertToBuyForSms(this.toBuy);
        return smsLine;
    }

    public void setToBuyRemember(boolean mealToBuy){
        // vooraleer to buy te wijzigen wordt deze onthouden in previous to buy
        this.previousToBuy = this.toBuy;
        this.toBuy = mealToBuy;
    }

    public String getToBuyAsString(){
        return convertBooleanToString(toBuy);
    }

    public String getWantedAsString(){
        return convertBooleanToString(wanted);
    }

    public boolean isWanted() {
        return wanted;
    }

    public boolean isToBuy() {
        return toBuy;
    }

    public boolean isPreviousToBuy() {
        return previousToBuy;
    }

    public void setPreviousToBuy(boolean previousToBuy) {
        this.previousToBuy = previousToBuy;
    }

    public void setWanted(boolean wanted) {
        this.wanted = wanted;
    }

    public void setToBuy(boolean toBuy) {
        this.toBuy = toBuy;
    }

    public boolean isCooled() {
        return cooled;
    }

    public void setCooled(boolean cooled) {
        this.cooled = cooled;
    }

    public String getCooledAsString(){
        return convertBooleanToString(cooled);
    }

    public String getProductAttributesForBtMsg(){
        String bTMsg = "";
        bTMsg = bTMsg.concat(getEntityId().getIdString());
        bTMsg = bTMsg.concat("><");
        bTMsg = bTMsg.concat(getEntityName());
        bTMsg = bTMsg.concat("><");
        bTMsg = bTMsg.concat(getPreferredShopId().getIdString());
        bTMsg = bTMsg.concat("><");
        bTMsg = bTMsg.concat(getToBuyAsString());
        bTMsg = bTMsg.concat("><");
        bTMsg = bTMsg.concat(getWantedAsString());
        bTMsg = bTMsg.concat("><");
        bTMsg = bTMsg.concat(getCooledAsString());
        bTMsg = bTMsg.concat(">");
        return bTMsg;
    }

    public void setBtContent(String bt2, String bt3, String bt4, String bt5, String bt6, String bt7){
        setEntityId(new IDNumber(bt2.replace(">", "")));
        setEntityName(bt3.replace(">",""));
        setPreferredShopId(new IDNumber(bt4.replace(">", "")));
        setToBuy(convertFileContentToBoolean(bt5.replace(">","")));
        setWanted(convertFileContentToBoolean(bt6.replace(">","")));
        setCooled(convertFileContentToBoolean(bt7.replace(">","")));
    }
}
