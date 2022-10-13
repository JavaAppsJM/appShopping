package be.hvwebsites.shopping.entities;

import be.hvwebsites.libraryandroid4.helpers.IDNumber;

public class ProductInMeal {
    private IDNumber productId;
    private IDNumber mealId;

    public ProductInMeal(IDNumber productId, IDNumber shopId) {
        this.mealId = shopId;
        this.productId = productId;
    }

    public ProductInMeal(String fileLine){
        // Maakt een productInMeal obv een fileline - format:
        // <meal><521><product><12>
        // fileLine splitsen in argumenten
        String[] fileLineContent = fileLine.split("<");
        for (int i = 0; i < fileLineContent.length; i++) {
            if (fileLineContent[i].matches("meal.*")){
                this.mealId = new IDNumber(fileLineContent[i+1].replace(">", ""));
            }
            if (fileLineContent[i].matches("product.*")){
                this.productId = new IDNumber(fileLineContent[i+1].replace(">", ""));
            }
        }
    }

    public String convertProdMealInFileLine(){
        return "<product><" + this.productId.getIdString()
                + "><meal><" + this.mealId.getIdString() + ">";
    }

    public IDNumber getMealId() {
        return mealId;
    }

    public void setMealId(IDNumber mealId) {
        this.mealId = mealId;
    }

    public IDNumber getProductId() {
        return productId;
    }

    public void setProductId(IDNumber productId) {
        this.productId = productId;
    }
}
