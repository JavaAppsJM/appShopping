package be.hvwebsites.shopping.entities;

import be.hvwebsites.libraryandroid4.helpers.IDNumber;

public class ProductInShop {
    private IDNumber productId;
    private IDNumber shopId;

    public ProductInShop(IDNumber productId, IDNumber shopId) {
        this.shopId = shopId;
        this.productId = productId;
    }

    public ProductInShop(String fileLine){
        // Maakt een productInShop obv een fileline - format:
        // <shop><521><product><12>
        // fileLine splitsen in argumenten
        String[] fileLineContent = fileLine.split("<");
        for (int i = 0; i < fileLineContent.length; i++) {
            if (fileLineContent[i].matches("shop.*")){
                this.shopId = new IDNumber(fileLineContent[i+1].replace(">", ""));
            }
            if (fileLineContent[i].matches("product.*")){
                this.productId = new IDNumber(fileLineContent[i+1].replace(">", ""));
            }
        }
    }

    public String convertProdShopInFileLine(){
        String fileLine = "<product><" + this.productId.getIdString()
                + "><shop><" + this.shopId.getIdString() + ">";
        return fileLine;
    }

    public IDNumber getShopId() {
        return shopId;
    }

    public void setShopId(IDNumber shopId) {
        this.shopId = shopId;
    }

    public IDNumber getProductId() {
        return productId;
    }

    public void setProductId(IDNumber productId) {
        this.productId = productId;
    }
}
