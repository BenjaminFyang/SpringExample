package com.example.springreactive.s01;


import com.example.springreactive.domain.Book;
import com.example.springreactive.domain.InMemoryDataSource;
import reactor.core.publisher.Flux;

import java.util.Comparator;

public class C02ReactiveProgramming101 {

    /**
     * 返回一个包含每种类别中最贵的书的列表, 响应式 编程
     *
     * @param books 书的总类.
     * @return the  Flux<Book>
     */
    private static Flux<Book> getMostExpensiveBooksByCategoryReactive(Flux<Book> books) {
        return books.collectMultimap(Book::getCategory)
                // 将一个map变成多个map的方法.
                .flatMapMany(m -> Flux.fromIterable(m.entrySet()))
                .flatMap(e -> Flux.fromIterable(e.getValue())
                        .sort(Comparator.comparing(Book::getPrice).reversed())
                        .next());
    }

    public static void main(String[] args) {
        Flux<Book> pipeline = getMostExpensiveBooksByCategoryReactive(Flux.just(InMemoryDataSource.books));
        pipeline = pipeline.doOnNext(System.out::println);
        System.out.println("什么都不会发生，直到pipeline开始");
        pipeline.subscribe();
    }
}