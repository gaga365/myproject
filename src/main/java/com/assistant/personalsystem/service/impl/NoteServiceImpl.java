package com.assistant.personalsystem.service.impl;

import com.assistant.personalsystem.model.Note;
import com.assistant.personalsystem.repository.NoteRepository;
import com.assistant.personalsystem.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class NoteServiceImpl implements NoteService {
    
    @Autowired
    private NoteRepository noteRepository;
    
    @Override
    public Note createNote(Note note) {
        return noteRepository.save(note);
    }
    
    @Override
    public Optional<Note> getNoteById(Long id) {
        return noteRepository.findById(id);
    }
    
    @Override
    public List<Note> getAllNotes() {
        return noteRepository.findAllByOrderByCreatedAtDesc();
    }
    
    @Override
    public Note updateNote(Long id, Note note) {
        Optional<Note> existingNote = noteRepository.findById(id);
        if (existingNote.isPresent()) {
            Note updatedNote = existingNote.get();
            updatedNote.setTitle(note.getTitle());
            updatedNote.setContent(note.getContent());
            updatedNote.setImagePath(note.getImagePath());
            return noteRepository.save(updatedNote);
        }
        throw new RuntimeException("Note not found with id: " + id);
    }
    
    @Override
    public void deleteNote(Long id) {
        noteRepository.deleteById(id);
    }
    
    @Override
    public List<Note> searchNotes(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllNotes();
        }
        return noteRepository.findByTitleOrContentContaining(keyword.trim());
    }
    
    @Override
    public Note saveSearchResultToNote(String title, String content) {
        Note note = Note.builder()
                .title(title)
                .content(content)
                .build();
        return noteRepository.save(note);
    }
} 