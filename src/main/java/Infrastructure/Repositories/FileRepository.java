package Infrastructure.Repositories;

import Domain.Entities.Entity;
import Domain.Repositories.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class FileRepository <T extends Entity> implements Repository<T> {

    private final String fileName;
    private final Function<String, T> mapper;

    public FileRepository(String fileName, Function<String, T> mapper) {
        this.fileName = "src/main/java/DB/" + fileName;
        this.mapper = mapper;
    }

    @Override
    public void save(T entity) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, true))) {
            bw.write(entity.toString());
            bw.newLine();
        }
    }

    @Override
    public List<T> findAll() throws IOException {
        List<T> entities = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                entities.add(mapper.apply(line));
            }
        }
        return entities;
    }

    @Override
    public boolean existsById(UUID id) throws Exception {
        return false;
    }

    @Override
    public Optional<T> findById(UUID id) throws IOException {
        List<T> entities = findAll();
        for (T entity : entities) {
            if (entity.getId() == (id)) {
                return Optional.of(entity);
            }
        }
        return Optional.empty();
    }

    @Override
    public void update(T updatedEntity) throws IOException {
        List<T> entities = findAll();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, false))) {
            for (T entity : entities) {
                if (entity.getId() == (updatedEntity.getId())) {
                    bw.write(updatedEntity.toString());
                } else {
                    bw.write(entity.toString());
                }
                bw.newLine();
            }
        }
    }

    @Override
    public void delete(UUID id) throws IOException {
        List<T> entities = findAll();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, false))) {
            for (T entity : entities) {
                if (entity.getId() != id) {
                    bw.write(entity.toString());
                    bw.newLine();
                }
            }
        }
    }

}