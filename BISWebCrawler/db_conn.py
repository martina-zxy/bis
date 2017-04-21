from bs4 import BeautifulSoup

import os
import csv
import pyodbc
import ReviewerPage
import ProductPage
from entity.user import Reviewer
from entity.book import Book

cnxn = pyodbc.connect('DRIVER={SQL Server};SERVER=10.195.25.10,54767;DATABASE=AmazonBook;UID=bis;PWD=bis')
cursor = cnxn.cursor()

def insert_reviewer(reviewer):
    query = "INSERT INTO Reviewer VALUES(?,?,?,?,?,?,0)"
    # query = "INSERT INTO Reviewer VALUES(\'" + reviewer.id + "\',\'" + reviewer.name + "\',\'" + reviewer.country + "\'," + str(reviewer.nbHelpfulVotes) + "," + str(reviewer.rank) + ",\'" + reviewer.profileText + "\',0)" #TODO
    print(query)
    cursor.execute(query, (reviewer.id,
                           reviewer.name,
                           reviewer.country,
                           str(reviewer.nbHelpfulVotes),
                           str(reviewer.rank),
                           reviewer.profileText))
    cnxn.commit()

def insert_book(book):
    query = "INSERT INTO Book VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
    # query = "INSERT INTO Book VALUES(\'" + book.asin + "\',\'" + book.isbn10 + "\',\'" + book.isbn13 + "\',\'" + book.title + "\',\'" + book.summary + "\',\'" + book.language + "\'," + str(book.avgCustReview) + "," + str(book.nbCustReview) + "," + str(book.percFiveStar) + "," + str(book.percFourStar) + "," + str(book.percThreeStar) + "," + str(book.percTwoStar) + "," + str(book.percOneStar) + ",\'" + "0" + "\'," + str(book.rank) + ",\'" + book.publisher + "\',\'" + book.publicationDate +"\')"
    print(query,book.asin,
                           book.isbn10,
                           book.isbn13,
                           book.title,
                           book.language,
                           str(book.avgCustReview),
                           str(book.nbCustReview),
                           str(book.percFiveStar),
                           str(book.percFourStar),
                           str(book.percThreeStar),
                           str(book.percTwoStar),
                           str(book.percOneStar),
                           str(book.rank),
                           book.publisher,
                           book.publicationDate,
                           book.category)
    cursor.execute(query, (book.asin,
                           book.isbn10,
                           book.isbn13,
                           book.title,
                           book.summary,
                           book.language,
                           str(book.avgCustReview),
                           str(book.nbCustReview),
                           str(book.percFiveStar),
                           str(book.percFourStar),
                           str(book.percThreeStar),
                           str(book.percTwoStar),
                           str(book.percOneStar),
                           str(book.rank),
                           book.publisher,
                           book.publicationDate,
                           book.category))
    cnxn.commit()

def insert_review(review):
    # query = "INSERT INTO CustomerReview (CustomerReviewID, ASIN, NbStar, ReviewTitle, ReviewDate, ReviewerID, VerifiedPurchase, ReviewText, NbHelpful, NbVotes) " \
    #         "VALUES(\'" + review.id + "\',\'" + review.asin + "\',\'" + str(int(review.nbStar)) + "\',\'" + review.title + "\','" + review.date + "','" + review.reviewerId + "'," + str(int(review.verifiedPurchase)) + ",'" + review.reviewText + "'," + str(review.nbHelpful) + "," + str(review.nbVotes) + ")" #TODO
    query = "INSERT INTO CustomerReview (CustomerReviewID, ASIN, NbStar, ReviewTitle, ReviewDate, ReviewerID, VerifiedPurchase, ReviewText, NbHelpful, NbVotes) " \
            "VALUES(?,?,?,?,?,?,?,?,?,?)"
    print(query)
    cursor.execute(query,(review.id,
                          review.asin,
                          str(int(review.nbStar)),
                          review.title,
                          review.date,
                          review.reviewerId,
                          str(int(review.verifiedPurchase)),
                          review.reviewText,
                          str(review.nbHelpful),
                          str(review.nbVotes)  ))
    cnxn.commit()

def insert_author(author):
    query = "INSERT INTO Author VALUES(?,?,?,?,?)"
    # query = "INSERT INTO Book VALUES(\'" + book.asin + "\',\'" + book.isbn10 + "\',\'" + book.isbn13 + "\',\'" + book.title + "\',\'" + book.summary + "\',\'" + book.language + "\'," + str(book.avgCustReview) + "," + str(book.nbCustReview) + "," + str(book.percFiveStar) + "," + str(book.percFourStar) + "," + str(book.percThreeStar) + "," + str(book.percTwoStar) + "," + str(book.percOneStar) + ",\'" + "0" + "\'," + str(book.rank) + ",\'" + book.publisher + "\',\'" + book.publicationDate +"\')"
    print(query)
    cursor.execute(query, (author.id,
                           author.name,
                           author.biography,
                           author.rank,
                           author.link))
    cnxn.commit()

