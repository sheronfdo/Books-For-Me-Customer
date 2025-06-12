# Books-For-Me-Customer
[![Ask DeepWiki](https://devin.ai/assets/askdeepwiki.png)](https://deepwiki.com/sheronfdo/Books-For-Me-Customer)

Books-For-Me-Customer is an Android application designed for users to discover, browse, search, and purchase books. It offers a comprehensive e-commerce experience tailored for book lovers, integrating features from user authentication to order fulfillment and profile management.

## Features

*   **User Authentication:** Secure sign-in/sign-up using Google Sign-In, powered by Firebase Authentication.
*   **Book Discovery:**
    *   Browse books across various categories.
    *   Explore featured books, new arrivals, and trending titles.
    *   Dynamic home screen with carousels showcasing different book sections.
*   **Search and Filtering:**
    *   Robust search functionality for books and authors.
    *   Live search suggestions as you type.
    *   Filter search results by category.
*   **Book Details:**
    *   View comprehensive details for each book, including cover image, title, author, price, condition, description, publication year, language, and tags.
    *   Access information about the seller, including their name and profile image.
    *   Direct contact option to call the seller.
*   **Shopping Cart:**
    *   Add books to a personal shopping cart.
    *   View and manage items within the cart before checkout.
*   **Checkout Process:**
    *   Secure checkout facilitated by PayPal integration.
    *   Enter and confirm shipping details (name, address, phone, email).
    *   Utilize an interactive map (Google Maps) for accurate delivery address selection.
*   **Order Management:**
    *   View a complete history of past orders.
    *   Access detailed information for each order, including items, total price, payment status, and receiver details.
    *   Track order status through a visual timeline (e.g., Order Confirmed, Processing, Shipped, Delivered).
*   **Profile Management:**
    *   View and update personal profile information, including first name, last name, display name, and phone number.
    *   Change profile picture with options to capture a new image using the camera or select one from the gallery.
    *   Profile image caching for optimized loading times.
*   **Theme Customization:**
    *   Personalize the app's appearance by choosing between Light Mode, Dark Mode, or System Default theme.
*   **Notifications:**
    *   Receive push notifications via Firebase Cloud Messaging for order updates and other relevant events.

## Tech Stack & Dependencies

*   **Platform:** Android (Java)
*   **Backend & Services:**
    *   Firebase:
        *   Authentication (Google Sign-In)
        *   Firestore (Real-time NoSQL Database)
        *   Storage (Cloud storage for image uploads)
        *   Cloud Messaging (Push notifications)
        *   Crashlytics (Crash reporting)
*   **Payment:** PayPal Android SDK
*   **Location:** Google Maps API, Google Play Services Location
*   **Networking:**
    *   OkHttp (HTTP client for API communication)
    *   Gson (JSON serialization/deserialization)
*   **UI & UX:**
    *   Material Components for Android
    *   Glide (Image loading and caching)
    *   Lottie (Animations for UI elements like order completion)
    *   CountryCodePicker (For international phone number input)
    *   ViewPager2 (For swipeable views like carousels)
    *   RecyclerView, CardView
*   **Utilities:**
    *   ModelMapper (Object mapping between DTOs and models)

## Setup and Installation

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/sheronfdo/Books-For-Me-Customer.git
    ```
2.  **Open in Android Studio:**
    Open the cloned project directory using Android Studio.
3.  **Firebase Configuration:**
    *   Obtain your Firebase project's `google-services.json` file from the Firebase console.
    *   Place this file in the `app/` directory of the project.
4.  **API Keys Configuration:**
    *   **PayPal Client ID:**
        Navigate to `app/src/main/res/values/strings.xml` and update the `paypal_client_id` string with your PayPal client ID:
        ```xml
        <string name="paypal_client_id">YOUR_PAYPAL_CLIENT_ID</string>
        ```
    *   **Google Maps API Key:**
        Open `app/src/main/AndroidManifest.xml`. Locate and update the `com.google.android.geo.API_KEY` meta-data tag with your Google Maps API key:
        ```xml
        <meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="YOUR_GOOGLE_MAPS_API_KEY" />
        ```
        If you are using Google Maps Cloud-based map styling, also configure your `MAP_ID`:
        ```xml
        <meta-data
            android:name="com.google.android.geo.MAP_ID"
            android:value="YOUR_MAP_ID_IF_USING_CLOUD_STYLING" />
        ```
5.  **Backend URL Configuration:**
    The application communicates with a custom backend. The base URL for this backend is defined in `app/src/main/java/com/jamith/booksformecustomer/util/UrlConstants.java`. Update the `BASE_URL` constant to point to your running backend instance if it differs from the placeholder:
    ```java
    public static final String BASE_URL = "YOUR_BACKEND_API_BASE_URL"; // e.g., http://192.168.1.100:8080/api
    ```
6.  **Build and Run:**
    Sync the project with Gradle files in Android Studio. Then, build and run the application on an Android device or emulator.
