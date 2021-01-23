package io.niceseason.rpc.server;

import io.niceseason.rpc.api.Book;
import io.niceseason.rpc.api.BookStore;

public class BookStoreImpl implements BookStore {
    @Override
    public String saleBook(Book book) {
        double v = book.getPrice() * 3;
        String s = "销售书并拿到钱" + book.getBookName() + v;
        System.out.println(s);
        return s;
    }
}
