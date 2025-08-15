package com.assistant.personalsystem.controller;

import com.assistant.personalsystem.model.Note;
import com.assistant.personalsystem.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class NoteController {
    
    @Autowired
    private NoteService noteService;
    
    // 渲染笔记本页面
    @GetMapping("/notebook")
    public String notebookPage(Model model) {
        List<Note> notes = noteService.getAllNotes();
        model.addAttribute("notes", notes);
        return "notebook";
    }
    
    // RESTful API - 获取所有笔记
    @GetMapping("/api/notes")
    @ResponseBody
    public ResponseEntity<List<Note>> getAllNotes() {
        List<Note> notes = noteService.getAllNotes();
        return ResponseEntity.ok(notes);
    }
    
    // RESTful API - 根据ID获取笔记
    @GetMapping("/api/notes/{id}")
    @ResponseBody
    public ResponseEntity<Note> getNoteById(@PathVariable Long id) {
        return noteService.getNoteById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // RESTful API - 创建笔记
    @PostMapping("/api/notes")
    @ResponseBody
    public ResponseEntity<Note> createNote(@RequestBody Note note) {
        Note createdNote = noteService.createNote(note);
        return ResponseEntity.ok(createdNote);
    }
    
    // RESTful API - 更新笔记
    @PutMapping("/api/notes/{id}")
    @ResponseBody
    public ResponseEntity<Note> updateNote(@PathVariable Long id, @RequestBody Note note) {
        try {
            Note updatedNote = noteService.updateNote(id, note);
            return ResponseEntity.ok(updatedNote);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // RESTful API - 删除笔记
    @DeleteMapping("/api/notes/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        noteService.deleteNote(id);
        return ResponseEntity.ok().build();
    }
    
    // RESTful API - 搜索笔记
    @GetMapping("/api/notes/search")
    @ResponseBody
    public ResponseEntity<List<Note>> searchNotes(@RequestParam String keyword) {
        List<Note> notes = noteService.searchNotes(keyword);
        return ResponseEntity.ok(notes);
    }
    
    // RESTful API - 保存搜索结果到笔记
    @PostMapping("/api/notes/save-search")
    @ResponseBody
    public ResponseEntity<Note> saveSearchResult(@RequestParam String title, @RequestParam String content) {
        Note note = noteService.saveSearchResultToNote(title, content);
        return ResponseEntity.ok(note);
    }
} 