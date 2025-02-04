package com.library.bible.address.respository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.library.bible.address.model.Address;

@Mapper
@Repository
public interface IAddressRepository {
    List<Address> selectAddressesByMemId(long memId);
    Address seleAddress(long addressId);
    int insertAddress(Address address);
    int updateAddress(Address address);
    int deleteAddressesByMemId(long memId);
    int deleteAddress(long addressId);
}
