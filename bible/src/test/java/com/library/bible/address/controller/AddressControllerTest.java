package com.library.bible.address.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.bible.address.model.Address;
import com.library.bible.address.service.IAddressService;

@Disabled("error발생")
@WebMvcTest(controllers = AddressController.class)
@AutoConfigureMockMvc(addFilters = false)  // Spring Security 비활성화
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IAddressService addressService;

    @Autowired
    private ObjectMapper objectMapper;

    private Address address;

    @BeforeEach
    void setUp() {
        address = new Address(1L, 1L, "12345", "Main St", "Apt 101", "John Doe", "010-1234-5678", 1);
    }

    /** 📌 GET /api/members/me/addresses 테스트 */
    @Test
    @WithMockUser
    void testSelectAddresses() throws Exception {
        List<Address> addresses = Arrays.asList(address);
        when(addressService.selectAddressesByMemId(anyLong())).thenReturn(addresses);

        mockMvc.perform(get("/api/members/me/addresses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].addressId").value(address.getAddressId()));
    }

    /** 📌 GET /api/members/{memId}/addresses 테스트 */
    @Test
    @WithMockUser
    void testSelectAddressesByMemId() throws Exception {
        List<Address> addresses = Arrays.asList(address);
        when(addressService.selectAddressesByMemId(anyLong())).thenReturn(addresses);

        mockMvc.perform(get("/api/members/1/addresses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].memId").value(address.getMemId()));
    }

    /** 📌 GET /api/members/addresses/{addressId} 테스트 */
    @Test
    @WithMockUser
    void testSelectAddressById() throws Exception {
        when(addressService.seleAddress(anyLong())).thenReturn(address);

        mockMvc.perform(get("/api/members/addresses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value(address.getAddress()));
    }

    /** 📌 POST /api/members/me/addresses 테스트 */
    @Test
    @WithMockUser
    void testInsertAddress() throws Exception {
        when(addressService.insertAddress(any(Address.class))).thenReturn(address);

        mockMvc.perform(post("/api/members/me/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(address)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.addressId").value(address.getAddressId()));
    }

    /** 📌 PUT /api/members/addresses/{addressId} 테스트 */
    @Test
    @WithMockUser
    void testUpdateAddress() throws Exception {
        when(addressService.updateAddress(any(Address.class))).thenReturn(address);

        mockMvc.perform(put("/api/members/addresses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(address)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.addressId").value(address.getAddressId()));
    }

    /** 📌 DELETE /api/members/me/addresses 테스트 */
    @Test
    @WithMockUser
    void testDeleteAddressesByMemId() throws Exception {
        doNothing().when(addressService).deleteAddressesByMemId(anyLong());

        mockMvc.perform(delete("/api/members/me/addresses"))
                .andExpect(status().isNoContent());
    }

    /** 📌 DELETE /api/members/addresses/{addressId} 테스트 */
    @Test
    @WithMockUser
    void testDeleteAddressById() throws Exception {
        doNothing().when(addressService).deleteAddress(anyLong());

        mockMvc.perform(delete("/api/members/addresses/1"))
                .andExpect(status().isNoContent());
    }
}
