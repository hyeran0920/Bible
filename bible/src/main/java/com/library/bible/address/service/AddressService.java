package com.library.bible.address.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.library.bible.address.model.Address;
import com.library.bible.address.respository.IAddressRepository;
import com.library.bible.exception.CustomException;
import com.library.bible.exception.ExceptionCode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressService implements IAddressService { 
	private final IAddressRepository addressRepository;

	@Override
	public List<Address> selectAddressesByMemId(long memId) {   
		return addressRepository.selectAddressesByMemId(memId);
	}

	@Override
	public Address seleAddress(long addressId) {
        return addressRepository.seleAddress(addressId);
	}

	@Override
	@Transactional
	public Address insertAddress(Address address) {
		
		System.out.println(address.getDefaultAddress());
		if(address.getDefaultAddress()!=1) {address.setDefaultAddress(0);}
		
		//insert address
        int result = addressRepository.insertAddress(address);
        if(result == 0) throw new CustomException(ExceptionCode.ADDRESS_INSERT_FAIL);
        
        //default address
        System.out.println("is default address="+address.getDefaultAddress());
        System.out.println("address count="+addressRepository.countAddress(address.getMemId()));
        
        if(address.getDefaultAddress()== 1 || 
        		addressRepository.countAddress(address.getMemId())==1)
        {
        	System.out.println("default address="+address.getAddressId());
			addressRepository.setDefaultAddress(address.getMemId(),address.getAddressId());
		}

        
        return address;
	}

	@Override
	@Transactional
	public Address updateAddress(Address address) {
		Address bAddress = this.seleAddress(address.getAddressId());
		if(bAddress.getMemId() != address.getMemId())
			throw new CustomException(ExceptionCode.FORBIDDEN);
		
		System.out.println(address);

		
		
        int result = addressRepository.updateAddress(address);
        if(result == 0) throw new CustomException(ExceptionCode.ADDRESS_UPDATE_FAIL);
        return address;
	}

	@Override
	@Transactional
	public void deleteAddressesByMemId(long memId) {
        addressRepository.deleteAddressesByMemId(memId);
	}

	@Override
	@Transactional
	public void deleteAddress(long addressId) {
		int result = addressRepository.deleteAddress(addressId);
        if(result == 0) throw new CustomException(ExceptionCode.ADDRESS_DELETE_FAIL);
	}

	@Override
	public Address selectDefaultAddress(long memId) {
		Address address=addressRepository.selectDefaultAddress(memId);
		return address;
		
	}

	@Override
	public int countAddress(long memId) {
		return addressRepository.countAddress(memId);
	}

	@Override
	public void setDefaultAddress(long memId, long addressId) {
		addressRepository.setDefaultAddress(memId, addressId);
		
	}
}
