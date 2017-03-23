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
   
   def displayCount(self):
     print ("Total Book %d" % Book.bookCount)

   def displayBook(self):
      print ("Name : ", self.title)
      print ("FiveStar : ", self.percFiveStar)

   def appendCustReview(self, custReview):
      self.listCustReview.append(custReview)

#print ("Book.__doc__:", Book.__doc__)
#print ("Book.__name__:", Book.__name__)
#print ("Book.__module__:", Book.__module__)
#print ("Book.__bases__:", Book.__bases__)
#print ("Book.__dict__:", Book.__dict__)
