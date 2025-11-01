package com.petstore.repository;


import com.petstore.model.Pet;
import com.petstore.enums.PetStatus;
import com.petstore.model.Category;
import com.petstore.model.User;
import com.petstore.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import com.petstore.config.JpaAuditingConfig;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for PetRepository.
 */
@DataJpaTest
@Import(JpaAuditingConfig.class)
@ActiveProfiles("test")
@DisplayName("Pet Repository Tests")
class PetRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PetRepository petRepository;

    private Category dogsCategory;
    private Category catsCategory;
    private User testUser;
    private User anotherUser;
    private Pet availableDog;
    private Pet soldCat;
    private Pet userOwnedPet;

    @BeforeEach
    void setUp() {
        dogsCategory = new Category();
        dogsCategory.setName("Dogs");
        dogsCategory = entityManager.persistAndFlush(dogsCategory);

        catsCategory = new Category();
        catsCategory.setName("Cats");
        catsCategory = entityManager.persistAndFlush(catsCategory);

        testUser = new User();
        testUser.setEmail("user@test.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setPassword("password123");
        testUser.setRoles(Set.of(Role.USER));
        testUser = entityManager.persistAndFlush(testUser);

        anotherUser = new User();
        anotherUser.setEmail("another@test.com");
        anotherUser.setFirstName("Jane");
        anotherUser.setLastName("Smith");
        anotherUser.setPassword("password456");
        anotherUser.setRoles(Set.of(Role.USER));

        anotherUser = entityManager.persistAndFlush(anotherUser);

        availableDog = new Pet();
        availableDog.setName("Buddy");
        availableDog.setCategory(dogsCategory);
        availableDog.setPrice(new BigDecimal("299.99"));
        availableDog.setStatus(PetStatus.AVAILABLE);
        availableDog.setPhotoUrls(Arrays.asList("buddy1.jpg", "buddy2.jpg"));
        availableDog.setTags(Arrays.asList("friendly", "energetic"));

        availableDog = entityManager.persistAndFlush(availableDog);

        soldCat = new Pet();
        soldCat.setName("Whiskers");
        soldCat.setCategory(catsCategory);
        soldCat.setPrice(new BigDecimal("199.99"));
        soldCat.setStatus(PetStatus.SOLD);
        soldCat.setPhotoUrls(Arrays.asList("whiskers1.jpg"));
        soldCat.setTags(Arrays.asList("calm", "indoor"));
        soldCat.setOwner(testUser);
        soldCat = entityManager.persistAndFlush(soldCat);

        userOwnedPet = new Pet();
        userOwnedPet.setName("Max");
        userOwnedPet.setCategory(dogsCategory);
        userOwnedPet.setPrice(new BigDecimal("399.99"));
        userOwnedPet.setStatus(PetStatus.SOLD);
        userOwnedPet.setPhotoUrls(Arrays.asList("max1.jpg"));
        userOwnedPet.setTags(Arrays.asList("loyal", "guard"));
        userOwnedPet.setOwner(anotherUser);
        userOwnedPet = entityManager.persistAndFlush(userOwnedPet);

        entityManager.clear();
    }

    /**
     * Find by status - Should return pets with specific status
     */
    @Test
    @DisplayName("Find by status - Should return pets with specific status")
    void findByStatus_ShouldReturnPetsWithSpecificStatus() {

        List<Pet> availablePets = petRepository.findByStatus(PetStatus.AVAILABLE);
        List<Pet> soldPets = petRepository.findByStatus(PetStatus.SOLD);

        assertThat(availablePets).hasSize(1);
        assertThat(availablePets.get(0).getName()).isEqualTo("Buddy");
        assertThat(availablePets.get(0).getStatus()).isEqualTo(PetStatus.AVAILABLE);

        assertThat(soldPets).hasSize(2);
        assertThat(soldPets).extracting(Pet::getName).containsExactlyInAnyOrder("Whiskers", "Max");
        assertThat(soldPets).allMatch(pet -> pet.getStatus() == PetStatus.SOLD);
    }

    /**
     * Find by category ID - Should return pets in specific category
     */
    @Test
    @DisplayName("Find by category ID - Should return pets in specific category")
    void findByCategoryId_ShouldReturnPetsInSpecificCategory() {

        List<Pet> dogs = petRepository.findByCategoryId(dogsCategory.getId());
        List<Pet> cats = petRepository.findByCategoryId(catsCategory.getId());

        assertThat(dogs).hasSize(2);
        assertThat(dogs).extracting(Pet::getName).containsExactlyInAnyOrder("Buddy", "Max");
        assertThat(dogs).allMatch(pet -> pet.getCategory().getId().equals(dogsCategory.getId()));

        assertThat(cats).hasSize(1);
        assertThat(cats.get(0).getName()).isEqualTo("Whiskers");
        assertThat(cats.get(0).getCategory().getId()).isEqualTo(catsCategory.getId());
    }

    /**
     * Find by name containing ignoring case - Should return matching pets ignoring case
     */
    @Test
    @DisplayName("Find by name containing - Should return matching pets ignoring case")
    void findByNameContainingIgnoreCase_ShouldReturnMatchingPets() {

        List<Pet> buddyResults = petRepository.findByNameContainingIgnoreCase("buddy");
        List<Pet> maxResults = petRepository.findByNameContainingIgnoreCase("MAX");
        List<Pet> emptyResults = petRepository.findByNameContainingIgnoreCase("nonexistent");

        assertThat(buddyResults).hasSize(1);
        assertThat(buddyResults.get(0).getName()).isEqualTo("Buddy");

        assertThat(maxResults).hasSize(1);
        assertThat(maxResults.get(0).getName()).isEqualTo("Max");

        assertThat(emptyResults).isEmpty();
    }

    /**
     * Find by owner is null and status - Should return store inventory pets
     */
    @Test
    @DisplayName("Find by owner is null and status - Should return store inventory pets")
    void findByOwnerIsNullAndStatus_ShouldReturnStoreInventoryPets() {

        List<Pet> storeInventory = petRepository.findByOwnerIsNullAndStatus(PetStatus.AVAILABLE);

        assertThat(storeInventory).hasSize(1);
        assertThat(storeInventory.get(0).getName()).isEqualTo("Buddy");
        assertThat(storeInventory.get(0).getOwner()).isNull();
        assertThat(storeInventory.get(0).getStatus()).isEqualTo(PetStatus.AVAILABLE);
    }

    /**
     * Find pets by filters - With name filter should return matching pets
     */
    @Test
    @DisplayName("Find pets by filters - With name filter should return matching pets")
    void findPetsByFilters_WithNameFilter_ShouldReturnMatchingPets() {

        List<Pet> results = petRepository.findPetsByFilters("buddy", null, null, Pageable.unpaged());

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Buddy");
    }

    /**
     * Find pets by filters - With category filter should return matching pets
     */
    @Test
    @DisplayName("Find pets by filters - With category filter should return matching pets")
    void findPetsByFilters_WithCategoryFilter_ShouldReturnMatchingPets() {

        List<Pet> results = petRepository.findPetsByFilters(null, dogsCategory.getId(), null, Pageable.unpaged());

        assertThat(results).hasSize(2);
        assertThat(results).extracting(Pet::getName).containsExactlyInAnyOrder("Buddy", "Max");
        assertThat(results).allMatch(pet -> pet.getCategory().getId().equals(dogsCategory.getId()));
    }

    /**
     * Find pets by filters - With status filter should return matching pets
     */
    @Test
    @DisplayName("Find pets by filters - With status filter should return matching pets")
    void findPetsByFilters_WithStatusFilter_ShouldReturnMatchingPets() {

        List<Pet> availableResults = petRepository.findPetsByFilters(null, null, PetStatus.AVAILABLE,
                Pageable.unpaged());
        List<Pet> soldResults = petRepository.findPetsByFilters(null, null, PetStatus.SOLD, Pageable.unpaged());

        assertThat(availableResults).hasSize(1);
        assertThat(availableResults.get(0).getStatus()).isEqualTo(PetStatus.AVAILABLE);

        assertThat(soldResults).hasSize(2);
        assertThat(soldResults).allMatch(pet -> pet.getStatus() == PetStatus.SOLD);
    }

    /**
     * Find pets by filters - With multiple filters should return matching pets
     */
    @Test
    @DisplayName("Find pets by filters - With multiple filters should return matching pets")
    void findPetsByFilters_WithMultipleFilters_ShouldReturnMatchingPets() {

        List<Pet> results = petRepository.findPetsByFilters(
                "max", dogsCategory.getId(), PetStatus.SOLD, Pageable.unpaged());

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Max");
        assertThat(results.get(0).getCategory().getId()).isEqualTo(dogsCategory.getId());
        assertThat(results.get(0).getStatus()).isEqualTo(PetStatus.SOLD);
    }

    /**
     * Find pets by filters - With limit should respect pageable
     */
    @Test
    @DisplayName("Find pets by filters - With limit should respect pageable")
    void findPetsByFilters_WithLimit_ShouldRespectPageable() {

        List<Pet> results = petRepository.findPetsByFilters(
                null, null, PetStatus.SOLD, PageRequest.of(0, 1));

        assertThat(results).hasSize(1);
    }

    /**
     * Find pets by filters - With null filters should return all pets
     */
    @Test
    @DisplayName("Find pets by filters - With null filters should return all pets")
    void findPetsByFilters_WithNullFilters_ShouldReturnAllPets() {

        List<Pet> results = petRepository.findPetsByFilters(null, null, null, Pageable.unpaged());

        assertThat(results).hasSize(3);
        assertThat(results).extracting(Pet::getName).containsExactlyInAnyOrder("Buddy", "Whiskers", "Max");
    }

    /**
     * Find latest pets by status - Should return pets ordered by created date descending
     */
    @Test
    @DisplayName("Find latest pets by status - Should return pets ordered by created date descending")
    void findLatestPetsByStatus_ShouldReturnPetsOrderedByCreatedAtDesc() {

        Pet olderPet = new Pet();
        olderPet.setName("OlderPet");
        olderPet.setCategory(dogsCategory);
        olderPet.setPrice(new BigDecimal("150.00"));
        olderPet.setStatus(PetStatus.AVAILABLE);
        entityManager.persistAndFlush(olderPet);

        Pet newerPet = new Pet();
        newerPet.setName("NewerPet");
        newerPet.setCategory(catsCategory);
        newerPet.setPrice(new BigDecimal("250.00"));
        newerPet.setStatus(PetStatus.AVAILABLE);
        entityManager.persistAndFlush(newerPet);

        entityManager.clear();

        List<Pet> results = petRepository.findLatestPetsByStatus(PetStatus.AVAILABLE, PageRequest.of(0, 2));

        assertThat(results).hasSize(2);
        assertThat(results.get(0).getName()).isEqualTo("NewerPet");
        assertThat(results.get(1).getName()).isEqualTo("OlderPet");
    }

    /**
     * Find latest pets by status - With limit should respect pageable
     */
    @Test
    @DisplayName("Find latest pets by status - With limit should respect pageable")
    void findLatestPetsByStatus_WithLimit_ShouldRespectPageable() {

        for (int i = 1; i <= 5; i++) {
            Pet pet = new Pet();
            pet.setName("Pet" + i);
            pet.setCategory(dogsCategory);
            pet.setPrice(new BigDecimal("100.00"));
            pet.setStatus(PetStatus.AVAILABLE);
            entityManager.persistAndFlush(pet);
        }

        entityManager.clear();

        List<Pet> results = petRepository.findLatestPetsByStatus(PetStatus.AVAILABLE, PageRequest.of(0, 3));

        assertThat(results).hasSize(3);
    }

    /**
     * Find by owner is null and status - Should filter different statuses correctly
     */
    @Test
    @DisplayName("Find by owner is null and status - Should filter different statuses correctly")
    void findByOwnerIsNullAndStatus_WithDifferentStatuses_ShouldFilterCorrectly() {

        Pet pendingPet = new Pet();
        pendingPet.setName("PendingPet");
        pendingPet.setCategory(dogsCategory);
        pendingPet.setPrice(new BigDecimal("350.00"));
        pendingPet.setStatus(PetStatus.PENDING);

        entityManager.persistAndFlush(pendingPet);

        entityManager.clear();

        List<Pet> availableStoreInventory = petRepository.findByOwnerIsNullAndStatus(PetStatus.AVAILABLE);
        List<Pet> pendingStoreInventory = petRepository.findByOwnerIsNullAndStatus(PetStatus.PENDING);
        List<Pet> soldStoreInventory = petRepository.findByOwnerIsNullAndStatus(PetStatus.SOLD);

        assertThat(availableStoreInventory).hasSize(1);
        assertThat(availableStoreInventory.get(0).getName()).isEqualTo("Buddy");

        assertThat(pendingStoreInventory).hasSize(1);
        assertThat(pendingStoreInventory.get(0).getName()).isEqualTo("PendingPet");

        assertThat(soldStoreInventory).isEmpty();
    }

    /**
     * Save - Should persist pet with all fields
     */
    @Test
    @DisplayName("Save - Should persist pet with all fields")
    void save_ShouldPersistPetWithAllFields() {

        Pet newPet = new Pet();
        newPet.setName("TestPet");
        newPet.setCategory(dogsCategory);
        newPet.setPrice(new BigDecimal("500.00"));
        newPet.setStatus(PetStatus.AVAILABLE);
        newPet.setPhotoUrls(Arrays.asList("test1.jpg", "test2.jpg"));
        newPet.setTags(Arrays.asList("test", "demo"));

        Pet savedPet = petRepository.save(newPet);

        assertThat(savedPet.getId()).isNotNull();
        assertThat(savedPet.getName()).isEqualTo("TestPet");
        assertThat(savedPet.getCategory().getId()).isEqualTo(dogsCategory.getId());
        assertThat(savedPet.getPrice()).isEqualByComparingTo(new BigDecimal("500.00"));
        assertThat(savedPet.getStatus()).isEqualTo(PetStatus.AVAILABLE);
        assertThat(savedPet.getPhotoUrls()).containsExactly("test1.jpg", "test2.jpg");
        assertThat(savedPet.getTags()).containsExactly("test", "demo");
        assertThat(savedPet.getCreatedAt()).isNotNull();
        assertThat(savedPet.getUpdatedAt()).isNotNull();
    }

    /**
     * Delete by ID - Should remove pet from database
     */
    @Test
    @DisplayName("Delete by ID - Should remove pet from database")
    void deleteById_ShouldRemovePetFromDatabase() {

        Long petId = availableDog.getId();
        assertThat(petRepository.findById(petId)).isPresent();

        petRepository.deleteById(petId);

        assertThat(petRepository.findById(petId)).isEmpty();
    }

    /**
     * Find pets by filters paginated - Should return paginated pets for user
     */
    @Test
    @DisplayName("Find pets by filters paginated - Should return paginated pets for user")
    void findPetsByFiltersPaginated_ShouldReturnPaginatedPetsForUser() {

        Pageable pageable = PageRequest.of(0, 2);

        Page<Pet> page = petRepository.findPetsByFiltersPaginated(
            null, null, null, testUser.getId(), pageable);
        assertThat(page.getContent()).extracting(Pet::getName).contains("Whiskers");
        assertThat(page.getContent()).allMatch(pet -> pet.getOwner() == null || pet.getOwner().getId().equals(testUser.getId()) || pet.getCreatedBy() == testUser.getId());
    }

    /**
     * Exists by ID and status - Should return true if pet exists with status
     */
    @Test
    @DisplayName("Exists by ID and status - Should return true if pet exists with status")
    void existsByIdAndStatus_ShouldReturnTrueIfPetExistsWithStatus() {

        boolean existsAvailable = petRepository.existsByIdAndStatus(availableDog.getId(), PetStatus.AVAILABLE);
        boolean existsSold = petRepository.existsByIdAndStatus(soldCat.getId(), PetStatus.SOLD);
        boolean notExists = petRepository.existsByIdAndStatus(availableDog.getId(), PetStatus.SOLD);

        assertThat(existsAvailable).isTrue();
        assertThat(existsSold).isTrue();
        assertThat(notExists).isFalse();
    }
    /**
     * Test: Should find pets by owner.
     */
    @Test
    @DisplayName("Find pets by owner - Should return pets for given owner")
    void findByOwner_ShouldReturnPetsForOwner() {
        

        Pet pet1 = new Pet();
        pet1.setName("Buddy");
        pet1.setPrice(new BigDecimal("200.00"));
        pet1.setOwner(testUser);
        pet1.setCategory(dogsCategory);

        petRepository.save(pet1);

        entityManager.persistAndFlush(pet1);

        entityManager.clear();

        List<Pet> pets = petRepository.findByOwner(testUser);
        assertThat(pets).hasSize(2);
        assertThat(pets).extracting(Pet::getName).containsExactlyInAnyOrder("Whiskers", "Buddy");
    }

    /**
     * Test: Should find pets by createdBy.
     */
    @Test
    @DisplayName("Find pets by createdBy - Should return pets for given creator")
    void findByCreatedBy_ShouldReturnPetsForCreator() {

        Pet pet1 = new Pet();
        pet1.setName("Maggie");
        pet1.setCategory(dogsCategory);
        pet1.setPrice(new BigDecimal("250.00"));
        pet1.setCreatedBy(1L);
        Pet pet2 = new Pet();
        pet2.setName("Maxi");
        pet2.setCategory(dogsCategory);
        pet2.setPrice(new BigDecimal("300.00"));
        pet2.setCreatedBy(1L);
        petRepository.save(pet1);
        petRepository.save(pet2);

        entityManager.persistAndFlush(pet1);
        entityManager.persistAndFlush(pet2);

        entityManager.clear();

        List<Pet> pets = petRepository.findByCreatedBy(1L);
        assertThat(pets).hasSize(2);
        assertThat(pets).extracting(Pet::getName).containsExactlyInAnyOrder("Maggie", "Maxi");
    }
}