def insert_comment(comment):
    query = "INSERT INTO CustomerReviewComment VALUES(?,?,?,?,?,?)"
    # query = "INSERT INTO Book VALUES(\'" + book.asin + "\',\'" + book.isbn10 + "\',\'" + book.isbn13 + "\',\'" + book.title + "\',\'" + book.summary + "\',\'" + book.language + "\'," + str(book.avgCustReview) + "," + str(book.nbCustReview) + "," + str(book.percFiveStar) + "," + str(book.percFourStar) + "," + str(book.percThreeStar) + "," + str(book.percTwoStar) + "," + str(book.percOneStar) + ",\'" + "0" + "\'," + str(book.rank) + ",\'" + book.publisher + "\',\'" + book.publicationDate +"\')"
    print(query, comment.id,
                           comment.reviewId,
                           comment.commenterId,
                           comment.date,
                           comment.text,
                           comment.commenterLink)
    cursor.execute(query, (comment.id,
                           comment.reviewId,
                           comment.commenterId,
                           comment.date,
                           comment.text,
                           comment.commenterLink))
    cnxn.commit()


# self.id = ''
# self.reviewId = ''
# self.commenterId = ''
# self.commenterName = ''
# self.commenterLink = ''
# self.date = ''
# self.text = ''
def insert_best_seller(bs):
    query = "INSERT INTO BestSeller (asin, link, crawled, reviewNb) VALUES(?,?,?,?)"
    print(query,bs.asin,
               bs.link,
               0,
               bs.reviewNb)
    cursor.execute(query, (bs.asin,
                           bs.link,
                           0,
                           bs.reviewNb))
    cnxn.commit()

def insert_authorbook(authorid, asin):
    query = "INSERT INTO AuthorBook VALUES(?,?)"
    print(query, authorid, asin)
    cursor.execute(query, (authorid,
                           asin))
    cnxn.commit()

def insert_booktocrawl(asin, link, source):
    query = "INSERT INTO BookToCrawl VALUES(?,?,0,?)"
    print(query, asin, link, source)
    cursor.execute(query, (asin,
                           link,
                           source))
    cnxn.commit()

def insert_authortocrawl(id, link, source):
    query = "INSERT INTO AuthorToCrawl VALUES(?,?,0,?)"
    print(query, id, link, source)
    cursor.execute(query, (id,
                           link,
                           source))
    cnxn.commit()

def insert_frequentlyboughttogether(asin1, asin2):
    query = "INSERT INTO FrequentlyBoughtTogether VALUES(?,?)"
    print(query, asin1, asin2)
    cursor.execute(query, (asin1,
                           asin2))
    cnxn.commit()

def insert_alsobought(asin1, asin2):
    query = "INSERT INTO AlsoBought VALUES(?,?)"
    print(query)
    cursor.execute(query, (asin1,
                           asin2))
    cnxn.commit()

def insert_reviewerfollowing(reviewer, author):
    query = "INSERT INTO ReviewerFollowing VALUES(?,?)"
    print(query, reviewer, author)
    cursor.execute(query, (reviewer,
                           author))
    cnxn.commit()

def insert_reviewerfollowingcustomer(reviewer, customer):
    query = "INSERT INTO ReviewerFollowingCustomer VALUES(?,?)"
    print(query, reviewer, customer)
    cursor.execute(query, (reviewer,
                           customer))
    cnxn.commit()

def insert_followedcustomer(id, link, name):
    query = "INSERT INTO FollowedCustomer VALUES(?,?,?)"
    print(query, id, link, name)
    cursor.execute(query, (id,
                           link,
                           name))
    cnxn.commit()

def get_best_seller_to_crawl(top):
    query = "select top " + str(top) + " * from BestSeller where crawled = 0 and reviewNb > 0 order by reviewNb"
    print(query, top)
    cursor.execute(query)
    rows = cursor.fetchall()
    return rows

def check_reviewer(reviewer_id):
    query = "select * from Reviewer where ReviewerID = '" + reviewer_id + "'"
    print(query)
    cursor.execute(query)
    rows = cursor.fetchall()
    if len(rows) > 0:
        return True
    else:
        return False

