package com.example.sentiment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private EditText editTextInput;
    private TextView textViewResult;
    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextInput = findViewById(R.id.editTextInput);
        textViewResult = findViewById(R.id.textViewResult);
        Button buttonAnalyze = findViewById(R.id.buttonAnalyze);

        buttonAnalyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                analyzeSentiment(editTextInput.getText().toString());
            }
        });
    }

    private void analyzeSentiment(String inputText) {
        String url = "http://192.168.1.3:5000/predict"; // Change this to your actual endpoint

        // Prepare JSON object for the API request
        JSONObject postData = new JSONObject();
        try {
            postData.put("text", inputText);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating JSON object.", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody requestBody = RequestBody.create(postData.toString(), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> textViewResult.setText("Error: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    runOnUiThread(() -> {
                        try {
                            JSONObject jsonResponse = new JSONObject(responseData);
                            String result = jsonResponse.getString("sentiment"); // Change "sentiment" to your API's response key
                            textViewResult.setText("Sentiment: " + result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            textViewResult.setText("Error in parsing response.");
                        }
                    });
                } else {
                    runOnUiThread(() -> textViewResult.setText("Error: " + response.message()));
                }
            }
        });
    }
}