package com.example.tlalos.myapplication.Model;

public class ExpenseListItem {

    private int id;
    private String field1;
    private String field2;
    private float value;

    public String getField2() {
        return field2;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }



    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }


    public ExpenseListItem() {

    }

    public ExpenseListItem(String field1,String field2,float value) {
        this.field1=field1;
        this.field2=field2;
        this.value=value;
    }



}
