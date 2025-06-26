package com.word.userservice.service;

import com.word.userservice.dto.NotebookRequestDTO;
import com.word.userservice.dto.NotebookResponseDTO;
import com.word.userservice.exception.NotebookNotFoundException;
import com.word.userservice.exception.UnauthorizedNotebookAccessException;
import com.word.userservice.exception.UserProfileNotFoundException;
import com.word.userservice.model.Notebook;
import com.word.userservice.model.UserProfile;
import com.word.userservice.repository.NotebookRepository;
import com.word.userservice.repository.UserProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class NotebookServiceImpl implements NotebookService{

    private final NotebookRepository notebookRepository;
    private final UserProfileRepository userProfileRepository;

    public NotebookServiceImpl(NotebookRepository notebookRepository, UserProfileRepository userProfileRepository) {
        this.notebookRepository = notebookRepository;
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public List<NotebookResponseDTO> getNotebooksByAuthUserId(String authUserId) {
        UserProfile profile = userProfileRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> new UserProfileNotFoundException("User profile not found"));
        return notebookRepository.findByUserProfile_ProfileId(profile.getProfileId())
                .stream().map(this::mapToResponseDto).toList();
    }

    @Override
    public NotebookResponseDTO createNotebook(String authUserId, NotebookRequestDTO request) {
        UserProfile profile = userProfileRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> new UserProfileNotFoundException("User profile not found"));

        Notebook notebook = Notebook.builder()
                .title(request.getTitle())
                .userProfile(profile)
                .build();

        return mapToResponseDto(notebookRepository.save(notebook));
    }

    @Override
    public NotebookResponseDTO updateNotebook(Long notebookId,
                                              String authUserId,
                                              NotebookRequestDTO request) {
        Notebook notebook = getNotebookIfOwnedBy(notebookId, authUserId);

        notebook.setTitle(request.getTitle());
        Notebook updated = notebookRepository.save(notebook);

        return mapToResponseDto(updated);
    }

    @Override
    public void deleteNotebook(Long notebookId, String authUserId) {
        Notebook notebook = getNotebookIfOwnedBy(notebookId, authUserId);
        notebookRepository.delete(notebook);
    }

    private NotebookResponseDTO mapToResponseDto(Notebook notebook) {
        return NotebookResponseDTO.builder()
                .notebookId(notebook.getNotebookId())
                .title(notebook.getTitle())
                .dateCreated(notebook.getDateCreated())
                .dateUpdated(notebook.getDateUpdated())
                .build();
    }


    private Notebook getNotebookIfOwnedBy(Long notebookId, String authUserId) {
        Notebook notebook = notebookRepository.findById(notebookId)
                .orElseThrow(() -> new NotebookNotFoundException("Notebook not found"));

        String ownerAuthId = notebook.getUserProfile().getAuthUserId();
        if (!ownerAuthId.equals(authUserId)) {
            throw new UnauthorizedNotebookAccessException("Unauthorized access to notebook");
        }
        return notebook;
    }
}
