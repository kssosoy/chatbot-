
package com.example.chatbotbychatgpt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {
    private final List<Message> messageList;
    private final MainActivity mainActivity;

    public MessageAdapter(List<Message> messageList, MainActivity mainActivity) {
        this.messageList = messageList;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Message message = messageList.get(position);

        if (message instanceof ButtonMessage) {
            ButtonMessage buttonMessage = (ButtonMessage) message;
            holder.rightChatView.setVisibility(View.GONE);
            holder.leftChatView.setVisibility(View.GONE);
            holder.recommendButton.setVisibility(View.VISIBLE);
            holder.chatbotIcon.setVisibility(View.GONE);
            holder.recommendButton.setText(buttonMessage.getRecommendedQuestion());

            holder.recommendButton.setOnClickListener(v -> {
                String recommendedQuestion = buttonMessage.getRecommendedQuestion();
                mainActivity.addToChat(recommendedQuestion, Message.SENT_BY_ME);
                mainActivity.callAPI(recommendedQuestion);
                messageList.remove(position);
                notifyDataSetChanged();
            });

        } else if (message.getSentBy().equals(Message.SENT_BY_ME)) {
            holder.leftChatView.setVisibility(View.GONE);
            holder.rightChatView.setVisibility(View.VISIBLE);
            holder.recommendButton.setVisibility(View.GONE);
            holder.chatbotIcon.setVisibility(View.GONE);
            holder.rightTextView.setText(message.getMessage());
        } else {
            holder.rightChatView.setVisibility(View.GONE);
            holder.leftChatView.setVisibility(View.VISIBLE);
            holder.recommendButton.setVisibility(View.GONE);
            holder.chatbotIcon.setVisibility(View.VISIBLE);
            holder.leftTextView.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View leftChatView;
        View rightChatView;
        Button recommendButton;
        Button recommendButton2;
        TextView leftTextView;
        TextView rightTextView;
        ImageView chatbotIcon;

        public MyViewHolder(View itemView) {
            super(itemView);

            leftChatView = itemView.findViewById(R.id.left_chat_view);
            rightChatView = itemView.findViewById(R.id.right_chat_view);
            leftTextView = itemView.findViewById(R.id.left_chat_text_view);
            rightTextView = itemView.findViewById(R.id.right_chat_text_view);
            recommendButton = itemView.findViewById(R.id.recommend_button);
            chatbotIcon = itemView.findViewById(R.id.chatbot_icon);
        }
    }
}