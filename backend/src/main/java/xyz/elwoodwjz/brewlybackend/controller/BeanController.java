package xyz.elwoodwjz.brewlybackend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import xyz.elwoodwjz.brewlybackend.dto.bean.BeanRequest;
import xyz.elwoodwjz.brewlybackend.dto.bean.BeanResponse;
import xyz.elwoodwjz.brewlybackend.entity.Bean;
import xyz.elwoodwjz.brewlybackend.service.BeanService;
import xyz.elwoodwjz.brewlybackend.security.CustomUserDetailsService.CustomUserPrincipal;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/beans")
@CrossOrigin(origins = "*")
public class BeanController {
    private final BeanService beanService;

    public BeanController(BeanService beanService) {
        this.beanService = beanService;
    }

    private UUID getUserIdFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserPrincipal) {
            return ((CustomUserPrincipal) authentication.getPrincipal()).getUser().getId();
        }
        return null;
    }

    @GetMapping
    public ResponseEntity<List<BeanResponse>> getAllBeans() {
        List<Bean> beans = beanService.getAllBeans();
        List<BeanResponse> responses = beans.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/user")
    public ResponseEntity<List<BeanResponse>> getUserBeans(Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        List<Bean> beans = beanService.getBeansByUserId(userId);
        List<BeanResponse> responses = beans.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeanResponse> getBeanById(@PathVariable UUID id) {
        Optional<Bean> bean = beanService.getBeanById(id);
        if (bean.isPresent()) {
            return ResponseEntity.ok(mapToResponse(bean.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<BeanResponse> createBean(@Valid @RequestBody BeanRequest request, Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        Bean bean = mapToEntity(request, userId);
        Bean savedBean = beanService.createBean(bean);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(savedBean));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BeanResponse> updateBean(@PathVariable UUID id, @Valid @RequestBody BeanRequest request, Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        Optional<Bean> existingBean = beanService.getBeanById(id);
        if (existingBean.isPresent()) {
            // Check if the bean belongs to the authenticated user
            if (!existingBean.get().getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            Bean bean = mapToEntity(request, userId);
            bean.setId(id);
            Bean updatedBean = beanService.updateBean(bean);
            return ResponseEntity.ok(mapToResponse(updatedBean));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBean(@PathVariable UUID id, Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        Optional<Bean> existingBean = beanService.getBeanById(id);
        if (existingBean.isPresent()) {
            // Check if the bean belongs to the authenticated user
            if (!existingBean.get().getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            beanService.deleteBean(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private BeanResponse mapToResponse(Bean bean) {
        BeanResponse response = new BeanResponse();
        response.setId(bean.getId());
        response.setUserId(bean.getUserId());
        response.setName(bean.getName());
        response.setOrigin(bean.getOrigin());
        response.setBlend(bean.getBlend());
        response.setRoaster(bean.getRoaster());
        response.setRoastType(bean.getRoastType());
        response.setRoastedAt(bean.getRoastedAt());
        response.setRestDays(bean.getRestDays());
        response.setFlavour(bean.getFlavour());
        response.setWeight(bean.getWeight());
        response.setConsumption(bean.getConsumption());
        response.setNotes(bean.getNotes());
        response.setCreatedAt(bean.getCreatedAt());
        response.setUpdatedAt(bean.getUpdatedAt());
        response.setIsActive(bean.getIsActive());
        return response;
    }

    private Bean mapToEntity(BeanRequest request, UUID userId) {
        return Bean.builder()
                .userId(userId)
                .name(request.getName())
                .origin(request.getOrigin())
                .blend(request.getBlend())
                .roaster(request.getRoaster())
                .roastType(request.getRoastType())
                .roastedAt(request.getRoastedAt())
                .restDays(request.getRestDays())
                .flavour(request.getFlavour())
                .weight(request.getWeight())
                .consumption(request.getConsumption())
                .notes(request.getNotes())
                .isActive(request.getIsActive())
                .build();
    }
}