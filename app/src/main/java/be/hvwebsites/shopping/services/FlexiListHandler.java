package be.hvwebsites.shopping.services;

import java.util.ArrayList;
import java.util.List;

import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.statics.StaticData;
import be.hvwebsites.shopping.entities.ShopEntity;

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

    public List<String> getNameListFromList(List<ShopEntity> inList){
        // bepaalt een lijst met entitynamen obv inlist
        List<String> nameList = new ArrayList<>();
        for (int i = 0; i < inList.size(); i++) {
            nameList.add(inList.get(i).getEntityName());
        }
        return nameList;
    }

}
