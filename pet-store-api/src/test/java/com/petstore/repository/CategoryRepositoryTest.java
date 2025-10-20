package com.petstore.repository;

import com.petstore.config.TestDatabaseConfig;
import com.petstore.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import jakarta.validation.ConstraintViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(TestDatabaseConfig.class)
@ActiveProfiles("test")
class CategoryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category dogsCategory;
    private Category catsCategory;

    @BeforeEach
    void setUp() {
        dogsCategory = new Category();
        dogsCategory.setName("Dogs");
        dogsCategory = entityManager.persistAndFlush(dogsCategory);

        catsCategory = new Category();
        catsCategory.setName("Cats");
        catsCategory = entityManager.persistAndFlush(catsCategory);

        entityManager.clear();
    }

    @Test
    void findByName_ShouldReturnCategoryWhenNameExists() {

        Optional<Category> foundCategory = categoryRepository.findByName("Dogs");

        assertThat(foundCategory).isPresent();
        assertThat(foundCategory.get().getName()).isEqualTo("Dogs");
        assertThat(foundCategory.get().getId()).isEqualTo(dogsCategory.getId());
    }

    @Test
    void findByName_ShouldReturnEmptyWhenNameDoesNotExist() {

        Optional<Category> foundCategory = categoryRepository.findByName("Birds");

        assertThat(foundCategory).isEmpty();
    }

    @Test
    void findByName_ShouldBeCaseSensitive() {

        Optional<Category> foundCategory1 = categoryRepository.findByName("dogs");
        Optional<Category> foundCategory2 = categoryRepository.findByName("DOGS");
        Optional<Category> foundCategory3 = categoryRepository.findByName("Dogs");

        assertThat(foundCategory1).isEmpty();
        assertThat(foundCategory2).isEmpty();
        assertThat(foundCategory3).isPresent();
    }

    @Test
    void findByName_ShouldHandleWhitespaceInName() {

        Category categoryWithSpaces = new Category();
        categoryWithSpaces.setName("  Birds  ");
        categoryWithSpaces = entityManager.persistAndFlush(categoryWithSpaces);
        entityManager.clear();

        Optional<Category> foundCategory = categoryRepository.findByName("Birds");

        assertThat(foundCategory).isPresent();
        assertThat(foundCategory.get().getName()).isEqualTo("Birds");
    }

    @Test
    void existsByName_ShouldReturnTrueWhenNameExists() {

        boolean exists = categoryRepository.existsByName("Dogs");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByName_ShouldReturnFalseWhenNameDoesNotExist() {

        boolean exists = categoryRepository.existsByName("Birds");

        assertThat(exists).isFalse();
    }

    @Test
    void existsByName_ShouldBeCaseSensitive() {

        boolean exists1 = categoryRepository.existsByName("dogs");
        boolean exists2 = categoryRepository.existsByName("DOGS");
        boolean exists3 = categoryRepository.existsByName("Dogs");

        assertThat(exists1).isFalse();
        assertThat(exists2).isFalse();
        assertThat(exists3).isTrue();
    }

    @Test
    void existsByName_ShouldHandleWhitespaceCorrectly() {

        Category categoryWithSpaces = new Category();
        categoryWithSpaces.setName("  Fish  ");
        categoryWithSpaces = entityManager.persistAndFlush(categoryWithSpaces);
        entityManager.clear();

        boolean exists = categoryRepository.existsByName("Fish");

        assertThat(exists).isTrue();
    }

    @Test
    void save_ShouldPersistNewCategory() {

        Category newCategory = new Category();
        newCategory.setName("Birds");

        Category savedCategory = categoryRepository.save(newCategory);

        assertThat(savedCategory.getId()).isNotNull();
        assertThat(savedCategory.getName()).isEqualTo("Birds");
        assertThat(savedCategory.getCreatedAt()).isNotNull();
        assertThat(savedCategory.getUpdatedAt()).isNotNull();
    }

    @Test
    void save_ShouldUpdateExistingCategory() {

        dogsCategory.setName("Updated Dogs");

        Category updatedCategory = categoryRepository.save(dogsCategory);

        assertThat(updatedCategory.getId()).isEqualTo(dogsCategory.getId());
        assertThat(updatedCategory.getName()).isEqualTo("Updated Dogs");
        assertThat(updatedCategory.getUpdatedAt()).isAfter(updatedCategory.getCreatedAt());
    }

    @Test
    void save_ShouldThrowExceptionForDuplicateName() {
        Category duplicateCategory = new Category();
        duplicateCategory.setName("Dogs");

        assertThatThrownBy(() -> {
            categoryRepository.save(duplicateCategory);
            entityManager.flush();
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void deleteById_ShouldRemoveCategoryFromDatabase() {

        Long categoryId = dogsCategory.getId();
        assertThat(categoryRepository.findById(categoryId)).isPresent();

        categoryRepository.deleteById(categoryId);

        assertThat(categoryRepository.findById(categoryId)).isEmpty();
    }

    @Test
    void findAll_ShouldReturnAllCategories() {

        Iterable<Category> allCategories = categoryRepository.findAll();

        assertThat(allCategories).hasSize(2);
        assertThat(allCategories).extracting(Category::getName)
                .containsExactlyInAnyOrder("Dogs", "Cats");
    }

    @Test
    void count_ShouldReturnCorrectNumberOfCategories() {

        long categoryCount = categoryRepository.count();

        assertThat(categoryCount).isEqualTo(2);
    }

    @Test
    void findById_ShouldReturnCategoryWhenIdExists() {

        Optional<Category> foundCategory = categoryRepository.findById(dogsCategory.getId());

        assertThat(foundCategory).isPresent();
        assertThat(foundCategory.get().getName()).isEqualTo("Dogs");
    }

    @Test
    void findById_ShouldReturnEmptyWhenIdDoesNotExist() {

        Optional<Category> foundCategory = categoryRepository.findById(999L);

        assertThat(foundCategory).isEmpty();
    }

    @Test
    void save_ShouldHandleNullName() {

        Category categoryWithNullName = new Category();
        categoryWithNullName.setName(null);

        assertThatThrownBy(() -> {
            categoryRepository.save(categoryWithNullName);
            entityManager.flush();
        }).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void save_ShouldHandleEmptyName() {

        Category categoryWithEmptyName = new Category();
        categoryWithEmptyName.setName("");

        assertThatThrownBy(() -> {
            categoryRepository.save(categoryWithEmptyName);
            entityManager.flush();
        }).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void save_ShouldTrimWhitespaceFromName() {

        Category categoryWithSpaces = new Category();
        categoryWithSpaces.setName("  Fish  ");

        Category savedCategory = categoryRepository.save(categoryWithSpaces);

        assertThat(savedCategory.getName()).isEqualTo("Fish");
    }
}