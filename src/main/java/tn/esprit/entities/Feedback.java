package tn.esprit.entities;

import java.util.Date;

public class Feedback {
    private int idFeedback;
    private String contentFeedback;
    private int ratingFeedback;
    private Date dateFeedback;
    private int idUser;


    public Feedback(String contentFeedback, int ratingFeedback, Date dateFeedback, int idUser) {
        this.contentFeedback = contentFeedback;
        this.ratingFeedback = ratingFeedback;
        this.dateFeedback = dateFeedback;
        this.idUser = idUser;
    }
    public Feedback(String contentFeedback, int ratingFeedback, int idUser) {
        this.contentFeedback = contentFeedback;
        this.ratingFeedback = ratingFeedback;
        this.idUser = idUser;
    }



    public Feedback(int idFeedback, String contentFeedback, int ratingFeedback, Date dateFeedback, int idUser) {
        this.idFeedback = idFeedback;
        this.contentFeedback = contentFeedback;
        this.ratingFeedback = ratingFeedback;
        this.dateFeedback = dateFeedback;
        this.idUser = idUser;
    }

    public Feedback(String content, Integer rating, int loggedInUserId) {
        this.contentFeedback = content;
        this.ratingFeedback = rating;
    }


    public int getIdFeedback() { return idFeedback; }
    public void setIdFeedback(int idFeedback) { this.idFeedback = idFeedback; }

    public String getContentFeedback() { return contentFeedback; }
    public void setContentFeedback(String contentFeedback) { this.contentFeedback = contentFeedback; }

    public int getRatingFeedback() { return ratingFeedback; }
    public void setRatingFeedback(int ratingFeedback) { this.ratingFeedback = ratingFeedback; }

    public Date getDateFeedback() { return dateFeedback; }
    public void setDateFeedback(Date dateFeedback) { this.dateFeedback = dateFeedback; }

    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }


    @Override
    public String toString() {
        return "Feedback{" +
                "idFeedback=" + idFeedback +
                ", contentFeedback='" + contentFeedback + '\'' +
                ", ratingFeedback=" + ratingFeedback +
                ", dateFeedback=" + dateFeedback +
                ", idUser=" + idUser +
                '}';
    }
}