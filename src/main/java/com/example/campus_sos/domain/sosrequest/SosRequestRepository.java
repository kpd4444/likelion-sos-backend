package com.example.campus_sos.domain.sosrequest;

import com.example.campus_sos.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SosRequestRepository extends JpaRepository<SosRequest, Long> {
    List<SosRequest> findAllByRequester(Member requester);
    List<SosRequest> findAllByHelper(Member helper);
    List<SosRequest> findByBuilding(BuildingType building);
}