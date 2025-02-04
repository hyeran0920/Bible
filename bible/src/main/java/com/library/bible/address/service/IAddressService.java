package com.library.bible.address.service;

import java.util.List;

import com.library.bible.address.model.Address;

public interface IAddressService {
    List<Address> selectAddressesByMemId(long memId);
    Address seleAddress(long addressId);
    Address insertAddress(Address address);
    Address updateAddress(Address address);
    void deleteAddressesByMemId(long memId);
    void deleteAddress(long addressId);    
}
