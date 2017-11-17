package com.shurik16.PhoneBook.backend;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findAllBy(Pageable pageable);

    List<Book> findByNameLikeIgnoreCase(String nameFilter);

    List<Book> findByNameLikeIgnoreCase(String nameFilter, Pageable pageable);

    long countByNameLike(String nameFilter);

}
