package com.shurik16.PhoneBook.vaadin;

import com.shurik16.PhoneBook.backend.Book;
import com.shurik16.PhoneBook.backend.BookRepository;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.*;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
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
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;

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

    final int PAGESIZE = 45;

    private MGrid<Book> list = new MGrid<>(Book.class)
            .withProperties("city", "company", "name", "position", "email", "phone", "shortPhone", "mobilePhone")
            .withColumnHeaders("Город", "Компания", "Имя", "Должность", "Почта", "Телефон", "Короткий н.", "Мобильный")
            .withFullWidth();

    private MTextField filterByName = new MTextField()
            .withPlaceholder("Фильтр по имени");
    //    private Switch filterByDone = new Switch("Выполненые");
    private Button addNew = new MButton(VaadinIcons.PLUS, this::add);
    private Button edit = new MButton(VaadinIcons.PENCIL, this::edit);
    private Button call = new MButton(VaadinIcons.PHONE, this::calls);
    private Button mail = new MButton(VaadinIcons.ENVELOPE_O, this::mails);
    private Label label = new Label("Пользователь:");
    private Label labelName = new Label("Пользователь ");
    private Button delete = new ConfirmButton(VaadinIcons.TRASH,
            "Are you sure you want to delete the entry?", this::remove);


    public MainUI(BookRepository r, BookForm f, EventBus.UIEventBus b) {
        this.repo = r;
        this.bookForm = f;
        this.eventBus = b;
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        String username = vaadinRequest.getRemoteAddr();

        Book book = repo.findBookByIp(username);

        //System.out.println(book+" "+ username);

        MHorizontalLayout components = new MHorizontalLayout(filterByName, /*filterByCompany,*/call,  mail);
        String str;
        if (book != null) {
          if (book.getPosition() != null && book.getPosition().startsWith("Админ")) {
              components = new MHorizontalLayout(filterByName, /*filterByCompany,*/  addNew, edit, delete, call, mail);
            }
            str = book.getName();
        } else
            str = "не найден";


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


       // getUI().getPage().open("mailto:" + list.asSingleSelect().getValue().getEmail(), "_blank");
        Page.getCurrent().open("mailto:" + list.asSingleSelect().getValue().getEmail(), null);
    }

    public void calls(Button.ClickEvent e) {

        Book b = list.asSingleSelect().getValue();

        Book book = repo.findBookByName(labelName.getValue());

        SelctForm sub = new SelctForm(b, book);

        UI.getCurrent().addWindow(sub);

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

}
