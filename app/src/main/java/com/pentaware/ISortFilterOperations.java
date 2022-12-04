package com.pentaware;

import com.pentaware.doorish.model.Product;

public interface ISortFilterOperations {
    void onSortSelected(String sortType);
    void onFilterSelected(String filterType);
}