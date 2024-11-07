
package com.appdevclaymaster.stocksbetavaluecalculator;

import java.util.Date;

/**
 *
 * @author Suraj Prajapati (Claymaster)
 */
class OHLC {
    Date date;
    double open;
    double high;
    double low;
    double close;
    double volume;

    public OHLC(Date date, double open, double high, double low, double close, double volume) {
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }
}