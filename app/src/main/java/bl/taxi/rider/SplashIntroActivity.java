package bl.taxi.rider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.app.NavigationPolicy;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SplashIntroActivity extends IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setButtonBackVisible(false);
        setButtonNextVisible(false);
        setButtonCtaVisible(true);
        setButtonCtaTintMode(BUTTON_CTA_TINT_MODE_BACKGROUND);
        autoplay(4000,INFINITE);


        addSlide(new SimpleSlide.Builder()
                .description("Check cab availability and book at your convenience.")
                .image(R.mipmap.splash_booking)
                .background(R.color.color_canteen)
                .backgroundDark(R.color.colorPrimary)
                .buttonCtaLabel("\t \t \t \t \t Book Ride \t \t \t \t \t")
                .buttonCtaClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(),SplashActivity.class);
                        startActivity(intent);
                    }
                })
                .build());

        addSlide(new SimpleSlide.Builder()
                .description("Track your ride in real time")
                .image(R.mipmap.splash_track_ride)
                .background(R.color.color_canteen)
                .backgroundDark(R.color.colorPrimary)
                .buttonCtaLabel("\t \t \t \t \t Book Ride \t \t \t \t \t")
                .buttonCtaClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(),SplashActivity.class);
                        startActivity(intent);
                    }
                })
                .build());

        addSlide(new SimpleSlide.Builder()
                .description("Share and let your close ones know that you are in safe hands")
                .image(R.mipmap.splash_favourites)
                .background(R.color.color_canteen)
                .backgroundDark(R.color.colorPrimary)
                .buttonCtaLabel("\t \t \t \t \t Book Ride \t \t \t \t \t")
                .buttonCtaClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(),SplashActivity.class);
                        startActivity(intent);
                    }
                })
                .build());

        //Feel free to add a navigation policy to define when users can go forward/backward
        setNavigationPolicy(new NavigationPolicy() {
            @Override
            public boolean canGoForward(int position) {
                return position != 2;

            }

            @Override
            public boolean canGoBackward(int position) {
                return true;
            }
        });


    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


}
