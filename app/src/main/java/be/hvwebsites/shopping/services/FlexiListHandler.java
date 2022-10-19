package be.hvwebsites.shopping.services;

import java.util.ArrayList;
import java.util.List;

import be.hvwebsites.libraryandroid4.helpers.CheckboxHelper;
import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.statics.StaticData;
import be.hvwebsites.shopping.constants.SpecificData;
import be.hvwebsites.shopping.entities.ShoppingEntity;
import be.hvwebsites.shopping.entities.SuperCombination;

public class FlexiListHandler {

    // Terug bij ShopEntitiesViewModel
/*
    public int determineHighestID(List<? extends ShoppingEntity> inList){
        int highestID = 0;
        for (int i = 0; i < inList.size(); i++) {
            if (inList.get(i).getEntityId().getId() > highestID ){
                highestID = inList.get(i).getEntityId().getId();
            }
        }
        return highestID;
    }
*/

    public List<String> getNameListFromList(List<ShoppingEntity> inList, int indisplay){
        // bepaalt een lijst met entitynamen obv inlist
        List<String> nameList = new ArrayList<>();
        for (int i = 0; i < inList.size(); i++) {
            switch (indisplay){
                case SpecificData.DISPLAY_SMALL:
                    nameList.add(inList.get(i).getEntityName());
                    break;
                case SpecificData.DISPLAY_LARGE:
                    nameList.add(inList.get(i).getDisplayLine());
                    break;
                default:
                    break;
            }
        }
        return nameList;
    }

    public int getFirstPartnerIndexfromId(List<SuperCombination> inList, IDNumber inID, boolean first){
        for (int i = 0; i < inList.size(); i++) {
            if ((inList.get(i).getFirstID().getId() == inID.getId()) && (first)){
                return i;
            }else if ((inList.get(i).getSecondID().getId() == inID.getId()) && !(first)){
                return i;
            }
        }
        return StaticData.ITEM_NOT_FOUND;
    }

    public void deleteCombinById(List<SuperCombination> inList, IDNumber inID, boolean first){
        // Verwijdert de combinaties voor een first of second opgegeven ID
        int position = getFirstPartnerIndexfromId(inList, inID, first);
        while (position != StaticData.ITEM_NOT_FOUND){
            inList.remove(position);
            position = getFirstPartnerIndexfromId(inList, inID, first);
        }
    }

    public List<ShoppingEntity> sortShopEntityList(List<ShoppingEntity> inList){
        // Sorteert een list op entityname alfabetisch
        ShoppingEntity tempEntity = new ShoppingEntity();
        List<ShoppingEntity> outList = new ArrayList<>();
        outList.addAll(inList);
        for (int i = outList.size() ; i > 0; i--) {
            for (int j = 1; j < i ; j++) {
                if (outList.get(j).getEntityName().compareToIgnoreCase(outList.get(j-1).getEntityName()) < 0){
                    tempEntity = outList.get(j);
                    outList.set(j, outList.get(j-1));
                    outList.set(j-1, tempEntity);
                }
            }
        }
        return outList;
    }

/*
    // TODO: niet rijp voor flexi !
    public List<CheckboxHelper> convertListToCheckboxs(List<ShoppingEntity> inList,
                                                           int inDisplayType,
                                                           boolean onlyChecked){
        // Converteert een lijst met Shopentities in een checkboxList obv een displaytype
        // Het displaytype bepaalt of de preferred shop meegetoond wordt (LARGE) of niet (SMALL)
        // enkel voor produkten
        List<CheckboxHelper> checkboxList = new ArrayList<>();
        String productDisplayLine = "";
        String cbTextStyle;
        for (int i = 0; i < inList.size(); i++) {
            cbTextStyle = SpecificData.STYLE_DEFAULT;
            // Controle only checked
            if ((inList.get(i).isToBuy() && onlyChecked) || (!onlyChecked)){
                if (inDisplayType == SpecificData.DISPLAY_LARGE){
                    // TODO: moet in viewmodel bepaald worden om de preferred shop op te halen
                    //  ve produkt
                    //productDisplayLine = getProductLargeDisplay(inList.get(i));
                }else {
                    productDisplayLine = inList.get(i).getEntityName();
                }
                if ((inDisplayType == SpecificData.DISPLAY_SMALL_BOLD) &&
                        (inList.get(i).isCooled())){
                    cbTextStyle = SpecificData.STYLE_COOLED_BOLD;
                }
                // Check eigenschap cooled, style = red
                if ((inDisplayType == SpecificData.DISPLAY_SMALL) &&
                        (inList.get(i).isCooled())){
                    cbTextStyle = SpecificData.STYLE_COOLED;
                }
                checkboxList.add(new CheckboxHelper(
                        productDisplayLine,
                        inList.get(i).isToBuy(),cbTextStyle,
                        inList.get(i).getEntityId()));
            }
        }
        return checkboxList;
    }
*/

    // Terug bij ShopEntitiesViewModel
/*
    public List<String> convertCombinListinDataList(List<SuperCombination> itemList){
        // Converteert een SuperCombinationList in een datalist voor bewaard te worden in een bestand
        List<String> lineList = new ArrayList<>();
        for (int i = 0; i < itemList.size(); i++) {
            lineList.add(itemList.get(i).convertCombinInFileLine());
        }
        return lineList;
    }
*/
}
