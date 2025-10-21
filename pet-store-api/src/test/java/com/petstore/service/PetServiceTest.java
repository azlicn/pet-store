package com.petstore.service;

import com.petstore.model.Pet;
import com.petstore.model.PetStatus;
import com.petstore.model.Category;
import com.petstore.model.User;
import com.petstore.model.Role;
import com.petstore.repository.PetRepository;
import com.petstore.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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

    @Test
    void getAllPets_ShouldReturnAllPets() {

        List<Pet> expectedPets = Arrays.asList(testPet);
        when(petRepository.findAll()).thenReturn(expectedPets);

        List<Pet> actualPets = petService.getAllPets();

        assertThat(actualPets).hasSize(1);
        assertThat(actualPets.get(0).getName()).isEqualTo("Buddy");
        verify(petRepository).findAll();
    }


    @Test
    void getPetById_WhenPetExists_ShouldReturnPet() {

        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));

        Optional<Pet> actualPet = petService.getPetById(1L);

        assertThat(actualPet).isPresent();
        assertThat(actualPet.get().getName()).isEqualTo("Buddy");
        verify(petRepository).findById(1L);
    }

    @Test
    void getPetById_WhenPetDoesNotExist_ShouldReturnEmpty() {

        when(petRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Pet> actualPet = petService.getPetById(999L);

        assertThat(actualPet).isEmpty();
        verify(petRepository).findById(999L);
    }

    @Test
    void savePet_ShouldSaveAndReturnPet() {

        when(petRepository.save(any(Pet.class))).thenReturn(testPet);

        Pet savedPet = petService.savePet(testPet);

        assertThat(savedPet).isNotNull();
        assertThat(savedPet.getName()).isEqualTo("Buddy");
        verify(petRepository).save(testPet);
    }

    @Test
    void purchasePet_WhenPetIsAvailable_ShouldAssignOwnerAndChangeStatus() {

        testPet.setStatus(PetStatus.AVAILABLE);
        testPet.setOwner(null);

        Pet purchasedPet = new Pet();
        purchasedPet.setId(1L);
        purchasedPet.setName("Buddy");
        purchasedPet.setCategory(testCategory);
        purchasedPet.setPrice(new BigDecimal("299.99"));
        purchasedPet.setStatus(PetStatus.SOLD);
        purchasedPet.setOwner(testUser);

        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(petRepository.save(any(Pet.class))).thenReturn(purchasedPet);

        Pet result = petService.purchasePet(1L, testUser);

        assertThat(result).isNotNull();
        assertThat(result.getOwner()).isEqualTo(testUser);
        assertThat(result.getStatus()).isEqualTo(PetStatus.SOLD);
        verify(petRepository).findById(1L);
        verify(petRepository).save(any(Pet.class));
    }

    @Test
    void purchasePet_WhenPetNotFound_ShouldReturnNull() {

        when(petRepository.findById(999L)).thenReturn(Optional.empty());

        Pet result = petService.purchasePet(999L, testUser);

        assertThat(result).isNull();
        verify(petRepository).findById(999L);
        verify(petRepository, never()).save(any(Pet.class));
    }

    @Test
    void purchasePet_WhenPetNotAvailable_ShouldReturnNull() {

        testPet.setStatus(PetStatus.SOLD);
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));

        Pet result = petService.purchasePet(1L, testUser);

        assertThat(result).isNull();
        verify(petRepository).findById(1L);
        verify(petRepository, never()).save(any(Pet.class));
    }

    @Test
    void updatePetStatus_WhenPetExists_ShouldUpdateStatus() {

        Pet updatedPet = new Pet();
        updatedPet.setId(1L);
        updatedPet.setName("Buddy");
        updatedPet.setStatus(PetStatus.PENDING);

        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(petRepository.save(any(Pet.class))).thenReturn(updatedPet);

        Pet result = petService.updatePetStatus(1L, PetStatus.PENDING);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(PetStatus.PENDING);
        verify(petRepository).findById(1L);
        verify(petRepository).save(any(Pet.class));
    }

    @Test
    void updatePetStatus_WhenPetNotFound_ShouldReturnNull() {

        when(petRepository.findById(999L)).thenReturn(Optional.empty());

        Pet result = petService.updatePetStatus(999L, PetStatus.PENDING);

        assertThat(result).isNull();
        verify(petRepository).findById(999L);
        verify(petRepository, never()).save(any(Pet.class));
    }

    @Test
    void deletePet_WhenPetExists_ShouldReturnTrue() {

        when(petRepository.existsById(1L)).thenReturn(true);

        boolean result = petService.deletePet(1L);

        assertThat(result).isTrue();
        verify(petRepository).existsById(1L);
        verify(petRepository).deleteById(1L);
    }

    @Test
    void deletePet_WhenPetDoesNotExist_ShouldReturnFalse() {

        when(petRepository.existsById(999L)).thenReturn(false);

        boolean result = petService.deletePet(999L);

        assertThat(result).isFalse();
        verify(petRepository).existsById(999L);
        verify(petRepository, never()).deleteById(any());
    }

    @Test
    void getPetsByOwner_ShouldReturnPetsOwnedByUser() {

        testPet.setOwner(testUser);
        List<Pet> userPets = Arrays.asList(testPet);
        when(petRepository.findByOwner(testUser)).thenReturn(userPets);

        List<Pet> result = petService.getPetsByOwner(testUser);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getOwner()).isEqualTo(testUser);
        verify(petRepository).findByOwner(testUser);
    }

}