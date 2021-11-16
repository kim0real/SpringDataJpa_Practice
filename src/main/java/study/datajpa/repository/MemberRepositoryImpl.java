package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

// 사용자 정의 레파지토리 구현
// 규칙 : MemberRepository로 시작해야한다. 즉 사용하는 Repository의 이름으로 시작해야하며 + Impl으로 끝나야 한다.
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m")
                .getResultList();
    }
}
