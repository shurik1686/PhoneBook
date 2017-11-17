package com.shurik16.PhoneBook.vaadin;

import com.shurik16.PhoneBook.backend.Book;

public class BookModifiedEvent {

    private final Book book;

    public Book getPerson() {
        return book;
    }

    public BookModifiedEvent(Book book) {
        this.book = book;
    }
}
