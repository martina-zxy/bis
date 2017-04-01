class Book:

    def __init__(self, asin, isbn10, isbn13, title):
        self.asin = asin
        self.isbn10 = isbn10
        self.isbn13 = isbn13
        self.title = title
        self.language = ''
        self.percFiveStar = 0
        self.percFourStar = 0
        self.percThreeStar = 0
        self.percTwoStar = 0
        self.percOneStar = 0
        self.summary = ''
        self.avgCustReview = 0
        self.publisher = ''
        self.publicationDate = ''
        self.rank = 0
        self.listAuthor = None
        self.listCustReview = None
        self.nbCustReview = 0
   
    def display_count(self):
        print ("Total Book %d" % Book.bookCount)

    def display_book(self):
        print ("Name : ", self.title)
        print ("FiveStar : ", self.percFiveStar)

    def append_cust_review(self, custReview):
      self.listCustReview.append(custReview)

    def set_star_percentage(self, perc5star, perc4star, perc3star, perc2star, perc1star):
        self.percFiveStar = perc5star
        self.percFourStar = perc4star
        self.percThreeStar = perc3star
        self.percTwoStar = perc2star
        self.percOneStar = perc1star

class Review:
    def __init__(self, asin):
        self.id = ''
        self.asin = asin
        self.nbStar = ''
        self.title = ''
        self.date = ''
        self.reviewerId = ''
        self.verifiedPurchase = False
        self.reviewText = ''
        self.nbHelpful = 0
        self.nbVotes = 0
        self.author_link = ''
