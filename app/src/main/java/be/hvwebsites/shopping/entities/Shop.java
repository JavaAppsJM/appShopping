package be.hvwebsites.shopping.entities;

import be.hvwebsites.libraryandroid4.helpers.IDNumber;

public class Shop extends ShoppingEntity {
    public static final String SHOP_LATEST_ID = "shop";

    public Shop() {
    }

    public Shop(String basedir, boolean b) {
        super(basedir, SHOP_LATEST_ID);
    }

    public Shop(String fileLine){
        super();
        convertFromFileLine(fileLine);
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
        }
    }

    public String convertToFileLine() {
        String fileLine = "<key><" + getEntityId().getIdString()
                + "><shop><" + getEntityName() + ">";
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
