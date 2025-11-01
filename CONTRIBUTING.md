# Contributing to Pawfect Store

First off, thank you for considering contributing to Pawfect Store! It's people like you that make Pawfect Store such a great tool.

## 📋 Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [How to Contribute](#how-to-contribute)
- [Development Workflow](#development-workflow)
- [Coding Standards](#coding-standards)
- [Commit Guidelines](#commit-guidelines)
- [Pull Request Process](#pull-request-process)
- [Reporting Bugs](#reporting-bugs)
- [Suggesting Enhancements](#suggesting-enhancements)

---

## 📜 Code of Conduct

### Our Pledge

We are committed to making participation in this project a harassment-free experience for everyone, regardless of age, body size, disability, ethnicity, gender identity and expression, level of experience, nationality, personal appearance, race, religion, or sexual identity and orientation.

### Our Standards

**Positive behavior includes:**
- Using welcoming and inclusive language
- Being respectful of differing viewpoints
- Gracefully accepting constructive criticism
- Focusing on what is best for the community
- Showing empathy towards other community members

**Unacceptable behavior includes:**
- Trolling, insulting/derogatory comments, and personal or political attacks
- Public or private harassment
- Publishing others' private information without permission
- Other conduct which could reasonably be considered inappropriate

---

## 🚀 Getting Started

### Prerequisites

Before you begin, ensure you have:
- Java 17+ installed
- Node.js 18+ and npm installed
- Maven 3.6+ installed
- MySQL 8.0+ (or Docker)
- Git installed and configured

### Setting Up Your Development Environment

1. **Fork the repository** on GitHub
2. **Clone your fork locally**:
   ```bash
   git clone https://github.com/YOUR_USERNAME/pet-store.git
   cd pet-store
   ```
3. **Add upstream remote**:
   ```bash
   git remote add upstream https://github.com/azlicn/pet-store.git
   ```
4. **Install dependencies**:
   ```bash
   # Backend
   cd pet-store-api
   mvn clean install

   # Frontend
   cd ../pet-store-frontend
   npm install
   ```
5. **Set up the database**:
   ```bash
   # Using Docker (Recommended)
   cd ../docker
   docker-compose up -d mysql
   ```

---

## 🤝 How to Contribute

### Types of Contributions

We welcome many types of contributions:

- 🐛 **Bug fixes**
- ✨ **New features**
- 📝 **Documentation improvements**
- 🎨 **UI/UX enhancements**
- ✅ **Test coverage**
- 🔧 **Code refactoring**
- 🌐 **Translations**

### Before You Start

1. **Check existing issues** to see if your contribution is already being discussed
2. **Create an issue** if one doesn't exist for your contribution
3. **Wait for approval** on feature requests before starting work
4. **Comment on the issue** to let others know you're working on it

---

## 💻 Development Workflow

### Branch Strategy

We follow a simplified Git Flow:

- `main` - Production-ready code
- `develop` - Integration branch for features
- `feature/*` - Feature branches
- `bugfix/*` - Bug fix branches
- `hotfix/*` - Urgent fixes for production

### Creating a Feature Branch

```bash
# Ensure you're on develop and up to date
git checkout develop
git pull upstream develop

# Create your feature branch
git checkout -b feature/your-feature-name
```

### Making Changes

1. **Write your code** following our coding standards
2. **Write/update tests** for your changes
3. **Run tests** to ensure everything works:
   ```bash
   # Backend tests
   cd pet-store-api
   mvn test

   # Frontend tests
   cd pet-store-frontend
   npm test
   ```
4. **Update documentation** if needed
5. **Commit your changes** following commit guidelines

---

## 📐 Coding Standards

### Backend (Java/Spring Boot)

#### Code Style
- Follow **Google Java Style Guide**
- Use **4 spaces** for indentation
- Maximum line length: **120 characters**
- Use **meaningful variable and method names**

#### Best Practices
- Follow **SOLID principles**
- Use **dependency injection** (constructor injection preferred)
- Write **self-documenting code** with clear names
- Add **JavaDoc** for public methods and classes
- Use **@Override** annotation when applicable
- Handle exceptions properly, don't catch generic Exception

#### Example:
```java
@Service
public class PetService {
    private final PetRepository petRepository;
    private final CategoryRepository categoryRepository;

    /**
     * Constructs a PetService with required dependencies.
     *
     * @param petRepository the repository for pet operations
     * @param categoryRepository the repository for category operations
     */
    public PetService(PetRepository petRepository, 
                      CategoryRepository categoryRepository) {
        this.petRepository = petRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Creates a new pet in the system.
     *
     * @param petRequest the pet creation request
     * @return the created pet
     * @throws CategoryNotFoundException if the category doesn't exist
     */
    public Pet createPet(PetRequest petRequest) {
        Category category = categoryRepository.findById(petRequest.getCategoryId())
            .orElseThrow(() -> new CategoryNotFoundException(
                "Category not found with id: " + petRequest.getCategoryId()));
        
        Pet pet = new Pet();
        pet.setName(petRequest.getName());
        pet.setCategory(category);
        
        return petRepository.save(pet);
    }
}
```

### Frontend (TypeScript/Angular)

#### Code Style
- Follow **Angular Style Guide**
- Use **2 spaces** for indentation
- Maximum line length: **120 characters**
- Use **camelCase** for variables and methods
- Use **PascalCase** for classes and interfaces

#### Best Practices
- Use **standalone components** (Angular 17+)
- Implement **OnDestroy** and unsubscribe from observables
- Use **async pipe** where possible
- Use **TypeScript strict mode**
- Use **interfaces** for type safety
- Follow **single responsibility principle**

#### Example:
```typescript
@Component({
  selector: 'app-pet-list',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule],
  templateUrl: './pet-list.component.html',
  styleUrls: ['./pet-list.component.scss']
})
export class PetListComponent implements OnInit, OnDestroy {
  pets$: Observable<Pet[]>;
  private destroy$ = new Subject<void>();

  constructor(private petService: PetService) {}

  ngOnInit(): void {
    this.loadPets();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadPets(): void {
    this.pets$ = this.petService.getAllPets().pipe(
      takeUntil(this.destroy$),
      catchError(error => {
        console.error('Error loading pets:', error);
        return of([]);
      })
    );
  }
}
```

### Database

- Use **meaningful table and column names**
- Follow **snake_case** naming convention
- Add **indexes** for frequently queried columns
- Use **foreign key constraints**
- Add **database comments** for clarity

---

## 📝 Commit Guidelines

### Commit Message Format

We follow the **Conventional Commits** specification:

```
<type>(<scope>): <subject>

<body>

<footer>
```

#### Type
- `feat`: A new feature
- `fix`: A bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, missing semi-colons, etc.)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks (dependencies, build, etc.)
- `perf`: Performance improvements

#### Scope
- `backend`: Spring Boot backend
- `frontend`: Angular frontend
- `database`: Database changes
- `docker`: Docker configuration
- `ci`: CI/CD changes

#### Examples

```bash
feat(backend): add discount code validation

feat(frontend): implement address management UI

fix(backend): resolve pet ownership verification bug

docs(readme): update setup instructions

refactor(frontend): extract reusable cart components

test(backend): add unit tests for OrderService
```

---

## 🔄 Pull Request Process

### Before Submitting

1. ✅ Ensure your code follows coding standards
2. ✅ All tests pass
3. ✅ Update documentation if needed
4. ✅ Add/update tests for your changes
5. ✅ Rebase on latest develop branch
6. ✅ Resolve any merge conflicts

### Submitting Your PR

1. **Push your branch** to your fork:
   ```bash
   git push origin feature/your-feature-name
   ```

2. **Create a Pull Request** from your branch to `develop`

3. **Fill out the PR template** completely:
   - Description of changes
   - Related issue number
   - Type of change (bug fix, feature, etc.)
   - Testing performed
   - Screenshots (if UI changes)

4. **Wait for review** - maintainers will review your PR

### PR Title Format

```
type(scope): Brief description (#issue-number)
```

Examples:
```
feat(backend): Add payment processing strategy pattern (#123)
fix(frontend): Resolve cart total calculation bug (#456)
docs: Update API documentation with new endpoints (#789)
```

### Review Process

- PRs require **at least one approval** from maintainers
- Address all review comments
- Keep your PR focused on a single feature/fix
- Be responsive to feedback
- Automated tests must pass

---

## 🐛 Reporting Bugs

### Before Submitting a Bug Report

- Check the [documentation](docs/)
- Search [existing issues](https://github.com/azlicn/pet-store/issues)
- Try to reproduce on the latest version

### How to Submit a Bug Report

1. Use the **bug report template**
2. Provide a **clear title**
3. Describe the **expected vs actual behavior**
4. Include **steps to reproduce**
5. Add **screenshots** if applicable
6. Include **system information**:
   - OS
   - Browser (for frontend issues)
   - Java version (for backend issues)
   - Node.js version (for frontend issues)

---

## 💡 Suggesting Enhancements

### Before Submitting

- Check if the enhancement is already requested
- Ensure it aligns with project goals
- Consider if it's a breaking change

### How to Submit an Enhancement

1. Use the **feature request template**
2. Provide a **clear title**
3. Describe the **problem** you're trying to solve
4. Propose your **solution**
5. Describe **alternatives** you've considered
6. Add **mockups** if it's a UI enhancement

---

## 🧪 Testing Guidelines

### Backend Testing

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=PetServiceTest

# Run with coverage
mvn test jacoco:report
```

### Frontend Testing

```bash
# Run all tests
npm test

# Run tests in watch mode
npm run test:watch

# Run with coverage
npm run test:coverage
```

### Test Coverage Goals

- **Backend**: Minimum 80% coverage
- **Frontend**: Minimum 70% coverage
- All new features must include tests

---

## 📞 Getting Help

If you need help with your contribution:

1. Check the [documentation](docs/)
2. Search [existing issues](https://github.com/azlicn/pet-store/issues)
3. Ask in discussions
4. Create an issue with the `question` label

---

## 🎉 Recognition

Contributors will be recognized in:
- README.md Contributors section
- Release notes for their contributions
- Project documentation where applicable

---

## 📄 License

By contributing, you agree that your contributions will be licensed under the MIT License.

---

Thank you for contributing to Pawfect Store! 🐾
