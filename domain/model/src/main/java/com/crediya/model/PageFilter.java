package com.crediya.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PageFilter {
    private final Map<String, Object> filters;
    private final String sortBy;
    private final String sortOrder;
    private final int pageNumber;
    private final int pageSize;

    public PageFilter(Map<String, Object> filters,
                     String sortBy,
                     String sortOrder,
                     int pageNumber,
                     int pageSize) {
        this.filters = filters != null ? new HashMap<>(filters) : new HashMap<>();
        this.sortBy = sortBy != null ? sortBy : "createdAt";
        this.sortOrder = sortOrder != null ? sortOrder : "DESC";
        this.pageNumber = Math.max(0, pageNumber);
        this.pageSize = Math.max(1, pageSize);
    }

    public Map<String, Object> getFilters() {
        return new HashMap<>(filters);
    }

    public <T> T getFilter(String key, Class<T> type) {
        Object value = filters.get(key);
        if (value != null && type.isInstance(value)) {
            return type.cast(value);
        }
        return null;
    }

    public String getSortBy() {
        return sortBy;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getOffset() {
        return (long) pageNumber * pageSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PageFilter that = (PageFilter) o;
        return pageNumber == that.pageNumber &&
                pageSize == that.pageSize &&
                Objects.equals(filters, that.filters) &&
                Objects.equals(sortBy, that.sortBy) &&
                Objects.equals(sortOrder, that.sortOrder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filters, sortBy, sortOrder, pageNumber, pageSize);
    }

    @Override
    public String toString() {
        return "PageFilter{" +
                "filters=" + filters +
                ", sortBy='" + sortBy + '\'' +
                ", sortOrder='" + sortOrder + '\'' +
                ", pageNumber=" + pageNumber +
                ", pageSize=" + pageSize +
                '}';
    }

    public static class Builder {
        private Map<String, Object> filters = new HashMap<>();
        private String sortBy = "createdAt";
        private String sortOrder = "DESC";
        private int pageNumber = 0;
        private int pageSize = 20;

        public Builder filter(String key, Object value) {
            if (value != null) {
                this.filters.put(key, value);
            }
            return this;
        }

        public Builder sortBy(String sortBy) {
            this.sortBy = sortBy;
            return this;
        }

        public Builder sortOrder(String sortOrder) {
            this.sortOrder = sortOrder;
            return this;
        }

        public Builder page(int pageNumber, int pageSize) {
            this.pageNumber = pageNumber;
            this.pageSize = pageSize;
            return this;
        }

        public PageFilter build() {
            return new PageFilter(filters, sortBy, sortOrder, pageNumber, pageSize);
        }
    }
}