package com.petstore.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.petstore.model.Category;
import com.petstore.model.Pet;
import com.petstore.model.Role;
import com.petstore.model.User;
import com.petstore.repository.CategoryRepository;
import com.petstore.repository.PetRepository;
import com.petstore.repository.UserRepository;

/**
 * Unit tests for DataInitializer.
 * Tests data initialization logic using mocked repositories.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DataInitializer Tests")
class DataInitializerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DataInitializer dataInitializer;

    @BeforeEach
    void setUp() {
        // Default mock behavior - use lenient() to avoid unnecessary stubbing exceptions
        lenient().when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
    }

    @Nested
    @DisplayName("Admin Initialization Tests")
    class AdminInitializationTests {

        @Test
        @DisplayName("Should create default admin when none exists")
        void testCreateDefaultAdmin() throws Exception {
            // Given
            when(userRepository.findByEmail("admin@pawfect.com")).thenReturn(Optional.empty());

            // When
            dataInitializer.run();

            // Then
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());

            User savedUser = userCaptor.getValue();
            assertThat(savedUser.getEmail()).isEqualTo("admin@pawfect.com");
            assertThat(savedUser.getFirstName()).isEqualTo("Admin");
            assertThat(savedUser.getLastName()).isEqualTo("User");
            assertThat(savedUser.getRoles()).contains(Role.ADMIN);
            assertThat(savedUser.getPassword()).isEqualTo("encodedPassword");
        }

        @Test
        @DisplayName("Should skip admin creation when admin already exists")
        void testSkipAdminCreationWhenExists() throws Exception {
            // Given
            User existingAdmin = new User("admin@pawfect.com", "password", "Admin", "User");
            when(userRepository.findByEmail("admin@pawfect.com")).thenReturn(Optional.of(existingAdmin));

            // When
            dataInitializer.run();

            // Then
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should encode admin password")
        void testAdminPasswordEncoding() throws Exception {
            // Given
            when(userRepository.findByEmail("admin@pawfect.com")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("admin123")).thenReturn("secureEncodedPassword");

            // When
            dataInitializer.run();

            // Then
            verify(passwordEncoder).encode("admin123");
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            assertThat(userCaptor.getValue().getPassword()).isEqualTo("secureEncodedPassword");
        }
    }

    @Nested
    @DisplayName("Category Initialization Tests")
    class CategoryInitializationTests {

        @Test
        @DisplayName("Should create all default categories when none exist")
        void testCreateAllDefaultCategories() throws Exception {
            // Given
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));
            when(categoryRepository.findByName(anyString())).thenReturn(Optional.empty());
            when(categoryRepository.count()).thenReturn(6L);
            when(petRepository.count()).thenReturn(1L); // Skip pet creation

            // When
            dataInitializer.run();

            // Then
            ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
            verify(categoryRepository, times(6)).save(categoryCaptor.capture());

            var savedCategories = categoryCaptor.getAllValues();
            assertThat(savedCategories).hasSize(6);
            assertThat(savedCategories)
                .extracting(Category::getName)
                .containsExactlyInAnyOrder("Dogs", "Cats", "Birds", "Fish", "Reptiles", "Small Pets");
        }

        @Test
        @DisplayName("Should skip category creation when category already exists")
        void testSkipExistingCategories() throws Exception {
            // Given
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));
            Category existingDogs = new Category();
            existingDogs.setName("Dogs");
            when(categoryRepository.findByName("Dogs")).thenReturn(Optional.of(existingDogs));
            when(categoryRepository.findByName("Cats")).thenReturn(Optional.empty());
            when(categoryRepository.findByName("Birds")).thenReturn(Optional.empty());
            when(categoryRepository.findByName("Fish")).thenReturn(Optional.empty());
            when(categoryRepository.findByName("Reptiles")).thenReturn(Optional.empty());
            when(categoryRepository.findByName("Small Pets")).thenReturn(Optional.empty());
            when(petRepository.count()).thenReturn(1L); // Skip pet creation

            // When
            dataInitializer.run();

            // Then
            ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
            verify(categoryRepository, times(5)).save(categoryCaptor.capture());

            var savedCategories = categoryCaptor.getAllValues();
            assertThat(savedCategories)
                .extracting(Category::getName)
                .doesNotContain("Dogs")
                .containsExactlyInAnyOrder("Cats", "Birds", "Fish", "Reptiles", "Small Pets");
        }

        @Test
        @DisplayName("Should create category with correct name")
        void testCategoryNaming() throws Exception {
            // Given
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));
            when(categoryRepository.findByName("Dogs")).thenReturn(Optional.empty());
            // Other categories exist, only Dogs should be created
            Category existingCat = new Category();
            existingCat.setName("Cats");
            when(categoryRepository.findByName("Cats")).thenReturn(Optional.of(existingCat));
            when(categoryRepository.findByName("Birds")).thenReturn(Optional.of(new Category()));
            when(categoryRepository.findByName("Fish")).thenReturn(Optional.of(new Category()));
            when(categoryRepository.findByName("Reptiles")).thenReturn(Optional.of(new Category()));
            when(categoryRepository.findByName("Small Pets")).thenReturn(Optional.of(new Category()));
            when(petRepository.count()).thenReturn(1L);

            // When
            dataInitializer.run();

            // Then
            ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
            verify(categoryRepository, times(1)).save(categoryCaptor.capture());

            Category savedCategory = categoryCaptor.getValue();
            assertThat(savedCategory.getName()).isEqualTo("Dogs");
        }
    }

    @Nested
    @DisplayName("Pet Initialization Tests")
    class PetInitializationTests {

        @Test
        @DisplayName("Should create sample pets when database is empty")
        void testCreateSamplePetsWhenEmpty() throws Exception {
            // Given
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));
            when(categoryRepository.findByName(anyString())).thenReturn(Optional.of(new Category()));
            when(petRepository.count()).thenReturn(0L);

            Category dogs = new Category();
            dogs.setName("Dogs");
            Category cats = new Category();
            cats.setName("Cats");
            Category birds = new Category();
            birds.setName("Birds");
            Category fish = new Category();
            fish.setName("Fish");

            when(categoryRepository.findByName("Dogs")).thenReturn(Optional.of(dogs));
            when(categoryRepository.findByName("Cats")).thenReturn(Optional.of(cats));
            when(categoryRepository.findByName("Birds")).thenReturn(Optional.of(birds));
            when(categoryRepository.findByName("Fish")).thenReturn(Optional.of(fish));

            // When
            dataInitializer.run();

            // Then - Total of 9 pets: 3 dogs + 2 cats + 2 birds + 2 fish
            ArgumentCaptor<Pet> petCaptor = ArgumentCaptor.forClass(Pet.class);
            verify(petRepository, times(9)).save(petCaptor.capture());

            var savedPets = petCaptor.getAllValues();
            assertThat(savedPets).hasSize(9);
        }

        @Test
        @DisplayName("Should skip pet creation when pets already exist")
        void testSkipPetCreationWhenNotEmpty() throws Exception {
            // Given
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));
            when(categoryRepository.findByName(anyString())).thenReturn(Optional.of(new Category()));
            when(petRepository.count()).thenReturn(5L);

            // When
            dataInitializer.run();

            // Then
            verify(petRepository, never()).save(any(Pet.class));
        }

        @Test
        @DisplayName("Should create dog pets with correct details")
        void testDogPetCreation() throws Exception {
            // Given
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));
            when(categoryRepository.findByName(anyString())).thenReturn(Optional.empty());
            when(petRepository.count()).thenReturn(0L);

            Category dogs = new Category();
            dogs.setName("Dogs");
            when(categoryRepository.findByName("Dogs")).thenReturn(Optional.of(dogs));
            when(categoryRepository.findByName("Cats")).thenReturn(Optional.empty());
            when(categoryRepository.findByName("Birds")).thenReturn(Optional.empty());
            when(categoryRepository.findByName("Fish")).thenReturn(Optional.empty());

            // When
            dataInitializer.run();

            // Then
            ArgumentCaptor<Pet> petCaptor = ArgumentCaptor.forClass(Pet.class);
            verify(petRepository, times(3)).save(petCaptor.capture());

            var dogPets = petCaptor.getAllValues();
            assertThat(dogPets).hasSize(3);
            assertThat(dogPets.get(0).getName()).contains("Golden Retriever");
            assertThat(dogPets.get(0).getCategory()).isEqualTo(dogs);
            assertThat(dogPets.get(0).getPhotoUrls()).isNotEmpty();
            assertThat(dogPets.get(0).getTags()).contains("friendly", "family-dog", "large");
        }

        @Test
        @DisplayName("Should create cat pets with correct details")
        void testCatPetCreation() throws Exception {
            // Given
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));
            when(categoryRepository.findByName(anyString())).thenReturn(Optional.empty());
            when(petRepository.count()).thenReturn(0L);

            Category cats = new Category();
            cats.setName("Cats");
            when(categoryRepository.findByName("Dogs")).thenReturn(Optional.empty());
            when(categoryRepository.findByName("Cats")).thenReturn(Optional.of(cats));
            when(categoryRepository.findByName("Birds")).thenReturn(Optional.empty());
            when(categoryRepository.findByName("Fish")).thenReturn(Optional.empty());

            // When
            dataInitializer.run();

            // Then
            ArgumentCaptor<Pet> petCaptor = ArgumentCaptor.forClass(Pet.class);
            verify(petRepository, times(2)).save(petCaptor.capture());

            var catPets = petCaptor.getAllValues();
            assertThat(catPets).hasSize(2);
            assertThat(catPets.get(0).getName()).contains("Persian Cat");
            assertThat(catPets.get(1).getName()).contains("Siamese Cat");
        }

        @Test
        @DisplayName("Should set pet status to AVAILABLE")
        void testPetStatusAvailable() throws Exception {
            // Given
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));
            when(categoryRepository.findByName(anyString())).thenReturn(Optional.empty());
            when(petRepository.count()).thenReturn(0L);

            Category dogs = new Category();
            when(categoryRepository.findByName("Dogs")).thenReturn(Optional.of(dogs));
            when(categoryRepository.findByName("Cats")).thenReturn(Optional.empty());
            when(categoryRepository.findByName("Birds")).thenReturn(Optional.empty());
            when(categoryRepository.findByName("Fish")).thenReturn(Optional.empty());

            // When
            dataInitializer.run();

            // Then
            ArgumentCaptor<Pet> petCaptor = ArgumentCaptor.forClass(Pet.class);
            verify(petRepository, times(3)).save(petCaptor.capture());

            var pets = petCaptor.getAllValues();
            assertThat(pets).allMatch(pet -> pet.getStatus().toString().equals("AVAILABLE"));
        }

        @Test
        @DisplayName("Should include photo URLs for all pets")
        void testPetPhotoUrls() throws Exception {
            // Given
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));
            when(categoryRepository.findByName(anyString())).thenReturn(Optional.empty());
            when(petRepository.count()).thenReturn(0L);

            Category dogs = new Category();
            when(categoryRepository.findByName("Dogs")).thenReturn(Optional.of(dogs));
            when(categoryRepository.findByName("Cats")).thenReturn(Optional.empty());
            when(categoryRepository.findByName("Birds")).thenReturn(Optional.empty());
            when(categoryRepository.findByName("Fish")).thenReturn(Optional.empty());

            // When
            dataInitializer.run();

            // Then
            ArgumentCaptor<Pet> petCaptor = ArgumentCaptor.forClass(Pet.class);
            verify(petRepository, times(3)).save(petCaptor.capture());

            var pets = petCaptor.getAllValues();
            assertThat(pets).allMatch(pet -> 
                pet.getPhotoUrls() != null && 
                !pet.getPhotoUrls().isEmpty() &&
                pet.getPhotoUrls().size() == 3
            );
        }
    }

    @Nested
    @DisplayName("Integration and Run Method Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should execute all initialization steps")
        void testFullInitializationFlow() throws Exception {
            // Given
            when(userRepository.findByEmail("admin@pawfect.com")).thenReturn(Optional.empty());
            lenient().when(categoryRepository.findByName(anyString())).thenReturn(Optional.empty());
            lenient().when(petRepository.count()).thenReturn(0L);

            // For pet creation phase, categories are found
            Category dogs = new Category();
            Category cats = new Category();
            Category birds = new Category();
            Category fish = new Category();
            
            lenient().when(categoryRepository.findByName("Dogs")).thenReturn(Optional.of(dogs));
            lenient().when(categoryRepository.findByName("Cats")).thenReturn(Optional.of(cats));
            lenient().when(categoryRepository.findByName("Birds")).thenReturn(Optional.of(birds));
            lenient().when(categoryRepository.findByName("Fish")).thenReturn(Optional.of(fish));

            // When
            dataInitializer.run();

            // Then - verify initialization occurred
            verify(userRepository).findByEmail("admin@pawfect.com");
            verify(userRepository).save(any(User.class));
            verify(petRepository).count();
        }

        @Test
        @DisplayName("Should handle empty command line arguments")
        void testRunWithNoArguments() throws Exception {
            // Given
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));
            when(categoryRepository.findByName(anyString())).thenReturn(Optional.of(new Category()));
            when(petRepository.count()).thenReturn(1L);

            // When
            dataInitializer.run(); // No arguments

            // Then - should complete without error
            verify(userRepository).findByEmail("admin@pawfect.com");
        }

        @Test
        @DisplayName("Should handle command line arguments gracefully")
        void testRunWithArguments() throws Exception {
            // Given
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));
            when(categoryRepository.findByName(anyString())).thenReturn(Optional.of(new Category()));
            when(petRepository.count()).thenReturn(1L);

            // When
            dataInitializer.run("arg1", "arg2", "arg3");

            // Then - should complete without error and ignore arguments
            verify(userRepository).findByEmail("admin@pawfect.com");
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle missing category gracefully")
        void testMissingCategoryHandling() throws Exception {
            // Given
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));
            when(categoryRepository.findByName(anyString())).thenReturn(Optional.empty());
            when(petRepository.count()).thenReturn(0L);

            // When
            dataInitializer.run();

            // Then - should not create pets without categories
            verify(petRepository, never()).save(any(Pet.class));
        }

        @Test
        @DisplayName("Should handle partial category availability")
        void testPartialCategoryAvailability() throws Exception {
            // Given
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));
            when(categoryRepository.findByName(anyString())).thenReturn(Optional.empty());
            when(petRepository.count()).thenReturn(0L);

            Category dogs = new Category();
            when(categoryRepository.findByName("Dogs")).thenReturn(Optional.of(dogs));
            when(categoryRepository.findByName("Cats")).thenReturn(Optional.empty());
            when(categoryRepository.findByName("Birds")).thenReturn(Optional.empty());
            when(categoryRepository.findByName("Fish")).thenReturn(Optional.empty());

            // When
            dataInitializer.run();

            // Then - should only create pets for available categories
            ArgumentCaptor<Pet> petCaptor = ArgumentCaptor.forClass(Pet.class);
            verify(petRepository, times(3)).save(petCaptor.capture());

            var pets = petCaptor.getAllValues();
            assertThat(pets).allMatch(pet -> pet.getCategory().equals(dogs));
        }
    }
}
