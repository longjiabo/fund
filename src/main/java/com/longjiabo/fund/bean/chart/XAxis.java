package com.longjiabo.fund.bean.chart;

import java.util.List;

public class XAxis {
    private List<String> data;
    private String type;

    public XAxis(List<String> data, String type) {
        this.data = data;
        this.type = type;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
