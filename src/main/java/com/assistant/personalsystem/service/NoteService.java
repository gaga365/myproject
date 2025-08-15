package com.assistant.personalsystem.service;

import com.assistant.personalsystem.model.Note;
import java.util.List;
import java.util.Optional;

public interface NoteService {
    
    // 创建笔记
    Note createNote(Note note);
    
    // 根据ID获取笔记
    Optional<Note> getNoteById(Long id);
    
    // 获取所有笔记
    List<Note> getAllNotes();
    
    // 更新笔记
    Note updateNote(Long id, Note note);
    
    // 删除笔记
    void deleteNote(Long id);
    
    // 搜索笔记
    List<Note> searchNotes(String keyword);
    
    // 保存搜索结果到笔记
    Note saveSearchResultToNote(String title, String content);
} 