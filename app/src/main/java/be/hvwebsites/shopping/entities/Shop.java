package be.hvwebsites.shopping.entities;

import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.shopping.helpers.OpenTime;

public class Shop extends ShoppingEntity {
    private OpenTime monday;
    private OpenTime tuesday;
    private OpenTime wensday;
    private OpenTime thursday;
    private OpenTime friday;
    private OpenTime satday;
    private OpenTime sunday;
    public static final String SHOP_LATEST_ID = "shop";

    public Shop() {
    }

    public Shop(String basedir, boolean b) {
        super(basedir, SHOP_LATEST_ID);
    }

    public Shop(String fileLine){
        super();
        initializeOpenHours();
        convertFromFileLine(fileLine);
    }

    private void initializeOpenHours(){
        // Openingsuren initialisern
        this.monday = new OpenTime();
        this.tuesday = new OpenTime();
        this.wensday = new OpenTime();
        this.thursday = new OpenTime();
        this.friday = new OpenTime();
        this.satday = new OpenTime();
        this.sunday = new OpenTime();
    }

    public OpenTime getMonday() {
        return monday;
    }

    public void setMonday(OpenTime monday) {
        this.monday = monday;
    }

    public OpenTime getTuesday() {
        return tuesday;
    }

    public void setTuesday(OpenTime tuesday) {
        this.tuesday = tuesday;
    }

    public OpenTime getWensday() {
        return wensday;
    }

    public void setWensday(OpenTime wensday) {
        this.wensday = wensday;
    }

    public OpenTime getThursday() {
        return thursday;
    }

    public void setThursday(OpenTime thursday) {
        this.thursday = thursday;
    }

    public OpenTime getFriday() {
        return friday;
    }

    public void setFriday(OpenTime friday) {
        this.friday = friday;
    }

    public OpenTime getSatday() {
        return satday;
    }

    public void setSatday(OpenTime satday) {
        this.satday = satday;
    }

    public OpenTime getSunday() {
        return sunday;
    }

    public void setSunday(OpenTime sunday) {
        this.sunday = sunday;
    }

    public void setShopEntity(Shop shop) {
        super.setShopEntity(shop);
    }

    public void convertFromFileLine(String fileLine) {
        // Maakt een shop obv een fileline - format: <key><1022021><shop><azerty>
        // fileLine splitsen in argumenten
        String[] fileLineContent = fileLine.split("<");
        for (int i = 0; i < fileLineContent.length; i++) {
            if (fileLineContent[i].matches("key.*")){
                setEntityId(new IDNumber(fileLineContent[i+1].replace(">", "")));
            }
            if (fileLineContent[i].matches("shop.*")){
                setEntityName(fileLineContent[i+1].replace(">",""));
            }
            // TODO: Aanpassen voor openingsuren
        }
    }

    public String convertToFileLine() {
        String fileLine = "<key><" + getEntityId().getIdString()
                + "><shop><" + getEntityName() + ">";
        // TODO: Aanpassen voor openingsuren
        return fileLine;
    }

    public void setShop(Shop inShop){
        setShopEntity(inShop);
    }

    public String getDisplayLine(){
        return getEntityName();
    }

    public String getAttributesForBtMsg(){
        String bTMsg = "";
        bTMsg = bTMsg.concat(getEntityId().getIdString());
        bTMsg = bTMsg.concat("><");
        bTMsg = bTMsg.concat(getEntityName());
        bTMsg = bTMsg.concat(">");
        return bTMsg;
    }

    public void setBtContent(String bt2, String bt3){
        setEntityId(new IDNumber(bt2.replace(">", "")));
        setEntityName(bt3.replace(">",""));
    }
}
