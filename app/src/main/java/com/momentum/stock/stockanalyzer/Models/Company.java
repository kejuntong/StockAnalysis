package com.momentum.stock.stockanalyzer.Models;

/**
 * Created by kevintong on 16-11-28.
 */
public class Company {

    String symbol;
    String name;

    public Company(String symbol, String name){
        this.symbol = symbol;
        this.name = name;
    }

    public String getSymbol(){
        return this.symbol;
    }
    public String getName(){
        return this.name;
    }

}
