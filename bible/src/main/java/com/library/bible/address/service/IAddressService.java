package com.library.bible.address.service;

import java.util.List;

import com.library.bible.address.model.Address;

public interface IAddressService {
    List<Address> selectAddressesByMemId(long memId);
    Address seleAddress(long addressId);
    Address selectDefaultAddress(long memId);
    Address insertAddress(Address address);
    Address updateAddress(Address address);
    void deleteAddressesByMemId(long memId);
    void deleteAddress(long addressId); 
    
    int countAddress(long memId);
    void setDefaultAddress(long memId, long addressId);
}
