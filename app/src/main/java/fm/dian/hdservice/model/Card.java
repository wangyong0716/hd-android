package fm.dian.hdservice.model;

import com.google.protobuf.ByteString;

import java.io.Serializable;

import fm.dian.service.blackboard.HDBlackboardCard;
import fm.dian.service.blackboard.HDBlackboardCard.Card.Builder;
import fm.dian.service.blackboard.HDBlackboardCard.CardImage;
import fm.dian.service.blackboard.HDBlackboardCard.CardText;
import fm.dian.service.blackboard.HDBlackboardCard.CardType;

/**
 * Created by tinx on 4/13/15.
 */
public class Card implements Serializable {

    private Builder builder;

    public Card() {
        this.builder = HDBlackboardCard.Card.newBuilder();
    }

    public Card(HDBlackboardCard.Card card) {
        this.builder = card.toBuilder();
    }

    public HDBlackboardCard.Card getCard() {
        return this.builder.build();
    }


    public CardType getCardType() {
        return builder.hasCardType() ? builder.getCardType() : null;
    }

    public void setCardType(CardType cardType) {
        this.builder.setCardType(cardType);
    }

    public String getData() {
        if (builder.hasExtension(CardText.cardText)) {
            CardText cardText = builder.getExtension(CardText.cardText);
            return cardText.getData().toStringUtf8();
        } else if (builder.hasExtension(CardImage.cardImage)) {
            CardImage cardImage = builder.getExtension(CardImage.cardImage);
            return cardImage.getData().toStringUtf8();
        } else {
            return null;
        }
    }

    public void setData(CardType type, String data) {
        this.setCardType(type);
        if (type == CardType.TEXT) {
            CardText cardText = CardText.newBuilder()
                    .setData(ByteString.copyFrom(data.getBytes()))
                    .build();
            builder.setExtension(CardText.cardText, cardText);
        } else if (type == CardType.IMAGE) {
            CardImage cardImage = CardImage.newBuilder()
                    .setData(ByteString.copyFrom(data.getBytes()))
                    .build();
            builder.setExtension(CardImage.cardImage, cardImage);
        }
    }

    public Long getCardId() {
        return builder.hasCardId() ? builder.getCardId() : null;
    }

    public void setCardId(Long cardId) {
        this.builder.setCardId(cardId);
    }

    public Long getUserId() {
        return builder.hasUserId() ? builder.getUserId() : null;
    }

    public void setUserId(Long userId) {
        this.builder.setUserId(userId);
    }
}
