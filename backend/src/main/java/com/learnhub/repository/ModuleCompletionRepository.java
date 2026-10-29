package com.learnhub.repository;

import com.learnhub.entity.ModuleCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleCompletionRepository extends JpaRepository<ModuleCompletion, Long> {

    boolean existsByStudentIdAndModuleId(Long studentId, Long moduleId);

    long countByStudentIdAndCourseId(Long studentId, Long courseId);

    List<ModuleCompletion> findByStudentIdAndCourseId(Long studentId, Long courseId);

    List<ModuleCompletion> findByStudentId(Long studentId);

    Optional<ModuleCompletion> findByStudentIdAndModuleId(Long studentId, Long moduleId);
}
