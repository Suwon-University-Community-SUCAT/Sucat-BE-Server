package com.Sucat.domain.game.repository;

import com.Sucat.domain.game.model.DepartmentRanking;
import com.Sucat.domain.game.model.GameCategory;
import com.Sucat.domain.user.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DepartmentRankingRepository extends JpaRepository<DepartmentRanking, Long> {

    @Query("SELECT dr FROM DepartmentRanking dr WHERE dr.department = :department AND dr.game.category = :category")
    Optional<DepartmentRanking> findByDepartmentAndCategory(@Param("department") Department department, @Param("category") GameCategory category);

}
