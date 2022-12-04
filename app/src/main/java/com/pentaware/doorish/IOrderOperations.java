package com.pentaware.doorish;

import com.pentaware.doorish.model.Orders;

public interface IOrderOperations {
    void DoOrderOperations(Orders order, Enums.OrderOperations orderOperations);
    void onNextClick();
    void onPreviousClick();
}
