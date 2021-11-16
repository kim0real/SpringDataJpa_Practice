package study.datajpa.repository;

import study.datajpa.entity.Member;

import java.util.List;

// 사용자 정의 레파지토리 구현(QueryDSL에서 많이 사용)
public interface MemberRepositoryCustom {
    List<Member> findMemberCustom();


}
