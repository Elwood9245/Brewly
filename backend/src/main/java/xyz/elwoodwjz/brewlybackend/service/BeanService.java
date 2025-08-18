package xyz.elwoodwjz.brewlybackend.service;

import org.springframework.stereotype.Service;
import xyz.elwoodwjz.brewlybackend.entity.Bean;
import xyz.elwoodwjz.brewlybackend.repository.BeanRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BeanService {

    private final BeanRepository beanRepository;

    public BeanService(BeanRepository beanRepository) {
        this.beanRepository = beanRepository;
    }

    public Bean createBean(Bean bean) {
        return beanRepository.save(bean);
    }

    public List<Bean> getAllBeans() {
        return beanRepository.findAll();
    }

    public List<Bean> getBeansByUserId(UUID userId) {
        return beanRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Bean> getActiveBeansByUserId(UUID userId) {
        return beanRepository.findByUserIdAndIsActiveTrueOrderByCreatedAtDesc(userId);
    }

    public Optional<Bean> getBeanById(UUID id) {
        return beanRepository.findById(id);
    }

    public Bean updateBean(Bean bean) {
        return beanRepository.save(bean);
    }

    public void deleteBean(UUID id) {
        beanRepository.deleteById(id);
    }
}