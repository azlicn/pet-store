package com.petstore.config;

import com.petstore.model.Category;
import com.petstore.model.Pet;
import com.petstore.model.PetStatus;
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

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting data initialization...");
        
        initializeDefaultAdmin();
        initializeDefaultCategories();
        initializeSamplePets();
        
        logger.info("Data initialization completed.");
    }

    private void initializeDefaultAdmin() {
        // Check if admin user already exists
        if (userRepository.findByEmail("admin@pawfect.com").isEmpty()) {
            User admin = new User(
                "admin@pawfect.com",
                passwordEncoder.encode("admin123"),
                "Admin",
                "User"
            );
            admin.setRoles(Set.of(Role.ADMIN));
            
            userRepository.save(admin);
            logger.info("Default admin user created: admin@petstore.com / admin123");
        } else {
            logger.info("Admin user already exists, skipping creation.");
        }
    }

    private void initializeDefaultCategories() {
        // Create default categories if they don't exist
        String[] defaultCategories = {"Dogs", "Cats", "Birds", "Fish", "Reptiles", "Small Pets"};
        
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
                    List.of("https://i.pinimg.com/736x/7b/70/f2/7b70f2601568f792edc52d23879df914.jpg", "http://i.pinimg.com/1200x/09/df/85/09df853d6a0405fdac22afb9074f3e5a.jpg","https://i.pinimg.com/1200x/ce/a9/ca/cea9caa18f6f2ef1cc02499e48447dce.jpg"), 
                    List.of("friendly", "family-dog", "large"));
                    
                createSamplePet("German Shepherd – Brave Rex", dogsCategory, new BigDecimal("1500.00"), 
                    List.of("https://i.pinimg.com/736x/3d/a4/a2/3da4a2013284394787ca5b86be91c271.jpg","https://i.pinimg.com/1200x/0d/c2/0f/0dc20ff69dca3d2061bb3c813ed27408.jpg","https://i.pinimg.com/1200x/8a/7a/4a/8a7a4a27c526f0e4d2b174d6200c58f3.jpg"), 
                    List.of("loyal", "guard-dog", "intelligent"));
                    
                createSamplePet("Labrador– Happy Bella", dogsCategory, new BigDecimal("1000.00"), 
                    List.of("https://i.pinimg.com/736x/d5/67/14/d56714ad747dc20457780224c33eb447.jpg","https://i.pinimg.com/736x/89/ad/d2/89add2e1495162f7eb00be5df26d5d6a.jpg", "https://i.pinimg.com/736x/10/3f/8d/103f8d0f7c4ee921d03a71976248458f.jpg"), 
                    List.of("gentle", "family-friendly", "active"));
            }
            
            if (catsCategory != null) {
                createSamplePet("Persian Cat– Royal Luna", catsCategory, new BigDecimal("800.00"), 
                    List.of("https://i.pinimg.com/736x/0f/ed/cb/0fedcbba99aefd27d19ac39be7346a16.jpg", "https://i.pinimg.com/736x/d5/f6/0c/d5f60c2a93598f41db2bb27bfd7ef377.jpg", "https://i.pinimg.com/736x/49/76/d6/4976d6b065a5d4be591e862f113243ad.jpg"), 
                    List.of("long-hair", "calm", "indoor"));
                    
                createSamplePet("Siamese Cat– Mister Milo", catsCategory, new BigDecimal("600.00"), 
                    List.of("https://i.pinimg.com/736x/8a/0f/da/8a0fdad0d74dc304bf14b829c5c6540e.jpg", "https://i.pinimg.com/736x/65/b2/90/65b2904f259c6e519c3a0c20f5475df0.jpg", "https://i.pinimg.com/736x/8d/03/9f/8d039fa5759464da50676c9887b8e18d.jpg"), 
                    List.of("vocal", "social", "elegant"));
            }
            
            if (birdsCategory != null) {
                createSamplePet("Canary– Golden Song", birdsCategory, new BigDecimal("150.00"), 
                    List.of("https://i.pinimg.com/1200x/29/72/fd/2972fd46306ca85d3a1c3e49846de4ed.jpg", "https://i.pinimg.com/736x/fb/55/a0/fb55a01619fdcc7b1158ed4120e873e5.jpg", "https://i.pinimg.com/736x/44/cb/14/44cb144bd2f8cdb22c1f99b6d094903b.jpg"), 
                    List.of("singing", "colorful", "small"));
                    
                createSamplePet("Parrot– Talking Rio", birdsCategory, new BigDecimal("500.00"), 
                    List.of("https://i.pinimg.com/736x/57/ec/7b/57ec7be8425fd50056a5be42bf046d29.jpg", "https://i.pinimg.com/736x/1e/0c/9c/1e0c9ca2b71e40e5ecc6b1beb6ba5ad9.jpg", "https://i.pinimg.com/736x/be/f5/a1/bef5a1731f8ec81cf87486da9bb6af67.jpg"), 
                    List.of("talking", "intelligent", "colorful"));
            }
            
            if (fishCategory != null) {
                createSamplePet("Goldfish – Golden Bubbles", fishCategory, new BigDecimal("25.00"), 
                    List.of("https://i.pinimg.com/1200x/c4/60/8a/c4608a0457b645f65e18af0f579ff816.jpg", "https://i.pinimg.com/1200x/f0/f4/f8/f0f4f8f22d58052cd22d0b20d6a530be.jpg", "https://i.pinimg.com/736x/8e/fa/24/8efa246ecc1b056945a0a79388cd1d84.jpg"), 
                    List.of("easy-care", "peaceful", "golden"));
                    
                createSamplePet("Betta Fish– Blue Sapphire", fishCategory, new BigDecimal("15.00"), 
                    List.of("https://i.pinimg.com/736x/63/9d/f8/639df8995693bf587446149541fe0ab6.jpg", "https://i.pinimg.com/736x/7b/ed/ff/7bedff1f3f3f4e2f4e2f4e2f4e2f4e2f.jpg", "https://i.pinimg.com/736x/0c/1a/2b/0c1a2b6f4e5f6a7b8c9d0e1f2a3b4c5d.jpg"), 
                    List.of("colorful", "low-maintenance", "beautiful"));
            }
            
            logger.info("Sample pets created successfully.");
        } else {
            logger.info("Pets already exist in database, skipping sample pet creation.");
        }
    }
    
    private void createSamplePet(String name, Category category, BigDecimal price, 
                                List<String> photoUrls, List<String> tags) {
        Pet pet = new Pet(name, category, price);
        pet.setStatus(PetStatus.AVAILABLE);
        pet.setPhotoUrls(photoUrls);
        pet.setTags(tags);
        petRepository.save(pet);
        logger.info("Created sample pet: {}", name);
    }
}