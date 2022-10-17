package be.hvwebsites.shopping.entities;

import be.hvwebsites.libraryandroid4.helpers.IDNumber;

public class SuperCombination {
    // Superklasse die een combinatie van 2 IDnumbers  behandeld
    private IDNumber firstID;
    private IDNumber secondID;
    private String firstName = "";
    private String secondName = "";

    public SuperCombination(IDNumber inFirstId, IDNumber inSecondId, String inFirstName, String inSecondName) {
        this.firstID = inFirstId;
        this.secondID = inSecondId;
        this.firstName = inFirstName;
        this.secondName = inSecondName;
    }

    public SuperCombination(String fileLine, String inFirstName, String inSecondName){
        // Maakt een combinatie obv een fileline - format:
        // <inFirstName><521><inSecondName><12>
        // fileLine splitsen in argumenten
        String firstNameTag = inFirstName.concat(".*");
        String secondNameTag = inSecondName.concat(".*");
        String[] fileLineContent = fileLine.split("<");
        for (int i = 0; i < fileLineContent.length; i++) {
            if (fileLineContent[i].matches(firstNameTag)){
                this.firstID = new IDNumber(fileLineContent[i+1].replace(">", ""));
                this.firstName = inFirstName;
            }
            if (fileLineContent[i].matches(secondNameTag)){
                this.secondID = new IDNumber(fileLineContent[i+1].replace(">", ""));
                this.secondName = inSecondName;
            }
        }
    }

    public String convertCombinInFileLine(){
        return "<" + this.firstName + "><" +
                this.firstID.getIdString() + "><" +
                this.secondName + "><" +
                this.secondID.getIdString() + ">";
    }

    public IDNumber getFirstID() {
        return firstID;
    }

    public void setFirstID(IDNumber firstID) {
        this.firstID = firstID;
    }

    public IDNumber getSecondID() {
        return secondID;
    }

    public void setSecondID(IDNumber secondID) {
        this.secondID = secondID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }
}
