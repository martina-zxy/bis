class Book:

    def __init__(self, asin, isbn10, isbn13, title):
        self.asin = asin
        self.isbn10 = isbn10
        self.isbn13 = isbn13
        self.title = title
        self.percFiveStar = 0
        self.percFourStar = 0
        self.percThreeStar = 0
        self.percTwoStar = 0
        self.percOneStar = 0
        self.summary = ''
        self.avgCustReview = 0
        self.publicationDate = ''
        self.rank = 0
        self.author = None
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

#print ("Book.__doc__:", Book.__doc__)
#print ("Book.__name__:", Book.__name__)
#print ("Book.__module__:", Book.__module__)
#print ("Book.__bases__:", Book.__bases__)
#print ("Book.__dict__:", Book.__dict__)
