//package com.library.bible.member.service;
//
//import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.doThrow;
//import static org.mockito.Mockito.when;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
//import com.library.bible.exception.CustomException;
//import com.library.bible.member.model.Member;
//import com.library.bible.member.repository.IMemberRepository;
//import com.library.bible.memberrent.service.IMemberRentService;
//import com.library.bible.role.service.IRoleService;
//import com.library.bible.security.jwt.JwtProvider;
//import com.library.bible.upload.service.UploadService;
//
//class MemberServiceTest {
//
//    @InjectMocks
//    private MemberService memberService;
//
//    @Mock
//    private IMemberRepository memberRepository;
//    
//    @Mock
//    private IRoleService roleService;
//
//    @Mock
//    private IMemberRentService memberRentService;
//    
//    @Mock
//    private UploadService uploadService;
//
//    @Mock
//    private RedisTemplate<String, String> redisTemplate;
//
//    @Mock
//    private JwtProvider jwtProvider;
//
//    @Mock
//    private BCryptPasswordEncoder passwordEncoder;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testSelectMember_Success() {
//        long memId = 1L;
//        Member mockMember = new Member(memId, "test@example.com", "Test User", "password123");
//        when(memberRepository.selectMember(memId)).thenReturn(mockMember);
//
//        Member result = memberService.selectMember(memId);
//
//        assertNotNull(result);
//        assertEquals(memId, result.getMemId());
//        assertEquals("test@example.com", result.getMemEmail());
//    }
//
//    @Test
//    void testSelectMember_NotFound() {
//        long memId = 1L;
//        when(memberRepository.selectMember(memId)).thenReturn(null);
//
//        assertThrows(CustomException.class, () -> memberService.selectMember(memId));
//    }
//
//    @Test
//    void testInsertMember_Success() {
//        Member newMember = new Member(1L, "new@example.com", "New User", "securePassword");
//        when(redisTemplate.opsForValue().get(anyString())).thenReturn("validCode");
//        when(memberRepository.insertMember(any(Member.class))).thenReturn(1);
//        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
//
//        Member result = memberService.insertMember(newMember, "user", "validCode");
//
//        assertNotNull(result);
//        assertEquals("new@example.com", result.getMemEmail());
//    }
//
//    @Test
//    void testInsertMember_DuplicateEmail() {
//        Member newMember = new Member(1L, "duplicate@example.com", "Duplicate User", "securePassword");
//        when(redisTemplate.opsForValue().get(anyString())).thenReturn("validCode");
//        doThrow(new CustomException("DUPLICATE_EMAIL")).when(memberRepository).insertMember(any(Member.class));
//
//        assertThrows(CustomException.class, () -> memberService.insertMember(newMember, "user", "validCode"));
//    }
//
//    @Test
//    void testUpdateMember_Success() {
//        Member existingMember = new Member(1L, "update@example.com", "Updated User", "newPassword");
//        when(memberRepository.updateMember(any(Member.class))).thenReturn(1);
//        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
//
//        Member result = memberService.updateMember(existingMember);
//
//        assertNotNull(result);
//        assertEquals("update@example.com", result.getMemEmail());
//    }
//
//    @Test
//    void testDeleteMember_Success() {
//        long memId = 1L;
//        doNothing().when(roleService).deleteRoles(memId);
//        doNothing().when(memberRentService).deleteMemberRent(memId);
//        doNothing().when(memberRepository).deleteMember(memId);
//        doNothing().when(uploadService).deleteMemberQRImage(memId);
//
//        assertDoesNotThrow(() -> memberService.deleteMember(memId));
//    }
//}
