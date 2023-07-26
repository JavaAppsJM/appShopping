package be.hvwebsites.shopping.helpers;

public class OpenTime {
    private int openFromHour;
    private int openFromMinutes;
    private int openTillHour;
    private int openTillMinutes;

    public OpenTime(int openFromHour, int openFromMinutes, int openTillHour, int openTillMinutes) {
        this.openFromHour = openFromHour;
        this.openFromMinutes = openFromMinutes;
        this.openTillHour = openTillHour;
        this.openTillMinutes = openTillMinutes;
    }

    public OpenTime(String inLine){
        // 00:00 - 00:00
        String[] inLineContent = inLine.split(" - ");
        String[] fromTime = inLineContent[0].split(":");
        this.openFromHour = Integer.parseInt(fromTime[0]);
        this.openFromMinutes = Integer.parseInt(fromTime[1]);
        String[] tillTime = inLineContent[1].split(":");
        this.openTillHour = Integer.parseInt(tillTime[0]);
        this.openTillMinutes = Integer.parseInt(tillTime[1]);
    }

    public OpenTime() {
        this.openFromHour = 0;
        this.openFromMinutes = 0;
        this.openTillHour = 0;
        this.openTillMinutes = 0;
    }

    public void setOpenFromHour(int openFromHour) {
        this.openFromHour = openFromHour;
    }

    public void setOpenFromMinutes(int openFromMinutes) {
        this.openFromMinutes = openFromMinutes;
    }

    public void setOpenTillHour(int openTillHour) {
        this.openTillHour = openTillHour;
    }

    public void setOpenTillMinutes(int openTillMinutes) {
        this.openTillMinutes = openTillMinutes;
    }

    public int getOpenFromHour() {
        return openFromHour;
    }

    public int getOpenFromMinutes() {
        return openFromMinutes;
    }

    public String getOpenFromHForm(){
        return String.valueOf(this.openFromHour);
    }

    public String getOpenTillHForm(){
        return String.valueOf(this.openTillHour);
    }

    public String getOpenFromMinForm(){
        return getMinutesFormatted(String.valueOf(this.openFromMinutes));
    }

    public String getOpenTillMinForm(){
        return getMinutesFormatted(String.valueOf(this.openTillMinutes));
    }

    private String getMinutesFormatted(String inString){
        if (inString.length() < 2){
            inString = "0" + inString;
        }
        return inString;
    }

    public int getOpenTillHour() {
        return openTillHour;
    }

    public int getOpenTillMinutes() {
        return openTillMinutes;
    }

    public String getOpenTime(){
        return this.openFromHour + ":" + getOpenFromMinForm();
    }

    public String getClosedTime(){
        return this.openTillHour + ":" + getOpenTillMinForm();
    }

    public String getOpenHoursString(){
        return getOpenTime() + " - " + getClosedTime();
    }

    public void setOpenTime(OpenTime inOpenTime){
        this.openFromHour = inOpenTime.getOpenFromHour();
        this.openFromMinutes = inOpenTime.getOpenFromMinutes();
        this.openTillHour = inOpenTime.getOpenTillHour();
        this.openTillMinutes = inOpenTime.getOpenTillMinutes();
    }

    public boolean isOpenTime(int inHour, int inMinutes){
        if ((getOpenFromHour() < inHour)
                && (getOpenTillHour() > inHour)){
            return true;
        }else if ((getOpenFromHour() == inHour)
                && (getOpenFromMinutes() <= inMinutes)){
            return true;
        }else if ((getOpenTillHour() == inHour)
                && (getOpenTillMinutes() >= inMinutes)){
            return true;
        }else {
            return false;
        }
    }

    public boolean isCloseToday(){
        return getOpenFromHour() == 0;
    }
}
