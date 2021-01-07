package com.example.springreactive.s01;


import com.example.springreactive.domain.Book;
import com.example.springreactive.domain.InMemoryDataSource;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class C01FunctionalProgramming101 {

    // 返回一个包含每种类别中最贵的书的列表, 非函数式编程
    private static List<Book> getMostExpensiveBooksByCategory() {
        HashMap<String, Book> map = new HashMap<>();
        for (Book book : InMemoryDataSource.books) {
            Book aBook = map.get(book.getCategory());
            if (aBook != null) {
                if (book.getPrice().compareTo(aBook.getPrice()) > 0) {
                    map.put(book.getCategory(), book);
                }
            } else {
                map.put(book.getCategory(), book);
            }
        }
        return (new ArrayList<>(map.values()));
    }

    // 返回一个包含每种类别中最贵的书的列表, 函数式编程
    private static List<Book> getMostExpensiveBooksByCategoryFunctional() {
        return Stream.of(InMemoryDataSource.books)
                .collect(Collectors.groupingByConcurrent(Book::getCategory)) // 通过修改一行代码就可以编程并行代码
                .entrySet().parallelStream()
                .map(e -> e.getValue().stream().max(Comparator.comparing(Book::getPrice)).get())
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        List<Book> books1 = getMostExpensiveBooksByCategory();
        books1.forEach(System.out::println);

        System.out.printf("%n%n");

        List<Book> books2 = getMostExpensiveBooksByCategoryFunctional();
        books2.forEach(System.out::println);
    }
}