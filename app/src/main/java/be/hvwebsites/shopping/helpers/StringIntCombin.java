package be.hvwebsites.shopping.helpers;

public class StringIntCombin {
    private int textID;
    private String text;
    private int teller1;
    private int teller2;
    private float procent;

    public StringIntCombin() {
        teller1 = 0;
        teller2 = 0;
        procent = 0;
    }

    public int getTextID() {
        return textID;
    }

    public void setTextID(int textID) {
        this.textID = textID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getTeller1() {
        return teller1;
    }

    public void setTeller1(int teller1) {
        this.teller1 = teller1;
    }

    public int getTeller2() {
        return teller2;
    }

    public void setTeller2(int teller2) {
        this.teller2 = teller2;
    }

    public float getProcent() {
        return procent;
    }

    public void setProcent(float procent) {
        this.procent = procent;
    }

    public String getFormattedString(int inShopNameMaxLength) {
        String fillString = "                                           ";
        String shopName = this.text;
        int teller1Length = 3;
        int teller2Length = 4;
        int procenLength = 7;

        // Beperken vn winkelnaam
        if (shopName.length() > inShopNameMaxLength){
            shopName = shopName.substring(0, (inShopNameMaxLength - 1));
        }

        return shopName
                .concat(fillString.substring(0, (inShopNameMaxLength - shopName.length())))
                .concat(fillString.substring(0, (teller1Length - String.valueOf(this.teller1).length())))
                .concat(String.valueOf(this.teller1))
                .concat(fillString.substring(0, (teller2Length - String.valueOf(this.teller2).length())))
                .concat(String.valueOf(this.teller2))
                .concat(fillString.substring(0, (procenLength - String.valueOf(this.procent).length())))
                .concat(String.valueOf(this.procent))
                .concat(" %");
    }

    public void setCombin(StringIntCombin inCombin){
        setTextID(inCombin.getTextID());
        setText(inCombin.getText());
        setTeller1(inCombin.getTeller1());
        setTeller2(inCombin.getTeller2());
        setProcent(inCombin.getProcent());
    }
}
