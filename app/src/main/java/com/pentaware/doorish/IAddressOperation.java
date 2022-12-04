package com.pentaware.doorish;

import com.pentaware.doorish.model.Address;

public interface IAddressOperation {
    void MakeAddressDefault(Address address);
    void editAddress(Address address);
    void deleteAddress(Address address, int position);
}

