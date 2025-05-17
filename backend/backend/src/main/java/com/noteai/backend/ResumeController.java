package com.noteai.backend;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ResumeController {

    @PostMapping("/resume")
    public ResponseEntity<Map<String, String>> getSummary(@RequestBody Map<String, String> payload) {
        String inputText = payload.get("text");

        // ✂️ Ici tu peux remplacer ça par un vrai résumé via IA plus tard
        String summary = "Résumé automatique : " + inputText;

        return ResponseEntity.ok(Map.of("summary", summary));
    }
}
