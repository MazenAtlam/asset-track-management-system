package com.assettrack.dto;

import java.util.List;

/**
 * Generic wrapper for paginated REST API responses.
 * Follows the 1-indexed pagination standard defined in the API specification.
 *
 * @param <T> The type of the data elements in the page.
 */
public class PaginatedResponseDTO<T> {

    private long totalItems;
    private int totalPages;
    private int currentPage;
    private List<T> data;

    public PaginatedResponseDTO(long totalItems, int totalPages, int currentPage, List<T> data) {
        this.totalItems = totalItems;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.data = data;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(long totalItems) {
        this.totalItems = totalItems;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}