def update_best_seller_crawled(asin):
    query = "update BestSeller set crawled = 1 where asin = " + asin
    print(query, asin)
    cursor.execute(query)

def check_author(author_id):
    query = "select * from Author where AuthorID = '" + author_id + "'"
    print(query)
    cursor.execute(query)
    rows = cursor.fetchall()
    if len(rows) > 0:
        return True
    else:
        return False

def check_book(asin):
    query = "select * from Book where ASIN = '" + asin + "'"
    print(query)
    cursor.execute(query)
    rows = cursor.fetchall()
    if len(rows) > 0:
        return True
    else:
        return False

def check_authorbook(author_id, asin):
    query = "select * from AuthorBook where ASIN = '" + asin + "' and AuthorID = '" + author_id + "'"
    print(query)
    cursor.execute(query)
    rows = cursor.fetchall()
    if len(rows) > 0:
        return True
    else:
        return False

def check_booktocrawl(asin):
    query = "select * from BookToCrawl where ASIN = '" + asin + "'"
    print(query)
    cursor.execute(query)
    rows = cursor.fetchall()
    if len(rows) > 0:
        return True
    else:
        return False

def check_customerreview(id):
    query = "select * from CustomerReview where CustomerReviewID = ?"
    print(query, id)
    cursor.execute("select * from CustomerReview where CustomerReviewID = ?",(id))
    rows = cursor.fetchall()
    if len(rows) > 0:
        return True
    else:
        return False

def check_customerreview(id):
    cursor.execute("select * from CustomerReview where CustomerReviewID = ?",(id))
    rows = cursor.fetchall()
    if len(rows) > 0:
        return True
    else:
        return False

def check_frequentlyboughttogether(asin1, asin2):
    cursor.execute("select * from FrequentlyBoughtTogether where ASIN = ? and ASINTogether = ?",(asin1, asin2))
    rows1 = cursor.fetchall()

    cursor.execute("select * from FrequentlyBoughtTogether where ASIN = ? and ASINTogether = ?", (asin2, asin1))
    rows2 = cursor.fetchall()
    if len(rows1) + len(rows2) > 0:
        return True
    else:
        return False


def check_alsobought(asin1, asin2):
    cursor.execute("select * from AlsoBought where ASIN = ? and ASINAlsoBought = ?",(asin1, asin2))
    rows1 = cursor.fetchall()

    cursor.execute("select * from AlsoBought where ASIN = ? and ASINAlsoBought = ?", (asin2, asin1))
    rows2 = cursor.fetchall()
    if len(rows1) + len(rows2) > 0:
        return True
    else:
        return False


def check_authortocrawl(id):
    query = "select * from AuthorToCrawl where authorid = ?"
    print(query,id)
    cursor.execute(query,(id))
    rows = cursor.fetchall()
    if len(rows) > 0:
        return True
    else:
        return False

def check_following(reviewer,author):
    query = "select * from ReviewerFollowing where ReviewerID = ? and AuthorID = ?"
    print(query, reviewer, author)
    cursor.execute(query,(reviewer,author))
    rows = cursor.fetchall()
    if len(rows) > 0:
        return True
    else:
        return False

def check_followingcustomer(reviewer,customer):
    query = "select * from ReviewerFollowingCustomer where ReviewerID = ? and CustomerID = ?"
    print(query, reviewer, customer)
    cursor.execute(query,(reviewer,customer))
    rows = cursor.fetchall()
    if len(rows) > 0:
        return True
    else:
        return False

def check_followed_customer(customer_id):
    query = "select * from FollowedCustomer where CustomerID = ?"
    print(query, customer_id)
    cursor.execute(query,(customer_id))
    rows = cursor.fetchall()
    if len(rows) > 0:
        return True
    else:
        return False

def check_comment(id):
    query = "select * from CustomerReviewComment where CommentID = ?"
    print(query, id)
    cursor.execute(query,(id))
    rows = cursor.fetchall()
    if len(rows) > 0:
        return True
    else:
        return False


def update_best_seller_crawled(asin):
    query = "update BestSeller set crawled = 1 where asin ='" + asin + "'"
    print(query)
    cursor.execute(query)
    cnxn.commit()

def update_book_crawled(asin):
    query = "update BookToCrawl set crawled = 1 where asin ='" + asin + "'"
    print(query)
    cursor.execute(query)
    cnxn.commit()

def update_author_crawled(authorid):
    query = "update Author set crawled = 1 where authorid ='" + authorid + "'"
    print(query)
    cursor.execute(query)
    cnxn.commit()