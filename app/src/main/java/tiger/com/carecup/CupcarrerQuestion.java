package tiger.com.carecup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Tiger on 12/28/2016.
 */

public class CupcarrerQuestion {
    String detail;
    String link;
    String author;
    String authorLink;
    int commentCount;
    Date date;
    String shortDate;  //December 28, 2016
    List<String> tags;
    boolean faviorte = false;

    public boolean isFaviorte() {
        return faviorte;
    }

    public CupcarrerQuestion() {
        tags = new ArrayList<>();
    }

    public void setDetails(String quesiton) {
        this.detail = quesiton;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void addTag(String tag) {
        tags.add(tag);
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setCommentCount(int count) {
        commentCount = count;
    }

    public void setShortDate(String date) {
        shortDate = date;
    }

    public void setSubmitDate(Date date) {this.date = date; }

}
