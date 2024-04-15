package ru.practicum.main.server.categories.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main.server.categories.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Boolean existsByName(String name);


}
