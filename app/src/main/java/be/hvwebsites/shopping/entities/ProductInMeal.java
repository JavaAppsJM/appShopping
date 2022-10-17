package be.hvwebsites.shopping.entities;

import be.hvwebsites.libraryandroid4.helpers.IDNumber;

public class ProductInMeal extends SuperCombination{
    // Artikels en gerecht combinaties
    private static final String MEAL = "meal";
    private static final String PRODUCT = "product";

    public ProductInMeal(IDNumber inProductId, IDNumber inMealId) {
        super(inMealId, inProductId, MEAL, PRODUCT);
    }

    public ProductInMeal(String fileLine){
        // Maakt een productInMeal obv een fileline - format:
        // <meal><521><product><12>
        // fileLine splitsen in argumenten
        super(fileLine, MEAL, PRODUCT);
/*
        String[] fileLineContent = fileLine.split("<");
        for (int i = 0; i < fileLineContent.length; i++) {
            if (fileLineContent[i].matches("meal.*")){
                this.mealId = new IDNumber(fileLineContent[i+1].replace(">", ""));
            }
            if (fileLineContent[i].matches("product.*")){
                this.productId = new IDNumber(fileLineContent[i+1].replace(">", ""));
            }
        }
*/
    }

/*
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
*/
}
