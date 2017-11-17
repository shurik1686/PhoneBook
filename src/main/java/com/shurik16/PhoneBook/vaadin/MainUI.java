package com.shurik16.PhoneBook.vaadin;

import com.shurik16.PhoneBook.backend.Book;
import com.shurik16.PhoneBook.backend.BookRepository;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
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
            .withProperties("id", "company", "name", "email", "phone", "shortPhone", "mobilePhone")
            .withColumnHeaders("Номер", "Название компании", "Имя", "Почта", "Телефон", "Короткий номер", "Мобильный")
            .withFullWidth();

    private MTextField filterByName = new MTextField()
            .withPlaceholder("Filter by name");
//    private Switch filterByDone = new Switch("Выполненые");
    private Button addNew = new MButton(VaadinIcons.PLUS, this::add);
    private Button edit = new MButton(VaadinIcons.PENCIL, this::edit);
    private Button delete = new ConfirmButton(VaadinIcons.TRASH,
            "Are you sure you want to delete the entry?", this::remove);


    public MainUI(BookRepository r, BookForm f, EventBus.UIEventBus b) {
        this.repo = r;
        this.bookForm = f;
        this.eventBus = b;
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        DisclosurePanel aboutBox = new DisclosurePanel("Описание:", new RichText().withMarkDownResource("/welcome.md"));
        setContent(
                new MVerticalLayout(
                        aboutBox,
                        new MHorizontalLayout(filterByName, /*filterByCompany,*/ addNew, edit, delete),
                        list
                ).expand(list)
        );
        listEntities();

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
