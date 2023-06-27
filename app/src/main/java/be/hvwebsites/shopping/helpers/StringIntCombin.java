package be.hvwebsites.shopping.helpers;

public class StringIntCombin {
    private int textID;
    private String Text;
    private int teller1;
    private int teller2;
    private double procent;

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
        return Text;
    }

    public void setText(String text) {
        Text = text;
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

    public double getProcent() {
        return procent;
    }

    public void setProcent(double procent) {
        this.procent = procent;
    }

    public String getFormattedString() {
        return this.Text
                .concat(" ")
                .concat(String.valueOf(this.teller1))
                .concat(" ")
                .concat(String.valueOf(this.teller2))
                .concat(" ")
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
