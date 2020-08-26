package com.shurik16.PhoneBook.vaadin;

import com.shurik16.PhoneBook.backend.Book;
import com.shurik16.PhoneBook.backend.BookRepository;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.*;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Value;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;
import org.vaadin.teemu.switchui.Switch;
import org.vaadin.viritin.button.ConfirmButton;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.components.DisclosurePanel;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.label.RichText;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.Cookie;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;

import static org.springframework.http.HttpHeaders.USER_AGENT;


/**
 * Created by popovav on 27.07.2017.
 */

@Title("Телефонный справочник")
@Theme("valo")
@SpringUI
public class MainUI extends UI {

    private static final long serialVersionUID = 1L;

    BookRepository repo;
    BookForm bookForm;
    EventBus.UIEventBus eventBus;

    @Value("${phonebook.asteriskip}")
    private String asteriskip;

    private String ipArray [];

    final int PAGESIZE = 45;

    private MGrid<Book> list = new MGrid<>(Book.class)
            .withProperties("city", "company", "name", "position", "email", "phone", "shortPhone", "mobilePhone")
            .withColumnHeaders("Город", "Компания", "Имя", "Должность", "Почта", "Телефон", "Короткий н.", "Мобильный")
            .withFullWidth();

    private MTextField filterByName = new MTextField()
            .withPlaceholder("Фильтр по имени");
    //    private Switch filterByDone = new Switch("Выполненые");
    private Book bookUser = null;
    private Button addNew = new MButton(VaadinIcons.PLUS, this::add);
    private Button edit = new MButton(VaadinIcons.PENCIL, this::edit);
    private MenuBar call = new MenuBar();
    private Button mail = new MButton(VaadinIcons.ENVELOPE_O, this::mails);
    private Label label = new Label("Пользователь:");
    private Label labelName = new Label("Пользователь ");
    private Button delete = new ConfirmButton(VaadinIcons.TRASH,
            "Удалить выбранную запись?", this::remove);
    private Button buttonClear = new MButton(VaadinIcons.CLOSE_SMALL, new Button.ClickListener() {
        @Override
        public void buttonClick(Button.ClickEvent event) {
            filterByName.setValue("");
        }
    });


    public MainUI(BookRepository r, BookForm f, EventBus.UIEventBus b) {
        this.repo = r;
        this.bookForm = f;
        this.eventBus = b;
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {

        //call.setCaption("Вызов");
        //call.setIcon(VaadinIcons.PHONE);

        if (asteriskip.length()>0)
            ipArray = asteriskip.split(";");

        call.setAutoOpen(true);

        list.setDescriptionGenerator(book -> {
            String rez = "Сотрудник: " + book.getName() + "\n" +
                    "Компания: " + book.getCompany() + "\n" +
                    "Отдел: " + book.getDepartment() + "\n" +
                    "Должность: " + book.getPosition() + "\n" +
                    "Город: " + book.getCity() + "\n" +
                    "Офис: " + book.getOffice() + " Кабинет: "
                    + book.getCabinet();

            return rez;
        });

        MenuBar.MenuItem callitem = call.addItem("", VaadinIcons.PHONE, this::calls);
        callitem.addItem("Короткий", VaadinIcons.PHONE, this::calls);
        callitem.addItem("Городской", VaadinIcons.PHONE_LANDLINE, this::calls);
        callitem.addItem("Сотовый", VaadinIcons.MOBILE_RETRO, this::calls);

        String username = vaadinRequest.getRemoteAddr();

        bookUser = repo.findBookByIp(username);


        MHorizontalLayout components = new MHorizontalLayout(filterByName, buttonClear,/*filterByCompany,*/call, mail);
        String str;
        if (bookUser != null) {
            if (bookUser.getPosition() != null && bookUser.getPosition().startsWith("Админ")) {
                components = new MHorizontalLayout(filterByName, buttonClear,/*filterByCompany,*/  addNew, edit, delete, call, mail);
            }
            str = bookUser.getName();
        } else
            str = "не найден (" + username + ")";


        labelName.setValue(str);

        DisclosurePanel aboutBox = new DisclosurePanel("Описание:", new RichText().withMarkDownResource("/welcome.md"));
        setContent(
                new MVerticalLayout(
                        new MHorizontalLayout(label, labelName),
                        aboutBox,
                        components,
                        list
                ).expand(list)
        );
        listEntities();
        list.sort("name");
        list.asSingleSelect().addValueChangeListener(e -> adjustActionButtonState());
        filterByName.addValueChangeListener(e -> {
            listEntities(e.getValue());
        });
        eventBus.subscribe(this);
    }

    protected void adjustActionButtonState() {
        boolean hasSelection = !list.getSelectedItems().isEmpty();
        edit.setEnabled(hasSelection);
        delete.setEnabled(hasSelection);
    }

    private void listEntities() {
        listEntities(filterByName.getValue());
    }

    private void listEntities(String nameFilter) {
        String likeFilter = "%" + nameFilter + "%";
        list.setRows(repo.findByNameLikeIgnoreCase(likeFilter));

        adjustActionButtonState();

    }

    /*
        private void listEntities(Boolean doneFilter) {
            String likeFilter = "%" + doneFilter + "%";
            list.setRows(repo.findByDone(doneFilter));

            adjustActionButtonState();

        }
    */
    public void add(Button.ClickEvent clickEvent) {
        edit(new Book());
    }

    public void edit(Button.ClickEvent e) {
        edit(list.asSingleSelect().getValue());
    }

    public void remove() {
        repo.delete(list.asSingleSelect().getValue());
        list.deselectAll();
        listEntities();
    }

    public void mails(Button.ClickEvent e) {
        Page.getCurrent().open("mailto:" + list.asSingleSelect().getValue().getEmail(), null);
    }

    public void calls(MenuBar.MenuItem e) {

        if (list.asSingleSelect().getValue() == null) {
            Notification.show("Контакт не выбран!", Notification.Type.HUMANIZED_MESSAGE);
            return;
        }

        Book bookSelect = list.asSingleSelect().getValue();

        //bookUser = repo.findBookByName(labelName.getValue());


        if (bookUser != null) {
            //Определим куда слать запрос. Если офисов много.
            String server = "";
            for(String ip:ipArray) {
                System.out.print(ip.split("/")[0]+"-");
                System.out.println(ip.split("/")[1]);
                if (bookUser.getIp().startsWith(ip.split("/")[0])) {
                    server = ip.split("/")[1];
                    break;
                }
            }
            /**
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
             */
            String phone = e.getText();
            if (phone.startsWith("Кор"))
                phone = bookSelect.getShortPhone();
            else if (phone.startsWith("Гор"))
                phone = bookSelect.getPhone();
            else if (phone.startsWith("Сот"))
                phone = bookSelect.getMobilePhone();
            else
                return;
            if (bookUser.getShortPhone().length() == 0 && phone.length() == 0) return;

            String str = "http://" + server + "/phonebook/index.php?callnum="
                    + phone + "&ext=" + bookUser.getShortPhone();
            try {
                sendGet(str);
            } catch (Exception e1) {
            }
        }

    }

    protected void edit(final Book bookEntry) {
        bookForm.setEntity(bookEntry);
        bookForm.openInModalPopup();
    }

    @EventBusListenerMethod(scope = EventScope.UI)
    public void onPersonModified(BookModifiedEvent event) {
        listEntities();
        bookForm.closePopup();
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
