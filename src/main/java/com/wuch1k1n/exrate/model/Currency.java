package com.wuch1k1n.exrate.model;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/10/6.
 */

public class Currency extends DataSupport {

    /**
     * 货币id
     */
    private int id;
    /**
     * 货币名称
     */
    private String name;
    /**
     * 货币代号
     */
    private String code;
    /**
     * 该货币兑换美元的汇率
     */
    private double exrate = -1;
    /**
     * 该货币是否被用户选中
     */
    private boolean selected;
    /**
     * 该货币兑换后的金额
     */
    private double afterChange = -1;

    // default constructor
    public Currency(){
    }

    public Currency(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getExrate() {
        return exrate;
    }

    public void setExrate(double exrate) {
        this.exrate = exrate;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public double getAfterChange() {
        return afterChange;
    }

    public void setAfterChange(double afterChange) {
        this.afterChange = afterChange;
    }
}

