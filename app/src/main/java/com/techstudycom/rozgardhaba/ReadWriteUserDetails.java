package com.techstudycom.rozgardhaba;

public class ReadWriteUserDetails {
    public  String fullName, ourEmail,dOB, gender , mobile,aadhaarNumber,  address;
   // private   String FullName,DOB, Gender, MobileNo, Address,AadhaarNo;

    public ReadWriteUserDetails(){};


   // public  ReadWriteUserDetails(String textDOB, String textGender, String textMobileNo, String textAadhaarNo, String textAddress){};



    public ReadWriteUserDetails(String textEmail, String textFullName ,String textDOB, String textGender,
                                String textMobile, String textAadhaarNo,  String textAddress){

        this.gender = textGender;
        this.dOB = textDOB;
        this.aadhaarNumber = textAadhaarNo;
        this.mobile = textMobile;
        this.address = textAddress;
        this.fullName = textFullName;
        this.ourEmail = textEmail;


    }

 /*  public ReadWriteUserDetails(String textFullName,String textDOB, String textGender,
                                String textMobileNo, String textAadhaarNo, String textAddress) {
        this.FullName = textFullName;
        this.Gender = textGender;
        this.DOB = textDOB;
        this.AadhaarNo = textAadhaarNo;
        this.MobileNo = textMobileNo;
        this.Address = textAddress;

    }

  */



}
