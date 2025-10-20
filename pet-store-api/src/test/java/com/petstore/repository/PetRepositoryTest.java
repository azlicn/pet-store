package com.petstore.repository;

import com.petstore.config.TestDatabaseConfig;
import com.petstore.model.Pet;
import com.petstore.model.PetStatus;
import com.petstore.model.Category;
import com.petstore.model.User;
import com.petstore.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestDatabaseConfig.class)
@ActiveProfiles("test")
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

    @Test
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

    @Test
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

    @Test
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

    @Test
    void findByOwnerIsNullAndStatus_ShouldReturnStoreInventoryPets() {

        List<Pet> storeInventory = petRepository.findByOwnerIsNullAndStatus(PetStatus.AVAILABLE);

        assertThat(storeInventory).hasSize(1);
        assertThat(storeInventory.get(0).getName()).isEqualTo("Buddy");
        assertThat(storeInventory.get(0).getOwner()).isNull();
        assertThat(storeInventory.get(0).getStatus()).isEqualTo(PetStatus.AVAILABLE);
    }

    @Test
    void findByOwner_ShouldReturnPetsOwnedBySpecificUser() {

        List<Pet> testUserPets = petRepository.findByOwner(testUser);
        List<Pet> anotherUserPets = petRepository.findByOwner(anotherUser);

        assertThat(testUserPets).hasSize(1);
        assertThat(testUserPets.get(0).getName()).isEqualTo("Whiskers");
        assertThat(testUserPets.get(0).getOwner().getId()).isEqualTo(testUser.getId());

        assertThat(anotherUserPets).hasSize(1);
        assertThat(anotherUserPets.get(0).getName()).isEqualTo("Max");
        assertThat(anotherUserPets.get(0).getOwner().getId()).isEqualTo(anotherUser.getId());
    }

    @Test
    void findPetsByFilters_WithNameFilter_ShouldReturnMatchingPets() {

        List<Pet> results = petRepository.findPetsByFilters("buddy", null, null, Pageable.unpaged());

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Buddy");
    }

    @Test
    void findPetsByFilters_WithCategoryFilter_ShouldReturnMatchingPets() {

        List<Pet> results = petRepository.findPetsByFilters(null, dogsCategory.getId(), null, Pageable.unpaged());

        assertThat(results).hasSize(2);
        assertThat(results).extracting(Pet::getName).containsExactlyInAnyOrder("Buddy", "Max");
        assertThat(results).allMatch(pet -> pet.getCategory().getId().equals(dogsCategory.getId()));
    }

    @Test
    void findPetsByFilters_WithStatusFilter_ShouldReturnMatchingPets() {

        List<Pet> availableResults = petRepository.findPetsByFilters(null, null, PetStatus.AVAILABLE,
                Pageable.unpaged());
        List<Pet> soldResults = petRepository.findPetsByFilters(null, null, PetStatus.SOLD, Pageable.unpaged());

        assertThat(availableResults).hasSize(1);
        assertThat(availableResults.get(0).getStatus()).isEqualTo(PetStatus.AVAILABLE);

        assertThat(soldResults).hasSize(2);
        assertThat(soldResults).allMatch(pet -> pet.getStatus() == PetStatus.SOLD);
    }

    @Test
    void findPetsByFilters_WithMultipleFilters_ShouldReturnMatchingPets() {

        List<Pet> results = petRepository.findPetsByFilters(
                "max", dogsCategory.getId(), PetStatus.SOLD, Pageable.unpaged());

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Max");
        assertThat(results.get(0).getCategory().getId()).isEqualTo(dogsCategory.getId());
        assertThat(results.get(0).getStatus()).isEqualTo(PetStatus.SOLD);
    }

    @Test
    void findPetsByFilters_WithLimit_ShouldRespectPageable() {

        List<Pet> results = petRepository.findPetsByFilters(
                null, null, PetStatus.SOLD, PageRequest.of(0, 1));

        assertThat(results).hasSize(1);
    }

    @Test
    void findPetsByFilters_WithNullFilters_ShouldReturnAllPets() {

        List<Pet> results = petRepository.findPetsByFilters(null, null, null, Pageable.unpaged());

        assertThat(results).hasSize(3);
        assertThat(results).extracting(Pet::getName).containsExactlyInAnyOrder("Buddy", "Whiskers", "Max");
    }

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
    void deleteById_ShouldRemovePetFromDatabase() {

        Long petId = availableDog.getId();
        assertThat(petRepository.findById(petId)).isPresent();

        petRepository.deleteById(petId);

        assertThat(petRepository.findById(petId)).isEmpty();
    }
}