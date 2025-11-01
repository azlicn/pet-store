package com.petstore.service;

import com.petstore.model.Pet;
import com.petstore.enums.PetStatus;
import com.petstore.exception.InvalidPetException;
import com.petstore.exception.PetNotFoundException;
import com.petstore.model.Category;
import com.petstore.model.User;
import com.petstore.model.Role;
import com.petstore.repository.PetRepository;
import com.petstore.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link PetService} covering CRUD operations, purchase logic,
 * and edge cases.
 * Uses Mockito for mocking dependencies and AssertJ for assertions.
 */
@ExtendWith(MockitoExtension.class)
@ExtendWith(MockitoExtension.class)
@DisplayName("Pet Service Tests")
class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private PetService petService;

    private Pet testPet;
    private Category testCategory;
    private User testUser;
    private User testAdmin;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Dogs");

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("user@test.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setRoles(Set.of(Role.USER));

        testAdmin = new User();
        testAdmin.setId(2L);
        testAdmin.setEmail("admin@test.com");
        testAdmin.setFirstName("Admin");
        testAdmin.setLastName("User");
        testAdmin.setRoles(Set.of(Role.ADMIN));

        testPet = new Pet();
        testPet.setId(1L);
        testPet.setName("Buddy");
        testPet.setCategory(testCategory);
        testPet.setPrice(new BigDecimal("299.99"));
        testPet.setStatus(PetStatus.AVAILABLE);
        testPet.setPhotoUrls(Arrays.asList("photo1.jpg", "photo2.jpg"));
        testPet.setTags(Arrays.asList("friendly", "energetic"));
    }

    /**
     * Test: Should return pet when it exists by ID.
     */
    @Test
    @DisplayName("Get pet by ID - Should return pet when exists")
    void getPetById_WhenPetExists_ShouldReturnPet() {

        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));

        Pet actualPet = petService.getPetById(1L);

        assertThat(actualPet).isNotNull();
        assertThat(actualPet.getName()).isEqualTo("Buddy");
        verify(petRepository).findById(1L);
    }

    /**
     * Test: Should return PetNotFoundException when pet does not exist by ID.
     */
    @Test
    @DisplayName("Get pet by ID - Should return PetNotFoundException when pet does not exist")
    void getPetById_WhenPetDoesNotExist_ShouldReturnPetNotFoundException() {

        when(petRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> petService.getPetById(999L))
                .isInstanceOf(PetNotFoundException.class)
                .hasMessageContaining("Pet not found with ID '999'");
        verify(petRepository).findById(999L);
    }

    /**
     * Test: Should save and return pet when valid.
     */
    @Test
    @DisplayName("Save pet - Should save and return pet")
    void savePet_ShouldSaveAndReturnPet() {

        when(petRepository.save(any(Pet.class))).thenReturn(testPet);

        Pet savedPet = petService.savePet(testPet);

        assertThat(savedPet).isNotNull();
        assertThat(savedPet.getName()).isEqualTo("Buddy");
        verify(petRepository).save(testPet);
    }

    /**
     * Test: Should return true when deleting a pet that exists.
     */
    @Test
    @DisplayName("Delete pet - Should return true when pet exists")
    void deletePet_WhenPetExists_ShouldReturnTrue() {

        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));

        petService.deletePet(1L);

        verify(petRepository).findById(1L);
        verify(petRepository).delete(testPet);
    }

    /**
     * Test: Should return false when deleting a non-existent pet.
     */
    @Test
    @DisplayName("Delete pet - Should return false when pet does not exist")
    void deletePet_WhenPetDoesNotExist_ShouldReturnFalse() {

        when(petRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> petService.deletePet(999L))
                .isInstanceOf(PetNotFoundException.class)
                .hasMessageContaining("Pet not found with ID '999'");
        verify(petRepository).findById(999L);
        verify(petRepository, never()).delete(any());
    }

    /**
     * Test: Should throw InvalidPetException when saving a null pet.
     */
    @Test
    @DisplayName("Save pet - Should throw InvalidPetException when pet is null")
    void savePet_WhenPetIsNull_ShouldThrowException() {
        assertThatThrownBy(() -> petService.savePet(null))
                .isInstanceOf(InvalidPetException.class)
                .hasMessageContaining("Pet cannot be null");
        verify(petRepository, never()).save(any());
    }

    /**
     * Test: Should throw InvalidPetException when deleting a pet with null ID.
     */
    @Test
    @DisplayName("Delete pet - Should throw InvalidPetException when ID is null")
    void deletePet_WhenIdIsNull_ShouldThrowException() {
        assertThatThrownBy(() -> petService.deletePet(null))
                .isInstanceOf(InvalidPetException.class)
                .hasMessageContaining("Pet ID cannot be null");
        verify(petRepository, never()).existsById(any());
        verify(petRepository, never()).deleteById(any());
    }

    /**
     * Test: Should handle saving a pet with missing name and category.
     */
    @Test
    @DisplayName("Save pet - Should handle missing name and category")
    void savePet_WhenMissingNameOrCategory_ShouldHandleGracefully() {
        Pet incompletePet = new Pet();
        incompletePet.setPrice(new BigDecimal("100.00"));
        when(petRepository.save(any(Pet.class))).thenReturn(incompletePet);
        Pet result = petService.savePet(incompletePet);
        assertThat(result).isNotNull();
        assertThat(result.getName()).isNull();
        assertThat(result.getCategory()).isNull();
        verify(petRepository).save(incompletePet);
    }

    /**
     * Test: Should return latest available pets with limit.
     */
    @Test
    @DisplayName("Get latest available pets - Should return limited list")
    void getLatestAvailablePets_ShouldReturnLimitedList() {

        List<Pet> expectedPets = Arrays.asList(testPet);
        when(petRepository.findLatestPetsByStatus(eq(PetStatus.AVAILABLE), any())).thenReturn(expectedPets);
        List<Pet> actualPets = petService.getLatestAvailablePets(1);

        assertThat(actualPets).hasSize(1);
        assertThat(actualPets.get(0).getStatus()).isEqualTo(PetStatus.AVAILABLE);
        verify(petRepository).findLatestPetsByStatus(eq(PetStatus.AVAILABLE), any());
    }

    /**
     * Test: Should update pet details when pet exists.
     */
    @Test
    @DisplayName("Update pet - Should update details when pet exists")
    void updatePet_WhenPetExists_ShouldUpdateDetails() {

        Pet petDetails = new Pet();
        petDetails.setName("UpdatedBuddy");
        petDetails.setDescription("Updated description");
        petDetails.setCategory(testCategory);
        petDetails.setPrice(new BigDecimal("399.99"));
        petDetails.setStatus(PetStatus.PENDING);
        petDetails.setOwner(testUser);
        petDetails.setPhotoUrls(Arrays.asList("updated1.jpg"));
        petDetails.setTags(Arrays.asList("updated"));

        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(categoryRepository.findById(testCategory.getId())).thenReturn(Optional.of(testCategory));
        when(petRepository.save(any(Pet.class))).thenReturn(petDetails);

        Pet updatedPet = petService.updatePet(1L, petDetails);
        assertThat(updatedPet).isNotNull();
        assertThat(updatedPet.getName()).isEqualTo("UpdatedBuddy");
        assertThat(updatedPet.getDescription()).isEqualTo("Updated description");
        assertThat(updatedPet.getCategory()).isEqualTo(testCategory);
        assertThat(updatedPet.getPrice()).isEqualByComparingTo(new BigDecimal("399.99"));
        assertThat(updatedPet.getStatus()).isEqualTo(PetStatus.PENDING);
        assertThat(updatedPet.getOwner()).isEqualTo(testUser);
        assertThat(updatedPet.getPhotoUrls()).containsExactly("updated1.jpg");
        assertThat(updatedPet.getTags()).containsExactly("updated");
        verify(petRepository).findById(1L);
        verify(categoryRepository).findById(testCategory.getId());
        verify(petRepository).save(any(Pet.class));
    }

    /**
     * Test: Should return null when updating a non-existent pet.
     */
    @Test
    @DisplayName("Update pet - Should return null when pet not found")
    void updatePet_WhenPetNotFound_ShouldReturnNull() {

        Pet petDetails = new Pet();
        when(petRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> petService.updatePet(999L, petDetails))
                .isInstanceOf(PetNotFoundException.class)
                .hasMessageContaining("Pet not found with ID '999'");
        verify(petRepository).findById(999L);
        verify(petRepository, never()).save(any(Pet.class));
    }

    /**
     * Test: Should return paginated pets with filters.
     */
    @Test
    @DisplayName("Find pets by filters paginated - Should return filtered and paginated pets")
    void findPetsByFiltersPaginated_ShouldReturnFilteredPage() {
        // Setup test data
        List<Pet> pets = Arrays.asList(testPet);
        org.springframework.data.domain.Page<Pet> petPage = new org.springframework.data.domain.PageImpl<>(pets);
        // Mock repository method
        when(petRepository.findPetsByFiltersPaginated(eq("Buddy"), eq(1L), eq(PetStatus.AVAILABLE), eq(1L), any()))
                .thenReturn(petPage);

        org.springframework.data.domain.Page<Pet> result = petService.findPetsByFiltersPaginated("Buddy", 1L,
                PetStatus.AVAILABLE, 1L, 0, 10);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Buddy");
        verify(petRepository).findPetsByFiltersPaginated(eq("Buddy"), eq(1L), eq(PetStatus.AVAILABLE), eq(1L), any());
    }
}