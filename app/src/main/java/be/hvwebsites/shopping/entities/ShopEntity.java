          package be.hvwebsites.shopping.entities;

import be.hvwebsites.libraryandroid4.helpers.IDNumber;

          public class ShopEntity {
              // Dit is een generiek object dat een super is van zowel Product als Shop
              private IDNumber entityId;
              private String entityName;


              public ShopEntity() {
              }

              public ShopEntity(String basedir, String entity){
                  entityId = new IDNumber(basedir, entity);
                  entityName = "";
              }

              public void setShopEntity(ShopEntity shopEntity){
                  setEntityId(shopEntity.getEntityId());
                  setEntityName(shopEntity.getEntityName());
              }

              public IDNumber getEntityId() {
                  return entityId;
              }

              public void setEntityId(IDNumber entityId) {
                  this.entityId = entityId;
              }

              public String getEntityName() {
                  return entityName;
              }

              public void setEntityName(String entityName) {
                  // Beginletter vd naam hoofdletter maken
                  this.entityName = entityName.substring(0,1).toLowerCase().toUpperCase().concat(entityName.substring(1));
                  int sdf=0;
              }

              public void convertFromFileLine(String fileLine){

              }

              public String convertToFileLine(){
                  return "";
              }

              public String getDisplayLine() {
                  return "";
              }
          }
