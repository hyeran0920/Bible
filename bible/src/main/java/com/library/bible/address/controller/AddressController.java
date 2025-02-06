package com.library.bible.address.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import com.library.bible.address.model.Address;
import com.library.bible.address.service.IAddressService;
import com.library.bible.member.model.Member;
import com.library.bible.resolver.AuthMember;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class AddressController {
	private final IAddressService addressService;
	private final String ADDRESS_PATH = "/addresses";
	
	//GET
	@GetMapping("/me" + ADDRESS_PATH)
	public ResponseEntity<List<Address>> selectAddresses(@AuthMember Member member) {
		List<Address> addresses = addressService.selectAddressesByMemId(member.getMemId());
		return ResponseEntity.ok(addresses);
	}

	@GetMapping("/{memId}" + ADDRESS_PATH)
	public ResponseEntity<List<Address>> selectAddresses(@PathVariable long memId) {
		List<Address> addresses = addressService.selectAddressesByMemId(memId);
		return ResponseEntity.ok(addresses);
	}
	
	@GetMapping(ADDRESS_PATH + "/{addressId}")
	public ResponseEntity<Address> selectAddress(@PathVariable long addressId) {
		Address address = addressService.seleAddress(addressId);
		return ResponseEntity.ok(address);
	}
	
	@GetMapping(ADDRESS_PATH+"/default")
	public ResponseEntity<Address> selectAddress(@AuthMember Member member){
		Address address=addressService.selectDefaultAddress(member.getMemId());
		return ResponseEntity.ok(address);
	}
	
	//POST
	@PostMapping("/me/addresses")
	public ResponseEntity<Address> insertAddress(@AuthMember Member member, @Valid @RequestBody Address address) {
		System.out.println(address.toString());
		address.setMemId(member.getMemId());
		address = addressService.insertAddress(address);
		return ResponseEntity.status(HttpStatus.CREATED).body(address);
	}

	@PostMapping("/{memId}" + ADDRESS_PATH)
	public ResponseEntity<Address> insertAddressByMemId(@PathVariable long memId, @Valid @RequestBody Address address) {
		System.out.println("주소 추가하기");
		address.setMemId(memId);
		address = addressService.insertAddress(address);
		return ResponseEntity.status(HttpStatus.CREATED).body(address);
	}

	
	
	//PUT
	@PutMapping(ADDRESS_PATH + "/{addressId}")
	public ResponseEntity<Address> updateAddress(@PathVariable long addressId, @Valid @RequestBody Address address, @AuthMember Member member) {
		address.setMemId(member.getMemId());
		address.setAddressId(addressId);
		addressService.updateAddress(address);
		return ResponseEntity.ok(address);
	}
	
	@PutMapping(ADDRESS_PATH + "/default/{addressId}")
	public ResponseEntity<?> updateDefaultAddress(@PathVariable long addressId, @AuthMember Member member){
		addressService.setDefaultAddress(member.getMemId(), addressId);
		return ResponseEntity.noContent().build();
	}
	
	
	//DELETE
	@DeleteMapping("/me" + ADDRESS_PATH)
	public ResponseEntity<?> deleteAddress(@AuthMember Member member) {
		addressService.deleteAddressesByMemId(member.getMemId());
		return ResponseEntity.noContent().build();
	}

	
	@DeleteMapping("/{memId}" + ADDRESS_PATH)
	public ResponseEntity<?> deleteAddressByMemId(@PathVariable long memId) {
		addressService.deleteAddressesByMemId(memId);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping(ADDRESS_PATH + "/{addressId}")
	public ResponseEntity<?> deleteAddress(@PathVariable long addressId) {
		addressService.deleteAddress(addressId);
		return ResponseEntity.noContent().build();
	}
}
