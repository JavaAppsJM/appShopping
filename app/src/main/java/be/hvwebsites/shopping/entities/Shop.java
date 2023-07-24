package be.hvwebsites.shopping.entities;

import java.util.Calendar;

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
        initializeOpenHours();
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

    private void setOpenHours(Shop shop){
        this.monday.setOpenTime(shop.getMonday());
        this.tuesday.setOpenTime(shop.getTuesday());
        this.wensday.setOpenTime(shop.getWensday());
        this.thursday.setOpenTime(shop.getThursday());
        this.friday.setOpenTime(shop.getFriday());
        this.satday.setOpenTime(shop.getSatday());
        this.sunday.setOpenTime(shop.getSunday());
    }

    public void convertFromFileLine(String fileLine) {
        // Maakt een shop obv een fileline - format: <key><1022021><shop><azerty><ma><00:00 - 00:00>...
        // fileLine splitsen in argumenten
        String[] fileLineContent = fileLine.split("<");
        for (int i = 0; i < fileLineContent.length; i++) {
            if (fileLineContent[i].matches("key.*")){
                setEntityId(new IDNumber(fileLineContent[i+1].replace(">", "")));
            }
            if (fileLineContent[i].matches("shop.*")){
                setEntityName(fileLineContent[i+1].replace(">",""));
            }
            if (fileLineContent[i].matches("ma.*")){
                setMonday(new OpenTime(fileLineContent[i+1].replace(">","")));
            }
            if (fileLineContent[i].matches("di.*")){
                setTuesday(new OpenTime(fileLineContent[i+1].replace(">","")));
            }
            if (fileLineContent[i].matches("wo.*")){
                setWensday(new OpenTime(fileLineContent[i+1].replace(">","")));
            }
            if (fileLineContent[i].matches("do.*")){
                setThursday(new OpenTime(fileLineContent[i+1].replace(">","")));
            }
            if (fileLineContent[i].matches("vr.*")){
                setFriday(new OpenTime(fileLineContent[i+1].replace(">","")));
            }
            if (fileLineContent[i].matches("za.*")){
                setSatday(new OpenTime(fileLineContent[i+1].replace(">","")));
            }
            if (fileLineContent[i].matches("zo.*")){
                setSunday(new OpenTime(fileLineContent[i+1].replace(">","")));
            }
        }
    }

    public String convertToFileLine() {
        String fileLine = "<key><" + getEntityId().getIdString()
                + "><shop><" + getEntityName()
                + "><ma><" + getMonday().getOpenHoursString()
                + "><di><" + getTuesday().getOpenHoursString()
                + "><wo><" + getWensday().getOpenHoursString()
                + "><do><" + getThursday().getOpenHoursString()
                + "><vr><" + getFriday().getOpenHoursString()
                + "><za><" + getSatday().getOpenHoursString()
                + "><zo><" + getSunday().getOpenHoursString()
                + ">";
        return fileLine;
    }

    public void setShop(Shop inShop){
        super.setShopEntity(inShop);
        setOpenHours(inShop);
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

    public boolean isOpen(){
        Calendar calendarDate = Calendar.getInstance();
        // Bepaal hoe laat het is
        int hour = calendarDate.get(Calendar.HOUR);
        int minutes = calendarDate.get(Calendar.MINUTE);

        // Bepaal dag van vandaag
        switch (calendarDate.get(Calendar.DAY_OF_WEEK_IN_MONTH)){
            case 0:
                // Monday
                return getMonday().isOpenTime(hour, minutes);
            case 1:
                return getTuesday().isOpenTime(hour, minutes);
            case 2:
                return getWensday().isOpenTime(hour, minutes);
            case 3:
                return getThursday().isOpenTime(hour, minutes);
            case 4:
                return getFriday().isOpenTime(hour, minutes);
            case 5:
                return getSatday().isOpenTime(hour, minutes);
            case 6:
                return getSunday().isOpenTime(hour, minutes);
            default:
                return false;
        }
    }

    public String isOpenString(){
        if (isOpen()){
            return "De winkel is open.";
        }else {
            return "De winkel is gesloten !";
        }
    }

}
