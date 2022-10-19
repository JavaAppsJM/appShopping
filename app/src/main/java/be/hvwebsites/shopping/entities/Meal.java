package be.hvwebsites.shopping.entities;

import be.hvwebsites.libraryandroid4.helpers.IDNumber;

public class Meal extends ShoppingEntity {
    private boolean toBuy;
    private boolean wanted; // wel of niet meer gewenst, kan tijdelijk zijn !
    public static final String MEAL_LATEST_ID = "meal";

    public Meal() {
    }

    public Meal(String basedir, boolean b) {
        super(basedir, MEAL_LATEST_ID);
        toBuy = false;
        wanted = true;
    }

    public Meal(String fileLine){
        // Maakt een meal obv een fileline - format:
        // <key><521><meal><azerty><tobuy><0><wanted><1>
        // fileLine splitsen in argumenten
        super();
        // Gegevens uit de fileline halen en in het product steken
        String[] fileLineContent = fileLine.split("<");
        for (int i = 0; i < fileLineContent.length; i++) {
            if (fileLineContent[i].matches("key.*")){
                setEntityId(new IDNumber(fileLineContent[i+1].replace(">", "")));
            }
            if (fileLineContent[i].matches("meal.*")){
                setEntityName(fileLineContent[i+1].replace(">",""));
            }
            if (fileLineContent[i].matches("tobuy.*")){
                this.toBuy = convertFileContentToBoolean(fileLineContent[i+1].replace(">",""));
            }
            if (fileLineContent[i].matches("wanted.*")){
                this.wanted = convertFileContentToBoolean(fileLineContent[i+1].replace(">",""));
            }
        }
    }

    public void setShopEntity(Meal inMeal){
        super.setShopEntity(inMeal);
        setToBuy(inMeal.isToBuy());
        setWanted(inMeal.isWanted());
    }

    public void setMeal(Meal inMeal){
        setShopEntity(inMeal);
        setToBuy(inMeal.isToBuy());
        setWanted(inMeal.isWanted());
    }

    public String convertToFileLine(){
        String fileLine = "<key><" + getEntityId().getIdString()
                + "><meal><" + getEntityName()
        + "><tobuy><" + convertBooleanToString(toBuy)
        + "><wanted><" + convertBooleanToString(wanted) + ">";
        return fileLine;
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

    public String getDisplayLine(){
        String listDisplay = getEntityName()
                + " " + convertBooleanToBuy(this.toBuy);
        return listDisplay;
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

    public void setWanted(boolean wanted) {
        this.wanted = wanted;
    }

    public void setToBuy(boolean toBuy) {
        this.toBuy = toBuy;
    }

    public String getMealAttributesForBtMsg(){
        String bTMsg = "";
        bTMsg = bTMsg.concat(getEntityId().getIdString());
        bTMsg = bTMsg.concat("><");
        bTMsg = bTMsg.concat(getEntityName());
        bTMsg = bTMsg.concat("><");
        bTMsg = bTMsg.concat(getToBuyAsString());
        bTMsg = bTMsg.concat("><");
        bTMsg = bTMsg.concat(getWantedAsString());
        bTMsg = bTMsg.concat(">");
        return bTMsg;
    }

    public void setBtContent(String bt2, String bt3, String bt5, String bt6){
        setEntityId(new IDNumber(bt2.replace(">", "")));
        setEntityName(bt3.replace(">",""));
        setToBuy(convertFileContentToBoolean(bt5.replace(">","")));
        setWanted(convertFileContentToBoolean(bt6.replace(">","")));
    }
}
