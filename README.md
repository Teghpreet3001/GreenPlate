# GreenPlate
<!-- ![cover](https://github.com/rchtgpt/NL2AR/assets/44428198/3ff39bdb-71ae-42ec-9b00-528dbf04c2d4) -->

GreenPlate is a sustainable food management app built using Android Studio and Java, backed by Firebase. The app’s design focuses on reducing food waste and promoting efficient use of resources. The Food Tracker app allows users to track daily calorie intake, create recipes, manage ingredient quantities, and generate shopping lists. Our team employed the MVVM (Model-View-ViewModel) design architecture, following SOLID/GRASP Principles across 4 successful sprints, and implemented 4 different software design patterns including Singleton, Strategy, and Observer, merging 70+ Pull requests and contributing 30+ Unit Tests.

![linkedin post image](https://github.com/user-attachments/assets/df47feb3-9381-45d9-b17f-27a294665cad)

## App Features

- **Comprehensive User Authentication and Management**: Seamless user authentication using Firebase, allowing for easy login for existing users and straightforward account creation for new users. 

- **Detailed Meal Logging and Calorie Tracking**: Users can log their meals, including the meal name and estimated calorie count, with the data securely stored in Firebase. Personalized daily calorie goals are calculated based on user input, and current daily calorie intake is prominently displayed.

 - **Advanced Data Visualization**: Integration with a data visualization library (AndroidChart) enables users to view and understand their calorie intake data through intuitive charts and graphs.

- **Robust Recipe and Pantry Management**: A dynamic pantry database tracks ingredients, while a comprehensive cookbook database stores user recipes. The app indicates ingredient availability for each recipe, automates ingredient deductions when recipes are selected, and synchronizes missing ingredients with the shopping list.

- **Automated and User-Friendly Shopping List**: The app automates the creation of shopping lists based on pantry needs, allowing users to add, update, or remove items as necessary. It provides suggestions for items based on the pantry inventory, ensuring that users are always prepared with the ingredients they need.

## Domain Model Diagram
<img width="554" alt="image" src="https://github.com/rchtgpt/NL2AR/assets/44428198/4d077ac9-6107-482b-8b6d-73109733b7ca">

## Use Case Diagram
<img width="723" alt="image" src="https://github.com/rchtgpt/NL2AR/assets/44428198/d4601609-321d-43c1-b137-97917bdbb923">

## References
- https://firebase.google.com/docs/database/android/read-and-write
- https://firebase.google.com/docs/auth/android/password-auth#java
- https://github.com/firebase/snippets-android/blob/2324a6cc01262e4df8a6b4c3623ad895119f0724/auth/app/src/main/java/com/google/firebase/quickstart/auth/EmailPasswordActivity.java#L49-L57
