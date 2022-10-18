package be.hvwebsites.shopping.services;

import java.util.ArrayList;
import java.util.List;

import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.statics.StaticData;
import be.hvwebsites.shopping.constants.SpecificData;
import be.hvwebsites.shopping.entities.ProductInShop;
import be.hvwebsites.shopping.entities.Shop;
import be.hvwebsites.shopping.entities.ShopEntity;
import be.hvwebsites.shopping.entities.SuperCombination;

public class FlexiListHandler {

    public int determineHighestID(List<ShopEntity> inList){
        int highestID = 0;
        for (int i = 0; i < inList.size(); i++) {
            if (inList.get(i).getEntityId().getId() > highestID ){
                highestID = inList.get(i).getEntityId().getId();
            }
        }
        return highestID;
    }

    public int getIndexById(List<ShopEntity> inList, IDNumber inID){
        // Bepaalt de index vh element met een opgegeven IDNumber
        for (int i = 0; i < inList.size(); i++) {
            if (inList.get(i).getEntityId().getId() == inID.getId()){
                return i;
            }
        }
        return StaticData.ITEM_NOT_FOUND;
    }

    public List<String> getNameListFromList(List<ShopEntity> inList, int indisplay){
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

    public List<ShopEntity> sortShopEntityList(List<ShopEntity> inList){
        // Sorteert een list op entityname alfabetisch
        ShopEntity tempEntity = new ShopEntity();
        List<ShopEntity> outList = new ArrayList<>();
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

    private List<String> convertCombinListinDataList(List<SuperCombination> itemList){
        // Converteert een SuperCombinationList in een datalist voor bewaard te worden in een bestand
        List<String> lineList = new ArrayList<>();
        for (int i = 0; i < itemList.size(); i++) {
            lineList.add(itemList.get(i).convertCombinInFileLine());
        }
        return lineList;
    }
}
