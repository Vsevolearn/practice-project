package com.uclab.everytree.models;

import java.util.List;

public class PageResponse<T> {
    private int count;
    private String next;
    private String previous;
    private List<T> results;
}
