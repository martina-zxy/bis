import ProductPage
import db_conn
import ReviewerPage
from datetime import datetime

urlReview = 'https://www.amazon.com/Big-Data-Revolution-Transform-Think/product-reviews/0544227751'
url = 'https://www.amazon.com/Big-Data-Revolution-Transform-Think/dp/0544227751'
url = 'https://www.amazon.com/Big-Little-Lies-Liane-Moriarty/dp/0425274861'
# url = 'https://www.amazon.com/Mangrove-Lightning-Doc-Ford-Novel-ebook/dp/B01HYUVHGY/ref=cm_rdp_product'


book = ProductPage.get_book(url, urlReview)

data_book = {
        'asin':book.asin,
        'language':book.language,
        'avgCustReview' : book.avgCustReview,
        'nbCustReview' : book.nbCustReview,
        'percFiveStar' : book.percFiveStar,
        'percFourStar' : book.percFourStar,
        'percThreeStar' : book.percThreeStar,
        'percTwoStar' : book.percTwoStar,
        'percOneStar' : book.percOneStar,
        'rank' : book.rank,
        'publisher' : book.publisher,
        'publicationDate' : book.publicationDate
        }
print(data_book)
for review in book.listCustReview:
    data_review = {
        'asin':review.asin,
        'nbStar' : review.nbStar,
        'title' : review.title,
        'date' : review.date,
        'reviewerId' : review.reviewerId,
        'verifiedPurchase' : review.verifiedPurchase,
        'text' : review.reviewText,
        'nbHelpful' : review.nbHelpful,
        'nbVotes' : review.nbVotes,
        'reviewer_profile' : review.author_link
        }
    print(data_review)

    isReviewer = db_conn.check_reviewer(review.reviewerId)
    print(isReviewer)
    if not isReviewer:
        reviewer_url = 'https://www.amazon.com' + review.author_link
        print(reviewer_url)
        reviewer = ReviewerPage.get_reviewer(reviewer_url)
        print(reviewer.name)
        db_conn.insert_reviewer(reviewer)

    db_conn.insert_review(review)

# for review in book.listCustReview:
#     print(review.author, ' : ', review.authorLink)
