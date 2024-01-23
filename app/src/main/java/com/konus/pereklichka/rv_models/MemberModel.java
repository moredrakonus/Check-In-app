package com.konus.pereklichka.rv_models;

public class MemberModel {
    String txtName, txtLName, special_id;
    boolean image;

    public MemberModel(String txtName, String txtLName, boolean image, String special_id) {
        this.txtName = txtName;
        this.txtLName = txtLName;
        this.image = image;
        this.special_id = special_id;
    }

    public String getTxtName() {
        return txtName;
    }

    public String getTxtLName() {
        return txtLName;
    }

    public boolean getImage() {
        return image;
    }

    public String getSpecial_id() {
        return special_id;
    }
}
