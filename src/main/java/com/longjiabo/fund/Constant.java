package com.longjiabo.fund;

public interface Constant {

    //Type=1买时，f_amount=f_price*f_volumn
    int TYPE_BUY = 1;
    //Type=2卖时，f_amount=f_price*f_volumn*（-1）
    int TYPE_SELL = 2;
    //Type=3现金分红，f_amount=现金分红的金额*（-1）
    int TYPE_CASH = 3;
    //Type=4红利再投时，f_amount=NULL
    int TYPE_CASHAGAIN = 4;

    String Target_CASH = "2";
    String Target_Other = "1";

    long oneDay = 24 * 60 * 60 * 1000;

}
