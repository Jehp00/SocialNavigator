package com.example.socialnavigator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.facebook.appevents.AppEventsLogger;

import org.json.JSONException;
import org.json.JSONObject;

public class DashBoard extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        FacebookSdk.fullyInitialize();
        AppEventsLogger.activateApp(this.getApplication());
        // Find TextViews
        TextView textLikes = findViewById(R.id.textLikes);
        TextView textComments = findViewById(R.id.textComments);
        TextView textShares = findViewById(R.id.textShares);

        // Login Facebook


        // Connect API of social networks for these data
        // Assign the values for counter
        // && !accessToken.isExpired()
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        System.out.println(accessToken);
        fetchLikesCount(textLikes);
        fetchCommentsCount(textComments);
        fetchSharesCount(textShares);

        Button button = findViewById(R.id.btn_back_to_main);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // Likes
    private void fetchLikesCount(final TextView textLikes) {
        GraphRequest request = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me?fields=likes.summary(true)",
                null,
                HttpMethod.GET,
                graphResponse -> {
                    if (graphResponse.getError() == null) {
                        JSONObject data = graphResponse.getJSONObject();
                        if (data != null) {
                            try {
                                System.out.println(data.getJSONObject("summary"));
                                System.out.println(data.getJSONArray("data"));
                                int likesCount = data.getJSONArray("data").length();
                                textLikes.setText(String.valueOf(likesCount));
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    } else {
                        throw new RuntimeException();
                    }
                }
        );
        request.executeAsync();
    }
    // Comments
    private void fetchCommentsCount(final TextView textComments) {
        GraphRequest request = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/122119605926149922_122119605326149922/comments?summary=1&filter=toplevel", // hardcoding must count the comments i others posts
                null,
                HttpMethod.GET,
                graphResponse -> {
                    if (graphResponse.getError() == null) {
                        JSONObject data = graphResponse.getJSONObject();
                        if (data != null) {
                            try {
                                int commentsCount = data.getJSONObject("summary").getInt("total_count");
                                textComments.setText(String.valueOf(commentsCount));
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    } else {
                        throw new RuntimeException();
                    }
                }
        );
        request.executeAsync();
    }

    // Shares
    private void fetchSharesCount(final TextView textShares) {
        GraphRequest request = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/feed",
                null,
                HttpMethod.GET,
                graphResponse -> {
                    if (graphResponse.getError() == null) {
                        JSONObject data = graphResponse.getJSONObject();
                        if (data != null) {
                            try {
                                // Access the first post (you may need to loop through posts)
                                JSONObject firstPost = data.getJSONArray("data").getJSONObject(0);
                                int sharesCount = firstPost.getJSONObject("shares").getInt("count");
                                textShares.setText(String.valueOf(sharesCount));
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    } else {
                        throw new RuntimeException();
                    }
                }
        );
        request.executeAsync();
    }

}