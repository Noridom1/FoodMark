# Coding Conventions for FoodMark Project

## General Guidelines
- Use English for all code, comments, and documentation
- Keep code DRY (Don't Repeat Yourself)
- Write self-documenting code with clear variable and function names
- Maximum line length: 80 characters

## Flutter/Dart Conventions

### Naming
- **Files**: lowercase_with_underscores.dart
- **Classes**: UpperCamelCase
- **Variables/Functions**: lowerCamelCase
- **Constants**: UPPERCASE_WITH_UNDERSCORES

### Structure
- Group widgets into separate files
- Keep widget files under /lib/widgets/
- Place screens under /lib/screens/
- Store models under /lib/models/

### Example
```dart
// Good
class UserProfile extends StatelessWidget {
  final String userName;
  
  const UserProfile({required this.userName});
}

// Bad
class userprofile extends StatelessWidget {
  String user_name;
}
```

## Python/FastAPI Conventions

### Naming
- **Files**: lowercase_with_underscores.py
- **Classes**: UpperCamelCase
- **Functions/Variables**: lowercase_with_underscores
- **Constants**: UPPERCASE_WITH_UNDERSCORES

### Structure
- Place routes in /routes/ directory
- Keep models in /models/ directory
- Store utilities in /utils/ directory

### Example
```python
# Good
def get_user_profile(user_id: int):
    return {"user_id": user_id}

# Bad
def GetUserProfile(userId):
    return {"userId": userId}
```

## Git Conventions

### Branches
- main: production code
- develop: development code
- feature/feature-name: new features

### Commits
- Use clear, concise messages
- Start with verb (Add, Fix, Update, etc.)
- Example: "Add user authentication system"

## Documentation
- Add comments for complex logic only
- Include docstrings for Python functions
- Document API endpoints using FastAPI's built-in docs

## Testing
- Write tests for critical functionality
- Name test files as test_*.py for Python
- Name test files as *_test.dart for Flutter