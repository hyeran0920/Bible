//package com.library.bible.rent.service;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import java.util.Arrays;
//import java.util.Optional;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import com.library.bible.rent.model.Rent;
//import com.library.bible.rent.repository.IRentRepository;
//
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
//    void testFindById() {
//        Rent rent = new Rent(1L, 101L, 201L);
//        when(rentRepository.findById(1L)).thenReturn(Optional.of(rent));
//
//        Rent result = rentService.findById(1L);
//        assertThat(result).isEqualTo(rent);
//    }
//
//    @Test
//    void testFindById_NotFound() {
//        when(rentRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(RuntimeException.class, () -> rentService.findById(1L));
//    }
//
//    @Test
//    void testSaveRent() {
//        Rent rent = new Rent(1L, 101L, 201L);
//        when(rentRepository.save(any(Rent.class))).thenReturn(rent);
//
//        Rent savedRent = rentService.saveRent(rent);
//        assertThat(savedRent).isEqualTo(rent);
//    }
//}
