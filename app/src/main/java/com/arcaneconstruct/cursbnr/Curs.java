package com.arcaneconstruct.cursbnr;


public class Curs {
    private int id;
    private String date; // format zz//ll//aaaa
    private String currency; //moneda
    private String rate; //curs

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
    @Override
        public String toString() {
            return "Curs " +getCurrency()+ "-" + "RON" +" " +getRate()+" la data de "+getDate();
    }
}
