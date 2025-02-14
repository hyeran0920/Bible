package com.library.bible.memberrent.service;

import org.springframework.stereotype.Service;

import com.library.bible.exception.CustomException;
import com.library.bible.exception.ExceptionCode;
import com.library.bible.memberrent.model.MemberRent;
import com.library.bible.memberrent.respository.IMemberRentRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberRentService implements IMemberRentService {
	private final IMemberRentRepository memberRentRepository;

	// MemberRent
	@Override
	public MemberRent selectMemberRentByMemId(long memId) {
        return memberRentRepository.selectMemberRentByMemId(memId);
	}

	@Override
	@Transactional
	public int insertMemberRent(MemberRent memberRent) {
        int result = memberRentRepository.insertMemberRent(memberRent);
        if(result == 0) throw new CustomException(ExceptionCode.MEMBER_RENT_INSERT_FAIL);
        return result;
	}

	@Override
	@Transactional
	public int updateMemberRent(MemberRent memberRent) {
        int result = memberRentRepository.updateMemberRent(memberRent);
        if(result == 0) throw new CustomException(ExceptionCode.MEMBER_RENT_UPDATE_FAIL);
        return result;
	}
	
	@Override
	@Transactional
    public void updateTotalRentCount(long memId, int newTotalCount) {
		// 현재 사용자의 대여 정보 확인
		MemberRent memberRent = this.selectMemberRentByMemId(memId);		

		// 사용자가 대여 중인 도서 수 업데이트
		memberRent.setTotalRentCount(memberRent.getTotalRentCount() - newTotalCount);
		
		// 대여 정보 변경사항 반영
		this.updateMemberRent(memberRent);
	}

	@Override
	@Transactional
	public void deleteMemberRent(long memId) {
        int result = memberRentRepository.deleteMemberRent(memId);
        if(result == 0) throw new CustomException(ExceptionCode.MEMBER_RENT_DELETE_FAIL);
	}
}
