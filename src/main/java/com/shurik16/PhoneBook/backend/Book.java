package com.shurik16.PhoneBook.backend;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "BOOK")

public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull(message = "Company is required")
    @Size(min = 3, max = 50, message = "company must be longer than 3 and less than 40 characters")
    private String company;

    @NotNull(message = "Name is required")
    @Size(min = 3, max = 50, message = "name must be longer than 3 and less than 40 characters")
    private String name;

    private String email;

    private String phone;

    @Size(min=3, max=4)
    private String shortphone;

    private String mobilephone;

    private String ip;

    private String position;

    private String city;

    private String office;

    private String cabinet;

    private String department;

    public Book() {}

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getCabinet() {
        return cabinet;
    }

    public void setCabinet(String cabinet) {
        this.cabinet = cabinet;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getShortPhone() {
        return shortphone;
    }

    public void setShortPhone(String shortPhone) {
        this.shortphone = shortPhone;
    }

    public String getMobilePhone() {
        return mobilephone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilephone = mobilePhone;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", company='" + company + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", shortphone='" + shortphone + '\'' +
                ", mobilephone='" + mobilephone + '\'' +
                ", ip='" + ip + '\'' +
                ", position='" + position + '\'' +
                ", city='" + city + '\'' +
                ", office='" + office + '\'' +
                ", cabinet='" + cabinet + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}
