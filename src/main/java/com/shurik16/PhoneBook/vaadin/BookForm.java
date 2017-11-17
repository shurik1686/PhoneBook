package com.shurik16.PhoneBook.vaadin;

import com.shurik16.PhoneBook.backend.Book;
import com.shurik16.PhoneBook.backend.BookRepository;
import com.vaadin.data.converter.LocalDateToDateConverter;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.TextField;
import org.vaadin.spring.events.EventBus;
import org.vaadin.teemu.switchui.Switch;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.form.AbstractForm;
import org.vaadin.viritin.layouts.MFormLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;


    @UIScope
    @SpringComponent
    public class BookForm extends AbstractForm<Book> {

        private static final long serialVersionUID = 1L;

        EventBus.UIEventBus eventBus;
        BookRepository repo;

        TextField company = new MTextField("Название компании");
        TextField name = new MTextField("Имя");
        TextField email = new MTextField("Почта");
        TextField phone = new MTextField("Телефон");
        TextField shortPhone = new MTextField("Коротки т.");
        TextField mobilePhone = new MTextField("Мобильник");

        public BookForm(BookRepository r, EventBus.UIEventBus b) {
            super(Book.class);
            this.repo = r;
            this.eventBus = b;

            setSavedHandler(book -> {

                repo.save(book);

                eventBus.publish(this, new BookModifiedEvent(book));
            });
            setResetHandler(p -> eventBus.publish(this, new BookModifiedEvent(p)));

            setSizeUndefined();
        }
/*
        @Override
        protected void bind() {
            getBinder()
                    .forMemberField(start_day)
                    .withConverter(new LocalDateToDateConverter());
            getBinder()
                    .forMemberField(end_day)
                    .withConverter(new LocalDateToDateConverter());
            super.bind();
        }
*/
        @Override
        protected Component createContent() {
            return new MVerticalLayout(
                    new MFormLayout(
                            company,
                            name,
                            email,
                            phone,
                            shortPhone,
                            mobilePhone
                    ).withWidth(""),
                    getToolbar()
            ).withWidth("");
        }
    }


