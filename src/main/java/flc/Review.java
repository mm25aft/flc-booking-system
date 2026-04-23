package flc;

public class Review {
    private final String reviewText;
    private final int rating;

    public Review(String reviewText, int rating) {
        this.reviewText = reviewText == null ? "" : reviewText;
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public int getRating() {
        return rating;
    }

    public boolean isValidRating() {
        return rating >= 1 && rating <= 5;
    }
}
