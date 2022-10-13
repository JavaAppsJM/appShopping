package be.hvwebsites.shopping.entities;

import be.hvwebsites.libraryandroid4.helpers.IDNumber;

public class MealInMeal {
    // Parent meal en child meal combinations
    private IDNumber parentMealId;
    private IDNumber childMealId;

    public MealInMeal(IDNumber productId, IDNumber shopId) {
        this.childMealId = shopId;
        this.parentMealId = productId;
    }

    public MealInMeal(String fileLine){
        // Maakt een MealInMeal obv een fileline - format:
        // <meal><521><meal><12>
        // fileLine splitsen in argumenten
        String[] fileLineContent = fileLine.split("<");
        for (int i = 0; i < fileLineContent.length; i++) {
            if (fileLineContent[i].matches("parentmeal.*")){
                this.childMealId = new IDNumber(fileLineContent[i+1].replace(">", ""));
            }
            if (fileLineContent[i].matches("childmeal.*")){
                this.parentMealId = new IDNumber(fileLineContent[i+1].replace(">", ""));
            }
        }
    }

    public String convertMealMealInFileLine(){
        return "<parentmeal><" + this.parentMealId.getIdString()
                + "><childmeal><" + this.childMealId.getIdString() + ">";
    }

    public IDNumber getChildMealId() {
        return childMealId;
    }

    public void setChildMealId(IDNumber childMealId) {
        this.childMealId = childMealId;
    }

    public IDNumber getParentMealId() {
        return parentMealId;
    }

    public void setParentMealId(IDNumber parentMealId) {
        this.parentMealId = parentMealId;
    }
}
