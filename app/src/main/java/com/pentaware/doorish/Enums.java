package com.pentaware.doorish;

public class Enums {

    public enum LoginMethod{
        PHONE,
        EMAIL,
        GOOGLE
    }

    public enum CartOperations{
        AddQuantity,
        RemoveQuantity,
        RemoveFromCart,
        SelectProduct
    }

    public enum OrderOperations{
        NoOperation,
        CancelOrder
    }

    public enum ReplacementStatus{
        PENDING,
        COMPLETED,
        REJECTED
    }

    public enum ProductSorting{
        None,
        PriceLowToHigh,
        PriceHighToLow,
        Brand
    }
}


