package com.example.saswat.mobileapp;

public class RegisteredUser {
    public String fullname, email, designation,docName,docPhone;

    public RegisteredUser(){

    }

    public RegisteredUser(String fullname, String email, String designation,String docName, String docPhone) {
        this.fullname = fullname;
        this.email = email;
        this.designation = designation;
        this.docName=docName;
        this.docPhone=docPhone;
    }

    public String getEmail() {
        return email;
    }


        public void setEmail( String usrEmail){
        this.email=usrEmail;
    }

    public void setFullname(String usrFullname){
        this.fullname=usrFullname;
    }

    public void setDesignation( String usrDesignation){
        this.designation=usrDesignation;
    }

    public String getFullname() {
        return fullname;
    }

    public String getDesignation() {
        return designation;
    }


    public String getDocName(){
        return  docName;
    }

    public String getDocPhone() {
        return docPhone;
    }


    public void setDocName(String doctorName) {
        this.docName = doctorName;
    }

    public void setDocPhone(String doctorPh){
        this.docPhone=doctorPh;
    }
}