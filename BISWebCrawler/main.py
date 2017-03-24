import ProductPage

urlReview = 'https://www.amazon.com/Big-Data-Revolution-Transform-Think/product-reviews/0544227751'
url = 'https://www.amazon.com/Big-Data-Revolution-Transform-Think/dp/0544227751'

book = ProductPage.get_book(url, urlReview)
print("main")
print(book.author)
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
        'author' : book.author,
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
        'nbVotes' : review.nbVotes
        }
    print(data_review)
# for review in book.listCustReview:
#     print(review.author, ' : ', review.authorLink)
