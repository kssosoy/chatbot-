package com.example.chatbotbychatgpt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView welcomeTextView;
    EditText messageEditText;
    ImageButton sendButton;
    List<Message> messageList;
    MessageAdapter messageAdapter;
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        messageList = new ArrayList<>();

        messageList.add(new Message("안녕하세요?\n 김감자님 편의점 레시피 챗봇입니다.무엇을 도와드릴까요?", Message.SENT_BY_BOT));
        messageList.add(new ButtonMessage("레시피를 추천해줘"));


        recyclerView = findViewById(R.id.recycler_view);
        welcomeTextView = findViewById(R.id.welcome_text);
        messageEditText = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.send_btn);

        //setup recycler view
        messageAdapter = new MessageAdapter(messageList, this);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        sendButton.setOnClickListener((v)->{
            String question = messageEditText.getText().toString().trim();
            addToChat(question,Message.SENT_BY_ME);
            messageEditText.setText("");
            callAPI(question);
            welcomeTextView.setVisibility(View.GONE);
        });
    }

    void addToChat(String message,String sentBy){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new Message(message,sentBy));
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });
    }

    void addResponse(String response){
        //messageList.remove(messageList.size()-1);  이 부분 주석처리
        addToChat(response,Message.SENT_BY_BOT);
    }


    void callAPI(String question){
        JSONObject systemMessage = new JSONObject();
        JSONObject userMessage = new JSONObject();
        JSONArray messages = new JSONArray();

        try {
            systemMessage.put("role", "system");
            systemMessage.put("content", "너는 편의점 레시피에 대해 잘알고 있는 챗봇이야");
            messages.put(systemMessage);

            userMessage.put("role", "user");
            userMessage.put("content", question);
            messages.put(userMessage);

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("model", "ft:gpt-3.5-turbo-0613:personal:pleaseplease:8Uzc8Hm7");
            jsonBody.put("messages", messages);
            jsonBody.put("max_tokens", 500);
            jsonBody.put("temperature", 0.2);

            RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
            Request request = new Request.Builder()
                    .url("https://api.openai.com/v1/chat/completions")
                    .header("Authorization", "Bearer apikey")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    addResponse("응답을 불러오는데에 실패하였습니다. " + e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if(response.isSuccessful()){
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response.body().string());
                            JSONArray jsonArray = jsonObject.getJSONArray("choices");
                            String result = jsonArray.getJSONObject(0).getJSONObject("message").getString("content");
                            addResponse(result.trim());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else{
                        addResponse("응답을 불러오는데에 실패하였습니다. "+response.body().string());
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




}