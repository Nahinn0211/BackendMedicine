package hunre.edu.vn.backend.controller;

import hunre.edu.vn.backend.dto.SocialAccountDTO;
import hunre.edu.vn.backend.service.SocialAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/social-accounts")
public class SocialAccountController {

    private final SocialAccountService socialAccountService;

    public SocialAccountController(SocialAccountService socialAccountService) {
        this.socialAccountService = socialAccountService;
    }

    @GetMapping
    public ResponseEntity<List<SocialAccountDTO.GetSocialAccountDTO>> getAllSocialAccounts() {
        List<SocialAccountDTO.GetSocialAccountDTO> accounts = socialAccountService.findAll();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SocialAccountDTO.GetSocialAccountDTO> getSocialAccountById(@PathVariable Long id) {
        return socialAccountService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/save")
    public ResponseEntity<SocialAccountDTO.GetSocialAccountDTO> saveOrUpdateSocialAccount(@RequestBody SocialAccountDTO.SaveSocialAccountDTO socialAccountDTO) {
        SocialAccountDTO.GetSocialAccountDTO savedAccount = socialAccountService.saveOrUpdate(socialAccountDTO);
        return ResponseEntity.ok(savedAccount);
    }

    @DeleteMapping("/{id}")
    public String deleteSocialAccount(@RequestBody List<Long> ids) {
        return socialAccountService.deleteByList(ids);
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<SocialAccountDTO.GetSocialAccountDTO>> getSocialAccountsByUserId(@PathVariable Long userId) {
        List<SocialAccountDTO.GetSocialAccountDTO> accounts = socialAccountService.findByUserId(userId);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/by-email/{email}")
    public ResponseEntity<SocialAccountDTO.GetSocialAccountDTO> getSocialAccountByEmail(@PathVariable String email) {
        return socialAccountService.findByProviderEmail(email)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}