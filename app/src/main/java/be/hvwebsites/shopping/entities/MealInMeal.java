package be.hvwebsites.shopping.entities;

import be.hvwebsites.libraryandroid4.helpers.IDNumber;

public class MealInMeal extends SuperCombination{
    // Parent meal en child meal combinations
    private static final String PARENTMEAL = "parentmeal";
    private static final String CHILDMEAL = "childmeal";

    public MealInMeal(IDNumber inParentMealId, IDNumber inChildMealId) {
        super(inParentMealId, inChildMealId, PARENTMEAL, CHILDMEAL);
    }

    public MealInMeal(String fileLine){
        // Maakt een MealInMeal obv een fileline - format:
        // <parentmeal><521><childmeal><12>
        // fileLine splitsen in argumenten
        super(fileLine, PARENTMEAL, CHILDMEAL);
    }
}
