package com.shurik16.PhoneBook.vaadin;

import com.shurik16.PhoneBook.backend.Book;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.Window;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.springframework.http.HttpHeaders.USER_AGENT;

public class SelctForm extends Window {
    Book book;
    Book bookuser;
    RadioButtonGroup<String> single =
            new RadioButtonGroup<>("Номер:");


    public SelctForm(Book book, Book bookuser) {
        super("Выбор телефона");
        this.book = book;
        this.bookuser = bookuser;
        center();
        single.setItems("Короткий", "Городской", "Сотовый");
        single.setSelectedItem("Коротки");
        setClosable(false);
        setContent(new MVerticalLayout(single,
                new MHorizontalLayout(
                        new Button("Вызов", event -> calls()),
                        new Button("Закрыть", event -> close()))));
    }

    private void calls() {

        if (book != null) {
            String server = "";
            if (bookuser.getIp().startsWith("172.16.2") ||
                    bookuser.getIp().startsWith("127.0.0") ||
                    bookuser.getIp().startsWith("0:0:0:0:0:0:0"))
                server = "172.16.2.170";
            else if (bookuser.getIp().startsWith("172.16.5"))
                server = "172.16.5.5";
            else if (bookuser.getIp().startsWith("192.168.0"))
                server = "192.168.0.60";
            else
                return;
            String phone = single.getSelectedItem().get();
            if (phone.startsWith("Кор"))
                phone = book.getShortPhone();
            else if (phone.startsWith("Гор"))
                phone = book.getPhone();
            else if (phone.startsWith("Сот"))
                phone = book.getMobilePhone();
            else
                return;

            String str = "http://" + server + "/phonebook/index.php?callnum="
                    + phone + "&ext=" + bookuser.getShortPhone();

            try {
                sendGet(str);
            } catch (Exception e1) {
            }
        }
        close();
    }

    private void sendGet(String url) throws Exception {

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();

    }
}
