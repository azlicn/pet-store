package com.petstore.config;

import com.petstore.enums.PetStatus;
import com.petstore.model.Category;
import com.petstore.model.Pet;
import com.petstore.model.Role;
import com.petstore.model.User;
import com.petstore.repository.CategoryRepository;
import com.petstore.repository.PetRepository;
import com.petstore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 *
 * Initializes essential application data when the Spring Boot application
 * starts.
 */
@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Executes after application startup to initialize core data in the database.
     *
     * @param args the command-line arguments (not used)
     * @throws Exception if a database or persistence error occurs during
     *                   initialization
     */
    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting data initialization...");

        initializeDefaultAdmin();
        initializeDefaultCategories();
        initializeSamplePets();

        logger.info("Data initialization completed.");
    }

    /**
     * Creates a default administrator account if one does not already exist.
     *
     */
    private void initializeDefaultAdmin() {
        // Check if admin user already exists
        if (userRepository.findByEmail("admin@pawfect.com").isEmpty()) {
            User admin = new User(
                    "admin@pawfect.com",
                    passwordEncoder.encode("admin123"),
                    "Admin",
                    "User");
            admin.setRoles(Set.of(Role.ADMIN));

            userRepository.save(admin);
            logger.info("Default admin user created: admin@petstore.com / admin123");
        } else {
            logger.info("Admin user already exists, skipping creation.");
        }
    }

    /**
     * Initializes default pet categories (e.g., Dogs, Cats, Birds, Fish).
     * 
     * <p>
     * If a category already exists in the database, it is skipped.
     * Otherwise, it is created and persisted using {@link CategoryRepository}.
     * </p>
     */
    private void initializeDefaultCategories() {
        // Create default categories if they don't exist
        String[] defaultCategories = { "Dogs", "Cats", "Birds", "Fish", "Reptiles", "Small Pets" };

        for (String categoryName : defaultCategories) {
            if (categoryRepository.findByName(categoryName).isEmpty()) {
                Category category = new Category();
                category.setName(categoryName);
                categoryRepository.save(category);
                logger.info("Created default category: {}", categoryName);
            }
        }

        long categoryCount = categoryRepository.count();
        logger.info("Total categories in database: {}", categoryCount);
    }

    /**
     * Seeds the database with example pets for each category.
     *
     * <p>
     * This method only executes if the pets table is empty to prevent duplicate
     * data.
     * It creates pets with image URLs, descriptive tags, and assigns them to
     * categories.
     * </p>
     */
    private void initializeSamplePets() {
        // Only create sample pets if database is empty
        if (petRepository.count() == 0) {
            logger.info("Creating sample pets...");

            Category dogsCategory = categoryRepository.findByName("Dogs").orElse(null);
            Category catsCategory = categoryRepository.findByName("Cats").orElse(null);
            Category birdsCategory = categoryRepository.findByName("Birds").orElse(null);
            Category fishCategory = categoryRepository.findByName("Fish").orElse(null);

            if (dogsCategory != null) {
                createSamplePet("Golden Retriever – Sunny Buddy", dogsCategory, new BigDecimal("1200.00"),
                        List.of("https://i.pinimg.com/736x/7b/70/f2/7b70f2601568f792edc52d23879df914.jpg",
                                "http://i.pinimg.com/1200x/09/df/85/09df853d6a0405fdac22afb9074f3e5a.jpg",
                                "https://i.pinimg.com/1200x/ce/a9/ca/cea9caa18f6f2ef1cc02499e48447dce.jpg"),
                        List.of("friendly", "family-dog", "large"));

                createSamplePet("German Shepherd – Brave Rex", dogsCategory, new BigDecimal("1500.00"),
                        List.of("https://i.pinimg.com/736x/3d/a4/a2/3da4a2013284394787ca5b86be91c271.jpg",
                                "https://i.pinimg.com/1200x/0d/c2/0f/0dc20ff69dca3d2061bb3c813ed27408.jpg",
                                "https://i.pinimg.com/1200x/8a/7a/4a/8a7a4a27c526f0e4d2b174d6200c58f3.jpg"),
                        List.of("loyal", "guard-dog", "intelligent"));

                createSamplePet("Labrador– Happy Bella", dogsCategory, new BigDecimal("1000.00"),
                        List.of("https://i.pinimg.com/736x/d5/67/14/d56714ad747dc20457780224c33eb447.jpg",
                                "https://i.pinimg.com/736x/89/ad/d2/89add2e1495162f7eb00be5df26d5d6a.jpg",
                                "https://i.pinimg.com/736x/10/3f/8d/103f8d0f7c4ee921d03a71976248458f.jpg"),
                        List.of("gentle", "family-friendly", "active"));
            }

            if (catsCategory != null) {
                createSamplePet("Persian Cat– Royal Luna", catsCategory, new BigDecimal("800.00"),
                        List.of("https://i.pinimg.com/736x/0f/ed/cb/0fedcbba99aefd27d19ac39be7346a16.jpg",
                                "https://i.pinimg.com/736x/d5/f6/0c/d5f60c2a93598f41db2bb27bfd7ef377.jpg",
                                "https://i.pinimg.com/736x/49/76/d6/4976d6b065a5d4be591e862f113243ad.jpg"),
                        List.of("long-hair", "calm", "indoor"));

                createSamplePet("Siamese Cat– Mister Milo", catsCategory, new BigDecimal("600.00"),
                        List.of("https://i.pinimg.com/736x/8a/0f/da/8a0fdad0d74dc304bf14b829c5c6540e.jpg",
                                "https://i.pinimg.com/736x/65/b2/90/65b2904f259c6e519c3a0c20f5475df0.jpg",
                                "https://i.pinimg.com/736x/8d/03/9f/8d039fa5759464da50676c9887b8e18d.jpg"),
                        List.of("vocal", "social", "elegant"));
            }

            if (birdsCategory != null) {
                createSamplePet("Canary– Golden Song", birdsCategory, new BigDecimal("150.00"),
                        List.of("https://i.pinimg.com/1200x/29/72/fd/2972fd46306ca85d3a1c3e49846de4ed.jpg",
                                "https://i.pinimg.com/736x/fb/55/a0/fb55a01619fdcc7b1158ed4120e873e5.jpg",
                                "https://i.pinimg.com/736x/44/cb/14/44cb144bd2f8cdb22c1f99b6d094903b.jpg"),
                        List.of("singing", "colorful", "small"));

                createSamplePet("Parrot– Talking Rio", birdsCategory, new BigDecimal("500.00"),
                        List.of("https://i.pinimg.com/736x/57/ec/7b/57ec7be8425fd50056a5be42bf046d29.jpg",
                                "https://i.pinimg.com/736x/1e/0c/9c/1e0c9ca2b71e40e5ecc6b1beb6ba5ad9.jpg",
                                "https://i.pinimg.com/736x/be/f5/a1/bef5a1731f8ec81cf87486da9bb6af67.jpg"),
                        List.of("talking", "intelligent", "colorful"));
            }

            if (fishCategory != null) {
                createSamplePet("Goldfish – Golden Bubbles", fishCategory, new BigDecimal("25.00"),
                        List.of("https://i.pinimg.com/1200x/c4/60/8a/c4608a0457b645f65e18af0f579ff816.jpg",
                                "https://i.pinimg.com/1200x/f0/f4/f8/f0f4f8f22d58052cd22d0b20d6a530be.jpg",
                                "https://i.pinimg.com/736x/8e/fa/24/8efa246ecc1b056945a0a79388cd1d84.jpg"),
                        List.of("easy-care", "peaceful", "golden"));

                createSamplePet("Betta Fish– Blue Sapphire", fishCategory, new BigDecimal("15.00"),
                        List.of("https://i.pinimg.com/736x/4b/75/a2/4b75a2127ef703e0f140d0df050d9e88.jpg",
                                "https://i.pinimg.com/736x/a9/1a/35/a91a35cb1faf5c347afd30d885d211f1.jpg",
                                "https://i.pinimg.com/736x/86/bb/2f/86bb2ff2bedf1cfcf4a4a815e843a2f9.jpg"),
                        List.of("colorful", "low-maintenance", "beautiful"));
            }

            logger.info("Sample pets created successfully.");
        } else {
            logger.info("Pets already exist in database, skipping sample pet creation.");
        }
    }

    /**
     * Helper method to create and persist a sample pet entity.
     *
     * @param name      the pet’s display name
     * @param category  the {@link Category} the pet belongs to
     * @param price     the pet’s price
     * @param photoUrls a list of image URLs representing the pet
     * @param tags      descriptive tags (e.g., “friendly”, “small”, “family-dog”)
     */
    private void createSamplePet(String name, Category category, BigDecimal price,
            List<String> photoUrls, List<String> tags) {
        Pet pet = new Pet(name, category, price);
        pet.setDescription("""
                A friendly and playful companion that adapts well to any home. 
                Easy to care for and full of personality, bringing joy and comfort to families and pet lovers alike.
                """);
        pet.setStatus(PetStatus.AVAILABLE);
        pet.setPhotoUrls(photoUrls);
        pet.setTags(tags);
        petRepository.save(pet);
        logger.info("Created sample pet: {}", name);
    }
}