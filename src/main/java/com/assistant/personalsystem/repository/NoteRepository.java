package com.assistant.personalsystem.repository;

import com.assistant.personalsystem.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    
    // 按标题搜索笔记
    List<Note> findByTitleContainingIgnoreCase(String title);
    
    // 按内容搜索笔记
    List<Note> findByContentContainingIgnoreCase(String content);
    
    // 按标题或内容搜索笔记
    @Query("SELECT n FROM Note n WHERE LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(n.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Note> findByTitleOrContentContaining(@Param("keyword") String keyword);
    
    // 按创建时间倒序排列
    List<Note> findAllByOrderByCreatedAtDesc();
} 