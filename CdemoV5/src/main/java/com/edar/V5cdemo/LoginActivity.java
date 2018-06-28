package com.edar.V5cdemo;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.ImageViewTarget;
import java.util.List;
import butterknife.ButterKnife;
import butterknife.BindViews;
import android.support.annotation.ColorRes;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    @BindViews(value = {R.id.logo,R.id.first,R.id.second,R.id.last})
    protected List<ImageView> sharedElements;
    private ImageView google_sign_in ;

    public static GoogleSignInClient mGoogleSignInClient;
    private static int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;

    private static final String TAG = "LOGIN ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        google_sign_in = (ImageView)findViewById(R.id.second) ;
        ImageView twitter = (ImageView)findViewById(R.id.last);


        ButterKnife.bind(this);
        final AnimatedViewPager pager= ButterKnife.findById(this,R.id.pager);
        final ImageView background=ButterKnife.findById(this,R.id.scrolling_background);
        int[] screenSize=screenSize();

        for(ImageView element:sharedElements){
            @ColorRes int color=element.getId()!=R.id.logo?R.color.white_transparent:R.color.color_logo_log_in;
            DrawableCompat.setTint(element.getDrawable(), ContextCompat.getColor(this,color));
        }

        //chnage status bar color that is coming white
        Window window = LoginActivity.this.getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(LoginActivity.this,R.color.blue));

        //load a very big image and resize it, so it fits our needs
        Glide.with(this)
                .load(R.drawable.back_login_right)
                .asBitmap()
                .override(screenSize[0]*2,screenSize[1])
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(new ImageViewTarget<Bitmap>(background) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        background.setImageBitmap(resource);
                        background.post(()->{
                            //we need to scroll to the very left edge of the image
                            //fire the scale animation
                            background.scrollTo(-background.getWidth()/2,0);
                            ObjectAnimator xAnimator=ObjectAnimator.ofFloat(background,View.SCALE_X,4f,background.getScaleX());
                            ObjectAnimator yAnimator=ObjectAnimator.ofFloat(background,View.SCALE_Y,4f,background.getScaleY());
                            AnimatorSet set=new AnimatorSet();
                            set.playTogether(xAnimator,yAnimator);
                            set.setDuration(getResources().getInteger(R.integer.duration));
                            set.start();
                        });
                        pager.post(()->{
                            AuthAdapter adapter = new AuthAdapter(getSupportFragmentManager(), pager, background, sharedElements);
                            pager.setAdapter(adapter);
                        });
                    }
                });


        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestProfile()
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        google_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main = new Intent(LoginActivity.this,NavActivity.class );
                startActivity(main);
            }
        });

    }

    private int[] screenSize(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return new int[]{size.x,size.y};
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth!=null) {
            FirebaseUser user = mAuth.getCurrentUser();
            updateUI(user);
        }

    }

    void updateUI(FirebaseUser user){

        if (user==null)
            return;

//        Log.e("LOGIN :","email:"+user.getEmail());
//        Log.e("LOGIN :","Name :"+user.getDisplayName());

        Intent main = new Intent(LoginActivity.this,NavActivity.class );
        Bundle extras = main.getExtras();
//        extras.putParcelable("googleclient",mGoogleSignInClient);
        startActivity(main);

    }

    void updateUI(){
        Log.e("LOGIN ","UNSUCCESSFUL");
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
//            GoogleSignInAccount account = task.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
//            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI();
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            Snackbar.make(findViewById(R.i), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI();
                        }

                        // ...
                    }
                });
    }



}