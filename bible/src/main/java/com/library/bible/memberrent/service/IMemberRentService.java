package com.library.bible.memberrent.service;

import com.library.bible.memberrent.model.MemberRent;

public interface IMemberRentService {
    MemberRent selectMemberRentByMemId(long memId);
    int insertMemberRent(MemberRent memberRent);
    int updateMemberRent(MemberRent memberRent);
    void updateTotalRentCount(long memId, int newTotalCount);
    void deleteMemberRent(long memId);
}
