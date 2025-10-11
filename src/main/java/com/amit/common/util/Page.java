package com.amit.common.util;

import java.util.List;

public final class Page<T> {

    private final List<T> items;

    private final boolean hasPrev;

    private final boolean hasNext;

    private final int lastPage;

    public Page(List<T> items, int pageNumber, int pageSize, long total) {
        this.items = items;
        this.lastPage = this.calculateLastPage(total, pageSize);
        this.hasPrev = pageNumber > 1;
        this.hasNext = pageNumber < this.lastPage;
    }

    public List<T> items() {
        return items;
    }

    public boolean hasPrev() {
        return hasPrev;
    }

    public boolean hasNext() {
        return hasNext;
    }

    public int lastPage() {
        return lastPage;
    }

    private int calculateLastPage(long total, int pageSize) {
        return (int) Math.max(1, (long) Math.ceil(total / (double) pageSize));
    }

}
