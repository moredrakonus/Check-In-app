package com.konus.pereklichka.rv_models;

public class GroupModel {
    String memberAmountTxt, group_name_txt;

    public GroupModel(String memberAmountTxt, String group_name_txt) {
        this.memberAmountTxt = memberAmountTxt;
        this.group_name_txt = group_name_txt;

    }

    public String getmemberAmountTxt() {
        return memberAmountTxt;
    }

    public String getgroup_name_txt() {
        return group_name_txt;
    }

}
