package com.longjiabo.fund.bean.chart;

import java.util.ArrayList;
import java.util.List;

public class Chart {
    public Chart() {
        this.series = new ArrayList<>();
    }

    public List<Series> getSeries() {
        return series;
    }

    public void setSeries(List<Series> series) {
        this.series = series;
    }

    public XAxis getxAxis() {
        return xAxis;
    }

    public void setxAxis(XAxis xAxis) {
        this.xAxis = xAxis;
    }

    public Legend getLegend() {
        return legend;
    }

    public void setLegend(Legend legend) {
        this.legend = legend;
    }

    protected List<Series> series;
    protected XAxis xAxis;
    protected Legend legend;
}
