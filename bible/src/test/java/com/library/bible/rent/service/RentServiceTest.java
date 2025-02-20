//package com.library.bible.rent.service;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import java.util.Arrays;
//import java.util.Optional;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import com.library.bible.exception.CustomException;
//import com.library.bible.rent.model.Rent;
//import com.library.bible.rent.repository.IRentRepository;
//
//@Disabled
//class RentServiceTest {
//
//    @InjectMocks
//    private RentService rentService;
//
//    @Mock
//    private IRentRepository rentRepository;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    @DisplayName("회원 ID로 대여 목록 조회 테스트")
//    void testGetRentsByMemberId() {
//        Rent rent1 = new Rent(1L, 101L, 201L);
//        Rent rent2 = new Rent(2L, 102L, 202L);
//        when(rentRepository.findByMemberId(101L)).thenReturn(Arrays.asList(rent1, rent2));
//
//        var rents = rentService.getRentsByMemberId(101L);
//
//        assertThat(rents).hasSize(2);
//        verify(rentRepository, times(1)).findByMemberId(101L);
//    }
//
//    @Test
//    @DisplayName("ID로 대여 정보를 찾는 테스트")
//    void testFindById() {
//        Rent rent = new Rent(1L, 101L, 201L);
//        when(rentRepository.findById(1L)).thenReturn(Optional.of(rent));
//
//        Rent result = rentService.findById(1L);
//        assertThat(result).isEqualTo(rent);
//    }
//
//    @Test
//    @DisplayName("ID로 대여 정보 찾기 실패 테스트")
//    void testFindById_NotFound() {
//        when(rentRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(CustomException.class, () -> rentService.findById(1L));
//    }
//}
