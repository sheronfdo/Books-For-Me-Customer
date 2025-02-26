package com.jamith.booksformecustomer;

import static android.app.Activity.RESULT_CANCELED;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Intent;
import android.net.Uri;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.jamith.booksformecustomer.activity.HomeActivity;
import com.jamith.booksformecustomer.activity.MainActivity;
import com.jamith.booksformecustomer.activity.ProfileDetailConfirmActivity;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest  {
    int RC_SIGN_IN = 100;
    @Rule
    public ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<>(MainActivity.class, true, false);

    @Mock
    private FirebaseAuth mockFirebaseAuth;

    @Mock
    private FirebaseFirestore mockFirestore;

    @Mock
    private GoogleSignInClient mockGoogleSignInClient;

    @Mock
    private FirebaseUser mockFirebaseUser;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    private void setupActivityWithMocks() {
        activityRule.launchActivity(new Intent());
        MainActivity activity = activityRule.getActivity();
        activity.firebaseAuth = mockFirebaseAuth;
        activity.firebaseFirestore = mockFirestore;
        activity.googleSignInClient = mockGoogleSignInClient;
    }

    @Test
    public void testGoogleSignInButtonVisibleWhenUserNotLoggedIn() {
        when(mockFirebaseAuth.getCurrentUser()).thenReturn(null);
        setupActivityWithMocks();
        onView(withId(R.id.googleSignInButton))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testRedirectToHomeActivityIfUserExists() throws Exception {
        String testUid = "test_uid";
        when(mockFirebaseAuth.getCurrentUser()).thenReturn(mockFirebaseUser);
        when(mockFirebaseUser.getUid()).thenReturn(testUid);

        DocumentSnapshot mockDoc = mock(DocumentSnapshot.class);
        when(mockDoc.exists()).thenReturn(true);
        when(mockFirestore.collection("customers").document(testUid).get())
                .thenReturn(Tasks.forResult(mockDoc));
        setupActivityWithMocks();
        intended(hasComponent(HomeActivity.class.getName()));
    }

    @Test
    public void testGoogleSignInIntentLaunched() {
        setupActivityWithMocks();
        onView(withId(R.id.googleSignInButton)).perform(click());
        intended(hasComponent(GoogleSignInClient.class.getName()));
    }

    @Test
    public void testNewUserRedirectedToProfileActivity() throws Exception {
        String testUid = "new_user_uid";
        DocumentSnapshot mockDoc = mock(DocumentSnapshot.class);
        when(mockDoc.exists()).thenReturn(false);
        when(mockFirestore.collection("customers").document(anyString()).get())
                .thenReturn(Tasks.forResult(mockDoc));

        when(mockFirebaseUser.getUid()).thenReturn(testUid);
        when(mockFirebaseUser.getDisplayName()).thenReturn("Test User");
        when(mockFirebaseUser.getEmail()).thenReturn("test@gmail.com");
        when(mockFirebaseUser.getPhotoUrl()).thenReturn(Uri.parse("http://test.com/photo.jpg"));

        setupActivityWithMocks();

        activityRule.getActivity().firebaseAuthenticate("fake_token");

        intended(hasComponent(ProfileDetailConfirmActivity.class.getName()));
        intended(hasExtra("uid", testUid));
        intended(hasExtra("email", "test@example.com"));
    }

    @Test
    public void testHandleGoogleSignInFailure() {
        setupActivityWithMocks();

        Task<GoogleSignInAccount> failedTask = Tasks.forException(new ApiException(null));
        activityRule.getActivity().onActivityResult(
                RC_SIGN_IN,
                RESULT_CANCELED,
                new Intent().putExtra("data", failedTask.getResult())
        );
    }

}