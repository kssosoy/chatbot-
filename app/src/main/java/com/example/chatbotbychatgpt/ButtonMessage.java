package com.example.chatbotbychatgpt;

public class ButtonMessage extends Message {
    private String recommendedQuestion;

    public ButtonMessage(String recommendedQuestion) {
        super(recommendedQuestion, SENT_BY_BOT);
        this.recommendedQuestion = recommendedQuestion;
    }

    public String getRecommendedQuestion() {
        return recommendedQuestion;
    }
}
