package br.imd.aqueducte.services.implementations;

import br.imd.aqueducte.repositories.GenericRepository;
import br.imd.aqueducte.services.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GenericServiceImpl<T> implements GenericService<T> {

    @Autowired
    private GenericRepository<T> repository;

    @Override
    public T createOrUpdate(T obj) {
        return this.repository.save(obj);
    }

    @Override
    public List<T> findAll() {
        return this.repository.findAll();
    }

    @Override
    public Optional<T> findById(String id) {
        return this.repository.findById(id);
    }

    @Override
    public String delete(String id) {
        Optional<T> obj = findById(id);
        if (obj.isEmpty())
            return null;
        this.repository.deleteById(id);
        return id;
    }
}
