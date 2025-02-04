package com.library.bible.memberrent.service;

import com.library.bible.memberrent.model.MemberRent;

public interface IMemberRentService {
    MemberRent selectMemberRentByMemId(long memId);
    int insertMemberRent(MemberRent memberRent);
    int updateMemberRent(MemberRent memberRent);
    void deleteMemberRent(long memId);
}
