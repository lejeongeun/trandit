package org.project.trandit.domain.request;

import org.project.trandit.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByRequester(Member requester);

    Optional<Request> findByRequesterAndId(Member requester, Long id);
    List<Request> findAllByOrderByCreatedAtDesc();
}
