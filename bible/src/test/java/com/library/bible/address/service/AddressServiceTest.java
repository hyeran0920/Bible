package com.library.bible.address.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
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

    @Test
    @DisplayName("memId로 주소 목록 조회")
    void testSelectAddressesByMemId() {
        // given
        Address address = new Address();
        address.setAddressId(1L);
        address.setMemId(100L);
        address.setAddress("Seoul");

        when(addressRepository.selectAddressesByMemId(100L))
                .thenReturn(Arrays.asList(address));

        // when
        List<Address> result = addressService.selectAddressesByMemId(100L);

        // then
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Seoul", result.get(0).getAddress());
        verify(addressRepository, times(1)).selectAddressesByMemId(100L);
    }

    @Test
    @DisplayName("addressId로 주소 단건 조회")
    void testSelectAddress() {
        // given
        Address address = new Address();
        address.setAddressId(10L);
        address.setMemId(100L);
        address.setAddress("Busan");

        when(addressRepository.seleAddress(10L))
                .thenReturn(address);

        // when
        Address result = addressService.seleAddress(10L);

        // then
        Assertions.assertNotNull(result);
        Assertions.assertEquals("Busan", result.getAddress());
        verify(addressRepository, times(1)).seleAddress(10L);
    }

    @Test
    @DisplayName("주소 추가 시, defaultAddress가 1이거나 등록된 주소가 처음일 때 기본 주소가 설정된다.")
    void testInsertAddress() {
        // given
        Address newAddress = new Address();
        newAddress.setAddressId(0L); // 아직 생성 전
        newAddress.setMemId(100L);
        newAddress.setAddress("Incheon");
        newAddress.setDefaultAddress(1); // 기본 주소로 요청
        // insertAddress 메서드에서 DB에 insert가 된다치면 addressId가 생성된다고 가정

        // Mocking
        when(addressRepository.insertAddress(any(Address.class))).thenAnswer(invocation -> {
            Address arg = invocation.getArgument(0, Address.class);
            // DB insert 후 key가 1L이라고 가정
            arg.setAddressId(1L);
            return 1; // 성공
        });
        // countAddress가 0이라면 첫 주소다
        when(addressRepository.countAddress(100L)).thenReturn(0);

        doNothing().when(addressRepository).setDefaultAddress(100L, 1L);

        // when
        Address inserted = addressService.insertAddress(newAddress);

        // then
        Assertions.assertNotNull(inserted);
        Assertions.assertEquals(1L, inserted.getAddressId());
        Assertions.assertEquals("Incheon", inserted.getAddress());
        // 기본 주소 설정 로직이 실행되었는지 확인
        verify(addressRepository, times(1)).setDefaultAddress(100L, 1L);
    }

    @Test
    @DisplayName("주소 업데이트 시 memId 불일치하면 FORBIDDEN 예외 발생")
    void testUpdateAddressForbidden() {
        // given
        Address addressInDB = new Address();
        addressInDB.setAddressId(5L);
        addressInDB.setMemId(10L); // DB에 저장된 주소의 memId

        Address updateDto = new Address();
        updateDto.setAddressId(5L);
        updateDto.setMemId(20L); // 업데이트 요청자의 memId가 다름

        when(addressRepository.seleAddress(5L)).thenReturn(addressInDB);

        // when & then
        CustomException ex = Assertions.assertThrows(CustomException.class,
                () -> addressService.updateAddress(updateDto));
        Assertions.assertEquals(ExceptionCode.FORBIDDEN, ex.getExceptionCode());
        verify(addressRepository, never()).updateAddress(any(Address.class));
    }

    @Test
    @DisplayName("주소 업데이트 성공")
    void testUpdateAddress() {
        // given
        Address addressInDB = new Address();
        addressInDB.setAddressId(5L);
        addressInDB.setMemId(10L);

        Address updateDto = new Address();
        updateDto.setAddressId(5L);
        updateDto.setMemId(10L);
        updateDto.setAddress("ChangedCity");
        updateDto.setDefaultAddress(0);

        when(addressRepository.seleAddress(5L)).thenReturn(addressInDB);
        when(addressRepository.updateAddress(updateDto)).thenReturn(1);

        // when
        Address updated = addressService.updateAddress(updateDto);

        // then
        Assertions.assertNotNull(updated);
        Assertions.assertEquals("ChangedCity", updated.getAddress());
        verify(addressRepository, times(1)).updateAddress(updateDto);
    }

    @Test
    @DisplayName("회원의 기본 주소 조회")
    void testSelectDefaultAddress() {
        // given
        Address defaultAddress = new Address();
        defaultAddress.setAddressId(10L);
        defaultAddress.setMemId(100L);
        defaultAddress.setDefaultAddress(1);

        when(addressRepository.selectDefaultAddress(100L))
                .thenReturn(defaultAddress);

        // when
        Address result = addressService.selectDefaultAddress(100L);

        // then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getDefaultAddress());
        verify(addressRepository, times(1)).selectDefaultAddress(100L);
    }

    @Test
    @DisplayName("기본 주소 설정 호출")
    void testSetDefaultAddress() {
        // given
        doNothing().when(addressRepository).setDefaultAddress(100L, 10L);

        // when
        addressService.setDefaultAddress(100L, 10L);

        // then
        verify(addressRepository, times(1)).setDefaultAddress(100L, 10L);
    }
    @Disabled
    @Test
    @DisplayName("회원의 주소들 일괄 삭제")
    void testDeleteAddressesByMemId() {
        // given
        doNothing().when(addressRepository).deleteAddressesByMemId(200L);

        // when
        addressService.deleteAddressesByMemId(200L);

        // then
        verify(addressRepository, times(1)).deleteAddressesByMemId(200L);
    }

    @Test
    @DisplayName("주소 단건 삭제")
    void testDeleteAddress() {
        // given
        when(addressRepository.deleteAddress(5L)).thenReturn(1);

        // when
        addressService.deleteAddress(5L);

        // then
        verify(addressRepository, times(1)).deleteAddress(5L);
    }

    @Test
    @DisplayName("주소 삭제 실패 시 예외 발생")
    void testDeleteAddressFail() {
        // given
        when(addressRepository.deleteAddress(999L)).thenReturn(0);

        // when & then
        CustomException ex = Assertions.assertThrows(CustomException.class, 
                () -> addressService.deleteAddress(999L));
        Assertions.assertEquals(ExceptionCode.ADDRESS_DELETE_FAIL, ex.getExceptionCode());
        verify(addressRepository, times(1)).deleteAddress(999L);
    }
}
