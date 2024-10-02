package com.Sucat.domain.game.repository;

import com.Sucat.domain.game.model.DepartmentRanking;
import com.Sucat.domain.user.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartmentRankingRepository extends JpaRepository<DepartmentRanking, Long> {
    Optional<DepartmentRanking> findByDepartment(Department department);
}
