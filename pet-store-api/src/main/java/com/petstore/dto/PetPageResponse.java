package com.petstore.dto;

import com.petstore.model.Pet;
import java.util.List;

/**
 * Pagination response for pets.
 */
public class PetPageResponse {
    private List<Pet> pets;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public PetPageResponse(List<Pet> pets, int page, int size, long totalElements, int totalPages) {
        this.pets = pets;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public List<Pet> getPets() { return pets; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }

    public void setPets(List<Pet> pets) { this.pets = pets; }
    public void setPage(int page) { this.page = page; }
    public void setSize(int size) { this.size = size; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
}
