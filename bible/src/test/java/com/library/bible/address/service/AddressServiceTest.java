package com.library.bible.address.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.library.bible.address.model.Address;
import com.library.bible.address.respository.IAddressRepository;
import com.library.bible.exception.CustomException;
import com.library.bible.exception.ExceptionCode;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @InjectMocks
    private AddressService addressService;

    @Mock
    private IAddressRepository addressRepository;

    private Address address;

    @BeforeEach
    void setUp() {
        address = new Address(5L, 10L, "12345", "Seoul", "Apt 101", "John Doe", "010-1234-5678", 1);
    }

    @Test
    @DisplayName("기본 주소로 설정된 상태에서 insertAddress 테스트")
    void testInsertAddressWithDefault() {
        when(addressRepository.insertAddress(any(Address.class))).thenReturn(1);
        when(addressRepository.countAddress(anyLong())).thenReturn(1);
        
        Address result = addressService.insertAddress(address);

        assertNotNull(result);
        verify(addressRepository, times(1)).insertAddress(address);
        verify(addressRepository, times(1)).setDefaultAddress(anyLong(), anyLong());
    }

    @Test
    @DisplayName("첫 번째 주소 추가 시 기본 주소로 설정되는지 테스트")
    void testInsertFirstAddress() {
        Address firstAddress = new Address(1L, 20L, "67890", "Busan", "Apt 301", "Jane Doe", "010-5678-1234", 0);
        
        when(addressRepository.insertAddress(any(Address.class))).thenReturn(1);
        when(addressRepository.countAddress(anyLong())).thenReturn(1);

        Address result = addressService.insertAddress(firstAddress);
        
        assertNotNull(result);
        verify(addressRepository, times(1)).setDefaultAddress(anyLong(), anyLong());
    }

    @Test
    @DisplayName("주소 업데이트 시 예외 발생 테스트 (FORBIDDEN)")
    void testUpdateAddressForbidden() {
        Address existingAddress = new Address(5L, 99L, "98765", "Incheon", "Apt 502", "Another User", "010-9876-5432", 0);
        
        when(addressRepository.seleAddress(5L)).thenReturn(existingAddress);

        CustomException ex = assertThrows(CustomException.class, () -> addressService.updateAddress(address));

        assertEquals(ExceptionCode.FORBIDDEN, ex.getExceptionCode());
    }

    @Test
    @DisplayName("주소 업데이트 실패 테스트 (ADDRESS_UPDATE_FAIL)")
    void testUpdateAddressFail() {
        when(addressRepository.seleAddress(5L)).thenReturn(address);
        when(addressRepository.updateAddress(address)).thenReturn(0);

        CustomException ex = assertThrows(CustomException.class, () -> addressService.updateAddress(address));

        assertEquals(ExceptionCode.ADDRESS_UPDATE_FAIL, ex.getExceptionCode());
    }
}
